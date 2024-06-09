import Demo.Clock;
import Demo.MasterInterfacePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import Demo.WorkerInterfacePrx;
import Demo.WorkerInterface;
import com.zeroc.IceStorm.*;
import implementation.WorkerImpl;

import java.util.UUID;

public class Worker {

    public static class ClockI implements Clock {
        @Override
        public void tick(String date, com.zeroc.Ice.Current current)
        {
            System.out.println(date);
        }
    }
    public static void main(String[] args) throws TopicExists {
        int status = 0;
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.worker", extraArgs)) {

            communicator.getProperties().setProperty("Ice.Default.Package","com.zeroc.demos.IceStorm.Integral");
            //
            // Destroy communicator during JVM shutdown
            //
            Thread destroyHook = new Thread(communicator::destroy);
            Runtime.getRuntime().addShutdownHook(destroyHook);

            run(communicator);
            try
            {
                //status = runSub(communicator, destroyHook, extraArgs.toArray(new String[extraArgs.size()]));
            }catch(Exception ex)
            {
                ex.printStackTrace();
                status = 1;
            }

            if(status != 0)
            {
                System.exit(status);
            }
            //
            // Else the application waits for Ctrl-C to destroy the communicator
            //
        }
    }

    public static void run(Communicator communicator) throws TopicExists {
        MasterInterfacePrx masterProxy = null;
        try{
            masterProxy = MasterInterfacePrx.checkedCast(communicator.propertyToProxy("Integral.Proxy"));
            if (masterProxy == null) {
                System.out.println("asdasdasdasdasd");

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        try(communicator){
            TopicManagerPrx topicManager = TopicManagerPrx.checkedCast(
                    communicator.propertyToProxy("TopicManager.Proxy")
            );
            if (topicManager == null) {
                System.err.println("Invalid proxy");
                return;
            }
            TopicPrx topic;
            try {
                topic = topicManager.retrieve("time");
            } catch (NoSuchTopic e) {
                topic = topicManager.create("time");
            }
            String uniqueSuffix = UUID.randomUUID().toString();
            String uniqueIdentity = "worker-" + uniqueSuffix;
            com.zeroc.Ice.Identity id = com.zeroc.Ice.Util.stringToIdentity(uniqueIdentity);

            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Clock.Subscriber");
            WorkerImpl sorter = new WorkerImpl(masterProxy);
            adapter.add(sorter, id);

            try {
                topic.subscribeAndGetPublisher(null, adapter.createDirectProxy(id));
            } catch (AlreadySubscribed | InvalidSubscriber | BadQoS e) {
                throw new RuntimeException(e);
            }



            // Activate the adapter
            adapter.activate();
            System.out.println("Worker llega aqui");
            // Wait for termination
            communicator.waitForShutdown();
            topic.unsubscribe(adapter.createDirectProxy(id));
        }
    }





}