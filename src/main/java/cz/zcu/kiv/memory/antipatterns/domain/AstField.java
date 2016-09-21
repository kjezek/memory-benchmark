package cz.zcu.kiv.memory.antipatterns.domain;

/**
 * @author Kamil Jezek [kamil.jezek@verifalabs.com]
 */
public class AstField {

    private AstType type;
    private String name;
    /** initial declaration. */
    private AstType init;

    public AstField(AstType type, String name, AstType init) {
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public AstType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public AstType getInit() {
        return init;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AstField)) return false;

        AstField astField = (AstField) o;

        if (type != null ? !type.equals(astField.type) : astField.type != null) return false;
        if (name != null ? !name.equals(astField.name) : astField.name != null) return false;
        if (init != null ? !init.equals(astField.init) : astField.init != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (init != null ? init.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return type + " " + name + " = " + init;
    }
}
