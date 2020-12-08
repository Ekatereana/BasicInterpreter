package intepreter.lexer;


import intepreter.Token;
import intepreter.enums.TokenTypes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static intepreter.enums.TokenTypes.*;


public class Lexer {
    private HashMap<Predicate<String>, Function<String, Token>> keywords = new HashMap<>();
    private List<String> keys = Arrays.asList(new String[]{"rem", "print", "goto", "\'", "\""});
    private Queue<String> buffer = new LinkedList<>();

    private void init() {
        keywords.put(x -> Pattern.matches(".*\n.*", x), x -> {
            buffer.addAll(Arrays.asList(x.split("\n")));
            return new Token(LINE_END, x);
        });
        keywords.put(x -> Pattern.matches(" ||\"\"", x), x -> new Token(SKIP, true));
        keywords.put(x -> Pattern.matches("-?([1-9][0-9]*)*", x), x -> new Token(INTEGER, x));
        keywords.put(x -> Pattern.matches(
                "^-?([1-9][0-9]*)[\\.]\\d*||^-?([1-9][0-9]*)[\\.]\\d+||\\.\\d*", x), x -> new Token(DOUBLE, x));
        keywords.put(x -> Pattern.matches("\".*\"||\'.*\'||\".*||.*\"||\'*", x), x -> new Token(STRING, x));
        keywords.put(x -> Pattern.matches("rem", x), x -> new Token(COMMENT, x));
        keywords.put(x -> Pattern.matches("print||goto||end||\\+||\\:||\\;", x), x -> new Token(TokenTypes.findByKey(x), x));
        keywords.put(x -> Pattern.matches(
                ".*\\+.+||.+\\+.*||" +
                        ".*\\:.+||.+\\:.*||" +
                        ".*\\;.+||.+\\;.*", x), x -> {
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

    public List<ArrayList<Token>> iterate(String str) {
        String[] args = str.trim().split("(\r| )");
        ArrayList<ArrayList<Token>> lexemes = new ArrayList<>();
        ArrayList<Token> rowLexemes = new ArrayList<>();
        Token current;
        for (int i = 0; i < args.length; i++) {
            //check \n buffer
            clearBuffer(rowLexemes);
            current = getToken(args[i]);

            if (current.getLexeme().equals(COMMENT) || current.getLexeme().equals(STRING)) {
                LexemeResponse lexeme = getString(args, i);
                current.setValue(lexeme.lexeme);
                i = lexeme.index - 1;

            }

            if (!current.getLexeme().equals(SKIP) && !current.getLexeme().equals(LINE_END)) {
                rowLexemes.add(current);
            }
            if (current.getLexeme().equals(LINE_END) && !rowLexemes.isEmpty()) {
                lexemes.add(rowLexemes);
                rowLexemes = new ArrayList<>();
            }

        }
        clearBuffer(rowLexemes);
        lexemes.add(rowLexemes);
        return lexemes;
    }

    private class LexemeResponse {
        String lexeme;
        int index;

        public LexemeResponse(String lexeme, int index) {
            this.lexeme = lexeme;
            this.index = index;
        }
    }

    private LexemeResponse getString(String[] args, int i) {
        StringBuilder rem = new StringBuilder();
        String delimiter = args[i].startsWith("\'") ? "\'" : "\"";
        while (i < args.length && !Pattern.matches(".*\n.*||.*\".*||.*\'.*", args[i])) {
            rem.append(args[i] + " ");
            i++;
        }
        if (i < args.length && Pattern.matches(".*\"|.*\'", args[i])) {
            rem.append(args[i]);
            i++;
        } else {
            if (i < args.length && Pattern.matches(".*\".+|.*\'.+", args[i])) {
                rem.append(delimiter);
                buffer.add(args[i].replace(delimiter, ""));
            }
        }
        LexemeResponse result = new LexemeResponse(rem.toString(), i);

        return result;

    }


    private void clearBuffer(List<Token> row) {
        Token current;
        buffer.removeAll(Arrays.asList(""));
        List<String> copy;
        while (!buffer.isEmpty()) {
            if (isKey()) {
                copy = List.copyOf(buffer);
                buffer.clear();
                row.add(iterate(String.join("", copy)).get(0).get(0));
            } else {
                current = getToken(buffer.poll());
                if (!current.isSkipped()) {
                    row.add(current);
                }
            }
        }
    }

    private boolean isKey() {
        for (int i = 0; i < keys.size(); i++) {
            if (String.join("", buffer).startsWith(keys.get(i))) {
                return true;
            }
        }

        return false;
    }

    public Token getToken(String s) {
        Token result = null;
        for (HashMap.Entry<Predicate<String>, Function<String, Token>> el : keywords.entrySet()) {
            if (el.getKey().test(s)) {
                result = el.getValue().apply(s);
                break;
            }

        }
        result = result == null ? new Token(UNKNOWN, s) : result;
//        System.out.println(result.getLexeme());
        return result;
    }

}
