package org.callatis.study.concurrency;

public class Producer {

    private final ProducerConsumer<Integer> pc;

    private final int n;

    private final long delay;

    public Producer(ProducerConsumer<Integer> pc, int n, long delay) {
        this.pc = pc;
        this.n = n;
        this.delay = delay;
    }

    public void produce() throws InterruptedException {
        for (int i = 0; i < this.n; i++) {
            this.pc.add(i);
            System.out.println("Added " + i);
            if (this.delay > 0) {
                Thread.sleep(this.delay);
            }
        }
    }
}