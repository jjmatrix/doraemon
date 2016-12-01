package org.jmatrix.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

/**
 * @author jmatrix
 * @date 16/9/12
 */
public class CacheFactory {

    private final static CacheFactory instance = new CacheFactory();

    private CacheFactory() {
    }

    public static CacheFactory Instance() {
        return instance;
    }

    public <K, V> LoadingCache createGuavaCache(CacheLoader<K, V> cacheLoader) {
        return CacheBuilder.newBuilder()
                .concurrencyLevel(64)
                .expireAfterWrite(1000, TimeUnit.MILLISECONDS)
                .recordStats()
                .build(cacheLoader);
    }
}
