package org.jmatrix.cache.redis;

/**
 * @author jmatrix
 * @date 16/11/24
 */
public interface Callback<V> {
    V doInRedis(Connection connection);
}
