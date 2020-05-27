package com.shusaku.study.zk.nameserver;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-23 14:15
 */
@Slf4j
public class SnowFlakeTest {

    public static void main(String[] args) throws InterruptedException {
        SnowflakeGenerator.instance.init(SnowflakeIdWoker.instance.getId());
        ExecutorService es = Executors.newFixedThreadPool(10);
        final HashSet<Long> idSet = new HashSet();
        Collection collection = Collections.synchronizedCollection(idSet);
        long start = System.currentTimeMillis();
        log.info(" start generate id *");
        for(int i = 0;i < 10;i ++) {
            es.execute(() -> {
                for(long j = 0;j < 5000000;j ++) {
                    Long id = SnowflakeGenerator.instance.nextId();
                    synchronized (idSet) {
                        idSet.add(id);
                    }
                }
            });
        }
        es.shutdown();
        es.awaitTermination(10, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        log.info(" end generate id *");
        log.info("* cost " + (end - start) + " ms!");
    }

}
