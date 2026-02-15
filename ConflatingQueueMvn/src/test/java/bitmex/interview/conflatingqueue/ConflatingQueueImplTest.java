package bitmex.interview.conflatingqueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ConflatingQueueImplTest {
    private final ConflatingQueueImpl<String,Integer> queue = new ConflatingQueueImpl<>();

    @BeforeEach
    void setup(){

    }

    @Test
    void offerMultipleValuesToTheSameKeyAndUpdateWithLatestValue() throws InterruptedException {
        assertThat(queue.isEmpty()).isTrue();
        queue.offer(new KeyValueNode<>("key123", 456));
        assertThat(queue.isEmpty()).isFalse();
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.offer(new KeyValueNode<>("key123", 123)));
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.offer(new KeyValueNode<>("key123", 234)));
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.offer(new KeyValueNode<>("key123", 567)));
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.offer(new KeyValueNode<>("key123", 897)));
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.take())
                .extracting(KeyValue::getKey, KeyValue::getValue)
                .containsExactly("key123", 897);

    }

    @Test
    void offerMultipleDifferentKeyAndAttachToQueueInOrder() throws InterruptedException {
        assertThat(queue.isEmpty()).isTrue();
        queue.offer(new KeyValueNode<>("key456", 456));
        assertThat(queue.isEmpty()).isFalse();
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.offer(new KeyValueNode<>("key123", 123)));
        assertThat(queue.size()).isEqualTo(2);
        assertThat(queue.offer(new KeyValueNode<>("key234", 234)));
        assertThat(queue.size()).isEqualTo(3);
        assertThat(queue.offer(new KeyValueNode<>("key567", 567)));
        assertThat(queue.size()).isEqualTo(4);
        assertThat(queue.offer(new KeyValueNode<>("key897", 897)));
        assertThat(queue.size()).isEqualTo(5);
        assertThat(queue.take())
                .extracting(KeyValue::getKey, KeyValue::getValue)
                .containsExactly("key456", 456);
        assertThat(queue.take())
                .extracting(KeyValue::getKey, KeyValue::getValue)
                .containsExactly("key123", 123);
        assertThat(queue.take())
                .extracting(KeyValue::getKey, KeyValue::getValue)
                .containsExactly("key234", 234);
        assertThat(queue.take())
                .extracting(KeyValue::getKey, KeyValue::getValue)
                .containsExactly("key567", 567);
        assertThat(queue.take())
                .extracting(KeyValue::getKey, KeyValue::getValue)
                .containsExactly("key897", 897);
        assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    void offerNewValueToExistingKeyAndQueueOrderKeptUnchanged() throws InterruptedException {
        assertThat(queue.isEmpty()).isTrue();
        queue.offer(new KeyValueNode<>("key456", 456));
        assertThat(queue.isEmpty()).isFalse();
        assertThat(queue.size()).isEqualTo(1);
        assertThat(queue.offer(new KeyValueNode<>("key123", 123)));
        assertThat(queue.size()).isEqualTo(2);
        assertThat(queue.offer(new KeyValueNode<>("key234", 234)));
        assertThat(queue.size()).isEqualTo(3);
        assertThat(queue.offer(new KeyValueNode<>("key456", 567)));
        assertThat(queue.size()).isEqualTo(3);
        assertThat(queue.take())
                .extracting(KeyValue::getKey, KeyValue::getValue)
                .containsExactly("key456", 567);
        assertThat(queue.take())
                .extracting(KeyValue::getKey, KeyValue::getValue)
                .containsExactly("key123", 123);
        assertThat(queue.take())
                .extracting(KeyValue::getKey, KeyValue::getValue)
                .containsExactly("key234", 234);
        assertThat(queue.isEmpty()).isTrue();
    }
}