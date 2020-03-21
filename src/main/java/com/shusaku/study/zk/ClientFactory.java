package com.shusaku.study.zk;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-21 14:14
 */
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

}
