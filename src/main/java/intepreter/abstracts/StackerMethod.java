package intepreter.abstracts;


import javax.swing.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StackerMethod extends Thread {
    private ExecutorService exec;
    private HashMap<String, Method> methods;
    private TreeMap<Integer, List<Thread>> lines;
    private Integer isThrown;

    private class UseGoTo extends Exception {
        int message;

        public UseGoTo(Integer message) {
            this.message = message;
        }
    }

    public StackerMethod() {
        methods = new HashMap<>();
        try {
            methods.put("printAll", StackerMethod.class.getDeclaredMethod("printAll", Object.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.exec = Executors.newSingleThreadExecutor();
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

    public String merge(String a, String b) {
        return a + b;
    }


    public Thread end() {
        return new Thread() {
            @Override
            public synchronized void start() {
                  Scanner scanner = new Scanner(System.in);
                  System.out.println("Try to call System.exit(). Continue? (y/n): ");
                  if(scanner.nextLine().equalsIgnoreCase("y")){
                      System.exit(1);
                  }
//                System.exit(0);
            }
        };
    }

    public Thread goTo(Number line) {
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

    public Thread print(Object string) {
        return new Thread() {
            @Override
            public synchronized void start() {
                System.out.println(string);
            }
        };
    }

    public Double sum(Number of, Number os) {
        Number result = of.doubleValue() + os.doubleValue();
        return result.doubleValue();

    }

    @Override
    public void run() {
        ArrayList<Integer> keys = new ArrayList<>(lines.keySet());
        Collection<List<Thread>> result = lines.values();
        Iterator<List<Thread>> it = result.iterator();
        ArrayList temp;
        Integer isOk = iterate(it);

        while (isOk != null) {
            temp = new ArrayList(result);
            it = temp.subList(keys.indexOf(isOk), temp.size()).iterator();
            isOk = iterate(it);

        }


    }

    private Integer iterate(Iterator<List<Thread>> it) {
        ListIterator<Thread> itF;
        Thread el;
        while (it.hasNext()) {
            itF = it.next().listIterator();
            while (itF.hasNext()) {
                el = itF.next();
                try {
                    el.start();
                    if (isThrown != null) {
                        throw new UseGoTo(isThrown);
                    }
                } catch (UseGoTo useGoTo) {
                    isThrown = null;
                    return useGoTo.message;
                }
            }
        }
        return null;
    }
}
