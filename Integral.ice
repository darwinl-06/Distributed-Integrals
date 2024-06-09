module Demo

{

    interface WorkerInterface{
        void update();
        void printString(string s);
        void computeIntegral(string function, double lowerLimit, double upperLimit, int method, int n);
    }

    interface MasterInterface{
        void getTask(string function, double lowerLimit, double upperLimit, int method, int n);
        void addPartialResult(double resultIntegral);
        void attachWorker(WorkerInterface* subscriber);
        void deattachWorker(WorkerInterface* subscriber);
        void printString(string s);
    }

    interface Clock
    {
        void tick(string time);
    }

    interface Printer
    {
        string printString(string s);
    }
}