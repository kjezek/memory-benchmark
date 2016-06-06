package cz.zcu.kiv.memory;

import com.verifa.jacc.ccu.ApiCheckersFactory;
import com.verifa.jacc.ccu.ApiCheckersSetting;
import com.verifa.jacc.ccu.ApiInterCompatibilityChecker;
import com.verifa.jacc.ccu.ApiInterCompatibilityResult;
import cz.zcu.kiv.memory.utils.JavaFileUtils;
import org.apache.commons.cli.ParseException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
@Measurement(iterations = 30, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 15)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
public class TesterMain {

    private File[] appFiles;
    private File[] libFiles;

    @Param("project")
    private String project;

    private PrintWriter fileWriter = null;

    @Setup(Level.Trial)
    public void beforeTrial() throws Exception {
        String appDir = "data/" + project + "/app";
        String libDir = "data/"  + project + "/libs";
        String extraDir = null;

        appFiles = JavaFileUtils.listBytecodeFiles(new File(appDir));
        libFiles = JavaFileUtils.listBytecodeFiles(new File(libDir));

        if (extraDir != null) {
            Set<File> libFilesSet = new HashSet<>();
            libFilesSet.addAll(Arrays.asList(libFiles));
            File[] extraFiles = JavaFileUtils.listBytecodeFiles(new File(extraDir));
            libFilesSet.addAll(Arrays.asList(extraFiles));
            libFiles = libFilesSet.toArray(new File[0]);
        }

        File csvResultFile = new File(project + "_memory.csv");
        fileWriter = new PrintWriter(csvResultFile);
        appendFile("before", "after");
    }


    @TearDown(Level.Trial)
    public void afterTrial() throws Exception {
        fileWriter.close();
    }

    @Benchmark
    public void test(Blackhole blackhole) {
        double before = measureMemory();  // measure memory before
        ApiInterCompatibilityResult res = evaluate(appFiles, libFiles);
        double after = measureMemory();  // measure memory after
        blackhole.consume(res);

        appendFile(before, after);
    }

    public static void main(final String[] args) throws ParseException, IOException, RunnerException {
        Options opt = new OptionsBuilder()
                .include(TesterMain.class.getSimpleName())
                .param("project", "proprietary", "wct", "jboss")
                .result("time.tex")
                .resultFormat(ResultFormatType.LATEX)
//                .addProfiler(GCProfiler.class)
                .forks(1)
                .build();


        new Runner(opt).run();
    }


    /**
     * Perform GC and then measure memory
     *
     * @return memory in MB
     */
    private static double measureMemory() {

        System.gc(); // garbage
        // wait a while for gc
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0 / 1024.0;
    }

    /**
     * Append line to file
     *
     * @param first  first column
     * @param second second column
     */
    private void appendFile(final String first, final String second) {
        fileWriter.println(first + "," + second);
    }

    /**
     * Append line to file
     *
     * @param first  first column
     * @param second second column
     */
    private void appendFile(final double first, final double second) {
        NumberFormat formatter = new DecimalFormat("#0.000");
        fileWriter.println(formatter.format(first) + "," + formatter.format(second));
    }

    /**
     * Invoke JaCC
     *
     * @param appFiles application files
     * @param libFiles library files
     * @return result
     */
    private static ApiInterCompatibilityResult evaluate(
            final File[] appFiles,
            final File[] libFiles) {

        ApiCheckersSetting settings = new ApiCheckersSetting.Builder().allCmp().allScs().build();
        ApiInterCompatibilityChecker<File> checker = ApiCheckersFactory.getApiInterCompatibilityChecker(settings);

        return checker.checkInterCompatibility(appFiles, libFiles);
    }
}
