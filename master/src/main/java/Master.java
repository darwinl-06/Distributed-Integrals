import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.ObjectAdapter;
import implementation.MasterImpl;

import Demo.WorkerInterfacePrx; 

import java.util.List;
import java.util.concurrent.ExecutorService;

public class Master {
    private ExecutorService executor;
    private List<WorkerInterfacePrx> workers;

    public static void main(String[] args) {

        try (Communicator communicator = Util.initialize(args, "config.master")) {

            MasterImpl master = new MasterImpl();
            ObjectAdapter adapter = communicator.createObjectAdapter("MasterInterface");

            adapter.add(master, com.zeroc.Ice.Util.stringToIdentity("MasterIntegral"));
            adapter.activate();

            System.out.println("Master initialized...");
            communicator.waitForShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}