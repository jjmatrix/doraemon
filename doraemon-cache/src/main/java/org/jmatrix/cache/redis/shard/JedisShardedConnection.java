package org.jmatrix.cache.redis.shard;

import org.jmatrix.cache.redis.Connection;
import org.jmatrix.cache.util.RedisSupport;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jmatrix
 * @date 16/9/25
 */
public class JedisShardedConnection implements Connection {

    private ShardedJedis shardedJedis;

    public JedisShardedConnection(ShardedJedis shardedJedis) {
        this.shardedJedis = shardedJedis;
    }

    @Override
    public void close() {
        shardedJedis.close();
    }

    @Override
    public void set(byte[] key, byte[] value) {
        shardedJedis.set(key, value);
    }

    @Override
    public byte[] get(byte[] key) {
        return shardedJedis.get(key);
    }

    @Override
    public void setEx(byte[] key, byte[] value, int timeInSecond) {
        shardedJedis.setex(key, timeInSecond, value);
    }

    @Override
    public void pSetEx(String key, String value, int timeInMs) {
        shardedJedis.psetex(key, timeInMs, value);
    }

    @Override
    public Boolean hSet(byte[] key, byte[] field, byte[] value) {
        return RedisSupport.toBoolean(shardedJedis.hset(key, field, value));
    }

    @Override
    public Boolean hmSet(String key, Map<String, String> hash) {
        return RedisSupport.toBoolean(shardedJedis.hmset(key, hash));
    }

    @Override
    public List<byte[]> hmGet(byte[] key, byte[]... fields) {
        return shardedJedis.hmget(key, fields);
    }

    @Override
    public List<byte[]> mGet(byte[]... keys) {
        if (keys == null || keys.length == 0) {
            return Collections.emptyList();
        }

        Map<Jedis, List<byte[]>> jedisKeyMap = new HashMap<>();
        for (byte[] key : keys) {
            Jedis jedis = shardedJedis.getShard(key);
            if (jedisKeyMap.get(jedis) == null) {
                List<byte[]> keyList = new ArrayList<>();
                keyList.add(key);
                jedisKeyMap.put(jedis, keyList);
            } else {
                jedisKeyMap.get(jedis).add(key);
            }
        }

        List<byte[]> result = new ArrayList<>(keys.length);
        for (Map.Entry<Jedis, List<byte[]>> jedisKeys : jedisKeyMap.entrySet()) {
            result.addAll(jedisKeys.getKey().mget(jedisKeys.getValue().toArray(new byte[0][0])));
        }
        return result;
    }

    @Override
    public void mSet(Map<byte[], byte[]> keyValues) {
        if (keyValues == null || keyValues.isEmpty()) {
            return;
        }

        Map<Jedis, Map<byte[], byte[]>> jedisKeyMap = new HashMap<>();
        for (Map.Entry<byte[], byte[]> keyValue : keyValues.entrySet()) {
            Jedis jedis = shardedJedis.getShard(keyValue.getKey());
            if (jedisKeyMap.get(jedis) == null) {
                Map<byte[], byte[]> instKeyValuemap = new HashMap<>();
                instKeyValuemap.put(keyValue.getKey(), keyValue.getValue());
                jedisKeyMap.put(jedis, instKeyValuemap);
            } else {
                jedisKeyMap.get(jedis).put(keyValue.getKey(), keyValue.getValue());
            }
        }

        for (Map.Entry<Jedis, Map<byte[], byte[]>> jedisKeys : jedisKeyMap.entrySet()) {
            jedisKeys.getKey().mset(RedisSupport.convertToKeyValues(jedisKeys.getValue()));
        }
    }
}
