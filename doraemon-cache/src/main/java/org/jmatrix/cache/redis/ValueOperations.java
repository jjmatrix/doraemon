package org.jmatrix.cache.redis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public class ValueOperations<K, V> extends AbstractStringOperation<K, V> {

    private RedisTemplate<V> redisTemplate;

    public ValueOperations(RedisTemplate<V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(K key, V value) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value);
        redisTemplate.execute((connection) -> {
            connection.set(rawKey, rawValue);
            return null;
        });
    }

    public void set(K key, V value, int expireTime, TimeUnit timeUnit) {
        redisTemplate.execute((connection) -> {
            connection.pSetEx((String) key, (String) value, (int) timeUnit.toMillis(expireTime));
            return null;
        });
    }

    public List<V> multiGet(K... keys) {
        byte[][] rawKeys = rawKeys(keys);
        return redisTemplate.execute((connection) -> {
            return deserializeValues(connection.mGet(rawKeys));
        });
    }

    public void multiSet(Map<K, V> keyValues) {
        Map<byte[], byte[]> rawHash = rawHash(keyValues);
        redisTemplate.execute(connection -> {
            connection.mSet(rawHash);
            return null;
        });
    }

    private V deserializeValue(byte[] rawValue) {
        return (V) getValueSerializer().unSerialize(rawValue);
    }

    private List<V> deserializeValues(List<byte[]> rawValues) {
        List<V> value = new ArrayList<>(rawValues.size());
        for (byte[] rawValue : rawValues) {
            value.add((V) getValueSerializer().unSerialize(rawValue));
        }
        return value;
    }

    private Map<byte[], byte[]> rawHash(Map<K, V> keyValues) {
        Map<byte[], byte[]> rawHash = new LinkedHashMap<>(keyValues.size());
        for (Map.Entry<K, V> keyValue : keyValues.entrySet()) {
            rawHash.put(getKeySerializer().serialize(keyValue.getKey()), getValueSerializer().serialize(keyValue.getValue()));
        }
        return rawHash;
    }
}
