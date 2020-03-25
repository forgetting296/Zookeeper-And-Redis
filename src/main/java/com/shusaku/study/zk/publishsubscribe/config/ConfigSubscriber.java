package com.shusaku.study.zk.publishsubscribe.config;

import com.shusaku.study.zk.ZkClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-25 15:04
 */
@Slf4j
public class ConfigSubscriber {

    private String workerPath = "/test/config";

    CuratorFramework client = ZkClient.instance.getClient();

    public void init() {

        try {
            PathChildrenCache childrenCache = new PathChildrenCache(client, workerPath, true);
            PathChildrenCacheListener listener = new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                    ChildData data = pathChildrenCacheEvent.getData();
                    if(data != null) {
                        PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                        switch (type){
                            case CHILD_ADDED:
                                setConfig(data);
                                break;
                            case CHILD_UPDATED:
                                updateConfig(data);
                                break;
                            case CHILD_REMOVED:
                                removeConfig(data);
                                break;
                            default:
                                break;
                        }
                    }
                }
            };
            childrenCache.getListenable().addListener(listener);
            childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            log.error("path监听失败:{}", workerPath);
        }
    }

    private void setConfig(ChildData data){
        log.info("子节点新增: path = {}, data = {}", data.getPath(), data.getData());
        ConfigManager.instance.setType(data.getPath(), data.getData());
    }

    private void updateConfig(ChildData data){
        log.info("子节点修改: path = {}, data = {}", data.getPath(), data.getData());
        ConfigManager.instance.setType(data.getPath(), data.getData());
    }

    private void removeConfig(ChildData data){
        log.info("子节点删除: path = {}, data = {}", data.getPath(), data.getData());
        ConfigManager.instance.removeType(data.getPath());
    }

}
