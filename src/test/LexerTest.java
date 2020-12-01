package test;

import main.Lexer;
import main.Token;
import main.TokenTypes;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LexerTest {
    private Lexer lexer = new Lexer();
    @Test
    public void testPrint(){
        Token current = lexer.getToken("print");
        Assert.assertEquals(current.getLexeme(), TokenTypes.PRINT);
        Assert.assertEquals(current.getValue(), "print");
    }

    @Test
    public void testGoto(){
        Token current = lexer.getToken("goto");
        Assert.assertEquals(current.getLexeme(), TokenTypes.GOTO);
        Assert.assertEquals(current.getValue(), "goto");
    }

    @Test
    public void testRow(){
        List<ArrayList<Token>> actual = lexer.iterate("print \"a\" \n");
        //only one row
        assertThat(actual.size(), is(1));
        //no \n and 2 Tokens
        assertThat(actual.get(0), hasItems(
                new Token(TokenTypes.PRINT, "print"),
                new Token(TokenTypes.STRING, "\"a\"")) );
    }

    @Test
    public void testMultiplyRow(){
        List<ArrayList<Token>> actual = lexer.iterate("goto 12\n end \n");
        //only one row
        assertThat(actual.size(), is(2));
        //no \n and 2 Tokens
        assertThat(actual.get(0), hasItems(
                new Token(TokenTypes.GOTO, "goto"),
                new Token(TokenTypes.INTEGER, "12")));
        //no \n and 3 tokens
        assertThat(actual.get(1), hasItems(
                new Token(TokenTypes.END, "end")));
    }
}
