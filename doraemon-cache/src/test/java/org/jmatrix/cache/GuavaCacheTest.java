package org.jmatrix.cache;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.jodah.concurrentunit.Waiter;
import org.jmatrix.core.utils.NamedDaemonThreadFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author jmatrix
 * @date 16/8/5
 */
public class GuavaCacheTest {

    private String testKey = "testKey";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testLoadCacheObjectIsDeepCopy() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor(new NamedDaemonThreadFactory("-cacheLoad-"));

        Map<String, CacheEntry> valueMap = new ConcurrentHashMap<>();
        valueMap.put(testKey, new CacheEntry("base", "base"));
        LoadingCache<String, CacheEntry> loadingCache = CacheFactory.Instance().createGuavaCache(new CacheLoader<String, CacheEntry>() {
            @Override
            public CacheEntry load(String key) throws Exception {
                if (valueMap.get(key) != null)
                    return valueMap.get(key);

                return new CacheEntry("new", "new");
            }
        });

        CacheEntry baseCacheValue = loadingCache.get(testKey);

        CacheEntry compCacheValue = loadingCache.get(testKey);
        compCacheValue.setKey("comp");
        assertThat(baseCacheValue, is(compCacheValue));

        executorService.submit(() -> {
            try {
                valueMap.clear();
                Thread.sleep(2000L);
                loadingCache.get(testKey);
            } catch (Exception e) {

            }
        });

        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);

        CacheEntry reloadCacheValue = loadingCache.get(testKey);

        assertThat(baseCacheValue, not(reloadCacheValue));

    }

    @Test
    public void testAsyncLoad_exclusive() throws Exception {

        Waiter waiter = new Waiter();

        ExecutorService executorService = Executors.newFixedThreadPool(100, new NamedDaemonThreadFactory("-cache-exclusive-"));
        AtomicLong loadCount = new AtomicLong(1);
        LoadingCache<String, Long> loadingCache = CacheFactory.Instance().createGuavaCache(new CacheLoader<String, Long>() {
            @Override
            public Long load(String key) throws Exception {
                LockSupport.parkNanos(1000 * 1000L);
                return loadCount.getAndIncrement();
            }
        });

        CountDownLatch latch = new CountDownLatch(2);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    latch.countDown();
                    latch.await();

                    loadingCache.get("test");
                    System.out.println(Thread.currentThread().getName());
                } catch (InterruptedException | ExecutionException e) {

                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(120, TimeUnit.SECONDS);

        assertThat(loadCount.get(), is(2L));

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
