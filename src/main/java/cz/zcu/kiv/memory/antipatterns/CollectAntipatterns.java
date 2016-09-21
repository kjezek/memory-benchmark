package cz.zcu.kiv.memory.antipatterns;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipFile;

/**
 *
 */
public class CollectAntipatterns {

    private static Logger LOGGER = LoggerFactory.getLogger(CollectAntipatterns.class);

    private static ExtractorFactory[] EXTRACTORS = {
            ExtractorFactory.EXC_FLEXIBLE
    };

    public static void main(String[] args) throws Exception {

        File DATA_FOLDER = new File(Preferences.getDataFolder());
        File OUTPUT_FOLDER = new File(Preferences.getOutputContractsFolder());
        int THREAD_COUNT = Preferences.getThreadCount();

        Collection<File> zips = FileUtils.listFiles(DATA_FOLDER, new String[]{"zip"}, true);
        int total = zips.size();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        FileUtils.forceMkdir(OUTPUT_FOLDER);
        AtomicInteger progressCounter = new AtomicInteger(0);
        AtomicInteger parserFailedCUCounter = new AtomicInteger(0);
        AtomicInteger parsedProgramVersionCounter = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        Set<String> antipatterns = Collections.synchronizedSet(new HashSet<>());
        ResultConsumer consumer = result -> antipatterns.add(result.toString());

        for (File zip : zips) {
            parsedProgramVersionCounter.incrementAndGet();
            ProgramVersion v = ProgramVersion.getOrCreateFromFile(zip);
            String programName = v.getName();
            String version = v.getVersion();

            Runnable task = () -> {
                try {
                    find(new ZipFile(zip), programName, version, consumer);
                    LOGGER.info("Processed " + progressCounter.incrementAndGet() + "/" + total + ": " + zip.getAbsolutePath() + " -- ");
                } catch (Exception e) {
                    // log errors and continue with next files
                    LOGGER.warn("Cannot parse file: " + zip, e);
                }
            };
            executor.submit(task);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
        long endTime = System.currentTimeMillis();

        LOGGER.info("Done!");
        LOGGER.info("\ttime: " + (endTime - startTime) + " ms");
        LOGGER.info("\tCUs where parsing failed: " + parserFailedCUCounter.intValue());
        LOGGER.info("\tprogram versions checked: " + parsedProgramVersionCounter.intValue());
        LOGGER.info("\tthreads used: " + THREAD_COUNT);

        LOGGER.info("Antipatterns");
        for (String antipattern : antipatterns) {
            LOGGER.info("\t" + antipattern);
        }
    }

    @SuppressWarnings("unchecked")
    private static void find(ZipFile zip, String programName, String version, ResultConsumer consumer) throws Exception {

        for (ExtractorFactory extractor : EXTRACTORS) {
            extractor.create(consumer).analyse(zip, programName, version);
        }

    }


}
