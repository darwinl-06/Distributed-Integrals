module Demo

{
    interface MasterInterface{
        void getTask();
        void addPartialResult(double resultIntegral);
        void attachWorker();
        void printString(string s);
    }

    interface WorkerInterface{
        void update();
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