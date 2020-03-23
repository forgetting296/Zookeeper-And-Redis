package com.shusaku.study.zk.nameserver;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-23 10:25
 */
public class SnowflakeGenerator {

    /**
     * 构造方法私有　对外暴露一个唯一对象
     */
    public static SnowflakeGenerator instance = new SnowflakeGenerator();

    private SnowflakeGenerator() {

    }

    /**
     * 初始化单例
     * @param workId　节点id 最大8091
     */
    public synchronized void init(long workId) {
        if(workId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("work id beyond max work id: {}" + (workId));
        }
        instance.workId = workId;
    }

    /**
     * 开始使用该算法的时间为：　2017-01-01 00:00:00
     */
    private static final long START_TIME = 1483200000000L;

    /**
     * work id 的bit 数量　最高支持8192个节点
     */
    private static final int WORK_ID_BITS = 13;

    /**
     * 序列号　支持单节点的最高每毫秒的最大ID数　1024
     */
    private static final int SEQUENCE_BITS = 10;

    /**
     * 最大的work id 8091
     * -1 的补码　（二进制全1） 右移13位　然后取反　
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORK_ID_BITS);

    /**
     * 最大的序列号　2013
     * -1 的补码　(二进制全1) 右移10位　然后取反
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * worker　节点编号的移位
     */
    private final static long APP_HOST_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 时间戳的移位
     */
    private final static long TIMESTAMP_LEFT_SHIFT = WORK_ID_BITS + APP_HOST_ID_SHIFT;

    /**
     * 该项目的worker 节点　id
     */
    private long workId;

    /**
     *
     */
    private long lastTimestamp = -1L;

    /**
     * 当前毫秒生成的序列
     */
    private long sequence = 0L;


    public Long nextId() {
        return generateId();
    }

    /**
     * 生成唯一id的具体实现
     * @return
     */
    private Long generateId() {

        long current = System.currentTimeMillis();

        //如果当前时间小于上一次生成的id的时间戳　　说明系统时钟回退过　出现问题返回-1
        if(current < lastTimestamp) {
            return -1L;
        }

        if(current == lastTimestamp) {
            //如果上次生成的时间与上一次生成id的时间相同　　那么对sequence序列号进行 + 1
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if(sequence == MAX_SEQUENCE) {
                //当前毫秒生成的序列数已经大于最大值了　阻塞到下一个毫秒值　再获取新的时间戳  保证当前时间戳已经是下一个毫秒值
                current = this.nextMs(lastTimestamp);
            }
        } else {
            //当前时间戳已经是下一个毫秒
            sequence = 0L;
        }
        //更新上一次生成id的时间戳
        lastTimestamp = current;

        //进行位移操作　生成int64位的时间戳
        //时间戳右移23位
        long time = (current - START_TIME) << TIMESTAMP_LEFT_SHIFT;

        /**
         * workId 右移10位
         */
        long workId = this.workId << APP_HOST_ID_SHIFT;

        return time | workId | sequence;
    }

    private long nextMs(long lastTimestamp) {
        long current = System.currentTimeMillis();
        while(current <= lastTimestamp) {
            current = System.currentTimeMillis();
        }
        return current;
    }

}
