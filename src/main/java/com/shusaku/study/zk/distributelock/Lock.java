package com.shusaku.study.zk.distributelock;

public interface Lock {

    boolean lock();

    boolean unLock();

}
