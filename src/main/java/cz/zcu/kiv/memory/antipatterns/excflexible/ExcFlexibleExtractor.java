package cz.zcu.kiv.memory.antipatterns.excflexible;

import com.github.javaparser.ast.visitor.VoidVisitor;
import cz.zcu.kiv.memory.antipatterns.AbstractExtractor;
import cz.zcu.kiv.memory.antipatterns.Extractor;
import cz.zcu.kiv.memory.antipatterns.ExtractorFactory;
import cz.zcu.kiv.memory.antipatterns.domain.AstField;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ExcFlexibleExtractor extends AbstractExtractor implements ExtractorFactory {

    private Map<String, ExcFlexibleVisitor> data = new HashMap<>();

    private Set<String> enums = new HashSet<>();

    @Override
    public VoidVisitor<Object> getVisitor(String name) {

        ExcFlexibleVisitor visitor = new ExcFlexibleVisitor(enums);
        data.put(name, visitor);

        return visitor;
    }

    @Override
    public void end() {
        // match fields with nemus
        for (String e : enums) {
            for (Map.Entry<String, ExcFlexibleVisitor> entry : data.entrySet()) {
                Collection<AstField> fields = entry.getValue().getParamTypes().get(e);

                for (AstField field : fields) {
                    System.out.println(entry.getKey() + " -> " + field);
                    System.exit(-1);
                }
            }
        }
        // process data here
        System.out.println();
    }

    @Override
    public Extractor create() {
        return new ExcFlexibleExtractor();
    }
}
