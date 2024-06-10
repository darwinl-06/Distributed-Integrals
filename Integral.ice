module Demo

{
    class Task {
                  string function;
                  double lowerLimit;
                  double upperLimit;
                  int integrationMethod;
                  int iterations;
                  bool isInfinite;
                }

    interface WorkerInterface{
        void update();
        void printString(string s);
        void computeIntegral(Task task);
    };

    interface PrinterCallback
        {
            void callbackString(string s);
        };

    interface MasterInterface{
        void receiveTaskInfo(string function, string lowerLimit, string upperLimit, int integrationMethod, int iterations, PrinterCallback* printerCallback);
        Task getTask();
        void addPartialResult(double resultIntegral);
        void attachWorker(WorkerInterface* subscriber);
        void deattachWorker(WorkerInterface* subscriber);
        void printString(string s);
    };

    interface Clock
    {
        void tick(string time);
    };


    interface Printer
    {
        string printString(string s,  PrinterCallback* cb);
    };


};