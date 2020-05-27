package com.shusaku.study.zk.publishsubscribe;

import com.shusaku.study.zk.ZkClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.Test;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-24 10:51
 */
@Slf4j
@Data
public class ZkWatcherDemo {

    private String woekPath = "/test/listener/remoteNode";
    private String subWorkerPath = "/test/listener/remoteNode/id-";

    /**
     * Watcher只能监听一次变化　　如果要继续监听　　需要重新usingWatcher()
     */
    @Test
    public void testWatcher() {

        CuratorFramework client = ZkClient.instance.getClient();

        //检查节点有没有被创建
        boolean isExist = ZkClient.instance.isNodeExist(woekPath);

        //如果没有创建　　先创建工作节点
        if(!isExist) {
            ZkClient.instance.createNode(woekPath,null);
        }

        try {
            Watcher watcher = new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    System.out.println("监听到新的变化　watchedEvent = " + watchedEvent);
                }
            };

            byte[] content = client.getData()
                    .usingWatcher(watcher)
                    .forPath(woekPath);
            log.info("监听节点内容： {}", new String(content));

            //第一次修改内容
            client.setData()
                    .forPath(woekPath, "第一次修改".getBytes());

            //第二次修改内容
            client.setData()
                    .forPath(woekPath, "第二次修改".getBytes());

            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testNodeCache() {

        CuratorFramework client = ZkClient.instance.getClient();

        boolean isExist = ZkClient.instance.isNodeExist(woekPath);

        if (!isExist) {
            ZkClient.instance.createNode(woekPath, null);
        }

        try {

            NodeCache nodeCache = new NodeCache(client, woekPath, false);
            NodeCacheListener listener = new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    ChildData childData = nodeCache.getCurrentData();
                    log.info("ZNode节点状态改变，　path= {}", childData.getPath());
                    log.info("ZNode节点状态改变，　data= {}", new String(childData.getData(),"UTF-8"));
                    log.info("ZNode节点状态改变，　stat= {}", childData.getStat());
                }
            };
            nodeCache.getListenable().addListener(listener);
            nodeCache.start();

            //第一次变更节点数据
            client.setData().forPath(woekPath, "第一次变更数据".getBytes());
            Thread.sleep(1000);

            //第二次变更节点数据
            client.setData().forPath(woekPath, "第二次变更数据".getBytes());
            Thread.sleep(1000);

            //第三次变更节点数据
            client.setData().forPath(woekPath, "第三次变更数据".getBytes());
            Thread.sleep(1000);

            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            log.error("创建节点监听失败, path = {}", woekPath);
        }

    }

    @Test
    public void testPathChildrenCache() {

        CuratorFramework client = ZkClient.instance.getClient();

        boolean isExist = ZkClient.instance.isNodeExist(woekPath);

        if(!isExist) {
            ZkClient.instance.createNode(woekPath, null);
        }

        try {
            PathChildrenCache childrenCache = new PathChildrenCache(client, woekPath, true);
            PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    try {

                        ChildData childData = pathChildrenCacheEvent.getData();
                        switch (pathChildrenCacheEvent.getType()) {
                            case CHILD_ADDED:
                                log.info("新增子节点，　path = {}, data = {}", childData.getPath(), new String(childData.getData(), "UTF-8"));
                                break;
                            case CHILD_UPDATED:
                                log.info("更新子节点，　path = {}, data = {}", childData.getPath(), new String(childData.getData(), "UTF-8"));
                                break;
                            case CHILD_REMOVED:
                                log.info("删除子节点，　path = {}, data = {}", childData.getPath(), new String(childData.getData(), "UTF-8"));
                                break;
                                default:
                                    break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            childrenCache.getListenable().addListener(childrenCacheListener);
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            Thread.sleep(1000);

            //新增节点
            for(int i = 0;i < 3;i ++) {
                ZkClient.instance.createNode(subWorkerPath + i, null);
            }
            Thread.sleep(1000);

            //删除节点
            for(int i = 0;i < 3;i ++) {
                ZkClient.instance.deleteNode(subWorkerPath + i);
            }
            Thread.sleep(1000);

        } catch (Exception e) {
            log.error("PathCache 监听失败，　path = {}", woekPath);
        }
    }

    @Test
    public void testTreeCache() {

        CuratorFramework client = ZkClient.instance.getClient();

        boolean isExist = ZkClient.instance.isNodeExist(woekPath);

        if(!isExist) {
            ZkClient.instance.createNode(woekPath, null);
        }

        try {

            TreeCache treeCache = new TreeCache(client, woekPath);

            TreeCacheListener listener = new TreeCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {

                    try {
                        ChildData data = treeCacheEvent.getData();
                        if(data == null) {
                            log.info("数据为空");
                            return;
                        }
                        TreeCacheEvent.Type type = treeCacheEvent.getType();
                        switch (type) {
                            case NODE_ADDED:
                                log.info("[TreeCache] 节点增加，　path = {}, data = {}", data.getPath(), data.getData());
                                break;
                            case NODE_UPDATED:
                                log.info("[TreeCache] 节点更新，　path = {}, data = {}", data.getPath(), data.getData());
                                break;
                            case NODE_REMOVED:
                                log.info("[TreeCache] 节点删除，　path = {}, data = {}", data.getPath(), data.getData());
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            treeCache.getListenable().addListener(listener);
            treeCache.start();
            Thread.sleep(1000);

            //新增节点
            for(int i = 0;i < 3;i ++) {
                ZkClient.instance.createNode(subWorkerPath + i, null);
            }
            Thread.sleep(1000);

            //删除节点
            for(int i = 0;i < 3;i ++) {
                ZkClient.instance.deleteNode(subWorkerPath + i);
            }
            Thread.sleep(1000);

            ZkClient.instance.deleteNode(woekPath);

            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            log.error("TreeNode　节点监听失败, path = {}", woekPath);
        }

    }

}
