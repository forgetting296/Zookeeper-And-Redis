package com.shusaku.study.redis.lock;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-27 16:55
 */
@Component
public class RedissionDistributedLocker {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 加锁
     * @param lockKey
     * @return
     */
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 加锁　过期自动释放
     * @param lockKey
     * @param leaseTime
     * @return
     */
    public RLock lock(String lockKey, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 尝试获取锁
     * @param lockKey
     * @param leaseTime
     * @param unit
     * @return
     */
    public boolean tryLock(String lockKey, long leaseTime, TimeUnit unit){
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean b = lock.tryLock(leaseTime, unit);
            return b;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 尝试获取锁
     * @param lockKey
     * @param unit
     * @param leaseTime
     * @param waitTime
     * @return
     */
    public boolean tryLock(String lockKey, TimeUnit unit, long leaseTime, long waitTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 释放锁
     * @param lockKey
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    /**
     * 释放锁
     * @param lock
     */
    public void unLock(RLock lock) {
        lock.unlock();
    }


}
