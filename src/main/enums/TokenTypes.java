package main;

import java.util.HashMap;
import java.util.Map;

public enum TokenTypes {
    LINE_END("\n"),
    SKIP("space"),
    STRING("string"),
    INTEGER("integer"),
    DOUBLE("double"),
    COMMENT("rem"),
    PRINT("print"),
    GOTO("goto"),
    END("end"),
    PLUS("+"),
    IS(":"),
    DOT_COME(";"),
    UNKNOWN("unknown"),
    ;

    private String token;

    @Override
    public String toString() {
        return token;
    }

    TokenTypes(String token) {
        this.token = token;
    }

    public String getToken() {
        return token.toString();
    }

    private static final HashMap<String,TokenTypes> map;
    static {
        map = new HashMap<String,TokenTypes>();
        for (TokenTypes v : TokenTypes.values()) {
            map.put(v.getToken(), v);
        }
    }
    public static TokenTypes findByKey(String token) {
        return map.get(token);
    }

}
