import Demo.WorkerInterfacePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import com.zeroc.IceStorm.NoSuchTopic;
import com.zeroc.IceStorm.TopicExists;
import com.zeroc.IceStorm.TopicManagerPrx;
import com.zeroc.IceStorm.TopicPrx;
import implementation.MasterImpl;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class    Master {
    private ExecutorService executor;
    private List<WorkerInterfacePrx> workers;
    public static int workerCount = 0;


    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args, "config.master")) {
            // Inicializar Master
            MasterImpl master = new MasterImpl();
            ObjectAdapter adapter = communicator.createObjectAdapter("MasterInterface");
            adapter.add(master, com.zeroc.Ice.Util.stringToIdentity("MasterIntegral"));
            adapter.activate();

            System.out.println("Master initialized...");

            // Inicializar Publisher
            communicator.getProperties().setProperty("Ice.Default.Package", "com.zeroc.demos.IceStorm.clock");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> communicator.destroy()));

            int status = runPublisher(communicator, master);
            System.exit(status);

            communicator.waitForShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int runPublisher(Communicator communicator, MasterImpl master) {
        String topicName = "time";
        TopicManagerPrx manager = TopicManagerPrx.checkedCast(
                communicator.propertyToProxy("TopicManager.Proxy"));
        if (manager == null) {
            System.err.println("invalid proxy");
            return 1;
        }

        TopicPrx topic;
        try {
            topic = manager.retrieve(topicName);
        } catch (NoSuchTopic e) {
            try {
                topic = manager.create(topicName);
            } catch (TopicExists ex) {
                System.err.println("temporary failure, try again.");
                return 1;
            }
        }

        com.zeroc.Ice.ObjectPrx publisher = topic.getPublisher();
        WorkerInterfacePrx worker = WorkerInterfacePrx.uncheckedCast(publisher);


        System.out.println("publishing tick events. Press ^C to terminate the application.");
        try {
            SimpleDateFormat date = new SimpleDateFormat("MM/dd/yy HH:mm:ss:SSS");
            while (true) {
                worker.printString("Conecta2");
                master.setWorkersNumber(topic.getSubscribers().length);
                System.out.println("Number of workers: " + topic.getSubscribers().length);
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    // Handle exception
                }
            }
        } catch (com.zeroc.Ice.CommunicatorDestroyedException ex) {
            // Ctrl-C triggered shutdown hook, which destroyed communicator - we're terminating
        }

        return 0;
    }
}
