package org.jmatrix.performance.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Ints;
import org.jmatrix.performance.cache.GetPutBenchmark;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jmatrix
 * @date 16/7/5
 */
@State(Scope.Thread)
@Fork(1)
public class LoadingCacheSingleThreadBenchmark {

    @Param({"1000", "2000"})
    int maxSize;

    @Param("5000")
    int distinctKeys;

    @Param("64")
    int segments;

    @Param("2.5")
    double concentration;

    Random random = new Random();

    LoadingCache<Long, Long> cache;

    int max;

    static AtomicLong requests = new AtomicLong(0);
    static AtomicLong misses = new AtomicLong(0);

    @Setup
    public void setUp() {
        max = Ints.checkedCast((long) Math.pow(distinctKeys, concentration));

        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(segments)
                .maximumSize(maxSize)
                .build(new CacheLoader<Long, Long>() {
                    @Override
                    public Long load(Long key) throws Exception {
                        return misses.incrementAndGet();
                    }
                });

        while (cache.getUnchecked(nextRandomKey()) < maxSize) ;

        requests.set(0);
        misses.set(0);
    }

    private long nextRandomKey() {
        int a = random.nextInt(max);
        return (long) Math.pow(a, 1.0 / concentration);
    }

    @org.openjdk.jmh.annotations.AuxCounters
    @State(Scope.Thread)
    public static class AdditionalCounters {
        public long hit() {
            return ((requests.get() - misses.get()) * 100) / requests.get();
        }
    }

    @Benchmark
    public long time(AdditionalCounters additionalCounters) {
        long dummy = 0;
        dummy = cache.getUnchecked(nextRandomKey());
        requests.addAndGet(1);
        return dummy;
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(GetPutBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .jvmArgs("-ea")
                .build();
        new Runner(options).run();
    }
}
