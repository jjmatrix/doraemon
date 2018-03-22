package org.jmatrix.performance.cache;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * @author jmatrix
 * @date 16/7/1
 */
@State(Scope.Thread)
public class GetPutBenchmark {

    private final static int SIZE = (2 << 14);
    private final static int MASK = SIZE - 1;
    private final static int ITEMS = SIZE / 3;

    private LoadingCache<Integer, Boolean> loadingCache;

    Integer[] ints;

    @State(Scope.Thread)
    public static class ThreadState {
        private final static Random random = new Random();
        int index = random.nextInt();
    }

    @Setup
    public void setup() {
        ints = new Integer[SIZE];
        loadingCache = GuavaCache.createCache(2 * SIZE, new CacheLoader<Integer, Boolean>() {
            @Override
            public Boolean load(Integer key) throws Exception {
                return Boolean.TRUE;
            }
        });

        //fill
        for (int i = 0; i < 2 * SIZE; i++) {
            loadingCache.put(i, Boolean.TRUE);
        }
        loadingCache.cleanUp();

//        NumberGenerator generator = new ScrambledZipfianGenerator(ITEMS);
//        for (int i = 0; i < SIZE; i++) {
//            ints[i] = generator.nextValue().intValue();
//            loadingCache.put(ints[i], Boolean.TRUE);
//        }

    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        loadingCache.cleanUp();
    }

    @Benchmark
    @Group("read_only")
    @GroupThreads(8)
    public Boolean readOnly(ThreadState threadState) throws ExecutionException {
        return loadingCache.get(ints[threadState.index++ & MASK]);
    }

    @Benchmark
    @Group("write_only")
    @GroupThreads(8)
    public void writeOnly(ThreadState threadState) {
        loadingCache.put(ints[threadState.index++ & MASK], Boolean.TRUE);
    }

    @Benchmark
    @Group("readwrite")
    @GroupThreads(6)
    public Boolean readWrite_get(ThreadState threadState) throws ExecutionException {
        return loadingCache.get(ints[threadState.index++ & MASK]);
    }

    @Benchmark
    @Group("readwrite")
    @GroupThreads(2)
    public void readwrite_set(ThreadState threadState) {
        loadingCache.put(ints[threadState.index & MASK], Boolean.TRUE);
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

