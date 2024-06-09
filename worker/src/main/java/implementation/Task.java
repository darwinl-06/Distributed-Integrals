package implementation;

public class Task {

    private String function;
    private double lowerLimit;
    private double upperLimit;
    private int integrationMethod;
    private int iterations;
    private boolean isInfinite;

    public Task(String function, double lowerLimit, double upperLimit, int integrationMethod, int iterations, boolean isInfinite) {
        this.function = function;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.integrationMethod = integrationMethod;
        this.iterations = iterations;
        this.isInfinite = isInfinite;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
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

    public boolean isInfinite() {
        return isInfinite;
    }

    public void setInfinite(boolean infinite) {
        isInfinite = infinite;
    }
}
