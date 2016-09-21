package cz.zcu.kiv.memory.antipatterns.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class AstType {

    private String name;
    private List<String> paramTypes = new ArrayList<>();

    public AstType(String name, List<String> paramTypes) {
        this.name = name;
        this.paramTypes = paramTypes;
    }

    public String getName() {
        return name;
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AstType)) return false;

        AstType astType = (AstType) o;

        if (name != null ? !name.equals(astType.name) : astType.name != null) return false;
        return paramTypes != null ? paramTypes.equals(astType.paramTypes) : astType.paramTypes == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (paramTypes != null ? paramTypes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String params = "";
        if (!paramTypes.isEmpty()) {
            params += "<";
            for (String p : paramTypes) {
                params += p + ",";
            }
            params += ">";
        }
        return name + params;
    }
}
