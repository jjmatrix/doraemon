package org.jmatrix.cache.redis;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public class HashOperations<K, V> extends AbstractStringOperation<K, V> {

    private RedisTemplate<V> redisTemplate;

    public HashOperations() {
    }

    public HashOperations(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean hSet(K key, K field, V value) {
        return redisTemplate.execute(redisConnection -> {
            return redisConnection.hSet(rawKey(key), rawKey(field), rawValue(value));
        });
    }

    public boolean hmSet(K key, Map<K, V> hash) {
        return redisTemplate.execute(redisConnection -> {
            return redisConnection.hmSet((String) key, (Map<String, String>) hash);
        });
    }

    public List<String> hmGet(K key, K... fields) {
        return redisTemplate.execute(redisConnection -> {
            return deserializeValue(redisConnection.hmGet(rawKey(key), rawKeyList(fields)));
        });
    }

    public <T> List<T> deserializeValue(List<byte[]> values) {
        return values.stream().map(value -> (T) getValueSerializer().unSerialize(value)).collect(Collectors.toList());
    }

    public byte[][] rawKeyList(K... keys) {
        byte[][] rawKeys = new byte[keys.length][];
        int i = 0;
        for (K key : keys) {
            rawKeys[i++] = rawKey(key);
        }
        return rawKeys;
    }
}
