package com.shusaku.study.zk.masterselector;

import com.shusaku.study.zk.util.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-25 16:03
 */
@Slf4j
@Data
public class Candidate {

    private CuratorFramework client = null;
    private String workerPath = "/test/balance/worker";

    private String host;
    private String port;
    private String name;

    //争抢Master
    private void takeMaster() {

        try {
            byte[] payload = JsonUtil.Object2JsonBytes(this);
            //尝试创建Master临时节点
            client.create()
                    .creatingParentsIfNeeded()
                    .withProtection()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(workerPath,payload);
        } catch (Exception e) {
            log.info("资源争抢失败: " + name);
        }
    }

    private void releaseMaster() throws Exception {

        byte[] payload = client.getData()
                .forPath(workerPath);
        Candidate candidate = JsonUtil.JsonBytes2Object(payload, this.getClass());
        if(candidate.getName().equals(this.getName())) {
            client.delete()
                    .forPath(workerPath);
        }
    }

}
