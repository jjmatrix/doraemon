package org.jmatrix.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author jmatrix
 * @date 16/8/5
 */
public class GuavaCacheTest {

    private LoadingCache<String, CacheEntry> loadingCache;

    private String testKey = "testKey";

    private Map<String, CacheEntry> valueMap = new ConcurrentHashMap<>();

    @Before
    public void setUp() throws Exception {
        valueMap.put(testKey, new CacheEntry("base", "base"));

        loadingCache = CacheBuilder.newBuilder()
                .concurrencyLevel(64)
                .maximumSize(1000)
                .expireAfterWrite(100, TimeUnit.MILLISECONDS)
                .recordStats()
                .build(new CacheLoader<String, CacheEntry>() {
                    @Override
                    public CacheEntry load(String key) throws Exception {
                        if (valueMap.get(key) != null)
                            return valueMap.get(key);

                        return new CacheEntry("new", "new");
                    }
                });
    }

    @Test
    public void testLoadCacheObjectIsDeepCopy() throws Exception {
        AtomicLong threadIdx = new AtomicLong(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor((runnable) ->
                new Thread(runnable, "cacheLoader-" + threadIdx.getAndIncrement())
        );

        CacheEntry baseCacheValue = loadingCache.get(testKey);

        CacheEntry compCacheValue = loadingCache.get(testKey);
        compCacheValue.setKey("comp");
        assertThat(baseCacheValue, is(compCacheValue));

        executorService.submit(() -> {
            try {
                valueMap.clear();
                Thread.sleep(200L);
                loadingCache.get(testKey);
            } catch (Exception e) {

            }
        });

        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);

        CacheEntry reloadCacheValue = loadingCache.get(testKey);

        assertThat(baseCacheValue, not(reloadCacheValue));

    }

    static class CacheEntry {

        private String key;

        private String value;

        public CacheEntry() {
        }

        public CacheEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheEntry that = (CacheEntry) o;

            if (!key.equals(that.key)) return false;
            return value.equals(that.value);

        }

        @Override
        public int hashCode() {
            int result = key.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }
    }
}
