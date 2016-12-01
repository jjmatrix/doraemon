package org.jmatrix.cache.redis;

import org.jmatrix.cache.redis.shard.JedisShardConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public class RedisStructureTest {

    private RedisTestConfig redisTestConfig = new RedisTestConfig();

    private StringRedisTemplate stringRedisTemplate;

    private JedisShardConnectionFactory connectionFactory;

    @Before
    public void setUp() throws Exception {
        connectionFactory = redisTestConfig.createJedisConnectionFactory();
        connectionFactory.initialize();
        stringRedisTemplate = new StringRedisTemplate(connectionFactory);
    }

    @Test
    @Ignore
    public void testHmSetKey() {
        Map<String, String> hash = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            hash.put("key_" + i, "value_" + i);
        }
        stringRedisTemplate.hashOperations().hmSet("hashkey1", hash);
    }

    @Test
    public void testMultiSet() {
        Map<String, String> hash = new LinkedHashMap<>();
        for (int i = 0; i < 100; i++) {
            hash.put("mSetKey_" + i, "mSetValue_" + i);
        }
        stringRedisTemplate.valueOperations.multiSet(hash);
    }

    @After
    public void tearDown() throws Exception {
        connectionFactory.destroy();
    }
}
