package cz.zcu.kiv.memory.antipatterns;

import com.github.javaparser.ParseException;
import cz.zcu.kiv.memory.antipatterns.excflexible.ExcFlexibleExtractor;

import java.io.IOException;
import java.util.zip.ZipFile;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public interface Extractor<T> {

    Extractor<ExcFlexibleExtractor.ExcFlexibleAntipattern> EXC_FLEXIBLE = (consumer, zipFile, deps) -> new ExcFlexibleExtractor(consumer, zipFile, deps).analyse();

    void analyse(ResultConsumer<T> consumer, ZipFile zipFile, ZipFile[] deps) throws IOException, ParseException;
}
