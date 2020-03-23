package com.shusaku.study.zk.nameserver;

import com.shusaku.study.zk.ZkClient;
import com.shusaku.study.zk.util.JsonUtil;
import lombok.Data;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-23 11:32
 */
@Data
public class SnowflakeIdWoker {

    //ZK客户端
    CuratorFramework client = null;

    String pathPrefix = "/test/IDMaker/worker-";
    String pathRegistered = null;

    public static SnowflakeIdWoker instance = new SnowflakeIdWoker();

    private SnowflakeIdWoker() {
        client = ZkClient.instance.getClient();
        instance.init();
    }

    //在zookeeper中创建临时节点并写入信息
    private void init() {

        try {
            //创建一个zNode节点
            //节点的payload为当前　worker实例
            byte[] payload = JsonUtil.Object2JsonBytes(this);
            pathRegistered = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(pathPrefix,payload);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public long getId() {

        String sid = null;
        if(pathRegistered == null) {
            throw new IllegalArgumentException("节点注册失败");
        }

        int index = pathRegistered.lastIndexOf(pathPrefix);
        if(index >= 0) {
            index += pathPrefix.length();
            sid = index < pathRegistered.length() ? pathRegistered.substring(index) : null;
        }

        if(sid == null) {
            throw new RuntimeException("节点ID生成失败");
        }

        return Long.parseLong(sid);
    }

}
