package implementation;

import java.util.function.Function;

public class Task {

    private Function<Double, Double> function;
    private double lowerLimit;
    private double upperLimit;
    private int integrationMethod;
    private int iterations;

    public Task(Function<Double, Double> function, double lowerLimit, double upperLimit, int integrationMethod, int iterations) {
        this.function = function;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.integrationMethod = integrationMethod;
        this.iterations = iterations;
    }

    public Function<Double, Double> getFunction() {
        return function;
    }

    public void setFunction(Function<Double, Double> function) {
        this.function = function;
    }

    public double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public int getIntegrationMethod() {
        return integrationMethod;
    }

    public void setIntegrationMethod(int integrationMethod) {
        this.integrationMethod = integrationMethod;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
