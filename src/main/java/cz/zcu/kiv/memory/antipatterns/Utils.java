package cz.zcu.kiv.memory.antipatterns;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class Utils {

    /**
     * Parse dependencies from json
     *
     * @param root
     * @param json json
     * @return deps.
     */
    public static List<ProgramVersion> parseDepsProgramVersion(File root, File json) throws IOException {

        List<ProgramVersion> r = new ArrayList<>();
        JSONObject o = new JSONObject(IOUtils.toString(new FileInputStream(json), Charset.forName("UTF-8")));

        for (Object d : o.getJSONArray("deps")) {
            JSONObject item = (JSONObject) d;
            ProgramVersion v = ProgramVersion.getOrCreate(
                    item.getString("artifactId"),
                    item.getString("version"));
            File file = toSrcFile(root, v);
            r.add(v.withFile(file));
        }

        return r;
    }

    /**
     * Parse project version info from json
     *
     * @param root
     * @param json json file
     * @return project
     * @throws IOException error
     */
    public static ProgramVersion parseVersion(File root, File json) throws IOException {
        JSONObject o = new JSONObject(IOUtils.toString(new FileInputStream(json), Charset.forName("UTF-8")));

        ProgramVersion v = ProgramVersion.getOrCreate(
                o.getString("artifactId"),
                o.getString("version"));

        File file = toSrcFile(root, v);

        return v.withFile(file);
    }

    public static File toSrcFile(File root, ProgramVersion version) {
        final File programFile = new File(root, version.getName());
        return new File(programFile, version.getName() + "-" + version.getVersion() + ".zip");
    }


    /**
     *
     * @return list all directories
     */
    public static File[] listProjects(File root) {

        return root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
    }

    /**
     *
     * @return list all directories
     */
    public static File[] listJsons(File project) {

        return project.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".json");
            }
        });
    }



}
