package DSA_SEM_3_Coursework;
// File: ThreadControllerSolution.java
class ThreadControllerSolution {
    private int n;
    private volatile int currentNumber = 0;
    private volatile boolean zeroPrinted = false;
    
    // Constructor
    public ThreadControllerSolution(int n) {
        this.n = n;
    }
    
    // Class to handle zero printing
    class ZeroThread implements Runnable {
        private NumberPrinter printer;
        
        public ZeroThread(NumberPrinter printer) {
            this.printer = printer;
        }
        
        @Override
        public void run() {
            try {
                while (currentNumber <= n) {
                    synchronized (ThreadControllerSolution.this) {
                        // Wait if zero shouldn't be printed now
                        while (zeroPrinted || currentNumber > n) {
                            if (currentNumber > n) return;
                            ThreadControllerSolution.this.wait();
                        }
                        
                        printer.printZero();
                        zeroPrinted = true;
                        ThreadControllerSolution.this.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    // Class to handle even numbers
    class EvenThread implements Runnable {
        private NumberPrinter printer;
        
        public EvenThread(NumberPrinter printer) {
            this.printer = printer;
        }
        
        @Override
        public void run() {
            try {
                while (currentNumber <= n) {
                    synchronized (ThreadControllerSolution.this) {
                        // Wait if it's not time for even number
                        while (!zeroPrinted || currentNumber % 2 != 0 || currentNumber >= n) {
                            if (currentNumber >= n) return;
                            ThreadControllerSolution.this.wait();
                        }
                        
                        printer.printEven(currentNumber);
                        currentNumber++;
                        zeroPrinted = false;
                        ThreadControllerSolution.this.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    // Class to handle odd numbers
    class OddThread implements Runnable {
        private NumberPrinter printer;
        
        public OddThread(NumberPrinter printer) {
            this.printer = printer;
        }
        
        @Override
        public void run() {
            try {
                while (currentNumber <= n) {
                    synchronized (ThreadControllerSolution.this) {
                        // Wait if it's not time for odd number
                        while (!zeroPrinted || currentNumber % 2 == 0 || currentNumber >= n) {
                            if (currentNumber >= n) return;
                            ThreadControllerSolution.this.wait();
                        }
                        
                        printer.printOdd(currentNumber);
                        currentNumber++;
                        zeroPrinted = false;
                        ThreadControllerSolution.this.notifyAll();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    // Main method to test the solution
    public static void main(String[] args) {
        int n = 5;
        NumberPrinter printer = new NumberPrinter();
        ThreadControllerSolution controller = new ThreadControllerSolution(n);
        
        Thread zeroThread = new Thread(controller.new ZeroThread(printer));
        Thread evenThread = new Thread(controller.new EvenThread(printer));
        Thread oddThread = new Thread(controller.new OddThread(printer));
        
        zeroThread.start();
        evenThread.start();
        oddThread.start();
        
        try {
            zeroThread.join();
            evenThread.join();
            oddThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Assuming this is the provided NumberPrinter class
class NumberPrinter {
    public void printZero() {
        System.out.print("0");
    }
    
    public void printEven(int n) {
        System.out.print(n);
    }
    
    public void printOdd(int n) {
        System.out.print(n);
    }
}