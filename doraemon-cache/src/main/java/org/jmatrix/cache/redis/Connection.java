package org.jmatrix.cache.redis;

import java.util.List;
import java.util.Map;

/**
 * @author jmatrix
 * @date 16/9/25
 */
public interface Connection {
    void close();

    void set(byte[] key, byte[] value);

    byte[] get(byte[] key);

    void setEx(byte[] key, byte[] value, int timeInSecond);

    void pSetEx(String key, String value, int timeInMs);

    Boolean hSet(byte[] key, byte[] field, byte[] value);

    Boolean hmSet(String key, Map<String, String> hash);

    List<byte[]> hmGet(byte[] key, byte[]... field);

    List<byte[]> mGet(byte[]... keys);

    void mSet(Map<byte[], byte[]> keyValues);
}
