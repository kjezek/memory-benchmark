package cz.zcu.kiv.memory;

import com.verifa.jacc.ccu.ApiCheckersFactory;
import com.verifa.jacc.ccu.ApiCheckersSetting;
import com.verifa.jacc.ccu.ApiInterCompatibilityChecker;
import com.verifa.jacc.ccu.ApiInterCompatibilityResult;
import cz.zcu.kiv.memory.utils.JavaFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class TestOne {


    public static void main(String[] args) throws IOException {

        String project = args[0];

        String appDir = "data/" + project + "/app";
        String libDir = "data/"  + project + "/libs";
        String extraDir = null;

        File[] appFiles = JavaFileUtils.listBytecodeFiles(new File(appDir));;
        File[] libFiles = JavaFileUtils.listBytecodeFiles(new File(libDir));;

        if (extraDir != null) {
            Set<File> libFilesSet = new HashSet<>();
            libFilesSet.addAll(Arrays.asList(libFiles));
            File[] extraFiles = JavaFileUtils.listBytecodeFiles(new File(extraDir));
            libFilesSet.addAll(Arrays.asList(extraFiles));
            libFiles = libFilesSet.toArray(new File[0]);
        }

        ApiCheckersSetting settings = new ApiCheckersSetting.Builder().allCmp().allScs().build();
        ApiInterCompatibilityChecker<File> checker = ApiCheckersFactory.getApiInterCompatibilityChecker(settings);

        ApiInterCompatibilityResult res = checker.checkInterCompatibility(appFiles, libFiles);
        System.out.println(res);
    }
}
