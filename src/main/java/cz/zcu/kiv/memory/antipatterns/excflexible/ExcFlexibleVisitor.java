package cz.zcu.kiv.memory.antipatterns.excflexible;

import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cz.zcu.kiv.memory.antipatterns.domain.AstField;
import cz.zcu.kiv.memory.antipatterns.domain.AstType;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class ExcFlexibleVisitor extends VoidVisitorAdapter<Object> {

    /**
     * List of commonly used Java collections, used as field assignment.
     */
    private static final List<String> COLLECTIONS_IMPL = Arrays.asList(
            "HashSet", "TreeSet", "LinkedHashSet",
            "ArrayList", "LinkedList"
    );

    /**
     * List of commonly used Java collections, used as field definitions.
     */
    private static final List<String> COLLECTIONS_DEF = Arrays.asList(
            "Set",  // TODO - need to check implementation - may be EnumSet, which is right
            "HashSet", "TreeSet", "LinkedHashSet",
            "List", "ArrayList", "LinkedList",
            "Collection"
    );


    private Set<AstField> fields = new HashSet<>();
    private Set<AstField> enumSetFields = new HashSet<>();


    /** Revers pointer from parameter types to fields. */
    private Multimap<String, AstField> paramTypes = HashMultimap.create();

    private Set<String> enums;

    /**
     *
     * @param enums enums are set from outside as we want merge them from all classes to one list
     */
    public ExcFlexibleVisitor(Set<String> enums) {
        this.enums = enums;
    }

    public void visit(final EnumDeclaration n, final Object arg) {
        enums.add(n.getName());
    }


    public void visit(final ObjectCreationExpr n, final Object arg) {
//        if (n.getType().getName().contains("List")) {
//            System.out.println(n.getType().getName());
//            System.out.println(n.getTypeArgs());
//        }
    }

    public void visit(final FieldDeclaration n, final Object arg) {
        if (!Modifier.isStatic(n.getModifiers())) {  // ignore static - they hardly cause memory bloat
            if (n.getType() instanceof ReferenceType) {
                ReferenceType t = (ReferenceType) n.getType();
                if (t.getType() instanceof ClassOrInterfaceType) {
                    ClassOrInterfaceType tt = (ClassOrInterfaceType) t.getType();

                    // extract name of field definition - e.g.  List of  List field = new ArrayList
                    String name = tt.getName();
                    if (COLLECTIONS_DEF.contains(name)) {    // check only collection types  TODO - we may ignore a lot of cases e.g. MyList<>
                        List<VariableDeclarator> variables = n.getVariables();
                        if (variables.size() > 0) { // TODO what if we have more than one var?
                            VariableDeclarator varDec = variables.get(0);
                            // TODO resolve also MethodCallExpr
                            if (varDec.getInit() != null && varDec.getInit() instanceof ObjectCreationExpr) {
                                // field may not be initialised
                                ClassOrInterfaceType type = ((ObjectCreationExpr) varDec.getInit()).getType();
                                List<String> args = new ArrayList<>();
                                for (Type typeArg : type.getTypeArgs()) {
                                    args.add(typeArg.toStringWithoutComments());
                                }
                                AstType fieldType = new AstType(name, args);
                                AstType initType = new AstType(type.getName(), args);
                                AstField field = new AstField(initType, name, fieldType);
                                // add field
                                fields.add(field);
                                for (String argType : args) {
                                    paramTypes.put(argType, field);
                                }

                                if (type.getName().equals("EnumSet")) {
                                    enumSetFields.add(field);
                                }
                            }
                        }
                        if (variables.size() > 1) {
                            System.out.println(variables);
                        }
                    }
                }
            }
        }

    }

    public Set<AstField> getFields() {
        return fields;
    }

    public Set<AstField> getEnumSetFields() {
        return enumSetFields;
    }

    public Multimap<String, AstField> getParamTypes() {
        return paramTypes;
    }


}
