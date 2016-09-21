package cz.zcu.kiv.memory.antipatterns;

import com.github.javaparser.ParseException;

import java.io.IOException;
import java.util.zip.ZipFile;

/**
 *
 */
public interface Extractor {

	void analyse(ZipFile zipFile, String programName, String version) throws IOException, ParseException;

}
