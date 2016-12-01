package org.jmatrix.cache.redis;

import org.jmatrix.cache.redis.shard.JedisShardConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public class RedisTestConfig {

    public JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMinIdle(5);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setMaxWaitMillis(1000);
        jedisPoolConfig.setBlockWhenExhausted(true);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(60000);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        jedisPoolConfig.setNumTestsPerEvictionRun(-1);
        return jedisPoolConfig;
    }

    public JedisShardConnectionFactory createJedisConnectionFactory() {
        JedisShardConnectionFactory jedisConnectionFactory = new JedisShardConnectionFactory();
        jedisConnectionFactory.setJedisPoolConfig(createJedisPoolConfig());
        jedisConnectionFactory.setTimeout(200);
        jedisConnectionFactory.setShardList("localhost:6379:host1,localhost:7379:host2");
        return jedisConnectionFactory;
    }
}
