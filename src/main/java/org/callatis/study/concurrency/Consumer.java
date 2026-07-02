package org.callatis.study.concurrency;

public class Consumer {

    private final int n;

    private final long delay;

    private final ProducerConsumer<Integer> pc;

    public Consumer(ProducerConsumer<Integer> pc, int n, int delay) {
        this.pc = pc;
        this.n = n;
        this.delay = delay;
    }

    public void consume() throws InterruptedException {
        for (int i = 0; i < n; i++) {
            int removed = pc.remove();
            System.out.println("Removed " + removed);
            if (this.delay > 0) {
                Thread.sleep(this.delay);
            }
        }
    }
}