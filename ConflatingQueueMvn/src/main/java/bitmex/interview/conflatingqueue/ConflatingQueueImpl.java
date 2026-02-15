package bitmex.interview.conflatingqueue;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class ConflatingQueueImpl<K, V> implements ConflatingQueue<K, V> {

    private final ConcurrentLinkedQueue<KeyValue<K, V>> queue = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<K, KeyValue<K, V>> map = new ConcurrentHashMap<>();
    private final AtomicReference<Thread> waiter = new AtomicReference<>();

    /**
     * Adds a key value item to the queue.
     * <p>
     * If a @{{@link KeyValue} already exists in the queue with the same key, then the old @{{@link KeyValue} in the queue is updated
     * in place with the new keyValue. The order of the new keyValue within the queue should be the same as the old @{{@link KeyValue}.
     * <p>
     * If no @{{@link KeyValue} item exists in the queue with the same key, the keyValue is added to the end of the queue.
     *
     * @param keyValue the key value item to add to the queue
     * @return true if the key value item was successfully added to the queue, false otherwise
     * @throws NullPointerException if keyValue is null
     */
    @Override
    public boolean offer(KeyValue<K, V> keyValue) {
        Objects.requireNonNull(keyValue);
        var isUpdated = new AtomicBoolean(false);

        map.compute(keyValue.getKey(), (k, kv) -> {
            if (kv != null) {
                kv.setValue(keyValue.getValue());
                isUpdated.set(true);
                return kv;
            } else { // new entity add to the queue
                isUpdated.set(queue.offer(keyValue));
                // set the atomic thread reference to null, and get the current waiter thread
                var w = waiter.getAndSet(null);
                if (w != null) {
                    // unpark the waiting thread
                    LockSupport.unpark(w);
                }
                return keyValue;
            }
        });

        return isUpdated.get();
    }

    /**
     * Removes the first key value item in the queue, blocking if the queue is empty.
     *
     * @return the first key value item in the queue
     * @throws InterruptedException if the thread was interrupted while waiting for a key value item to be added to the queue
     */
    @Override
    public KeyValue<K, V> take() throws InterruptedException {
        while (true) {
            var node = queue.poll();
            if (node != null) {
                if (map.remove(node.getKey()) != null) {
                    // successfully remove from top of queue
                    return new KeyValueNode<>(node.getKey(), node.getValue()); // Create new node to prevent ghost change and GC lifecycle
                }
                // pass for the multi thread triggering
                continue;
            }
            // prepare for blocking current thread for empty queue
            var current = Thread.currentThread();
            waiter.set(current);

            // Double check queue size for preventing loss wake up
            if(!queue.isEmpty()){
                waiter.compareAndSet(current,null);
                continue;
            }

            //Block this thread
            LockSupport.park(this);
        }
    }

    /**
     * Checks whether the queue is currently empty
     *
     * @return true if the queue is currently empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public int size() {
        return queue.size();
    }
}
