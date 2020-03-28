package com.shusaku.study.redis.sharedpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.ShardedJedis;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-26 15:32
 */
@Slf4j
@Repository("shardedPoolSourceTemplate")
public class ShardedPoolSourceTemplate {

    @Autowired
    private ShardedPoolSource<ShardedJedis> redisDataSource;

    public String save(String key, String value, long seconds) {
        String result = null;

        ShardedJedis redisClient = redisDataSource.getRedisCLient();
        if(redisClient == null) {
            return result;
        }

        boolean broken = false;
        try {
            result = setBySeconds(redisClient,key,value,seconds);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(redisClient,broken);
        }

        return result;
    }

    private String setBySeconds(ShardedJedis redisClient, String key, String value, long seconds) {

        boolean isExists = redisClient.exists(key);
        if(isExists) {
            //NX是不存在时才set XX是存在时才set EX是秒　PX是毫秒
            return redisClient.set(key,value,"XX","EX",seconds);
        } else {
            return redisClient.set(key,value,"NX","EX",seconds);
        }
    }

    //获取单个值
    public String get(String key) {

        String result = null;

        ShardedJedis shardedJedis = redisDataSource.getRedisCLient();
        if(shardedJedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = shardedJedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(shardedJedis, broken);
        }

        return result;
    }

    public Boolean exist(String key) {
        Boolean exists = false;

        ShardedJedis jedis = redisDataSource.getRedisCLient();
        if(jedis == null) {
            return exists;
        }
        boolean broken = false;
        try {
            exists = jedis.exists(key);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(jedis,broken);
        }
        return exists;
    }

    public String type(String key) {

        String result = null;
        ShardedJedis jedis = redisDataSource.getRedisCLient();
        if(jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.type(key);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(jedis,broken);
        }
        return result;
    }

    public Long expire(String key, int seconds) {
        Long result = null;
        ShardedJedis jedis = redisDataSource.getRedisCLient();
        if(jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.expire(key, seconds);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(jedis, broken);
        }
        return result;
    }

    //在某个时间点失效
    public Long expireAt(String key, long unixTime) {
        Long result = null;
        ShardedJedis jedis = redisDataSource.getRedisCLient();
        if(jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.expireAt(key, unixTime);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(jedis, broken);
        }
        return result;
    }

    public Long ttl(String key) {
        Long result = null;
        ShardedJedis jedis = redisDataSource.getRedisCLient();
        if(jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(jedis, broken);
        }
        return result;
    }

    public boolean setbit(String key, long offset, boolean value) {
        boolean result = false;
        ShardedJedis jedis = redisDataSource.getRedisCLient();
        if(jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.setbit(key,offset,value);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(jedis, broken);
        }
        return result;
    }

    public boolean getbit(String key, long offset) {
        boolean result = false;
        ShardedJedis jedis = redisDataSource.getRedisCLient();
        if(jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.getbit(key,offset);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(jedis, broken);
        }
        return result;
    }

    public long setRange(String key, long offset, String value) {
        Long result = null;
        ShardedJedis jedis = redisDataSource.getRedisCLient();
        if(jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.setrange(key,offset,value);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(jedis, broken);
        }
        return result;
    }

    public String getRange(String key, long offset, long endOffset) {
        String result = null;
        ShardedJedis jedis = redisDataSource.getRedisCLient();
        if(jedis == null) {
            return result;
        }
        boolean broken = false;
        try {
            result = jedis.getrange(key,offset,endOffset);
        } catch (Exception e) {
            e.printStackTrace();
            broken = true;
        } finally {
            redisDataSource.returnResource(jedis, broken);
        }
        return result;
    }

}
