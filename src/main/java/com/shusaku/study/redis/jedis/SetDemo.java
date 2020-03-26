package com.shusaku.study.redis.jedis;

import com.shusaku.study.zk.util.Logger;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 *              Redis 的 Set 是　String　类型的无序集合
 *              集合成员是唯一的　即不会出现重复数据
 *              Redis中集合是通过哈希表实现的　添加删除查找等操作时间复杂度都是O(1)
 *              集合中最大的成员数为2^32
 * @author: Shusaku
 * @create: 2020-03-26 10:00
 */
public class SetDemo {

    @Test
    public void operateSet() {

        Jedis jedis = new Jedis("localhost");

        jedis.del("set1");
        Logger.info("jedis.ping()", jedis.ping());

        //sadd函数　想集合中添加元素
        Logger.info("jedis.sadd()", jedis.sadd("set1","user01","user02","user03"));

        //smembers函数　遍历集合中的所有元素
        Logger.info("jedis.smembers()", jedis.smembers("set1"));

        //scard函数　获取集合中的元素个数
        Logger.info("jedis.scard()", jedis.scard("set1"));

        //sismember函数　判断是否为集合中的元素
        Logger.info("jedis.sismember(user04)", jedis.sismember("set1","user04"));

        //srem函数　　移除元素
        Logger.info("jedis.srem()", jedis.srem("set1","user02","user03"));

        //smembers()遍历所有元素
        Logger.info("jedis.smembers()", jedis.smembers("set1"));

        jedis.close();
    }

}
