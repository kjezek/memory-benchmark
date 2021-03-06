package cz.zcu.kiv.memory.antipatterns;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public abstract class AbstractExtractor<T>  {

    private ResultConsumer<T> consumer;

    private ZipFile zipFile;

    private ZipFile[] deps;

    private static final Map<String, VoidVisitor<Object>> cache = new HashMap<>();

    public AbstractExtractor(ResultConsumer<T> consumer, ZipFile zipFile, ZipFile[] deps) {
        this.consumer = consumer;
        this.zipFile = zipFile;
        this.deps = deps;
    }

    public final void analyse() throws IOException, ParseException {

        Enumeration<? extends ZipEntry> en = zipFile.entries();
        while (en.hasMoreElements()) {
            ZipEntry e = en.nextElement();
            String name = e.getName();
            if (name.endsWith(".java")) {
                try (InputStream in = zipFile.getInputStream(e)) {
                    CompilationUnit cu = JavaParser.parse(in);
                    VoidVisitor<Object> visitor = getVisitor(name);
                    visitor.visit(cu, null);
                }
            }
        }

        for (ZipFile dep : deps) {
            if (!containsCache(dep)) {
                Enumeration<? extends ZipEntry> en2 = dep.entries();
                while (en2.hasMoreElements()) {
                    ZipEntry e = en2.nextElement();
                    String name = e.getName();
                    if (name.endsWith(".java")) {
                        try (InputStream in = dep.getInputStream(e)) {
                            CompilationUnit cu = JavaParser.parse(in);
                            VoidVisitor<Object> visitor = getVisitorDeps(name);
                            visitCache(dep, cu, visitor);  // cache values
                        }
                    }
                }
            }
        }

        end();
    }

    public abstract VoidVisitor<Object> getVisitor(String name);

    public abstract VoidVisitor<Object> getVisitorDeps(String name);


    public abstract void end();

    public ResultConsumer<T> getConsumer() {
        return consumer;
    }

    public ZipFile getZipFile() { return zipFile; }

    private static synchronized void visitCache(ZipFile file, CompilationUnit cu, VoidVisitor<Object> visitor) {
        if (!cache.containsKey(file.getName())) {
            visitor.visit(cu, null);
        }
        cache.put(file.getName(), visitor);
    }

    private static synchronized boolean containsCache(ZipFile file) {
        return cache.containsKey(file.getName());
    }
}
