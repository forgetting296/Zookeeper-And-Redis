package com.shusaku.study.redis.sharedpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-26 15:19
 */
@Slf4j
@Service("ShardedPoolSourceImpl")
public class ShardedPoolSourceImpl implements ShardedPoolSource<ShardedJedis> {

    @Autowired
    private ShardedJedisPool shardedJedisPool;

    @Override
    public ShardedJedis getRedisCLient() {

        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
            return jedis;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    @Override
    public void returnResource(ShardedJedis shardedJedis, boolean broken) {

        if(broken) {
            log.error("redis 连接池出现异常");
        }
        shardedJedis.close();
    }
}
