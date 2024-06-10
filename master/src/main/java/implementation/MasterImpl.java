package implementation;

import Demo.MasterInterface;
import Demo.WorkerInterfacePrx;
import com.zeroc.Ice.Current;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class MasterImpl implements MasterInterface {

    private boolean tasksCompleted = false;
    private List<WorkerInterfacePrx> workers = new ArrayList<>();
    private List<Demo.Task> tasks = new ArrayList<>();
    private double totalResult = 0.0;
    private int completedTasks = 0;
    private int totalTasks = 0;

    private static AtomicInteger workersCount = new AtomicInteger(0);

    public static Function<Double, Double> transformFunction(Function<Double, Double> f) {
        return (t) -> {
            double x = Math.tan(t);
            return f.apply(x) * (1 / Math.cos(t) / Math.cos(t));
        };
    }

    public static Function<Double, Double> parseFunction(String expression) {
        return (x) -> {
            Expression e = new ExpressionBuilder(expression)
                    .variables("x")
                    .build()
                    .setVariable("x", x);
            return e.evaluate();
        };
    }


    @Override
    public void receiveTaskInfo(String f, String lowerLimit, String upperLimit, int integrationMethod, int iterations, Current current) {
        System.out.println("Received task info: ");
        double a, b;
        boolean isInfinite = false;

        if (lowerLimit.equalsIgnoreCase("inf") && upperLimit.equalsIgnoreCase("inf")) {
            a = -Math.PI / 2 + 1e-6;
            b = Math.PI / 2 - 1e-6;
            isInfinite = true;
        } else {
            a = lowerLimit.equalsIgnoreCase("inf") ? Double.NEGATIVE_INFINITY : Double.parseDouble(lowerLimit);
            b = upperLimit.equalsIgnoreCase("inf") ? Double.POSITIVE_INFINITY : Double.parseDouble(upperLimit);

            if (Double.isInfinite(a) || Double.isInfinite(b)) {
                throw new IllegalArgumentException("Si uno de los límites es infinito, ambos deben ser infinitos.");
            }
        }


        double interval = (b - a) / 2;
        double start = a;


        System.out.println("WOKERS CONECTADOS: " + workersCount);

        for (int i = 0; i < 2; i++) {
            double end = start + interval;
            Demo.Task task = new Demo.Task(f, start, end, integrationMethod, iterations , isInfinite);
            System.out.println("Start:" + start + "END:"+ end);
            tasks.add(task);
            start = end;
        }

        System.out.println(tasks);
    }

    @Override
    public Demo.Task getTask(Current current) {
        if (tasks.isEmpty()) {
            System.out.println(totalResult);
            return null;
        } else {
            System.out.println("Assigning task to worker: " + current.id.name);
            Demo.Task task = tasks.get(0);
            tasks.remove(0);
            return task;
        }
    }

    @Override
    public void addPartialResult(double resultIntegral, Current current) {
        totalResult += resultIntegral;
        completedTasks++;

        System.out.println("Total integral result: " + totalResult);
    }

    @Override
    public void attachWorker(WorkerInterfacePrx subscriber, Current current) {
        workersCount.incrementAndGet();
        workers.add(subscriber);
    }

    @Override
    public void deattachWorker(WorkerInterfacePrx subscriber, Current current) {
        workersCount.decrementAndGet();
        workers.remove(subscriber);
    }

    @Override
    public void printString(String s, Current current) {
        System.out.println(s);
    }
}
