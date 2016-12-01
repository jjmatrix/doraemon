package org.jmatrix.cache.redis;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public interface Serializer<T> {

    byte[] serialize(T key);

    T unSerialize(byte[] key);
}
