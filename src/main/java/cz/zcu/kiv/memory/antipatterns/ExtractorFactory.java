package cz.zcu.kiv.memory.antipatterns;

import cz.zcu.kiv.memory.antipatterns.excflexible.ExcFlexibleExtractor;

import java.util.zip.ZipFile;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public interface ExtractorFactory {

    ExtractorFactory EXC_FLEXIBLE = new ExtractorFactory() {
        @Override
        public Extractor create(ZipFile zipFile, ResultConsumer consumer) {
            return new ExcFlexibleExtractor(zipFile, consumer);
        }
    };

    Extractor create(ZipFile zipFile, ResultConsumer consumer);
}
