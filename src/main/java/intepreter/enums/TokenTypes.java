package intepreter.enums;

import java.util.*;

public enum TokenTypes {
    LINE_END("\n"),
    SKIP("space"),
    STRING("string", "\'||\""),
    INTEGER("integer"),
    DOUBLE("decimal"),
    ASSIGN("=", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, STRING}), 1 ),
    RANGE("range", Arrays.asList( new TokenTypes[]{INTEGER, DOUBLE}), 1),
    VAR("var", Arrays.asList(new TokenTypes[]{ASSIGN, RANGE}),  1),
    FOR("for", Arrays.asList(new TokenTypes[]{VAR}), 1),
    IN("in",  Arrays.asList( new TokenTypes[]{RANGE}),1),
    COMMENT("rem", ".*\n"),
    PRINT("print", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, STRING}), 1),
    GOTO("goto", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE}), 1),
    END("end", new ArrayList<>(), 0),
    PLUS("+", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE}), 2),
    IS(":"),
    DOT_COME(";", Arrays.asList(new TokenTypes[]{STRING}), 2),
    UNKNOWN("unknown"),
    ;

    private String token;
    private String id;
    private List<TokenTypes> isAllowed = new LinkedList();
    private int after = 0;

    public String getId() {
        return id;
    }

    TokenTypes(String token, String id) {
        this.token = token;
        this.id = id;
    }

    public int getAfter() {
        return after;
    }

    TokenTypes(String token, List<TokenTypes> isAllowed, int after) {
        this.token = token;
        this.isAllowed = isAllowed;
        this.after = after;
        this.id = token;
    }

    @Override
    public String toString() {
        return token;
    }

    TokenTypes(String token) {
        this.token = token;
        this.id = token;
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
