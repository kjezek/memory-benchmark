package cz.zcu.kiv.memory.antipatterns;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public abstract class AbstractExtractor<T> implements Extractor {

    private ResultConsumer<T> consumer;

    public AbstractExtractor(ResultConsumer<T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public final void analyse(ZipFile zip, String programName, String version) throws IOException, ParseException {

        Enumeration<? extends ZipEntry> en = zip.entries();
        while (en.hasMoreElements()) {
            ZipEntry e = en.nextElement();
            String name = e.getName();
            if (name.endsWith(".java")) {
                try (InputStream in = zip.getInputStream(e)) {
                    CompilationUnit cu = JavaParser.parse(in);
                    VoidVisitor<Object> visitor = getVisitor(name);
                    visitor.visit(cu, null);
                }
            }
        }

        end();
    }

    public abstract VoidVisitor<Object> getVisitor(String name);

    public abstract void end();

    public ResultConsumer<T> getConsumer() {
        return consumer;
    }
}
