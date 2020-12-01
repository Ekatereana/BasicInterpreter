package main;

import java.util.Objects;

public class Token {
    private TokenTypes lexeme;
    private String value;
    private boolean isSkipped;

    public Token(TokenTypes lexeme) {
        this.lexeme = lexeme;
        this.isSkipped = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return isSkipped == token.isSkipped &&
                lexeme == token.lexeme &&
                Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexeme, value, isSkipped);
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
