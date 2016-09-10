package org.jmatrix.cache;

import com.google.common.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jmatrix
 * @date 16/3/24
 */
public class JCache<K, V> extends CacheLoader<K, V> {

    private Logger logger = LoggerFactory.getLogger(JCache.class);

    private RemoteCache<K, V> remoteCache;

    @Override
    public V load(K k) throws Exception {
        V value = remoteCache.loadFromLevelCache(k);
        if (value == null) {
            logger.debug("key[{}] not exist in second cache.");
            value = remoteCache.loadFromDB(k);
        }
        return value;
    }

    protected V loadFromRemote(K k) {
        return null;
    }

}
