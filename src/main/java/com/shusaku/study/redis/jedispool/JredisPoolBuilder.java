package com.shusaku.study.redis.jedispool;

import com.shusaku.study.zk.util.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-26 13:47
 */
public class JredisPoolBuilder {

    public static final int MAX_IDLE = 50;
    public static final int MAX_TOTAL = 50;

    private static JedisPool pool = null;

    static{

        //创建连接池
        buildPool();
        //预热连接池
        hotPool();
    }

    public synchronized static Jedis getJedis() {
        return pool.getResource();
    }

    //构建连接池
    private static JedisPool buildPool() {

        if(pool == null) {
            long start = System.currentTimeMillis();

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(100 * 10);
            //在borrow一个jedis实例时　是否提前进行validate操作
            //如果为true 则得到的jedis实例都是可用的
            config.setTestOnBorrow(true);

            pool = new JedisPool(config, "127.0.0.1", 6379, 10000 );

            long end = System.currentTimeMillis();
            Logger.info("build pool:", (end - start));
        }

        return pool;
    }

    //连接池的预热
    private static void hotPool() {
        long start = System.currentTimeMillis();

        List<Jedis> minIdleJedisList = new ArrayList<>(MAX_IDLE);
        Jedis jedis = null;
        for(int i = 0;i < MAX_IDLE;i ++) {
            try {
                jedis = pool.getResource();
                minIdleJedisList.add(jedis);
                jedis.ping();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();
        Logger.info("hotPool 毫秒数：" + (end - start));
    }

}
