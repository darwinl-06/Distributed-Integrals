import Demo.Clock;
import Demo.MasterInterfacePrx;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import Demo.WorkerInterfacePrx;
import Demo.WorkerInterface;
import com.zeroc.IceStorm.TopicManagerPrx;

import java.util.UUID;

public class Worker {

    public static class ClockI implements Clock
    {
        @Override
        public void tick(String date, com.zeroc.Ice.Current current)
        {
            System.out.println(date);
        }
    }
    public static void main(String[] args) {
        int status = 0;
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "worker.config", extraArgs)) {
            communicator.getProperties().setProperty("Ice.Default.Package","com.zeroc.demos.IceStorm.integral");
            //
            // Destroy communicator during JVM shutdown
            //
            Thread destroyHook = new Thread(communicator::destroy);
            Runtime.getRuntime().addShutdownHook(destroyHook);

            run(communicator);
            try
            {
                status = runSub(communicator, destroyHook, extraArgs.toArray(new String[extraArgs.size()]));
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

    private static int runSub(com.zeroc.Ice.Communicator communicator, Thread destroyHook, String[] args)
    {

        String topicName = "time";

        com.zeroc.IceStorm.TopicManagerPrx manager = com.zeroc.IceStorm.TopicManagerPrx.checkedCast(
                communicator.propertyToProxy("TopicManager.Proxy"));
        if(manager == null)
        {
            System.err.println("invalid proxy");
            return 1;
        }

        //
        // Retrieve the topic.
        //
        com.zeroc.IceStorm.TopicPrx topic;
        try
        {
            topic = manager.retrieve(topicName);
        }
        catch(com.zeroc.IceStorm.NoSuchTopic e)
        {
            try
            {
                topic = manager.create(topicName);
            }
            catch(com.zeroc.IceStorm.TopicExists ex)
            {
                System.err.println("temporary failure, try again.");
                return 1;
            }
        }

        com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Clock.Subscriber");

        //
        // Add a servant for the Ice object. If --id is used the
        // identity comes from the command line, otherwise a UUID is
        // used.
        //
        // id is not directly altered since it is used below to detect
        // whether subscribeAndGetPublisher can raise
        // AlreadySubscribed.
        //

        com.zeroc.Ice.ObjectPrx subscriber = adapter.add(new ClockI(), Util.stringToIdentity(UUID.randomUUID().toString()));

        //
        // Activate the object adapter before subscribing.
        //
        adapter.activate();

        java.util.Map<String, String> qos = new java.util.HashMap<>();


        try
        {
            topic.subscribeAndGetPublisher(qos, subscriber);
        }
        catch(com.zeroc.IceStorm.AlreadySubscribed e)
        {
            System.out.println("reactivating persistent subscriber");
        }
        catch(com.zeroc.IceStorm.InvalidSubscriber e)
        {
            e.printStackTrace();
            return 1;
        }
        catch(com.zeroc.IceStorm.BadQoS e)
        {
            e.printStackTrace();
            return 1;
        }

        //
        // Replace the shutdown hook to unsubscribe during JVM shutdown
        //
        final com.zeroc.IceStorm.TopicPrx topicF = topic;
        final com.zeroc.Ice.ObjectPrx subscriberF = subscriber;
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try
            {
                topicF.unsubscribe(subscriberF);
            }
            finally
            {
                communicator.destroy();
            }
        }));
        Runtime.getRuntime().removeShutdownHook(destroyHook); // remove old destroy-only shutdown hook

        return 0;
    }
}