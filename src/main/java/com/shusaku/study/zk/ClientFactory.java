package com.shusaku.study.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-21 14:14
 */
@Slf4j
public class ClientFactory {

    /**
     *
     * @param connectionString zk的连接地址
     * @return　CutatorFramework对象
     */
    public static CuratorFramework createSimple(String connectionString) {

        //重试策略　第一次等待１s　第二次等待2s 第三次等待4s
        //第一个参数: 等待时间的基础单位　1000ms ==> 1s
        //第二个参数: 最大重试次数
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        //获取CuratorFramework的最简单方式
        //第一个参数: zk连接地址
        //第二个参数: 重试策略
        return CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
    }

    /**
     * 使用建造者模式　通过多条件创建CuratorFramework对象
     * @param connectionString zk连接地址
     * @param retryPolicy       重试策略
     * @param connectionTimeOutMs   连接超时时间配置
     * @param sessionTimeoutMs      session超时时间
     * @return
     */
    public static CuratorFramework createWithOptions(String connectionString, RetryPolicy retryPolicy, int connectionTimeOutMs, int sessionTimeoutMs) {

        //建造者模式创建CuratorFramework对象
        return CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeOutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .build();
    }

    public static void main(String[] args) {
        CuratorFramework framework = ClientFactory.createSimple("127.0.0.1:2181");
        framework.start();
        //CloseableUtils.closeQuietly(framework);
    }

    //===========================================practice method===================================================

    private void checkNode(String zkAddress,String zkPath) {

        CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        try {
            client.start();
            Stat stat = client.checkExists()
                    .forPath(zkPath);
            if(stat == null) {
                log.info("节点不存在：{}", zkPath);
            } else {
                log.info("节点存在：{}", zkPath);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }

    private void addNode(String zkAddress, String data, String zkPath) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        try {
            client.start();
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(zkPath, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }

    private void updateNode(String zkAddress, String data, String zkPath) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        try {
            client.start();
            Stat stat = client.checkExists()
                    .forPath(zkPath);
            if(stat != null) {
                client.setData()
                        .forPath(zkPath, data.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }

    private void deleteNode(String zkAddress, String zkPath) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        try {
            client.start();
            Stat stat = client.checkExists()
                    .forPath(zkPath);
            if(stat != null) {
                client.delete()
                        .forPath(zkPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }

    private void selectNode(String zkAddress, String data, String zkPath) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        try {
            client.start();
            if(client.checkExists().forPath(zkPath) != null) {
                byte[] bytes = client.getData().forPath(zkPath);
                log.info("节点内容：{}", new String(bytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }
}
