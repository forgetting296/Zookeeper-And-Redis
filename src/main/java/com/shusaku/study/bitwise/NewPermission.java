package com.shusaku.study.bitwise;

/**
 * @program: ZoopeeperAndRedis
 * @description: 使用位运算　实现权限赋予以及权限查询等操作
 * @author: Shusaku
 * @create: 2020-03-24 10:00
 */
public class NewPermission {

    public static final int ALLOW_SELECT = 1 << 0; //0001
    public static final int ALLOW_INSERT = 1 << 2; //0010
    public static final int ALLOW_UPDATE = 1 << 3; //0100
    public static final int ALLOW_DELETE = 1 << 4; //1000

    //用来存储目前的权限状态
    private int flag;

    /**
     * 设置权限
     * @param permission
     */
    public void setPermission(int permission) {
        flag = permission;
    }

    /**
     * 添加一项或者多项权限
     * @param permission
     */
    public void enable(int permission) {
        flag |= permission;
    }

    /**
     * 删除一项或者多项权限
     * @param permission
     */
    public void disable(int permission) {
        flag &= (~permission);
    }

    /**
     * 是否拥有某些权限
     * @param permission
     * @return
     */
    public boolean isAllow(int permission) {
        return (flag & permission) == permission;
    }

    /**
     * 是否禁用了某些权限
     * @param permission
     * @return
     */
    public boolean isNotAllow(int permission) {
        return (flag & permission) == 0;
    }

    /**
     * 是否仅仅拥有某些权限
     */
    public boolean isOnlyAllow(int permission) {
        return flag == permission;
    }
}
