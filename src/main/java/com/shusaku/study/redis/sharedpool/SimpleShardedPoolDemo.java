package com.shusaku.study.redis.sharedpool;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Arrays;
import java.util.List;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-26 16:12
 */
public class SimpleShardedPoolDemo {

    private static ShardedJedisPool shardedPool = null;

    public static ShardedJedisPool getShardedPool() {

        if(shardedPool == null) {

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(500);
            config.setMaxIdle(5);
            config.setMaxWaitMillis(1000 * 10);
            config.setTestOnBorrow(true);

            String host = "127.0.0.1";
            JedisShardInfo info1 = new JedisShardInfo(host,6379, 500);
            info1.setPassword("password");
            JedisShardInfo info2 = new JedisShardInfo(host,6379, 500);
            info1.setPassword("password");
            JedisShardInfo info3 = new JedisShardInfo(host,6379, 500);
            info1.setPassword("password");
            List<JedisShardInfo> shardInfos = Arrays.asList(info1,info2,info3);
            shardedPool = new ShardedJedisPool(config, shardInfos);
        }
        return shardedPool;
    }

    public synchronized static ShardedJedis getResource() {
        if(shardedPool == null) {
            shardedPool = getShardedPool();
        }
        return shardedPool.getResource();
    }


}
