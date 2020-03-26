package com.shusaku.study.redis.jedis;

import com.shusaku.study.zk.util.Logger;
import redis.clients.jedis.Jedis;

/**
 * @program: ZoopeeperAndRedis
 * @description: Redis 字符串数据类型的相关命令　用于操作 redis字符串
 * @author: Shusaku
 * @create: 2020-03-25 16:52
 */
public class StringDemo {

    public static void main(String[] args) {

        Jedis jedis = new Jedis("localhost", 6379);

        //如果返回 pang 代表连接成功
        Logger.info("jedis.ping()", jedis.ping());

        //设置key0的值 123456
        jedis.set("key0","123456");

        //返回数据类型　String
        Logger.info("jedis.type(key0)", jedis.type("key0"));

        //key 是否存在
        Logger.info("jedis.exists(key0)", jedis.exists("key0"));

        //返回 key 的长度
        Logger.info("jedis.strlen(key0)", jedis.strlen("key0"));

        //返回截取字符串　0 -1 表示截取全部
        Logger.info("jedis.substr(key0,0,-1)", jedis.substr("key0",0, -1));

        //返回截取字符串　范围[1,4]
        Logger.info("jedis.substr(key0,1,4)",jedis.substr("key0", 1,4));

        //追加
        Logger.info("jedis.append(key0,appendStr)", jedis.append("key0","appendStr"));
        Logger.info("jedis.get(key0)", jedis.get("key0"));

        //重命名
        Logger.info("jedis.rename(key0,key0_new)", jedis.rename("key0","key0_new"));

        //判断 key　是否存在
        Logger.info("jedis.exists(key0)",jedis.exists("key0"));

        //批量导入
        Logger.info("jedis.mset(key1,val1,key2,val2,key3,val3)", jedis.mset("key1","val1","key2","val2","key3","100"));

        //批量取出
        Logger.info("jedis.mget(key1, key2, key3)", jedis.mget("key1", "key2", "key3"));

        //删除
        Logger.info("jedis.del(key1)", jedis.del("key1"));
        Logger.info("jedis.exists(key1)", jedis.exists("key1"));

        //取出旧值　设置新值
        Logger.info("jedis.getSet(key2, value2)", jedis.getSet("key2","value2"));

        //自增１　要求数值必须是数字
        Logger.info("jedis.incr(key3)", jedis.incr("key3"));

        //自增15
        Logger.info("jedis.incrBy(key3, 15)", jedis.incrBy("key3", 15));

        //自减15
        Logger.info("jedis.decrBy(key3, 15)", jedis.decrBy("key3",15));

        //自减1
        Logger.info("jedis.decr(key3)", jedis.decr("key3"));

        //自增浮点数
        Logger.info("jedis.incrByFloat(key3,1.1)", jedis.incrByFloat("key3",1.1));

        //返回0 只有在 key　不存在的时候才设置
        Logger.info("jedis.setnx(key3)", jedis.setnx("key3","value is exists"));
        Logger.info("jedis.get(key3)", jedis.get("key3"));

        //只有在 key　都不存在的时候才设置　　此处返回null
        Logger.info("jedis.msetnx(key2, key3)", jedis.msetnx("key2", "exists2", "key3", "exists3"));
        Logger.info("jedis.mget(key2, key3)", jedis.mget("key2", "key3"));

        //设置　key 两秒后　失效
        Logger.info("jedis.setex(key4,2,value)", jedis.setex("key4",2,"2 seconds no value"));
        Logger.info("jedis.get(key4)", jedis.get("key4"));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Logger.info("jedis.get(key4)", jedis.get("key4"));

        //
        jedis.set("key6", "123456789");
        //下标从0开始　从第3位　新值覆盖旧值
        jedis.setrange("key6",3,"abcdefg");
        Logger.info("jedis.get(key6)", jedis.get("key6"));

        //返回所有匹配的key
        Logger.info("jedis.get(key*)", jedis.get("key*"));

        jedis.close();
    }

}
