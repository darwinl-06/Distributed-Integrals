import implementation.PrinterCallbackImpl;

import javax.swing.*;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client
{

    public static final Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws UnknownHostException
    {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args,"config.client",extraArgs))
        {
            //com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("SimplePrinter:default -p 10000");

            Demo.MasterInterfacePrx master = Demo.MasterInterfacePrx
                    .checkedCast(communicator.propertyToProxy("Integral.Proxy"));

            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Client");
            adapter.add(new PrinterCallbackImpl(),com.zeroc.Ice.Util.stringToIdentity("ClientIntegral"));
            adapter.activate();

            if (master == null) {
                throw new Error("Invalid proxy");
            }

            menu(master);
        }
    }

    public static void menu(Demo.MasterInterfacePrx service) throws UnknownHostException {

        int integrationMethod = 0;
        String info = getInfo();
        String message = "";

        boolean flag = true;

        while (flag) {

            System.out.println("------------------------------------------------------------------");
            System.out.println("| Ingrese la función a integrar (en términos de x):              |");
            System.out.println("------------------------------------------------------------------\n");

            System.out.print(info + ":");
            String function = sc.nextLine();

            if (function.equals("exit")) {
                flag = false;

            } else {

                System.out.println("----------------------------------------------------------------------");
                System.out.println("| Ingrese el límite inferior de integración (escriba 'inf' para -∞): |");
                System.out.println("----------------------------------------------------------------------\n");

                System.out.print(info + ":");
                String lowerLimit = sc.nextLine();

                System.out.println("----------------------------------------------------------------------");
                System.out.println("| Ingrese el límite superior de integración (escriba 'inf' para ∞):  |");
                System.out.println("----------------------------------------------------------------------\n");
                System.out.print(info + ":");
                String upperLimit  = sc.nextLine();

                while(integrationMethod > 3 || integrationMethod <= 0 ){
                    System.out.println("---------------------------------------------------------------");
                    System.out.println("| Elija el metodo por el cual quiera efectuar la integración  |");
                    System.out.println("| 1. Metodo de Simpson                                        |");
                    System.out.println("| 2. Metodo del Trapecio                                      |");
                    System.out.println("| 3. Metodo de Punto Medio                                    |");
                    System.out.println("---------------------------------------------------------------\n");
                    System.out.print(info + ":");
                    integrationMethod  = sc.nextInt();
                    sc.nextLine();
                }

                int n = 1000;

                try{
                    System.out.println("Resultado: " );
                    service.receiveTaskInfo(function,lowerLimit, upperLimit, integrationMethod, n);
                } catch (Exception e) {
                    System.out.println("Servidor no esta disponible");
                }
            }
        }
    }


    public static String getInfo() throws UnknownHostException {
        String username = System.getProperty("user.name");
        String hostname = java.net.InetAddress.getLocalHost().getHostName();
        return username + "@" + hostname;
    }


}