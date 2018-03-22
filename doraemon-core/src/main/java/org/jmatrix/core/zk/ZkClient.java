package org.jmatrix.core.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by matrix on 2018/3/2.
 */
public class ZkClient {
    private CuratorFramework client;

    public ZkClient() {
        client = CuratorFrameworkFactory.newClient("", 100, 100, new ExponentialBackoffRetry(100, 10));
    }
}
