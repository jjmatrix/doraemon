package org.jmatrix.performance.cache;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.openjdk.jmh.annotations.*;

/**
 * @author jmatrix
 * @date 16/7/1
 */
@State(Scope.Thread)
public class EvictionBenchmark {

    private LoadingCache loadingCache;

    @Param({"0", "100", "10000", "1000000"})
    int size;

    @State(Scope.Thread)
    public static class ThreadState {
        int key = 0;
    }

    @Setup
    public void setup() {
        loadingCache = GuavaCache.createCache(size, new CacheLoader<Integer, Boolean>() {
            @Override
            public Boolean load(Integer key) throws Exception {
                return Boolean.TRUE;
            }
        });
        for (int i = 0; i < size; i++) {
            loadingCache.put(Integer.MIN_VALUE + i, Boolean.TRUE);
        }
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        loadingCache.cleanUp();
    }

    @Benchmark
    public void evict(ThreadState threadState) {
        loadingCache.put(threadState.key++, Boolean.TRUE);
    }
}
