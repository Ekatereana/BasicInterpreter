package intepreter;


import intepreter.expander.Expander;
import intepreter.lexer.Lexer;
import intepreter.parser.Parser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        Lexer l = new Lexer();

        String[] files = args.length > 0 ? args : new String[]{"testData/LoopTest.txt"};
//                new String[]{"testData/test.txt", "testData/test2.txt", "testData/test3.txt"};
        long startTime = System.nanoTime();
        String filename;
        long let;
        long end;
        try {
            Parser parser = new Parser();
            Expander expander = new Expander();
            for (String el : files) {
                let = System.nanoTime();
                List<ArrayList<Token>> tokens = l.iterateFile(el);
                filename = parser.parse(el, tokens);
                expander.expand(filename);
                end = System.nanoTime();
                System.out.println("Time for " + el + ": " +  (end - let)/ 1000000);
            }
        } catch (IOException |
                NoSuchMethodException |
                ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println("Total Time:" +  duration / 1000000);

    }
}
