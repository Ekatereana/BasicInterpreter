package intepreter;


import intepreter.enums.TokenTypes;


public class Token {
    private TokenTypes lexeme;
    private String value;
    private boolean isSkipped;

    public Token(TokenTypes lexeme) {
        this.lexeme = lexeme;
        this.isSkipped = false;
    }

    @Override
    public String toString() {
       return value.startsWith("\"")
              || lexeme.equals(TokenTypes.DOUBLE)
              || lexeme.equals(TokenTypes.INTEGER)
                ? "{" + lexeme + ": " + value + "}"
                : "{" + lexeme + ": \'" + value + "\'}";
    }

    public Token(TokenTypes lexeme, boolean isSkipped) {
        this.lexeme = lexeme;
        this.value = lexeme.getToken();
        this.isSkipped = isSkipped;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Token(TokenTypes lexeme, String value) {
        this.lexeme = lexeme;
        this.value = value;
        this.isSkipped = false;
    }

    public TokenTypes getLexeme() {
        return lexeme;
    }

    public String getValue() {
        return value;
    }

    public boolean isSkipped() {
        return isSkipped;
    }
}
