package main.enums;

import java.util.HashMap;

public enum TreeNodeType{
    ROOT("root"),
    PRINT("print", true),
    REM("rem", true),
    GO_TO("goto", true),
    PLUS("sum", "+"),
    MINUS("diff", "-"),
    INTEGER("integer"),
    DECIMAL("decimal"),
    STRING("string"),
    CONCAT(";"),
    SPLIT(":"),
    END("end");

    @Override
    public String toString() {
        return "TreeNodeType{" +
                "type='" + type + '\'' +
                '}';
    }

    private String type;
    private String literal;
    private boolean isUnary;

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

    TreeNodeType(String type) {
        this.type = type;
        this.literal = type;
        this.isUnary = false;
    }

    TreeNodeType(String type, String literal) {
        this.type = type;
        this.literal = literal;
        this.isUnary = false;
    }

    TreeNodeType(String type, boolean isUnary) {
        this.type = type;
        this.isUnary = isUnary;
        this.literal = type;
    }

    public String getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    public boolean isUnary() {
        return isUnary;
    }
}
