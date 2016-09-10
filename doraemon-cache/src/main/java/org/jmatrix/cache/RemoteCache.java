package org.jmatrix.cache;

/**
 * @author jmatrix
 * @date 16/3/24
 */
public interface RemoteCache<K, V> {

    V loadFromLevelCache(K k);

    V loadFromDB(K k);

}
