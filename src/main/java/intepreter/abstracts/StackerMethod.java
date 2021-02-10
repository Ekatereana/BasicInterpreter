package intepreter.abstracts;


import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;


public class StackerMethod implements Runnable {
    private static final String FUNCTION = "in function ::";
    private static final String EXPECTED = "expected ::";
    private final String CALLED_AT = " called at ::";
    private HashMap<String, Method> methods;
    private HashMap<String, Object> vars;
    private TreeMap<Integer, List<Thread>> lines;
    private HashMap<Integer, String> keyErrors;
    private HashMap<Thread, LinkedList<Thread>> buffer;
    private LinkedList<Thread> CURRENT_PROCESS;
    private Integer isThrown;
    private int counter = 0;
    private Boolean continueExec = false;

    private class UseGoTo extends Exception {
        int message;

        public UseGoTo(Integer message) {
            this.message = message;
        }
    }

    public StackerMethod() {
        keyErrors = new HashMap<>();
        CURRENT_PROCESS = new LinkedList<>();
        buffer = new HashMap<>();
        keyErrors.put(0, "No such line exception");
        keyErrors.put(1, "Unable call go to from loop body");
        keyErrors.put(2, "Incorrect type of increment var provided");
        keyErrors.put(3, "Unexpected end of the line. Else without if statement provided");
        methods = new HashMap<>();
        vars = new HashMap<>();
        lines = new TreeMap<>();
    }

    public void addLine(int position, List<Thread> methods) {
        lines.put(position, methods);
    }


    private void printAll(Object object) {
        System.out.println(object);
    }


    public Integer integer(int integer) {
        return Integer.valueOf(integer);
    }

    public Double decimal(double d) {
        return Double.valueOf(d);
    }

    public String merge(int line, String a, String b) {
        return Pattern.matches("\\'.*||\".*", a) ? a + b : vars.get(a) + b;
    }


    public Thread end(int line) {
        return new Thread("end-" + line) {
            @Override
            public synchronized void start() {
                if (!CURRENT_PROCESS.isEmpty()) {
                    CURRENT_PROCESS.peekLast().start();
                } else {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Try to call System.exit(). Continue? (y/n): ");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        return;
                    }
                    System.exit(0);
                }
            }
        };
    }

    public String var(String name) {
        if (!vars.containsKey(name)) {
            vars.put(name, 0);
        }
        return name;
    }

    public Thread assign(int position, String name, Object value) {
        return new Thread() {
            @Override
            public synchronized void start() {
                vars.put(name, value);
            }
        };
    }

    public Thread thenCase(int position, Thread action) {
        return new Thread() {
            @Override
            public synchronized void start() {
                action.start();
            }
        };
    }


    public Checker<String, Predicate<Number>> lower(int line, String var, Number value) {
        return new Checker<>(var, x -> x.doubleValue() < value.doubleValue());
    }


