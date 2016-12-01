package org.jmatrix.cache.redis.shard;

import org.jmatrix.cache.redis.Connection;
import org.jmatrix.cache.redis.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author jmatrix
 * @date 16/9/25
 */
public class JedisShardConnectionFactory implements ConnectionFactory {

    private Logger logger = LoggerFactory.getLogger(JedisShardConnectionFactory.class);

    private ShardedJedisPool shardedJedisPool;

    private JedisPoolConfig jedisPoolConfig;

    /**
     * format: host:ip[:name],host:ip[:name]...
     */
    private String shardList;

    /**
     * connectTimeout & readTimeout, if less or equal 0, then use Jedis default timeout that is 2000 ms.
     */
    private int timeout;

    /**
     * Reference to redis document about hash tag. when it is equal to null, use key directly.
     */
    private Pattern keyTagPattern;

    public JedisShardConnectionFactory() {
    }

    public JedisShardConnectionFactory(JedisPoolConfig jedisPoolConfig, String shardList) {
        this(jedisPoolConfig, shardList, 0, null);
    }

    public JedisShardConnectionFactory(JedisPoolConfig jedisPoolConfig, String shardList, int timeout) {
        this(jedisPoolConfig, shardList, timeout, null);
    }

    public JedisShardConnectionFactory(JedisPoolConfig jedisPoolConfig, String shardList, Pattern keyTagPattern) {
        this(jedisPoolConfig, shardList, 0, keyTagPattern);
    }

    public JedisShardConnectionFactory(JedisPoolConfig jedisPoolConfig, String shardList, int timeout, Pattern keyTagPattern) {
        this.jedisPoolConfig = jedisPoolConfig;
        this.shardList = shardList;
        this.timeout = timeout;
        this.keyTagPattern = keyTagPattern;
    }

    @Override
    public Connection getConnection() {
        return new JedisShardedConnection(shardedJedisPool.getResource());
    }

    @Override
    public void destroy() throws Exception {
        if (shardedJedisPool != null) {
            try {
                shardedJedisPool.destroy();
            } catch (Exception e) {
                logger.error("cannot properly close redis pool");
            }
        }
    }

    @Override
    public void initialize() throws Exception {
        if (shardedJedisPool == null) {
            if (shardList == null || shardList.length() == 0)
                throw new IllegalStateException("shardList must be set.");
            if (jedisPoolConfig == null) {
                throw new IllegalStateException("jedisPoolConfig must be set.");
            }
        }

        if (shardedJedisPool == null) {
            shardedJedisPool = createJedisPool();
        }
    }

    private ShardedJedisPool createJedisPool() {
        List<JedisShardInfo> shardInfos = new ArrayList<>();
        String[] shardInfoArr = shardList.split(",");
        if (shardInfoArr != null && shardInfoArr.length > 0) {
            for (String shardInfo : shardInfoArr) {
                String[] shardArr = shardInfo.split(":");
                if (shardArr != null && shardArr.length >= 2) {
                    if (timeout > 0) {
                        shardInfos.add(new JedisShardInfo(shardArr[0], Integer.parseInt(shardArr[1]), timeout, shardArr.length > 2 ? shardArr[2] : shardInfo));
                    } else {
                        shardInfos.add(new JedisShardInfo(shardArr[0], Integer.parseInt(shardArr[1]), shardArr.length > 2 ? shardArr[2] : shardInfo));
                    }
                }
            }
        }

        if (shardInfos.isEmpty()) {
            throw new IllegalStateException("please make sure shardList is config properly!");
        }

        return new ShardedJedisPool(jedisPoolConfig, shardInfos, keyTagPattern);
    }

    public void setJedisPoolConfig(JedisPoolConfig jedisPoolConfig) {
        this.jedisPoolConfig = jedisPoolConfig;
    }

    public void setShardList(String shardList) {
        this.shardList = shardList;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setKeyTagPattern(Pattern keyTagPattern) {
        this.keyTagPattern = keyTagPattern;
    }
}
