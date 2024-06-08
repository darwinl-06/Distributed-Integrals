package implementation;

import Demo.MasterInterface;
import Demo.WorkerInterfacePrx;
import com.zeroc.Ice.Current;

import java.util.ArrayList;
import java.util.List;

public class MasterImpl implements MasterInterface {

    private boolean tasksCompleted = false;
    private List<WorkerInterfacePrx> workers = new ArrayList<>();

    @Override
    public void getTask(Current current) {
        for (WorkerInterfacePrx worker : workers){
            worker.processTask();
        }
    }

    @Override
    public void addPartialResult(double resultIntegral, Current current) {

    }

    @Override
    public void attachWorker(WorkerInterfacePrx subscriber, Current current) {
        workers.add(subscriber);
    }

    @Override
    public void printString(String s, Current current) {
        System.out.println(s);
    }
}
