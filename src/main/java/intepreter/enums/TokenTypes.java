package intepreter.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public enum TokenTypes {
    LINE_END("\n"),
    SKIP("space"),
    STRING("string"),
    INTEGER("integer"),
    DOUBLE("double"),
    COMMENT("rem"),
    PRINT("print", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, STRING})),
    GOTO("goto", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE})),
    END("end"),
    PLUS("+", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE})),
    IS(":"),
    DOT_COME(";", Arrays.asList(new TokenTypes[]{STRING})),
    UNKNOWN("unknown"),
    ;

    private String token;
    private List<TokenTypes> isAllowed = new LinkedList();

    @Override
    public String toString() {
        return token;
    }

    TokenTypes(String token) {
        this.token = token;
    }

    TokenTypes(String token, List<TokenTypes> isAllowed) {
        this.token = token;
        this.isAllowed = isAllowed;
    }

    public String getToken() {
        return token.toString();
    }

    private static HashMap<String, TokenTypes> map;

    static {
        map = new HashMap<String, TokenTypes>();
        for (TokenTypes v : TokenTypes.values()) {
            map.put(v.getToken(), v);
        }
    }

    public static TokenTypes findByKey(String token) {
        return map.get(token);
    }

    public List<TokenTypes> getIsAllowed() {
        return isAllowed;
    }
}
