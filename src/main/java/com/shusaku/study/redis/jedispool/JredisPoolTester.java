package com.shusaku.study.redis.jedispool;

import com.shusaku.study.zk.util.Logger;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-26 14:22
 */
public class JredisPoolTester {

    public static final int NUM = 200;
    public static final String ZSET_KEY = "zset1";

    @Test
    public void testDel() {
        Jedis jedis = null;
        try {
            jedis = JredisPoolBuilder.getJedis();
            long start = System.currentTimeMillis();
            jedis.del(ZSET_KEY);
            long end = System.currentTimeMillis();
            Logger.info("删除key毫秒数: " + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    @Test
    public void testSet() {

        testDel();

        try (Jedis jedis = JredisPoolBuilder.getJedis()){
            int loop = 0;
            long start = System.currentTimeMillis();
            while(loop < NUM) {
                jedis.zadd(ZSET_KEY, loop, "field-" + loop);
                loop ++;
            }
            long end = System.currentTimeMillis();
            Logger.info("设置zset :" + loop + "　次，毫秒数：　" + (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGet() {

        try (Jedis jedis = JredisPoolBuilder.getJedis()){
            long start = System.currentTimeMillis();
            Set<String> zrange = jedis.zrange(ZSET_KEY, 0, -1);
            long end = System.currentTimeMillis();
            Logger.info("顺序获取 zset 毫秒数: ",end - start);
            Logger.info(zrange.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Jedis jedis = JredisPoolBuilder.getJedis()){
            long start = System.currentTimeMillis();
            Set<String> zrange = jedis.zrevrange(ZSET_KEY, 0, -1);
            long end = System.currentTimeMillis();
            Logger.info("顺序获取 zset 毫秒数: ",end - start);
            Logger.info(zrange.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
