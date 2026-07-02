package org.callatis.study.concurrency;

public class AlternateFooBar {

    private final int n;
    
    private boolean bar = false;

    public AlternateFooBar(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException {
        
        for (int i = 0; i < n; i++) {
            synchronized (this) {
                while (this.bar) {
                    this.wait();
                }
                // printFoo.run() outputs "foo". Do not change or remove this line.
                printFoo.run();
                this.bar = true;
                this.notifyAll();
            }
            
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        
        for (int i = 0; i < n; i++) {
            synchronized (this) {
                while (!this.bar) {
                    this.wait();
                }
                // printBar.run() outputs "bar". Do not change or remove this line.
                printBar.run();
                this.bar = false;
                this.notifyAll();
            }
        }
    }

}