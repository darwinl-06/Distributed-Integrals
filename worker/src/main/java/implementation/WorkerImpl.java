package implementation;

import Demo.WorkerInterface;
import Demo.MasterInterfacePrx;
import com.zeroc.Ice.Current;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.function.Function;

public class WorkerImpl implements WorkerInterface {

    private MasterInterfacePrx masterPrx;

    public MasterInterfacePrx getMasterPrx() {
        return masterPrx;
    }

    public void setMasterPrx(MasterInterfacePrx masterPrx) {
        this.masterPrx = masterPrx;
    }

    public WorkerImpl(MasterInterfacePrx masterPrx) {
        this.masterPrx = masterPrx;
    }

    @Override
    public void update(Current current) {
        // Implementación vacía
    }

    @Override
    public void printString(String s, Current current) {
        System.out.println("entra");
        System.out.println(masterPrx.getTask());
        computeIntegral(masterPrx.getTask(), current);
    }

    @Override
    public void computeIntegral(Demo.Task task, Current current) {
        System.out.println("entra");

        Function<Double, Double> f;

        if (task == null) {
            System.out.println("se acabo");
        } else {

            if (!task.isInfinite) {
                f = parseFunction(task.function);
            } else {
                f = transformFunction(parseFunction(task.function));
            }

            double result;
            switch (task.integrationMethod) {
                case 1:
                    result = simpson(task.lowerLimit, task.upperLimit, task.iterations, f);
                    break;
                case 2:
                    result = trapecio(task.lowerLimit, task.upperLimit, task.iterations, f);
                    break;
                case 3:
                    result = puntoMedio(task.lowerLimit, task.upperLimit, task.iterations, f);
                    break;
                default:
                    throw new IllegalArgumentException("Método de integración no válido");
            }

            masterPrx.addPartialResult(result);
        }
    }

    private Function<Double, Double> parseFunction(String expression) {
        return (x) -> {
            Expression e = new ExpressionBuilder(expression)
                    .variables("x")
                    .build()
                    .setVariable("x", x);
            return e.evaluate();
        };
    }

    public static Function<Double, Double> transformFunction(Function<Double, Double> f) {
        return (t) -> {
            double x = Math.tan(t);
            return f.apply(x) * (1 / Math.cos(t) / Math.cos(t));
        };
    }

    private double simpson(double a, double b, int n, Function<Double, Double> f) {
        if (n % 2 != 0) {
            throw new IllegalArgumentException("El número de intervalos n debe ser par.");
        }

        double h = (b - a) / n;
        double sum = f.apply(a) + f.apply(b);

        for (int i = 1; i < n; i += 2) {
            sum += 4 * f.apply(a + i * h);
        }

        for (int i = 2; i < n - 1; i += 2) {
            sum += 2 * f.apply(a + i * h);
        }

        return (h / 3) * sum;
    }

    private double trapecio(double a, double b, int n, Function<Double, Double> f) {
        double h = (b - a) / n;
        double sum = (f.apply(a) + f.apply(b)) / 2.0;

        for (int i = 1; i < n; i++) {
            sum += f.apply(a + i * h);
        }

        return h * sum;
    }

    private double puntoMedio(double a, double b, int n, Function<Double, Double> f) {
        double h = (b - a) / n;
        double sum = 0.0;

        for (int i = 0; i < n; i++) {
            double mid = a + (i + 0.5) * h;
            sum += f.apply(mid);
        }

        return h * sum;
    }
}