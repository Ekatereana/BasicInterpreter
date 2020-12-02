package main.parser;

import main.STreeNode;
import main.Token;
import main.enums.TokenTypes;
import main.enums.TreeNodeType;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Parser {
    private HashMap<Predicate<TokenTypes>, Supplier<STreeNode>> keyActions;
    private HashMap<Predicate<TokenTypes>, Method> keyMethods;
    private HashMap<Integer, Function<String, String>> keyErrors;
    private final String ErrorStart = "Exception in thread \"main\" stacker.lang.";
    private final String AT_POSITION = " at position: ";


    public Parser() throws NoSuchMethodException {
        keyActions = new HashMap<>();
        keyMethods = new HashMap<>();
//        keyMethods.put(x -> !TreeNodeType.findByKey(x.getToken()).isUnary(), Parser.class.getDeclaredMethod("parseBinary"));
        keyErrors = new HashMap<>();
        keyActions.put(x -> x.equals(TokenTypes.COMMENT), () -> new STreeNode<String>(TreeNodeType.REM));
        keyActions.put(x -> TreeNodeType.findByKey(x.getToken()).isUnary(), () -> new STreeNode());
        keyActions.put(x -> TreeNodeType.findByKey(x.getToken()).equals(TreeNodeType.CONCAT) ||
                        TreeNodeType.findByKey(x.getToken()).equals(TreeNodeType.PLUS),
                () -> new STreeNode<STreeNode<String>>());
        keyErrors.put(0, x -> "LineStartFormatException" + AT_POSITION + x);
        keyErrors.put(1, x -> "UnknownTokenFormatException" + AT_POSITION + x);
        keyErrors.put(2, x -> "Unexpected end of the line. \nIncorrect number of args provided" + AT_POSITION + x);

    }


    public String parse(String path, List<ArrayList<Token>> tokens) {
        STreeNode root = new STreeNode<STreeNode>(0, TreeNodeType.ROOT);
        Iterator<ArrayList<Token>> atIterator = tokens.iterator();
        ListIterator<Token> tIterator;
        ArrayList<Token> line;
        while (atIterator.hasNext()) {
            line = atIterator.next();
            tIterator = line.listIterator();
            root.addAll(parseLine(tIterator));

        }

        System.out.println(root.toString());
        return "";
    }

    private LinkedList<STreeNode> parseLine(ListIterator<Token> tIterator) {
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
                    children.add(parseExpression(line, offset, buffer, null));
                    buffer.clear();
                } else {
                    buffer.add(currentToken);
                }
                offset++;
                if (!tIterator.hasNext()) {
                    children.add(parseExpression(line, offset, buffer, null));
                }

            }


        } catch (NumberFormatException e) {
            throw new Error(keyErrors.get(0).apply("0"));
        }

        return children;
    }

    private STreeNode parseExpression(int line, int offset, LinkedList<Token> buffer, Token isPart) {
        int position = 0 + offset;
        if (buffer.isEmpty() && isPart != null) {
            throw new Error(keyErrors.get(2).apply(line + "::" + position + " at function " + isPart));
        }

        Token parent = buffer.poll();
        STreeNode result = null;
        if (isNumber(parent) || isString(parent)) {
            if (buffer.isEmpty()) {
                result = new STreeNode(line, TreeNodeType.findByKey(parent.getLexeme().getToken()));
                result.addChild(parent.getValue());
                return result;
            }
            Token operator = buffer.poll();

            if (operator.getLexeme().equals(TokenTypes.PLUS) || operator.getLexeme().equals(TokenTypes.DOT_COME)) {
                result = new STreeNode(line, TreeNodeType.findByKey(operator.getLexeme().getToken()));
                position++;
                buffer.add(0, parent);
                Token current = buffer.poll();
                STreeNode newChild;
                while ((!isFunction(current.getLexeme()) || current.getLexeme().getToken().equals(isPart)) && !buffer.isEmpty()) {
                    if(current.getLexeme().getToken().equals(isPart)){
                        continue;
                    }
                    newChild = new STreeNode(position, TreeNodeType.findByKey(current.getLexeme().getToken()));
                    newChild.addChild(current.getValue());
                    result.addChild(newChild);
                }

                return result;

            } else {
                buffer.add(0, operator);
                if (isPart != null) {
                    result = new STreeNode(line, TreeNodeType.findByKey(parent.getLexeme().getToken()));
                    result.addChild(parent.getValue());
                    result.addChild(parseExpression(line, offset + position, buffer, isPart));
                }
            }

        } else {
            try {
                for (HashMap.Entry<Predicate<TokenTypes>, Supplier<STreeNode>> el : keyActions.entrySet()
                ) {
                    if (el.getKey().test(parent.getLexeme())) {
                        result = el.getValue().get();
                        result.setPosition(line);
                        position++;
                        if (parent.getLexeme().equals(TokenTypes.COMMENT)) {
                            result.addChild(parent.getValue());
                        } else {
                            result.setType(TreeNodeType.findByKey(parent.getLexeme().getToken()));
                            result.addChild(parseExpression(line, offset + position, buffer, parent));
                        }

                    }

                }

            } catch (NullPointerException e) {
                throw new Error(keyErrors.get(1).apply(line + "::" + offset));
            }
        }


        return result;
    }

    private boolean isFunction(TokenTypes lexeme) {
        return !(lexeme.equals(TokenTypes.DOUBLE) || lexeme.equals(TokenTypes.INTEGER) || lexeme.equals(TokenTypes.STRING));
    }

    private boolean isNumber(Token token) {
        return token.getLexeme().equals(TokenTypes.DOUBLE) || token.getLexeme().equals(TokenTypes.INTEGER);
    }

    private boolean isString(Token token) {
        return token.getLexeme().equals(TokenTypes.STRING);
    }


}
