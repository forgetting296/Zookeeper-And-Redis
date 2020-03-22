package com.shusaku.study.zk.basicoperate;

import com.shusaku.study.zk.ClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @program: ZoopeeperAndRedis
 * @description:  本机的伪分布式系统  共三个zookeeper节点  全都启动以后运行以下代码才不会报错  否侧会 connection refused
 *              正常是一半的节点启动就ok的  这个没有太去纠结
 * @author: Shusaku
 * @create: 2020-03-21 15:39
 */
@Slf4j
public class CRUD {

    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    @Test
    public void checkNode() {

        //创建客户端
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        try {
            //启动客户端
            client.start();

            String zkPath = "/test/CRUD/remotNode-1";

            Stat stat = client.checkExists().forPath(zkPath);

            if(stat == null) {
                log.info("节点不存在： {}",zkPath);
            } else {
                log.info("节点存在： {}",zkPath);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建节点
     */
    @Test
    public void createNode() {

        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        String zkPath = "/test/CRUD/remotNode-1";
        String s;
        byte[] data = "hello".getBytes();
        try {
            client.start();

            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(zkPath,data);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            CloseableUtils.closeQuietly(client);
        }
    }

    /**
     * 创建临时节点
     */
    @Test
    public void createEphemeralNode() {

        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        byte[] data = "hello".getBytes();;
        try {
            client.start();

            String zkPath = "/test/CRUD/remotNode-2";
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(zkPath,data);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }

    @Test
    public void createPersistentSeqNode() {

        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);

        try {
            client.start();
            String data = "hello";
            for(int i = 0;i < 10;i ++) {
                String zkPath = "/test/remotNode-seq-" + i;
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(zkPath,data.getBytes("UTF-8"));
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }

    }

    @Test
    public void readNode() {

        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new ExponentialBackoffRetry(1000, 3));

        try {
            client.start();

            //校验节点是否存在
            String zkPath = "/test/CRUD/remotNode-1";
            Stat stat = client.checkExists().forPath(zkPath);
            if(null == stat) {
                log.info("节点不存在: {}",zkPath);
            } else {
                log.info("节点存在: {}", zkPath);
                byte[] bytes = client.getData().forPath(zkPath);
                String data = new String(bytes,"UTF-8");
                log.info("read data: {}", data);
                String parentPaht = "/test";
                List<String> childred = client.getChildren().forPath(parentPaht);
                childred.forEach(c -> {
                    log.info("childred: {}", c);
                });
            }


        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }

    }

    /**
     * 更新节点
     */
    @Test
    public void updateNode() {
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new ExponentialBackoffRetry(1000, 3));

        try {

            client.start();
            String zkPath = "/test/remotNOde-1";
            Stat stat = client.checkExists().forPath(zkPath);
            if(stat != null) {
                client.setData().forPath(zkPath,"hello world".getBytes(Charset.forName("UTF-8")));
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }


    @Test
    public void updateNodeAsync() {

        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDRESS, new ExponentialBackoffRetry(1000, 3));

        try {

            //更新完成监听器
            AsyncCallback.StringCallback stringCallback = new AsyncCallback.StringCallback() {
                @Override
                public void processResult(int i, String s, Object o, String s1) {
                    System.out.println(
                            "i = " + i + " | " +
                                    "s " + s + " | " +
                                    "o " + o + " | " +
                                    "s1 " + s1
                    );
                }
            };

            client.start();

            byte[] data = "hello every body".getBytes();

            String zkPath = "/test/remotNode-1";

            Stat stat = client.checkExists().forPath(zkPath);

            if(null != stat) {
                client.setData()
                        .inBackground(stringCallback)
                        .forPath(zkPath,data);
            }
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }

    /**
     * 删除节点
     */
    @Test
    public void deleteNode() {
        CuratorFramework client = ClientFactory.createSimple(ZK_ADDRESS);
        try {

            client.start();

            String zkPath = "/test/remotNode-1";

            Stat stat = client.checkExists().forPath(zkPath);

            if(stat != null) {
                log.info("节点存在: {}" , zkPath);
                client.delete().forPath(zkPath);
                //删除后查看结果
                String parentPath = "/test";
                List<String> children = client.getChildren().forPath(parentPath);
                children.forEach(c -> {
                    log.info("children: {}" , c);
                });
            }

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }

}
