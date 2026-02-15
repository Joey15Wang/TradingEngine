package bitmex.interview.conflatingqueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class KeyValueNodeTest {
    private KeyValueNode<String, Integer> node;
    @BeforeEach
    void setup() {
        node = new KeyValueNode<>("key123", 123);
    }

    @Test
    public void testGetKey() {
        assertThat(node.getKey()).isEqualTo("key123");
    }

    @Test
    public void testGetValue() {
        assertThat(node.getValue()).isEqualTo(123);
    }

    @Test
    public void testSetValue() {
        assertThat(node.getValue()).isEqualTo(123);
        node.setValue(456);
        assertThat(node.getValue()).isEqualTo(456);
    }
}