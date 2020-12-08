package intepreter;


import intepreter.lexer.Lexer;
import intepreter.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        Lexer l = new Lexer();
        String [] files = args.length > 0 ? args : new String[]{"test.txt", "test2.txt" ,"test3.txt"};
        long startTime = System.nanoTime();


        try {
            for (String el : files) {
                List<ArrayList<Token>> tokens = l.iterateFile(el);
                Parser parser = new Parser();
                parser.parse(el, tokens);
            }
        } catch (IOException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println(duration/1000000);

    }
}
