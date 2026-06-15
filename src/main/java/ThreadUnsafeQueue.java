public interface ThreadUnsafeQueue<T> {
     void enqueue(T object);
     T dequeue();
     boolean isQueueFull();
     boolean isQueueEmpty();

}