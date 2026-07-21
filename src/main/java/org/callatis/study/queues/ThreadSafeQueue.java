
package org.callatis.study.queues;

public class ThreadSafeQueue<T> implements ThreadUnsafeQueue<T> {
	
	private final ThreadUnsafeQueue<T> queue;
	
	public ThreadSafeQueue(ThreadUnsafeQueue<T> queue) {
		this.queue = queue; 
	}
	
	@Override
	public void enqueue(T object) {
		try {
			synchronized(this.queue) {
				while (this.queue.isQueueFull()) {
					Thread.currentThread().wait();
				}
				this.queue.enqueue(object);
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
    @Override
	public T dequeue() {
		T result;
		try {
			synchronized(this.queue) {
				while (this.queue.isQueueEmpty()) {
					Thread.currentThread().wait();
				}
				result = this.queue.dequeue();
				Thread.currentThread().notify();
			}
			
			return result;
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		} 
	}
	
	@Override
	public boolean isQueueFull() {
		synchronized(this.queue) {
			return this.queue.isQueueFull();
		}
	}

	@Override
	public boolean isQueueEmpty() {
		synchronized(this.queue) {
			return this.queue.isQueueEmpty();
		}
	}

}