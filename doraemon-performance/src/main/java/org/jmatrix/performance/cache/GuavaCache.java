package org.jmatrix.performance.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author jmatrix
 * @date 16/7/1
 */
public class GuavaCache {

    public static final Integer CONCURRENT_LEVEL = 64;

    public static <K, V> LoadingCache createCache(long maxSize, CacheLoader<K, V> cacheLoader) {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(CONCURRENT_LEVEL)
                .maximumSize(maxSize)
                .build(cacheLoader);
    }
}
