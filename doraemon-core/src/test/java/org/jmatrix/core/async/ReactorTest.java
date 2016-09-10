package org.jmatrix.core.async;

import net.jodah.concurrentunit.Waiter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * @author jmatrix
 * @date 16/8/23
 */
public class ReactorTest {

    private static AtomicLong threadIdx = new AtomicLong(1);

    private ExecutorService executorService;

    @Before
    public void setUp() throws Exception {
        executorService = Executors.newFixedThreadPool(5, (runnable) -> {
            SecurityManager s = System.getSecurityManager();
            ThreadGroup g = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            return new Thread(g, runnable, "schedules_" + threadIdx.getAndIncrement());
        });
    }

    @After
    public void tearDown() throws Exception {
        if (executorService != null && !executorService.isShutdown())
            executorService.shutdown();
    }

    @Test
    public void testMonoSchedulerOnDiff() throws Exception {
        Waiter waiter = new Waiter();
        String mainThreadName = Thread.currentThread().getName();
        Mono<String> testMono = Mono.fromSupplier(() -> {
            String scheduleName = Thread.currentThread().getName();
            waiter.assertTrue(!scheduleName.equals(mainThreadName));
            System.out.println("1:" + scheduleName);
            return scheduleName;
        }).subscribeOn(Schedulers.fromExecutor(executorService));

        testMono.doOnNext(threadName -> {

            waiter.assertTrue(threadName.equals(Thread.currentThread().getName()));
        }).subscribeOn(Schedulers.fromExecutor(executorService))
                .doOnNext(threadName -> {
                    //subscribe on same scheduler, use same thread
                    waiter.assertTrue(threadName.equals(Thread.currentThread().getName()));
                });

        Mono<String> testMonoOther = Mono.fromSupplier(() -> {
            String schedulerName = Thread.currentThread().getName();
            waiter.assertTrue(!schedulerName.equals(mainThreadName));
            return schedulerName;
        }).subscribeOn(Schedulers.fromExecutor(executorService))
                .doOnNext(threadName -> {
                    System.out.println("2:" + threadName);
                    try {
                        Thread.sleep(300L);
                    } catch (Exception e) {

                    }
                    waiter.assertTrue(threadName.equals(Thread.currentThread().getName()));
                });


        Mono<String> compositeMono = testMono.and(testMonoOther).map(tuple -> {
            //It will use the same thread if composite two mono that subscribe on same schedulers;
            System.out.println("3:" + Thread.currentThread().getName());
            waiter.assertTrue(!tuple.getT1().equals(tuple.getT2()));
            return tuple.getT1();
        }).doOnNext(threadName -> {
            System.out.println("4:" + threadName);
            waiter.assertTrue(!threadName.equals(Thread.currentThread().getName()));
        });

        compositeMono.block();
    }

    @Test
    public void testMonoDoNext() throws Exception {
        Waiter waiter = new Waiter();
        String mainThreadName = Thread.currentThread().getName();
        Mono<Long> testMono = Mono.fromSupplier(() -> {
            long baseTime = System.currentTimeMillis();
            waiter.assertTrue(!mainThreadName.equals(Thread.currentThread().getName()));
            waiter.resume();
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
            }
            return baseTime;
        }).subscribeOn(Schedulers.fromExecutor(executorService))
                .doOnNext((baseTime -> {
                    waiter.assertTrue(!mainThreadName.equals(Thread.currentThread().getName()));
                    waiter.assertTrue((System.currentTimeMillis() - baseTime) > (200));
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {
                    }
                }))
                .doOnNext((baseTime -> {
                    waiter.assertTrue(!mainThreadName.equals(Thread.currentThread().getName()));
                    waiter.assertTrue((System.currentTimeMillis() - baseTime) > 400);
                    waiter.resume();
                })).otherwise(ex -> {
                    ex.printStackTrace();
                    return null;
                });

        testMono.block();
    }

    @Test
    public void testMonoThreadSwitch() throws Exception {
        Waiter waiter = new Waiter();
        String mainThreadName = Thread.currentThread().getName();
        Mono<Long> testMono = Mono.fromSupplier(() -> {
            waiter.assertTrue(!mainThreadName.equals(Thread.currentThread().getName()));
            waiter.resume();
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
            }
            return System.currentTimeMillis();
        }).subscribeOn(Schedulers.fromExecutor(executorService)).doOnNext(baseTime -> {
            waiter.assertTrue(!mainThreadName.equals(Thread.currentThread().getName()));
            waiter.resume();
        }).doOnNext(time -> {
            waiter.assertTrue(!mainThreadName.equals(Thread.currentThread().getName()));
            waiter.resume();
        });

        waiter.assertTrue(Thread.currentThread().getName().equals(mainThreadName));

        testMono.block();
    }

    @Test
    public void testMultiMonoThreadSwitch() throws Exception {
        Waiter waiter = new Waiter();
        String mainThreadName = Thread.currentThread().getName();
        Mono<String> firstMono = Mono.fromSupplier(() -> {
            LockSupport.parkNanos(1000 * 500);
            waiter.assertTrue(!mainThreadName.equals(Thread.currentThread().getName()));
            waiter.resume();
            return Thread.currentThread().getName();
        }).subscribeOn(Schedulers.fromExecutor(executorService))
                .doOnNext(threadName -> {
                    waiter.assertTrue(!mainThreadName.equals(Thread.currentThread().getName()));
                    waiter.assertTrue(threadName.equals(Thread.currentThread().getName()));
                });

        Mono<String> secondMono = Mono.fromSupplier(() -> {
            LockSupport.parkNanos(1000 * 300);
            waiter.assertTrue(!mainThreadName.equals(Thread.currentThread().getName()));
            waiter.resume();
            return Thread.currentThread().getName();
        }).subscribeOn(Schedulers.fromExecutor(executorService))
                .doOnNext((threadName -> {
                    waiter.assertTrue(!mainThreadName.equals(Thread.currentThread().getName()));
                    waiter.assertTrue(threadName.equals(Thread.currentThread().getName()));
                }));

        System.out.println("running in main thread.");
        waiter.assertTrue(mainThreadName.equals(Thread.currentThread().getName()));
        waiter.resume();

        Mono<String> mergeMono = Mono.when(secondMono, firstMono).map(tuple2 -> {
            System.out.println(Thread.currentThread().getName() + "_" + tuple2.getT1() + "_" + tuple2.getT2());
            /**
             * failed random, choose which thread is random.
             */
            waiter.assertTrue(Thread.currentThread().getName().equals(tuple2.getT1()));
            waiter.resume();
            return "end";
        }).doOnNext(tips -> System.out.println(tips));

        mergeMono.block();
    }
}