package com.shusaku.study.zk.distributelock;

import com.shusaku.study.zk.ZkClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 *              Zookeeper的节点是连续的　顺序递增　可以创建临时顺序节点　保证锁的公平获取　　节点下再创建子节点　可重入
 *              节点监听机制可以保证锁的公平获取　　而且即使节点挂掉　　也会被正常删除　　不影响锁的获取
 *              可以有效地避免羊群效应　　
 * @author: Shusaku
 * @create: 2020-03-24 16:44
 */
@Slf4j
public class ZkLock implements Lock {

    //Zklock的节点链接
    private static final String ZK_PATH = "/test/lock";
    private static final String LOCK_PREFIX = ZK_PATH + "/";
    private static final long WAIT_TIME = 1000;

    //ZK客户端
    CuratorFramework client = null;

    private String locked_short_path = null;
    private String locked_path = null;
    private String prior_path = null;
    private Thread thread;
    final AtomicInteger lockCount = new AtomicInteger(0);

    public ZkLock() {
        ZkClient.instance.init();
        if(!ZkClient.instance.isNodeExist(ZK_PATH)) {
            ZkClient.instance.createNode(ZK_PATH, null);
        }
        client = ZkClient.instance.getClient();
    }

    @Override
    public boolean lock() {

        //保证可重入　
        synchronized (this) {
            //锁定节点数量为0 　将锁定数量 + 1 当前线程获取锁
            if(lockCount.get() == 0) {
                thread = Thread.currentThread();
                lockCount.incrementAndGet();
            }
            //父节点已经获取锁
            else {
                //当前线程未获取锁　　返回false
                if(!thread.equals(Thread.currentThread())) {
                    return false;
                }
                //当前线程获取　锁定数量 + 1
                lockCount.incrementAndGet();
                return true;
            }
        }

        //确保公平
        try {
            boolean locked = false;
            //尝试获取锁
            locked = tryLock();

            if(locked) {
                return true;
            }

            //如果没获取到锁　　自旋　　直到获取到锁
            while(!locked) {

                //等待　　直到监听到上一个节点删除　或者　超时
                await();

                //获取等待的子节点列表
                List<String> waiters = getWaiters();

                //检查　自己的节点是不是等待节点中的第一个节点　如果是　获取锁　如果不是　获取上一个节点的路径　监听删除事件
                if(checkLocked(waiters)) {
                    locked = true;
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            //解除锁定　　如果锁定数量减少为0　将节点删除　　释放掉以后一边下一个节点可以获取到锁
            unLock();
        }
        return false;
    }

    private boolean checkLocked(List<String> waiters) {

        Collections.sort(waiters);

        if(locked_short_path.equals(waiters.get(0))) {
            return true;
        }

        return false;
    }

    private List<String> getWaiters() {

        List<String> children = null;
        try {
            children = client.getChildren().forPath(ZK_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return children;
    }

    private void await() throws Exception {

        //校验当前节点的前一个节点
        if(null == prior_path) {
            throw new Exception("prior_path error");
        }

        try {

            boolean locked = false;
            final CountDownLatch latch = new CountDownLatch(1);

            //前一个节点路径不为空　　监听前一个节点的变动　　因为这里不涉及节点内容的修改　　发生变动就是unLock时的节点删除
            Watcher watcher = new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    log.info("[watchedEvent] 节点被删除　：　" + watchedEvent);
                    latch.countDown();
                }
            };

            //因为节点只会发生一次删除　　只需要一次监听即可　Watcher完全可以满足  也可以使用TreeCache
            /*TreeCache treeCache = new TreeCache(client, prior_path);
            TreeCacheListener treeCacheListener = new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                    ChildData data = treeCacheEvent.getData();
                    if(null != data) {
                        TreeCacheEvent.Type type = treeCacheEvent.getType();
                        switch (type){
                            case NODE_REMOVED:
                                log.info("[TreeCache] 节点删除，　data = {}, path = {}", data.getData(), data.getPath());
                                latch.countDown();
                                break;
                        }
                    }
                }
            };
            treeCache.getListenable().addListener(treeCacheListener);
            treeCache.start();*/


            client.getData().usingWatcher(watcher).forPath(prior_path);

            latch.await(WAIT_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //尝试获取锁
    private boolean tryLock() throws Exception {

        //创建临时节点 返回创建的节点路径
        locked_path = ZkClient.instance.createEphemeralSeqNode(LOCK_PREFIX);
        //获取所有等待节点
        List<String> waiters = getWaiters();

        //截取路径的编号
        locked_short_path = getShortPath(locked_path);

        //判断locked_short_path　是否为最小编号的节点
        if(checkLocked(waiters)) {
            return true;
        }

        //当前节点不是最小编号的　拿到前一个节点的路径　　监听删除事件
        int index = Collections.binarySearch(waiters, locked_short_path);

        if(index < 0) {
            throw new Exception("节点不存在： " + locked_short_path);
        }
        prior_path = ZK_PATH + waiters.get(index - 1);

        return false;
    }

    private String getShortPath(String locked_path) {
        int index = locked_path.lastIndexOf(ZK_PATH + "/");
        if(index >= 0) {
            index += ZK_PATH.length() + 1;
            return locked_path.length() >= index ? locked_path.substring(index) : "";
        }
        return null;
    }

    @Override
    public boolean unLock() {

        if(!thread.equals(Thread.currentThread())) {
            return false;
        }

        int count = lockCount.decrementAndGet();

        if(count < 0) {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock :" + locked_path);
        }

        if(count != 0) {
            return true;
        }

        //已经没有节点持有锁了　　删除节点
        try{
            if(ZkClient.instance.isNodeExist(locked_path)) {
                ZkClient.instance.deleteNode(locked_path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
