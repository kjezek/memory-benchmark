package cz.zcu.kiv.memory.antipatterns;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public interface ResultConsumer<T> {

    void consume(T result);
}
