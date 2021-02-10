package intepreter.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum TreeNodeType{
    ROOT("root"),
    REM("rem", false),
    INTEGER("integer", false, true),
    DECIMAL("decimal", false, true),
    STRING("string",  false, true),
    VAR("var", true, true),
    INCREMENT("increment", "+="),
    IF("ifCase","if", true),
    ELSE("elseCase","else", false),
    THEN("thenCase","then", true),
    NEXT("next", true),
    ASSIGN("assign", "="),
    CONCAT("merge", ";"),
    OVER("over", ">"),
    LOWER("lower", "<"),
    PLUS("sum", "+"),
    SPLIT(":"),
    FOR("forLoop", "for", true),
    IN("in"),
    RANGE("range", true),
    PRINT("print", true),
    GO_TO("goTo", "goto", false),
    END("end", false);

    @Override
    public String toString() {
        return "TreeNodeType{" +
                "type='" + type + '\'' +
                '}';
    }

    TreeNodeType(String type, String literal, boolean isUnary) {
        this.type = type;
        this.literal = literal;
        this.isUnary = isUnary;
    }

    @JsonValue
    private String type;
    private String literal;
    private boolean isUnary = false;
    private boolean isType = false;

    private static final HashMap<String,TreeNodeType> map;
    static {
        map = new HashMap<String,TreeNodeType>();
        for (TreeNodeType v : TreeNodeType.values()) {
            map.put(v.getLiteral(), v);
        }
    }

    private static final HashMap<String,TreeNodeType> actual;
    static {
        actual = new HashMap<String,TreeNodeType>();
        for (TreeNodeType v : TreeNodeType.values()) {
            map.put(v.getType(), v);
        }
    }

    public static TreeNodeType findByKey(String token) {
        return map.get(token);
    }
    public static TreeNodeType findByType(String token) {
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
