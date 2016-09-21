package cz.zcu.kiv.memory.antipatterns;

import cz.zcu.kiv.memory.antipatterns.excflexible.ExcFlexibleExtractor;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public interface ExtractorFactory {

    ExtractorFactory EXC_FLEXIBLE = new ExtractorFactory() {
        @Override
        public Extractor create(ResultConsumer consumer) {
            return new ExcFlexibleExtractor(consumer);
        }
    };

    Extractor create(ResultConsumer consumer);
}
