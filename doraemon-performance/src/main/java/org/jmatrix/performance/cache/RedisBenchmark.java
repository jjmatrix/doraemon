package org.jmatrix.performance.cache;

import org.jmatrix.cache.redis.shard.JedisShardConnectionFactory;
import org.jmatrix.cache.redis.StringRedisTemplate;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collections;
import java.util.List;

/**
 * @author jmatrix
 * @date 16/12/1
 */
@State(Scope.Thread)
public class RedisBenchmark {

    private RedisTestConfig redisTestConfig = new RedisTestConfig();

    private StringRedisTemplate stringRedisTemplate;

    private JedisShardConnectionFactory connectionFactory;

    @Setup
    public void setup() throws Exception {
        connectionFactory = redisTestConfig.createJedisConnectionFactory();
        connectionFactory.init();
        stringRedisTemplate = new StringRedisTemplate(connectionFactory);
    }

    @Benchmark
    public List<String> nhmGet() {
        try {
            return stringRedisTemplate.hashOperations().hmGet("hashkey1", new String[]{"key_1", "key_2", "key_3", "key_4", "key_7", "key_10", "key_8", "key_9"});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Benchmark
    public List<String> multiGet() {
        String[] keys = new String[]{"mSetKey_1", "mSetKey_2", "mSetKey_3", "mSetKey_4", "mSetKey_5", "mSetKey_6", "mSetKey_7", "mSetKey_8", "mSetKey_9", "mSetKey_10"};
        try {
            return stringRedisTemplate.valueOperations().multiGet(keys);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @TearDown
    public void tearDown() throws Exception {
        connectionFactory.destroy();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(RedisBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(40)
                .forks(1)
                .threads(150)
                .syncIterations(false)
                .jvmArgs("-ea")
                .build();
        new Runner(options).run();
    }
}
