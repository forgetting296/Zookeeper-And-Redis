package com.shusaku.study.zk.publishsubscribe.config;

import com.shusaku.study.zk.ZkClient;
import com.shusaku.study.zk.util.JsonUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-25 15:03
 */
public class ConfigPublish {

    private String workerPath = "/test/config";

    CuratorFramework client = ZkClient.instance.getClient();

    public void publish(String s, ConfigItem i) {
        try {

            String zkPath = workerPath + "/" + s;
            byte[] payload = JsonUtil.Object2JsonBytes(i);

            client.create()
                    .creatingParentsIfNeeded()
                    .withProtection()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(zkPath, payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
