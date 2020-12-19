package intepreter.parser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import intepreter.STreeNode;
import intepreter.Token;
import intepreter.enums.TokenTypes;
import intepreter.enums.TreeNodeType;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class Parser {
    private HashMap<Predicate<TokenTypes>, Method> keyActions;
    private HashMap<Integer, Function<String, String>> keyErrors;
    private final String ErrorStart = "Exception in thread \"main\" stacker.lang.";
    private final String AT_POSITION = " at position: ";
    private final List<String> EXPECTED = Arrays.asList(new String[]{"\nexpected types: ", " but resolved as "});

    public Parser() throws NoSuchMethodException {
        keyActions = new HashMap<>();
        keyErrors = new HashMap<>();
        keyActions.put(x -> x.equals(TokenTypes.COMMENT) || x.equals(TokenTypes.END),
                Parser.class.getDeclaredMethod("parseNoEntry", int.class, int.class, LinkedList.class, Token.class));

        keyActions.put(x -> TreeNodeType.findByKey(x.getToken()).isType(),
                Parser.class.getDeclaredMethod("parseArgument", int.class, int.class, LinkedList.class, Token.class));
        keyActions.put(x -> TreeNodeType.findByKey(x.getToken()).isUnary(),
                Parser.class.getDeclaredMethod("parseLexeme", int.class, int.class, LinkedList.class, Token.class));
        keyActions.put(x -> !TreeNodeType.findByKey(x.getToken()).isUnary()
                        && !TreeNodeType.findByKey(x.getToken()).isType() && !x.equals(TokenTypes.COMMENT),
                Parser.class.getDeclaredMethod("parseOperator", int.class, int.class, LinkedList.class, Token.class));
        keyErrors.put(0, x -> "LineStartFormatException" + AT_POSITION + x);
        keyErrors.put(1, x -> "UnknownTokenFormatException" + AT_POSITION + x);
        keyErrors.put(2, x -> "Unexpected end of the line. \nIncorrect number of args provided" + AT_POSITION + x);
        keyErrors.put(3, x -> "Unexpected end of the line. \nIncorrect type of args provided" + AT_POSITION + x);

    }


    public String parse(String path, List<ArrayList<Token>> tokens) throws IOException {
        STreeNode root = new STreeNode<STreeNode>(0, TreeNodeType.ROOT);
        Iterator<ArrayList<Token>> atIterator = tokens.iterator();
        ListIterator<Token> tIterator;
        ArrayList<Token> line;
        while (atIterator.hasNext()) {
            line = atIterator.next();
            tIterator = line.listIterator();
            try {
                root.addAll(parseLine(tIterator));
            } catch (Error e) {
                throw new Error("\nStacker exception in filepath :: " + path + " " + e.getMessage());
            }


        }
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        String[] spited = path.split("/");
        String filename = "output/" + spited[spited.length - 1].split("\\.")[0] + ".json";
        mapper.writerWithDefaultPrettyPrinter().writeValue(Paths.get(filename).toFile(), root);

        return filename;
    }


    private LinkedList<STreeNode> parseLine(ListIterator<Token> tIterator) throws Error {
        LinkedList<STreeNode> children = new LinkedList<>();
        LinkedList<Token> buffer = new LinkedList<>();
        int line;
        int offset = 0;
        Token currentToken;
        try {
            line = Integer.parseInt(tIterator.next().getValue());
            while (tIterator.hasNext()) {
                currentToken = tIterator.next();
                if (currentToken.getLexeme().equals(TokenTypes.IS)) {
                    children.add(parseExpression(line, offset, buffer, null, currentToken.getLexeme().getAfter()));
                    buffer.clear();
                } else {
                    buffer.add(currentToken);
                }
                offset++;
                if (!tIterator.hasNext()) {
                    children.add(parseExpression(line, offset, buffer, null, currentToken.getLexeme().getAfter()));
                }

            }

        } catch (NumberFormatException e) {
            throw new Error(keyErrors.get(0).apply("0"));
        }

        return children;
    }

    private STreeNode parseExpression(int line, int offset, LinkedList<Token> buffer, Token isPart, int count)
            throws Error {
        int position = offset;
        STreeNode result = null;
        try {
            isWaitingFor(isPart, buffer, line, position, count);
            Token parent = buffer.poll();
            for (HashMap.Entry<Predicate<TokenTypes>, Method> el : keyActions.entrySet()
            ) {
                if (el.getKey().test(parent.getLexeme())) {
                    result = (STreeNode) el.getValue().invoke(new Parser(), line, offset, buffer, parent);
                    position++;
                    return result;
                }
            }

        } catch (NullPointerException | IllegalAccessException | NoSuchMethodException e) {
            throw new Error(keyErrors.get(1).apply(line + "::" + offset));
        } catch (InvocationTargetException e) {
            throw new Error(e.getCause().getMessage());
        }

        return result;
    }


    private STreeNode parseLexeme(int position, int offset, LinkedList<Token> buffer, Token parent) throws Error {
        STreeNode<STreeNode> result = new STreeNode<>(position, TreeNodeType.findByKey(parent.getLexeme().getToken()));
        result.addChild(parseExpression(position, ++offset, buffer, parent, parent.getLexeme().getAfter()));
        return result;
    }

    private void isWaitingFor(Token parent, List<Token> actual, int line, int position, int count) {
        if (parent == null) {
            return;
        }
        try {
            List<TokenTypes> isAllowed = parent.getLexeme().getIsAllowed();
            for (int i = 0; i < count; i++) {
                if (!isAllowed.contains(actual.get(i).getLexeme())) {
                    String errors = EXPECTED.get(0) + isAllowed.toString() + EXPECTED.get(1) + actual.get(i);
                    throw new Error(keyErrors.get(3).apply(line + "::" + position + " at function " + parent + errors));
                }
            }

        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new Error(keyErrors.get(2).apply(line + "::" + position + " at function " + parent));
        }
    }


    private STreeNode parseNoEntry(int position, int offset, LinkedList<Token> buffer, Token parent) throws Error {
        STreeNode<String> result = new STreeNode<>(position, TreeNodeType.findByKey(parent.getLexeme().getToken()));
        result.addChild(parent.getValue());
        if (!buffer.isEmpty()) throw new Error(keyErrors.get(1).apply(position + "::" + offset));
        return result;
    }

    private STreeNode parseOperator(int position, int offset, LinkedList<Token> buffer, Token parent) throws Error {
        STreeNode<STreeNode> result = new STreeNode<>(position, TreeNodeType.findByKey(parent.getLexeme().getToken()));
        List<TokenTypes> allowed = parent.getLexeme().getIsAllowed();
        Token actual;
        while (!buffer.isEmpty() && (allowed.contains(buffer.peek().getLexeme()) || buffer.peek().equals(parent))) {
            actual = buffer.poll();
            if (actual.equals(parent)) {
                isWaitingFor(parent, buffer, position, ++offset, parent.getLexeme().getAfter());
                actual = buffer.poll();
                result.addChild(parseArgument(position, offset, buffer, actual));
            } else {
                result.addChild(parseArgument(position, ++offset, buffer, actual));
            }


        }
        if (result.getChildren().size() < parent.getLexeme().getAfter()) {
            throw new Error(keyErrors.get(2).apply(position + "::" + offset + " at function " + parent));
        }
        return result;
    }

    private STreeNode parseArgument(int position, int offset, LinkedList<Token> buffer, Token parent) throws Error {
        STreeNode result;
        if (!buffer.isEmpty() && isOperator(buffer.peek().getLexeme())) {
            Token newCore = buffer.pollFirst();
            buffer.add(0, parent);
            result = parseOperator(position, offset++, buffer, newCore);
        } else {
            result = new STreeNode<>(position, TreeNodeType.findByKey(parent.getLexeme().getToken()));
            result.addChild(parent.getValue());
        }

        return result;
    }


    private boolean isOperator(TokenTypes lexeme) {
        return (lexeme.equals(TokenTypes.PLUS) || lexeme.equals(TokenTypes.DOT_COME));
    }


}
