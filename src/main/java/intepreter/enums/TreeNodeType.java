package intepreter.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum TreeNodeType{
    ROOT("root"),
    REM("rem", true),
    INTEGER("integer", false, true),
    DECIMAL("double", false, true),
    STRING("string",  false, true),
    CONCAT("merge", ";"),
    PLUS("sum", "+"),
    SPLIT(":"),
    PRINT("print", true),
    GO_TO("goto", true),
    END("end",true);

    @Override
    public String toString() {
        return "TreeNodeType{" +
                "type='" + type + '\'' +
                '}';
    }

    private String type;
    private String literal;
    private boolean isUnary;
    private boolean isType;

    private static final HashMap<String,TreeNodeType> map;
    static {
        map = new HashMap<String,TreeNodeType>();
        for (TreeNodeType v : TreeNodeType.values()) {
            map.put(v.getLiteral(), v);
        }
    }
    public static TreeNodeType findByKey(String token) {
        return map.get(token);
    }

    TreeNodeType(String type, boolean isUnary, boolean isType) {
        this.type = type;
        this.literal = type;
        this.isUnary = isUnary;
        this.isType = isType;
    }

    TreeNodeType(String type) {
        this.type = type;
        this.literal = type;
        this.isUnary = false;
        this.isType = false;
    }

    TreeNodeType(String type, boolean isUnary) {
        this.type = type;
        this.isUnary = isUnary;
        this.literal = type;
        this.isType = false;
    }

    TreeNodeType(String type, String literal) {
        this.type = type;
        this.literal = literal;
        this.isUnary = false;
        this.isType = false;
    }

    public boolean isType() {
        return isType;
    }

    public String getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    public boolean isUnary() {
        return isUnary && !isType;
    }
}
