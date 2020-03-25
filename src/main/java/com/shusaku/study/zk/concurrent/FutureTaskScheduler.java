package com.shusaku.study.zk.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-25 13:55
 */
public class FutureTaskScheduler extends Thread {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //任务队列
    private ConcurrentLinkedQueue<ExecuteTask> executeTaskQueue = new ConcurrentLinkedQueue<>();

    //线程休眠时间
    private long sleepTime = 200;

    //
    private ExecutorService pool = Executors.newFixedThreadPool(10);

    private static FutureTaskScheduler inst = new FutureTaskScheduler();

    private FutureTaskScheduler(){this.start();}

    /**
     * 向任务队列中添加任务
     * @param task
     */
    public static void add(ExecuteTask task){
        inst.executeTaskQueue.add(task);
    }

    @Override
    public void run() {
        while(true) {
            handleTask();
            threadSleep(sleepTime);
        }
    }

    private void threadSleep(long sleepTime) {

        try {
            sleep(sleepTime);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

    private void handleTask() {

        try {
            ExecuteTask task;
            while(executeTaskQueue.peek() != null) {
                task = executeTaskQueue.poll();
                handleTask(task);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    private void handleTask(ExecuteTask task) {
        pool.execute(new ExecuteRunnable(task));
    }

    class ExecuteRunnable implements Runnable {

        ExecuteTask task;

        public ExecuteRunnable(ExecuteTask task) {
            this.task = task;
        }

        @Override
        public void run() {
            task.execute();
        }
    }

}
