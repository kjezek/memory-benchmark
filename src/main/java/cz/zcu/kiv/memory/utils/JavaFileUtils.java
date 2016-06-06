package cz.zcu.kiv.memory.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class JavaFileUtils {

    /**
     * This method tries to analyse content of the input file and collect its byte-code class
     * packages. The input can be:
     * 1. a JAR file itself
     * 2. one exploded JAR file
     * 3. a directory with JAR files or exploded JAR files as in 1. and 2.
     *
     * @param input input
     * @return a list of files
     */
    public static File[] listBytecodeFiles(File input) throws IOException {
        File[] r = listBytecodeFilesR(input);

//        if (r.length == 0) {
//            throw new IOException(input + " contains no .jar or .class files");
//        }

        return r;
    }

    /**
     * This method tries to analyse content of the input file and collect its byte-code class
     * packages. The input can be:
     * 1. a JAR file itself
     * 2. one exploded JAR file
     * 3. a directory with JAR files or exploded JAR files as in 1. and 2.
     *
     * @param input input
     * @return a list of files
     */
    private static File[] listBytecodeFilesR(File input) throws IOException {
        File[] r = new File[0];

        // we have a JAR file
        if (input.isFile() && input.getName().endsWith(".jar")) {
            r = new File[]{input};
        } else if (input.isDirectory() && listClassFiles(input).length > 0 && !explodedSubdirectories(input)) {
            r = new File[]{input};
        } else if (input.isDirectory()) {

            Collection<File> files = new HashSet<>();
            for (File file : input.listFiles()) {
                files.addAll(Arrays.asList(listBytecodeFilesR(file)));
            }

            r = files.toArray(new File[files.size()]);

        }

        return r;
    }

    /**
     * This method returns true in the input directory contains subdirectories with exploded JAR files.
     * Caution: there is no exact way of determining exploded directory, so we use a heuristic that
     * marks a directory as containing exploded subdirectories when:
     * (1) any subdirectories  contain a META-INF dir,
     * (2) or any subdirectories  have a ".jar" extension,
     * (3) or the directory contains other JAR files
     * @param input dir
     * @return true if one of the above holds
     */
    private static boolean explodedSubdirectories(final File input) {
        boolean containsJARs = !FileUtils.listFiles(input, new String[] {"jar"}, false).isEmpty();
        boolean metaSubdirs = false;
        boolean jarExts = false;

        File[] subdirs = input.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        for (File subdir : subdirs) {
            jarExts |= subdir.getName().endsWith(".jar");

            File[] metaSubdirFiles = subdir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return dir.isDirectory() && name.equals("META-INF");
                }
            });

            metaSubdirs |= metaSubdirFiles.length > 0;
        }

        return containsJARs || metaSubdirs || jarExts;
    }

    /**
     * List all .class files.
     * @param input input directory.
     * @return class files.
     */
    public static File[] listClassFiles(File input) {
        Collection<File> files = FileUtils.listFiles(input, new String[]{"class"}, true);
        return files.toArray(new File[files.size()]);
    }
}
