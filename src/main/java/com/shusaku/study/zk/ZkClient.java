package com.shusaku.study.zk;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-21 14:52
 */
@Data
@Slf4j
public class ZkClient {

    private CuratorFramework client;

    //zk集群地址
    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    public static ZkClient instance = null;

    static {
        instance = new ZkClient();
        instance.init();
    }

    public void init() {

        if(null != client) {
            return;
        }

        client = ClientFactory.createSimple(ZK_ADDRESS);
        client.start();
    }

    //构造方法私有　　只需要ZkClient.instance就可以　获取客户端实例　加载的时候就已经启动客户端　　
    private ZkClient() {

    }

    public void destory() {
        CloseableUtils.closeQuietly(client);
    }

    /**
     * 创建zk节点
     * @param zkPath
     * @param data
     */
    public void createNode(String zkPath, String data) {
        try {

            byte[] payload = "to set content".getBytes("UTF-8");
            if(null != data) {
                payload = data.getBytes();
            }

            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(zkPath,payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除节点
     * @param zkPath
     */
    public void deleteNode(String zkPath) {
        try {
            if(isNodeExist(zkPath)) {
                client.delete().forPath(zkPath);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查节点
     * @param zkPath
     * @return
     */
    public boolean isNodeExist(String zkPath) {
        try {
            Stat stat = client.checkExists().forPath(zkPath);
            if(null == stat) {
                log.info("节点不存在:",zkPath);
                return false;
            } else {
                log.info("节点存在:",zkPath);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建临时顺序节点
     * @param srcpath
     * @return
     */
    public String createEphemeralSeqNode(String srcpath) {

        try {
            String path = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(srcpath);
            return path;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
