package org.jmatrix.cache.redis.cluster;

import org.jmatrix.cache.redis.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author jmatrix
 * @date 16/12/1
 */
public class JedisClusterConnection implements Connection {

    private Logger logger = LoggerFactory.getLogger(JedisClusterConnection.class);

    private JedisCluster jedisCluster;

    @Override
    public void close() {
        try {
            jedisCluster.close();
        } catch (IOException e) {

        }
    }

    @Override
    public void set(byte[] key, byte[] value) {

    }

    @Override
    public byte[] get(byte[] key) {
        return new byte[0];
    }

    @Override
    public void setEx(byte[] key, byte[] value, int timeInSecond) {

    }

    @Override
    public void pSetEx(String key, String value, int timeInMs) {

    }

    @Override
    public Boolean hSet(byte[] key, byte[] field, byte[] value) {
        return null;
    }

    @Override
    public Boolean hmSet(String key, Map<String, String> hash) {
        return null;
    }

    @Override
    public List<byte[]> hmGet(byte[] key, byte[]... field) {
        return null;
    }

    @Override
    public List<byte[]> mGet(byte[]... keys) {
        return null;
    }

    @Override
    public void mSet(Map<byte[], byte[]> keyValues) {

    }
}
