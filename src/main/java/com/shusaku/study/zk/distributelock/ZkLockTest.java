package com.shusaku.study.zk.distributelock;

import com.shusaku.study.zk.ZkClient;
import com.shusaku.study.zk.concurrent.FutureTaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.Test;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-25 13:48
 */
@Slf4j
public class ZkLockTest {

    int count = 0;

    @Test
    public void testLock() throws InterruptedException {
        for(int i = 0;i < 10;i ++) {
            FutureTaskScheduler.add(() -> {
                ZkLock lock = new ZkLock();
                lock.lock();
                for(int j = 0;j < 10;j ++) {
                    count ++;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("count: {}", (count));
                lock.unLock();
            });
        }
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 不需要重复造轮子　　直接使用封装好的分布式锁工具对象就OK
     * @throws InterruptedException
     */
    @Test
    public void testZkMutex() throws InterruptedException {

        CuratorFramework client = ZkClient.instance.getClient();
        final InterProcessMutex zkMutex = new InterProcessMutex(client, "/mutex");
        for(int i = 0;i < 10;i ++) {
            FutureTaskScheduler.add(() -> {
                try {
                    zkMutex.acquire();
                    for(int j = 0;j < 10;j ++) {
                        count ++;
                    }
                    Thread.sleep(1000);
                    log.info("count: {}", count);
                    zkMutex.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(Integer.MAX_VALUE);
    }


}
