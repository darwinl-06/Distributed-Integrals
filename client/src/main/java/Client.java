import Demo.PrinterCallbackPrx;
import implementation.PrinterCallbackImpl;

import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws UnknownHostException {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "config.client", extraArgs)) {
            Demo.MasterInterfacePrx master = Demo.MasterInterfacePrx
                    .checkedCast(communicator.propertyToProxy("Integral.Proxy"));

            com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Callback.Client");
            PrinterCallbackPrx printerCallbackPrx = PrinterCallbackPrx.uncheckedCast(
                    adapter.addWithUUID(new PrinterCallbackImpl()));
            adapter.activate();

            if (master == null) {
                throw new Error("Invalid proxy");
            }

            menu(master, printerCallbackPrx);
        }
    }

    public static void menu(Demo.MasterInterfacePrx service, PrinterCallbackPrx printerCallbackPrx) throws UnknownHostException {

        int integrationMethod = 0;
        String info = getInfo();
        String message = "";

        boolean flag = true;

        while (flag) {
            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("\n");
            System.out.println("------------------------------------------------------------------");
            System.out.println("| Ingrese la función a integrar (en términos de x):              |");
            System.out.println("| O ingresa 'exit' para salir                                    |");
            System.out.println("------------------------------------------------------------------");

            System.out.print(info + ":");
            String function = sc.nextLine();

            if (function.equals("exit")) {
                flag = false;

            } else {
                System.out.println("\n");
                System.out.println("----------------------------------------------------------------------");
                System.out.println("| Ingrese el límite inferior de integración (escriba 'inf' para -∞): |");
                System.out.println("----------------------------------------------------------------------");

                System.out.print(info + ":");
                String lowerLimit = sc.nextLine();

                System.out.println("\n");
                System.out.println("----------------------------------------------------------------------");
                System.out.println("| Ingrese el límite superior de integración (escriba 'inf' para ∞):  |");
                System.out.println("----------------------------------------------------------------------");
                System.out.print(info + ":");
                String upperLimit  = sc.nextLine();

                System.out.println("\n");
                System.out.println("---------------------------------------------------------------");
                System.out.println("| Elija el metodo por el cual quiera efectuar la integración  |");
                System.out.println("| 1. Metodo de Simpson                                        |");
                System.out.println("| 2. Metodo del Trapecio                                      |");
                System.out.println("| 3. Metodo de Punto Medio                                    |");
                System.out.println("---------------------------------------------------------------");
                System.out.print(info + ":");
                integrationMethod  = sc.nextInt();
                sc.nextLine();

                while(integrationMethod > 3 || integrationMethod <= 0 ){
                    integrationMethod  = sc.nextInt();
                    sc.nextLine();
                }

                int n = 10000;

                try {
                    // Iniciar el cronómetro
                    long startTime = System.nanoTime();

                    service.receiveTaskInfo(function, lowerLimit, upperLimit, integrationMethod, n, printerCallbackPrx);

                    // Detener el cronómetro
                    long endTime = System.nanoTime();

                    // Calcular el tiempo transcurrido
                    long duration = endTime - startTime;
                    double seconds = duration / 1_000_000_000.0;
                    System.out.println("Tiempo de ejecución: " + seconds + " segundos");

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
