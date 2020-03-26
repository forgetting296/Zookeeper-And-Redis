package com.shusaku.study.redis.jedis;

import com.shusaku.study.zk.util.Logger;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 *                  Redis有序集合和集合一样　也是String类型的元素的集合　而且不允许重复的成员
 *                  不同的是每个元素都会关联一个double类型的分数
 *                  redis正是通过分数来为集合中的成员进行从小到大的排序
 *                  有序的成员是唯一的　　但是分数可以重复
 *                  集合是通过哈希表实现的　添加、删除、查询时间复杂度都是O(1)
 *                  集合中最大元素个数是2^32 - 1
 * @author: Shusaku
 * @create: 2020-03-26 10:19
 */
public class ZsetDemo {

    @Test
    public void operateZset(){

        Jedis jedis = new Jedis("localhost");

        Logger.info("jedis.ping()", jedis.ping());
        Logger.info("jedis.del()", jedis.del("salary"));

        Map<String, Double> map = new HashMap<>();
        map.put("u01", 1000.0);
        map.put("u02", 2000.0);
        map.put("u03", 3000.0);
        map.put("u04", 13000.0);
        map.put("u05", 23000.0);

        //批量添加元素
        Logger.info("jedis", jedis.zadd("salarm", map));

        //数据类型
        Logger.info("jedis.type()", jedis.type("salary"));

        //获取集合元素个数
        Logger.info("jedis.zcard()", jedis.zcard("salary"));

        //按照下标便利元素
        Logger.info("jedis.zrange()", jedis.zrange("salyar",0 , -1));

        //按照下标　反序遍历元素
        Logger.info("jedis.zrevrange()", jedis.zrevrange("salary", 0, -1));

        //按照分数遍历元素
        Logger.info("jedis.zrangeBYScore()", jedis.zrangeByScore("salary",1000, 10000));

        //按照薪资　[起,止] 便利元素　带分数返回
        //Logger.info("jedis.zrangeBYScoreWithScores", jedis.zrangeByScoreWithScores("salary",1000.0, 10000.0));
        Set<Tuple> tuples = jedis.zrangeByScoreWithScores("salary", 1000.0, 10000.0);
        for(Tuple tuple : tuples) {
            Logger.info("Tuple.get()" + tuple.getElement() + " -> " + tuple.getScore());
        }

        //按照分数起止遍历元素　倒序
        Logger.info("jedis", jedis.zrevrangeByScore("salary", 1000, 10000));

        //获取元素　起止　分数区间的元素数量
        Logger.info("jedis.zcount()", jedis.zcount("salary",1000,10000));

        //获取元素的下标
        Logger.info("jedis.zrank()", jedis.zrank("salary", "u01"));

        //获取元素的score值
        Logger.info("jedis.zscore(u01)", jedis.zscore("salary", "u01"));

        //倒序获取元素下标
        Logger.info("jedis.zrevrank(u01)", jedis.zrevrank("salary","u01"));

        //删除元素
        Logger.info("jedis.zrem()", jedis.zrem("salary", "u01", "u01"));

        //删除元素　通过下标范围
        Logger.info("jedis", jedis.zremrangeByRank("salary",0 , 1));

        //删除元素　通过分数范围
        Logger.info("jedis", jedis.zremrangeByScore("salary", 20000, 30000));

        //按照下标　便利元素
        Logger.info("jedis.zrange()", jedis.zrange("salary",0, -1));

        Map<String, Double> members2 = new HashMap<String, Double>();
        members2.put("u11", 1136.0);
        members2.put("u12", 2212.0);
        members2.put("u13", 3324.0);

        //批量添加元素
        jedis.zadd("salary", members2);

        //增加指定分数
        Logger.info("jedis", jedis.zincrby("salary", 100000, "u13"));

        //按照下标便利元素
        Logger.info("jedis.zrange()", jedis.zrange("salary",0,-1));

        jedis.close();
    }

}
