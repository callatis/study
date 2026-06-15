
public class ThreadSafeQueue<T> implements ThreadUnsafeQueue<T> {
	
	private ThreadUnsafeQueue<T> queue;
	
	public ThreadSafeQueue(ThreadUnsafeQueue<T> queue) {
		this.queue = queue; 
	}
	
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
	
	public boolean isQueueFull() {
		synchronized(this.queue) {
			return this.queue.isQueueFull();
		}
	}

	public boolean isQueueEmpty() {
		synchronized(this.queue) {
			return this.queue.isQueueEmpty();
		}
	}

}