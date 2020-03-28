package com.shusaku.study.redis.sharedpool;

import redis.clients.jedis.JedisCommands;

public interface ShardedPoolSource<T extends JedisCommands> {

    T getRedisCLient();

    void returnResource(T shardedJedis, boolean broken);
}
