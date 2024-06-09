package implementation;

import Demo.WorkerInterface;
import Demo.MasterInterfacePrx;
import com.zeroc.Ice.Current;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.function.Function;

public class WorkerImpl implements WorkerInterface {

    @Override
    public void update(Current current) {
        // Implementación vacía
    }

    @Override
    public void printString(String s, Current current) {
        System.out.println(s);
    }

    @Override
    public void computeIntegral(String function, double lowerLimit, double upperLimit, int method, int n, Current current) {
        Function<Double, Double> f = parseFunction(function);

        double result;
        switch (method) {
            case 1:
                result = simpson(lowerLimit, upperLimit, n, f);
                break;
            case 2:
                result = trapecio(lowerLimit, upperLimit, n, f);
                break;
            case 3:
                result = puntoMedio(lowerLimit, upperLimit, n, f);
                break;
            default:
                throw new IllegalArgumentException("Método de integración no válido");
        }

        MasterInterfacePrx master = MasterInterfacePrx.checkedCast(current.con.createProxy(current.id));
        master.addPartialResult(result);

//        return result;
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