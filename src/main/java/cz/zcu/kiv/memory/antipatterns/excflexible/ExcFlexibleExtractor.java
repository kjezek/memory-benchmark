package cz.zcu.kiv.memory.antipatterns.excflexible;

import com.github.javaparser.ast.visitor.VoidVisitor;
import cz.zcu.kiv.memory.antipatterns.AbstractExtractor;
import cz.zcu.kiv.memory.antipatterns.ResultConsumer;
import cz.zcu.kiv.memory.antipatterns.domain.AstField;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ExcFlexibleExtractor extends AbstractExtractor<ExcFlexibleExtractor.ExcFlexibleAntipattern>  {

    private Map<String, ExcFlexibleVisitor> data = new HashMap<>();

    private Set<String> enums = new HashSet<>();

    public ExcFlexibleExtractor(ResultConsumer<ExcFlexibleExtractor.ExcFlexibleAntipattern> consumer) {
        super(consumer);
    }

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
                    ExcFlexibleAntipattern r = new ExcFlexibleAntipattern(entry.getKey(), field);
                    getConsumer().consume(r);
                }
            }
        }
    }

    public static class ExcFlexibleAntipattern {

        private String cu;
        private AstField astField;

        public ExcFlexibleAntipattern(String cu, AstField astField) {
            this.cu = cu;
            this.astField = astField;
        }

        public String getCu() {
            return cu;
        }

        public AstField getAstField() {
            return astField;
        }

        @Override
        public String toString() {
            return cu + " -> " + astField;
        }
    }
}
