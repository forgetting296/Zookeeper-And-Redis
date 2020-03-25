package com.shusaku.study.zk.publishsubscribe.config;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonElement;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-25 15:03
 */
@Slf4j
@Data
public class ConfigItem {

    private String type;
    private JSONObject data;

    public String getValue(String key) {
        Object value = data.get(key);
        return value.toString();
    }
}
