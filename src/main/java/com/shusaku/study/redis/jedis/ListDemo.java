package com.shusaku.study.redis.jedis;

import com.shusaku.study.zk.util.Logger;
import org.junit.Test;
import redis.clients.jedis.Jedis;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 *              Redis LIst　是一个简单的字符串列表
 *             　按照插入顺序进行排序　可以添加到元素列表的头部或者尾部
 * @author: Shusaku
 * @create: 2020-03-25 18:59
 */
public class ListDemo {

    @Test
    public void operateList() {

        Jedis jedis = new Jedis("localhost", 6379);
        Logger.info("jedis.ping()", jedis.ping());

        jedis.del("list1");

        //从 list　尾部添加算个元素
        Logger.info("", jedis.rpush("list1","zhangsan", "lisi", "wangwu"));

        //取得类型
        Logger.info("jedis.type(list1)", jedis.type("list1"));

        //便利区间　[0,-1] 取得全部的元素
        Logger.info("", jedis.lrange("list1",0, -1));

        //便利区间　[1,2] 取得区间内的元素
        Logger.info("jedis.lrange(list1,1,2)", jedis.lrange("list1",1,2));

        //获取　list　的长度
        Logger.info("jedis.llen(list1)", jedis.llen("list1"));

        //获取下标为1的元素
        Logger.info("jedis.lindex(list1,1)", jedis.lindex("list1",1));

        //左侧弹出元素
        Logger.info("jedis.lpop(list1)", jedis.lpop("list1"));

        //右侧弹出新元素
        Logger.info("jedis.rpop(list1)", jedis.rpop("list1"));

        //设置下标为 0 的元素val
        Logger.info("jedis.lset(list1,0,val)", jedis.lset("list1",0,"val"));

        //遍历区间　获取全部元素
        Logger.info("jedis.lrange()", jedis.lrange("list1",0,-1));

    }

}
