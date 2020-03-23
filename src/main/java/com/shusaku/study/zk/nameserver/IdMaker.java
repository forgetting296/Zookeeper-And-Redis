package com.shusaku.study.zk.nameserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @program: ZoopeeperAndRedis
 * @description: zookeeper基于CuratorFramework的ID生成器　保障分布式消息等任务有唯一标示
 * @author: Shusaku
 * @create: 2020-03-23 09:57
 */
@Slf4j
public class IdMaker {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    CuratorFramework client = null;

    public void init() {
        client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new ExponentialBackoffRetry(1000, 3));
        client.start();
    }

    public void destory() {
        if(client != null) {
            client.close();
        }
    }

    private String createSeqNode(String pathPefix) {

        try {
            //创建一个顺序节点  临时顺序节点
            String destPath = client.create()
                    .creatingParentsIfNeeded()
                    //避免zookeeper的顺序节点暴增　需要删除创建的持久化顺序节点
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(pathPefix);
            //log.info("destPath: {}", destPath);
            return destPath;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 返回的字符串应该是nameNode + 十位数的一个数字
     * 这个方法就是拿到返回的拼接字符串　截取nameNode后边的数字ID 由于是顺序创建的节点　ID是连续的
     * @param nodeName
     * @return
     */
    public String makeId(String nodeName) {

        String destPath = createSeqNode(nodeName);

        if(null == destPath) {
            return null;
        }

        int index = destPath.lastIndexOf(nodeName);

        if(index >= 0) {
            index += nodeName.length();
            return index <= destPath.length() ? destPath.substring(index) : "";
        }

        return destPath;
    }

    public static void main(String[] args) {
        IdMaker idMaker = new IdMaker();
        idMaker.init();
        String nodeName = "/test/IDMaker/ID-";
        for(int i = 0;i < 10;i ++) {
            String seqNode = idMaker.makeId(nodeName);
            log.info("id: {}", seqNode);
        }
    }

}
