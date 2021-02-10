package intepreter.enums;

import java.util.*;

public enum TokenTypes {
    LINE_END("\n"),
    SKIP("space"),
    STRING("string", "\'||\""),
    INTEGER("integer"),
    DOUBLE("decimal"),
    VAR("var"),
    ASSIGN("=", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, STRING, VAR}), 1),
    ARG_START("(", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, STRING}), 1),
    IS(":"),
    IF("if", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, VAR, STRING}), 1),
    LOWER("<", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, VAR}), 2),
    OVER(">", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, VAR}), 2),
    INCREMENT("+=", Arrays.asList(new TokenTypes[]{VAR, INTEGER, DOUBLE}), 2),
    NEXT("next", Arrays.asList(new TokenTypes[]{VAR}), 1),
    ARG_END(")", Arrays.asList(new TokenTypes[]{IS}), 1),
    RANGE("range", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE}), 1),

    FOR("for", Arrays.asList(new TokenTypes[]{VAR}), 1),
    IN("in", Arrays.asList(new TokenTypes[]{RANGE, VAR}), 2),
    COMMENT("rem", ".*\n"),
    PRINT("print", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, STRING, VAR}), 1),
    GOTO("goto", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE}), 1),
    END("end", new ArrayList<>(), 0),
    PLUS("+", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, VAR}), 2),
    DOT_COME(";", Arrays.asList(new TokenTypes[]{STRING, VAR}), 2),
    THEN("then", Arrays.asList(new TokenTypes[]{INTEGER, DOUBLE, VAR, STRING, PRINT, GOTO, END, FOR, COMMENT}), 1),
    ELSE("else", Arrays.asList(new TokenTypes[]{DOT_COME}), 0),
    UNKNOWN("unknown"),
    ;

    private String token;
    private String id;
    private List<TokenTypes> isAllowed = new LinkedList();
    private int after = 0;
    private Integer maxArg = Integer.MAX_VALUE;

    TokenTypes(String token, List<TokenTypes> isAllowed, int after, Integer maxArg) {
        this.token = token;
        this.isAllowed = isAllowed;
        this.after = after;
        this.maxArg = maxArg;
    }

    public Integer getMaxArg() {
        return maxArg;
    }

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
