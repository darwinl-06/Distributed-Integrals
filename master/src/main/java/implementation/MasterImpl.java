package implementation;

import Demo.MasterInterface;
import Demo.WorkerInterfacePrx;
import com.zeroc.Ice.Current;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

public class MasterImpl implements MasterInterface {

    private boolean tasksCompleted = false;
    private List<WorkerInterfacePrx> workers = new ArrayList<>();
    private Queue<Task> tasks = new LinkedList<>();
    private double totalResult = 0.0;
    private int completedTasks = 0;
    private int totalTasks = 0;


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
    public void receiveTaskInfo(String f, String lowerLimit, String upperLimit, int integrationMethod, int iterations,Current current) {
        System.out.println("Received task info: ");
        double a, b;
        boolean isInfinite = false;
        Function<Double, Double> function =  null;


        if (lowerLimit.equalsIgnoreCase("inf") && upperLimit.equalsIgnoreCase("inf")) {
            // Si ambos límites son infinitos
            a = -Math.PI / 2 + 1e-6; // pequeño desplazamiento para evitar problemas en los límites
            b = Math.PI / 2 - 1e-6;  // pequeño desplazamiento para evitar problemas en los límites
            function = transformFunction(parseFunction(f));
            isInfinite = true;
        } else {
            a = lowerLimit.equalsIgnoreCase("inf") ? Double.NEGATIVE_INFINITY : Double.parseDouble(lowerLimit);
            b = upperLimit.equalsIgnoreCase("inf") ? Double.POSITIVE_INFINITY : Double.parseDouble(upperLimit);

            if (Double.isInfinite(a) || Double.isInfinite(b)) {
                throw new IllegalArgumentException("Si uno de los límites es infinito, ambos deben ser infinitos.");
            }
        }

        totalTasks = workers.size();
        double interval = (a - b) / workers.size();
        double start = b;
        System.out.println("ESTA ES LA CANTIDAD DE WORKERS " + workers.size());

        for (WorkerInterfacePrx worker : workers) {
            double end = start + interval;
            Task task = new Task(function, start, end, integrationMethod, iterations);
            tasks.add(task);
            start = end;
        }

        System.out.println(tasks);


    }

    @Override
    public void getTask(Current current) {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available to process.");
            return;
        }

        Task task = tasks.poll();
        if (task == null) {
            System.out.println("All tasks have been assigned.");
            return;
        }

        System.out.println("Assigning task to worker: " + current.id.name);

        for (WorkerInterfacePrx worker : workers) {
            worker.computeIntegral(task.getFunction().toString(), task.getLowerLimit(), task.getUpperLimit(), task.getIntegrationMethod(), task.getIterations());
        }
    }



    @Override
    public void addPartialResult(double resultIntegral, Current current) {
        totalResult += resultIntegral;
        completedTasks++;
        if (completedTasks == totalTasks) {
            System.out.println("Total integral result: " + totalResult);
        }
    }

    @Override
    public void attachWorker(WorkerInterfacePrx subscriber, Current current) {
        workers.add(subscriber);
    }

    @Override
    public void deattachWorker(WorkerInterfacePrx subscriber, Current current) {
        workers.remove(subscriber);
    }

    @Override
    public void printString(String s, Current current) {

        System.out.println(s);

    }
}