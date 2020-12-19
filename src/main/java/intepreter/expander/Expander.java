package intepreter.expander;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import intepreter.STreeNode;

import intepreter.enums.TreeNodeType;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public class Expander {
    private final String IMPORTS = "import java.util.concurrent.FutureTask;" +
            "\nimport intepreter.abstracts.StackerMethod;" +
            "\nimport java.util.*;";
    private final String CLASS_START = "public class ";
    private final String INIT = "{\n public static void main(String[] args)" +
            " {\n" + "StackerMethod st = new StackerMethod();";
    private HashMap<Predicate<TreeNodeType>, Method> methods;
    private final String STACKER = "st.";
    private final String ADD = "addLine(";
    private final String EXEC = "run(";
    private final String SEPARATOR = "(";
    private final String END_FUNCTION = ");";
    private final String END_ARGUMENT = ")";
    private final String END = "\n}";

    public Expander() throws NoSuchMethodException {
        init();
    }

    public void init() throws NoSuchMethodException {
        methods = new HashMap<>();
        methods.put(x -> x.equals(TreeNodeType.REM), Expander.class.getDeclaredMethod("ignore", STreeNode.class, boolean.class));
        methods.put(x -> !x.isType() && !x.equals(TreeNodeType.REM), Expander.class.getDeclaredMethod("expandLine", STreeNode.class, boolean.class));
        methods.put(x -> x.isType(), Expander.class.getDeclaredMethod("expandLexeme", STreeNode.class, boolean.class));
    }

    public void expand(String parsedFile)
            throws IOException,
            ClassNotFoundException,
            InvocationTargetException,
            IllegalAccessException,
            NoSuchMethodException {

        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructParametricType(STreeNode.class, String.class);
        STreeNode<STreeNode> tree = mapper.readValue(new File(parsedFile), type);
        StringBuilder data = new StringBuilder();
        data.append(IMPORTS);
        String className = Paths.get(parsedFile).getFileName().toString()
                .replaceFirst("[.][^.]+$", "");
        data.append(CLASS_START + className + INIT);
        HashMap<Integer, List<String>> code = new HashMap<>();
        Iterator<STreeNode> iterator = tree.getChildren().listIterator();
        STreeNode<STreeNode> node;
        while (iterator.hasNext()){
            node = iterator.next();
            if (!code.containsKey(getLine( node))) {
                code.put(getLine( node), new ArrayList<>());
            }
            code.get(getLine( node))
                    .add(expandLine( node, !iterator.hasNext()));
        }
        openCode(code, data);
        data.append(END);
        data.append(END);
        long startCompile = System.nanoTime();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Iterable<? extends JavaFileObject> fileObjects = getJavaFileFromString(data.toString(), className);
        compiler.getTask(null, null, null, null, null, fileObjects).
                call();
        long endCompile = System.nanoTime();
        System.out.println("Compilation time: " + (int)((endCompile - startCompile)/1000000));
        Path temp = Paths.get(System.getProperty("user.dir"));

        startCompile = System.nanoTime();
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URLClassLoader urlClassLoader = new URLClassLoader(
                new URL[]{temp.toUri().toURL()},
                classLoader);
        Class javaDemoClass = urlClassLoader.loadClass(className);
        Method method = javaDemoClass.getMethod("main", String[].class);
        method.invoke(null, (Object) new String[0]);
        endCompile = System.nanoTime();
        System.out.println("Method invocation time: " + (int)((endCompile - startCompile)/1000000));


    }


    private void openCode(HashMap<Integer, List<String>> code, StringBuilder data) {
        for (HashMap.Entry<Integer, List<String>> line : code.entrySet()) {
            data.append(STACKER + ADD + line.getKey() + ", Arrays.asList( new Thread[]{");
            line.getValue().remove(null);
            Iterator it = line.getValue().iterator();
            while (it.hasNext()){
                data.append(it.next());
            }
            data.append("}));");
        }
        data.append( STACKER + EXEC + END_FUNCTION);
    }

    private Integer getLine(STreeNode<STreeNode> node) {
        return node.getPosition();
    }

    private String expandLine(STreeNode<STreeNode> node, boolean isArgument) {
        if(node.getType().equals(TreeNodeType.REM)){
            ignore(node, isArgument);
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("\n" + STACKER + node.getType().getType() + SEPARATOR);
        ListIterator<STreeNode> iterator = node.getChildren().listIterator();
        STreeNode child;
        while (iterator.hasNext()) {
            child = iterator.next();
            for (HashMap.Entry<Predicate<TreeNodeType>, Method> math : methods.entrySet()) {
                if (math.getKey().test(child.getType())) {
                    try {
                        builder.append(math.getValue().invoke(new Expander(), child, !iterator.hasNext()));
                        break;
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        builder.append(isArgument ? END_ARGUMENT  : END_ARGUMENT + ",");
        return builder.toString();
    }

    private void ignore(STreeNode<STreeNode> node, boolean isArgument) {
    }

    private String expandLexeme(STreeNode<String> node, boolean isLast) {
        StringBuilder builder = new StringBuilder();
        builder.append(STACKER + node.getType().getLiteral() + SEPARATOR);
        node.getChildren().forEach(el -> builder.append(el));
        builder.append(isLast ? END_ARGUMENT : END_ARGUMENT + ",");
        return builder.toString().replace("\'", "\"");
    }

    private Iterable<JavaFileFromString> getJavaFileFromString(String code, String fileName) {
        final JavaFileFromString jsfs;
        jsfs = new JavaFileFromString(fileName, code);
        return () -> new Iterator<>() {
            boolean isNext = true;

            public boolean hasNext() {
                return isNext;
            }

            public JavaFileFromString next() {
                if (!isNext)
                    throw new NoSuchElementException();
                isNext = false;
                return jsfs;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    private class JavaFileFromString extends SimpleJavaFileObject {
        final String code;

        JavaFileFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
