package main;

import java.io.IOException;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Lexer l = new Lexer();
        System.out.println(Pattern.matches("-?([1-9][0-9]*)(\\.([1-9][0-9]*))*", "3"));
        System.out.println(Pattern.matches(".*\\+.+||.+\\+\\.*", "4+"));
        try {
            l.iterateFile("test.txt")
                    .forEach(row -> {
                        row
                                .forEach(token -> System.out.print(token));
                        System.out.println();
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
