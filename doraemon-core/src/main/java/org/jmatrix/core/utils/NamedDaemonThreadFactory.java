package org.jmatrix.core.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jmatrix
 * @date 16/9/12
 */
public class NamedDaemonThreadFactory implements ThreadFactory {

    private final ThreadGroup threadGroup;

    private final AtomicLong threadIdx = new AtomicLong(1);

    private final String namePrefix;

    public NamedDaemonThreadFactory() {
        this("-default-named-pool-");
    }

    public NamedDaemonThreadFactory(String namePrefix) {
        SecurityManager s = System.getSecurityManager();
        this.threadGroup = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(threadGroup, r, this.namePrefix + threadIdx.getAndIncrement());
        thread.setDaemon(true);
        thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }
}
