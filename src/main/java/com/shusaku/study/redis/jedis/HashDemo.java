package com.shusaku.study.redis.jedis;

import com.shusaku.study.zk.util.Logger;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 *              Redis Hash 是一个string类型的field 和　value 的映射表
 *              hash比较适合用来存储对象
 *              Redis 中每个hash可以存储2^32 - 1个键值对
 * @author: Shusaku
 * @create: 2020-03-25 19:15
 */
public class HashDemo {

    @Test
    public void operateHash() {

        Jedis jedis = new Jedis("localhost", 6379);
        jedis.del("config");

        //设置hash的　field-value对
        jedis.hset("config", "ip", "127.0.0.1");

        //取得hash的　key-value对
        Logger.info("jedis.hget()", jedis.hget("config","ip"));

        //取得类型
        Logger.info("jedis.type()", jedis.type("config"));

        //批量添加key-value对　参数为java中的map
        Map<String,String> map = new HashMap<>();
        map.put("port","6379");
        map.put("maxalive","3600");
        map.put("weight","1.0");
        Logger.info("jedis", jedis.hmset("config",map));

        //批量获取 取得全部 key-value　返回 java map
        Logger.info("jedis.hmget()", jedis.hmget("config"));
        Logger.info("jedis.hgetAll()", jedis.hgetAll("config"));

        //批量获取  指定　key 的多个field
        Logger.info("jedis.hmget(config,ip,port)", jedis.hmget("config","ip","port"));

        //获取所有key
        Logger.info("jedis.hkeys()", jedis.hkeys("config"));

        //获取所有value
        Logger.info("jedis.hvals()", jedis.hvals("config"));

        //获取长度
        Logger.info("jedis.hlen()", jedis.hlen("config"));

        //判断field会否存在
        Logger.info("jedis.hexists(config,ip)", jedis.hexists("config","ip"));

        //删除一个field
        Logger.info("jedis.hdel(config, weight)", jedis.hdel("config","weight"));

        jedis.close();
    }

}
