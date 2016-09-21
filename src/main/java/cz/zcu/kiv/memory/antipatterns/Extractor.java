package cz.zcu.kiv.memory.antipatterns;

import com.github.javaparser.ParseException;

import java.io.IOException;

/**
 *
 */
public interface Extractor {

	void analyse() throws IOException, ParseException;

}
