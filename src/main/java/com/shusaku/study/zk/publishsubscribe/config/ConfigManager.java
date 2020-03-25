package com.shusaku.study.zk.publishsubscribe.config;

import com.shusaku.study.zk.util.JsonUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @program: ZoopeeperAndRedis
 * @description:
 * @author: Shusaku
 * @create: 2020-03-25 15:03
 */
public class ConfigManager {

    public static ConfigManager instance = new ConfigManager();

    private ConfigManager(){

    }

    private Map<String, ConfigItem> itemMap = new LinkedHashMap<>();

    public void setType(String t, byte[] payload) {

        String s = path2Type(t);
        if(s == null) {
            return;
        }

        itemMap.put(s, JsonUtil.JsonBytes2Object(payload, ConfigItem.class));
    }

    public void removeType(String t) {
        String s = path2Type(t);
        if(s != null) {
            itemMap.remove(s);
        }
    }

    public String getConfigValue(String string, String key) {

        ConfigItem configItem = itemMap.get(string);
        if(configItem == null) {
            return null;
        }
        String value = configItem.getValue(key);
        return value;
    }

    private String path2Type(String t) {

        int index = t.lastIndexOf("/");
        if(index >= 0) {
            index ++;
            return index < t.length() ? t.substring(index) : null;
        }

        return null;
    }

}
