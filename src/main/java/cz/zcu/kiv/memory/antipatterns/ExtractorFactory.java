package cz.zcu.kiv.memory.antipatterns;

import cz.zcu.kiv.memory.antipatterns.excflexible.ExcFlexibleExtractor;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public interface ExtractorFactory<T> {

    ExtractorFactory<ExcFlexibleExtractor.ExcFlexibleAntipattern> EXC_FLEXIBLE = ExcFlexibleExtractor::new;

    Extractor create(ResultConsumer<T> consumer);
}
