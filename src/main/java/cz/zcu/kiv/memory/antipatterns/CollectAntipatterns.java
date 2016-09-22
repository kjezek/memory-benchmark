package cz.zcu.kiv.memory.antipatterns;

import cz.zcu.kiv.memory.antipatterns.excflexible.ExcFlexibleExtractor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *
 */
public class CollectAntipatterns {

    private static Logger LOGGER = LoggerFactory.getLogger(CollectAntipatterns.class);
    private static File DATA_FOLDER = new File(Preferences.getDataFolder());

    private static Extractor[] EXTRACTORS = {
            Extractor.EXC_FLEXIBLE
    };
    public static final File MVN_DEPENDENCIES = new File("mvn-dependencies");

    public static void main(String[] args) throws Exception {

        File OUTPUT_FOLDER = new File(Preferences.getResultsFolder());
        int THREAD_COUNT = Preferences.getThreadCount();

        Collection<File> zips = FileUtils.listFiles(DATA_FOLDER, new String[]{"zip"}, true);
        int total = zips.size();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        FileUtils.forceMkdir(OUTPUT_FOLDER);
        AtomicInteger progressCounter = new AtomicInteger(0);
        AtomicInteger parserFailedCUCounter = new AtomicInteger(0);
        AtomicInteger parsedProgramVersionCounter = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        // do not forget to add this folder!
        File jdkDir = new File(Preferences.getDataFolder(), "open-jdk");
        File jdk = new File(jdkDir, "open-jdk-8.zip");
        File[] globalDeps = new File[] {jdk};


        for (File zip : zips) {
            parsedProgramVersionCounter.incrementAndGet();
            ProgramVersion v = ProgramVersion.getOrCreateFromFile(zip);
            String programName = v.getName();
            String version = v.getVersion();

            Runnable task = () -> {
                try {
                    Set<Antipattern> antipatterns = new HashSet<>();
                    ResultConsumer<ExcFlexibleExtractor.ExcFlexibleAntipattern> consumer = antipatterns::add;

                    find(zip, globalDeps, programName, version, consumer);
                    LOGGER.info("Processed " + progressCounter.incrementAndGet() + "/" + total + ": " + zip.getAbsolutePath() + " -- ");

                    if (!antipatterns.isEmpty()) {
                        final File programFile = new File(OUTPUT_FOLDER, programName);
                        FileUtils.forceMkdir(programFile);
                        final File resultFile = new File(programFile, programName + "-" + version + ".txt");
                        try (PrintStream stream = new PrintStream(resultFile)) {
                            for (Antipattern a : antipatterns) {
                                stream.println(a.toTxt());
                            }
                        }
                    }
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

    }

    @SuppressWarnings("unchecked")
    private static void find(File zip, File[] globalDeps, String programName, String version, ResultConsumer consumer) throws Exception {

        // add global dependencies
        Set<ZipFile> depsZips = new HashSet<>();
        for (File file : globalDeps) {
            depsZips.add(new ZipFile(file));
        }

        // add project dependencies
//        final File programFile = new File(DATA_FOLDER, programName);
//        File json = new File(programFile, programName + "-" + version + "-deps.json");
//        List<ProgramVersion> deps = Utils.parseDepsProgramVersion(MVN_DEPENDENCIES, json);
//        for (ProgramVersion dep : deps) {
//            try {
//                depsZips.add(new ZipFile(dep.getFile()));
//            } catch (ZipException e) {
//                LOGGER.trace("Cannot parse {}", dep.getFile());
//            }
//        }

        ZipFile[] depsArray = depsZips.toArray(new ZipFile[0]);

        for (Extractor extractor : EXTRACTORS) {
            extractor.analyse(consumer, new ZipFile(zip), depsArray);
        }

    }


}
