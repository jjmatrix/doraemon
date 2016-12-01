package org.jmatrix.cache.redis;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public class AbstractStringOperation<K, V> {
    private Serializer keySerializer;

    private Serializer valueSerializer;

    public byte[] rawValue(V value) {
        return getValueSerializer().serialize(value);
    }

    public byte[] rawKey(K key) {
        return getKeySerializer().serialize(key);
    }

    public byte[][] rawKeys(K... keys) {
        byte[][] result = new byte[keys.length][];
        int idx = 0;
        for (K key : keys) {
            result[idx++] = getKeySerializer().serialize(key);
        }
        return result;
    }

    public Serializer getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(Serializer<?> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public Serializer getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(Serializer<?> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }
}
