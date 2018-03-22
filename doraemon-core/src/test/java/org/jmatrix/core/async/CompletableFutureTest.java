package org.jmatrix.core.async;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jmatrix
 * @date 16/9/10
 */
public class CompletableFutureTest {

    private ExecutorService executorService;

    @Before
    public void setUp() throws Exception {
        executorService = Executors.newCachedThreadPool();
    }

    @After
    public void tearDown() throws Exception {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    @Test
    public void testCompletableFutureReturnNull() throws Exception {
        CompletableFuture completableFuture = new CompletableFuture();

    }
}