//    public Thread increment(int line, String var, Double increment) {
//        return new Thread("increment-at" + line){
//           ;
//        };
//    }


    public Double range(int position, Number element) {
        return element.doubleValue();
    }

    public Checker<String, Predicate<Double>> in(int position, String var, Double max) {
        return new Checker<>(var,
                (x) -> x < max);
    }


    public Thread ifCase(int line, Checker<String, Predicate<Number>> checker) {
        Thread ifCase = new Thread("if-at-" + line) {

            @Override
            public synchronized void start() {
                if (!CURRENT_PROCESS.contains(this)) {
                    CURRENT_PROCESS.add(this);
                    buffer.put(this, new LinkedList<>());
                    return;
                }
                try {
                    LinkedList<Thread> body = buffer.remove(this);
                    Double value = Double.parseDouble(vars.get(checker.variable).toString());
                    CURRENT_PROCESS.pollLast();
                    if (checker.criteria.test(value)) {
                        for (Thread el : body
                        ) {
                            if (el.getName().startsWith("else")) {
                                return;
                            } else {
                                el.start();
                            }
                        }
                    } else {
                        boolean add = false;
                        Thread elth = null;
                        for (Thread th : body
                        ) {
                            if (th.getName().startsWith("else")) {
                                add = true;
                                elth = th;
                                CURRENT_PROCESS.addLast(th);
                                buffer.put(th, new LinkedList<>());
                            } else {
                                if (add) {
                                    buffer.get(elth).addLast(th);
                                }
                            }


                        }
                        if (elth != null){
                            elth.start();
                        }
                    }

                } catch (NumberFormatException e) {
                    throw new Error(keyErrors.get(2) + CALLED_AT + line + EXPECTED + "[integer, double]");
                }

            }
        };
        return ifCase;
    }

    public Thread forLoop(int line, Checker<String, Predicate<Double>> isIn) {
        Thread loop = new Thread("loop-at-" + line) {
            @Override
            public synchronized void start() {
                if (!CURRENT_PROCESS.contains(this)) {
                    CURRENT_PROCESS.add(this);
                    buffer.put(this, new LinkedList<>());
                    return;
                }
                LinkedList<Thread> body = buffer.remove(this);
                try {
                    Double value = Double.parseDouble(vars.get(isIn.variable).toString());
                    while (isIn.criteria.test(value)) {
                        try {
                            forLineFunctions(body, true);
                            value = Double.parseDouble(vars.get(isIn.variable).toString());
                            vars.put(isIn.variable, value + 1);
                        } catch (UseGoTo useGoTo) {
                            throw new Error(keyErrors.get(1) + CALLED_AT + line + FUNCTION + "[for]");
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new Error(keyErrors.get(2) + CALLED_AT + line + EXPECTED + "[integer, double]");
                }
                CURRENT_PROCESS.pollLast();
            }
        };
        return loop;
    }

    public Thread elseCase(int position) {
        return new Thread("else-at-" + position) {

            @Override
            public synchronized void start() {
                try {
                    LinkedList<Thread> body = buffer.remove(this);
                    body.forEach(el -> el.start());
                    CURRENT_PROCESS.pollLast();
                } catch (NullPointerException e) {
                    throw new Error(keyErrors.get(3) + CALLED_AT + position + FUNCTION + "[else]");
                }
            }
        };
    }

    public Thread goTo(int position, Number line) {
        return new Thread() {
            @Override
            public synchronized void start() {
                isThrown = line.intValue();
                super.interrupt();
            }
        };
    }

    public String string(String str) {
        return str;
    }

    public Thread print(int position, Object string) {
        return new Thread() {
            @Override
            public synchronized void start() {
                System.out.println(string);
            }
        };
    }

    public Double sum(int position, Number of, Number os) {
        Number result = of.doubleValue() + os.doubleValue();
        return result.doubleValue();

    }


    public Double sum(int position, String of, Number os) {
        Number result = Double.parseDouble(vars.get(of).toString()) + os.doubleValue();
        return result.doubleValue();

    }

    @Override
    public void run() {
        ArrayList<Integer> keys = new ArrayList<>(lines.keySet());
        Collection<List<Thread>> result = lines.values();
        Iterator<List<Thread>> it = result.iterator();
        ArrayList temp;
        Integer isOk = iterate(it);

        try {
            while (isOk != null) {
                temp = new ArrayList(result);
                it = temp.subList(keys.indexOf(isOk), temp.size()).iterator();
                isOk = iterate(it);

            }
        } catch (IndexOutOfBoundsException e) {
            throw new Error(keyErrors.get(0));
        }


    }

    private Integer iterate(Iterator<List<Thread>> it) {
        while (it.hasNext()) {
            try {
                forLineFunctions(it.next(), false);
            } catch (UseGoTo useGoTo) {
                isThrown = null;
                return useGoTo.message;
            }
        }
        return null;
    }


    private void forLineFunctions(List<Thread> next, boolean loop) throws UseGoTo {
        ListIterator<Thread> itF;
        Thread el;
        itF = next.listIterator();
        while (itF.hasNext()) {
            el = itF.next();
            fillBuffer(el, loop);
            if (isThrown != null) {
                throw new UseGoTo(isThrown);
            }

        }
    }

    private void fillBuffer(Thread el, boolean loop) {
        if (CURRENT_PROCESS.isEmpty()) {
            el.start();
            return;
        }

        if (!loop && (!Pattern.matches("^(end|loop|if).*", el.getName()) || CURRENT_PROCESS.size()>2)) {
            buffer.get(CURRENT_PROCESS.peekLast()).add(el);
            return;
        }

        el.start();
    }

    private class Checker<K, M> {

        public K variable;
        public M criteria;

        public Checker(K variable, M criteria) {
            this.variable = variable;
            this.criteria = criteria;
        }
    }
}
