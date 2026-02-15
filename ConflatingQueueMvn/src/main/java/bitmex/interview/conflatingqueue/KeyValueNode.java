package bitmex.interview.conflatingqueue;

import java.util.concurrent.atomic.AtomicReference;

public final class KeyValueNode<K, V> implements KeyValue<K, V> {

    final K key;
    final AtomicReference<V> value;

    public KeyValueNode(K key, V value) {
        this.key = key;
        this.value = new AtomicReference<>(value);
    }

    /**
     * Returns the key
     *
     * @return the key
     */
    @Override
    public K getKey() {
        return this.key;
    }

    /**
     * Returns the value
     *
     * @return the value
     */
    @Override
    public V getValue() {
        return this.value.get();
    }

    public void setValue(V newValue) {
        this.value.set(newValue);
    }
}
