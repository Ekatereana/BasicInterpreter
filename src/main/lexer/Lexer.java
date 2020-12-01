package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static main.TokenTypes.*;


public class Lexer {
    private HashMap<Predicate<String>, Function<String, Token>> keywords = new HashMap<>();
    private Queue<String> buffer = new LinkedList<>();

    private void init() {
        keywords.put(x -> Pattern.matches(".*\n.*", x), x -> {
                buffer.addAll(Arrays.asList(x.split("\n")));
                return new Token(LINE_END, x);});
        keywords.put(x -> Pattern.matches(" ||\"\"", x), x ->  new Token(SKIP,true));
        keywords.put(x -> Pattern.matches("-?([1-9][0-9]*)*", x), x -> new Token(INTEGER, x));
        keywords.put(x -> Pattern.matches(
                "^-?([1-9][0-9]*)[\\.]\\d*||^-?([1-9][0-9]*)[\\.]\\d+", x), x -> new Token(DOUBLE, x));
        keywords.put(x -> Pattern.matches("\".*\"||\'.*\' ", x), x -> new Token(STRING, x));
        keywords.put(x -> Pattern.matches("rem", x), x-> new Token(COMMENT, x));
        keywords.put(x -> Pattern.matches("print||goto||end||\\+||\\:||\\;", x), x -> new Token(TokenTypes.findByKey(x), x));
        keywords.put(x -> Pattern.matches(
                ".*\\+.+||.+\\+.*||" +
                        ".*:.+||.+:.*||" +
                        ".*;.+||.+;.*", x), x -> {
            buffer.addAll(Arrays.asList(x.split("")));
            return getToken(buffer.poll());
            }
        );
    }

    public Lexer() {
        init();
    }

    public List<ArrayList<Token>> iterateFile(String filename) throws IOException {
        return iterate(Files.readString(Paths.get(filename)));
    }

    public List<ArrayList<Token>> iterate(String str){
        String[] args = str.trim().split("(\r| )");
        ArrayList<ArrayList<Token>> lexemes = new ArrayList<>();
        ArrayList<Token> rowLexemes = new ArrayList<>();
        Token current;
        for (int i = 0; i < args.length; i++) {
            //check \n buffer
            clearBuffer(rowLexemes);
            current = getToken(args[i]);

//            \nprint
            if (current.getLexeme().equals(COMMENT)){
                StringBuilder rem = new StringBuilder();
                while(!Pattern.matches(".*\n.*", args[i])){
                    rem.append(args[i] + " ");
                    i++;
                }
                current.setValue(rem.toString());
                i--;

            }

            if (!current.getLexeme().equals(SKIP) && !current.getLexeme().equals(LINE_END)) {
                rowLexemes.add(current);
            }
            if(current.getLexeme().equals(LINE_END)){
                lexemes.add(rowLexemes);
                rowLexemes = new ArrayList<>();
            }

        }
        clearBuffer(rowLexemes);
        lexemes.add(rowLexemes);
        return lexemes;
    }

    private void clearBuffer( List<Token> row) {
        Token current;
        buffer.removeAll(Arrays.asList(""));
        while (!buffer.isEmpty()){
            current = getToken(buffer.poll());
            if (!current.isSkipped()){
                row.add(current);
            }
        }
    }

    public Token getToken(String s){
       Token result = null;
        for (HashMap.Entry<Predicate<String>, Function<String, Token>> el: keywords.entrySet()) {
            if (el.getKey().test(s)) {
                result = el.getValue().apply(s);
                break;
            }

        }
        result = result == null ? new Token(UNKNOWN, s): result;
//        System.out.println(result.getLexeme());
        return result;
    }

}
