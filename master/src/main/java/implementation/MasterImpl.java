package implementation;

import Demo.MasterInterface;
import Demo.WorkerInterfacePrx;
import com.zeroc.Ice.Current;

import java.util.ArrayList;
import java.util.List;

public class MasterImpl implements MasterInterface {

    private boolean tasksCompleted = false;
    private List<WorkerInterfacePrx> workers = new ArrayList<>();
    private double totalResult = 0.0;
    private int completedTasks = 0;
    private int totalTasks = 0;

    @Override
    public void getTask(String function, double lowerLimit, double upperLimit, int method, int n, Current current) {
        if (workers.isEmpty()) {
            System.out.println("No workers available to process the task.");
            return;
        }

        totalTasks = workers.size();
        double interval = (upperLimit - lowerLimit) / workers.size();
        double start = lowerLimit;

        for (WorkerInterfacePrx worker : workers) {
            double end = start + interval;
            worker.computeIntegral(function, start, end, method, n / workers.size());
            start = end;
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