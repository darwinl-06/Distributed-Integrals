import Demo.MasterInterfacePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import Demo.WorkerInterfacePrx;
import Demo.WorkerInterface;
import com.zeroc.IceStorm.TopicManagerPrx;

public class Worker {

    public static void main(String[] args) {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "worker.config", extraArgs)) {
            communicator.getProperties().setProperty("Ice.Default.Package","com.zeroc.demos.IceStorm.integral");
            //
            // Destroy communicator during JVM shutdown
            //
            Thread destroyHook = new Thread(communicator::destroy);
            Runtime.getRuntime().addShutdownHook(destroyHook);

            run(communicator);
            //
            // Else the application waits for Ctrl-C to destroy the communicator
            //
        }
    }

    public static void run(Communicator communicator){
        MasterInterfacePrx masterProxy = null;
        try{
            masterProxy = MasterInterfacePrx.checkedCast(communicator.stringToProxy("masterIntegral"));
        }catch(Exception e){
            e.printStackTrace();
        }
        try(communicator){
            TopicManagerPrx topicManager = TopicManagerPrx.checkedCast(
                communicator.propertyToProxy("TopicManager.proxy")
            );
        }
    }
}