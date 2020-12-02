package main;

import main.lexer.Lexer;
import main.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Lexer l = new Lexer();
        try {
            List<ArrayList<Token>> tokens = l.iterateFile("test.txt");
            tokens.forEach(row -> {
                row
                        .forEach(token -> System.out.print(token));
                System.out.println();
            });
            Parser parser = new Parser();
            parser.parse("test.txt", tokens);

        } catch ( IOException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
