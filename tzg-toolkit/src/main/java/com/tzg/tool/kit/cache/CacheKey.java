package com.tzg.tool.kit.cache;

/**
 * cache key枚举类
 * Filename:    CacheKey.java  
 * Description: memcached key枚举类,key杜绝使用空格，长度控制在256个字符内;value控制小于1MB必要时使用压缩  
 * Copyright:   Copyright (c) 2012-2013 All Rights Reserved.
 * Company:     tzg-soft.com Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2012-5-25 下午04:20:04  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2012-5-25      heyiwu      1.0         1.0 Version  
 *
 */
public enum CacheKey {
 
    USERINFO("USER_INFO_", "用户信息"), 
    USERAUTH("USER_AUTH_", "用户权限"), 
    SYSAUTH("SYS_AUTH_", "系统权限");
 

    private String prefix, desc;

    CacheKey(String prefix, String desc) {
        this.prefix = prefix;
        this.desc = desc;
    }

    public static void main(String[] args) {
        
    }

    public static String getCacheKey(CacheKey cacheKey, long key) {
        return cacheKey.getPrefix() + key;
    }

    public static String getCacheKey(CacheKey cacheKey, String key) {
        return cacheKey.getPrefix() + key;
    }

    /**
     * 获取用户信息缓存key
     * @param key id
     * @return 缓存key
     */
    public static String getUserInfoCacheKey(long key) {
        return USERINFO.getPrefix() + key;
    }
    /**
     * 过期不推荐使用,替代方法： getCacheKey(CacheKey cacheKey, long key)
     * @param prefix
     * @param key
     * @return
     */
    @Deprecated
    public static String getCacheKey(String prefix, long key) {
        return getCacheKey(prefix).getPrefix() + key;
    }

    public static CacheKey getCacheKey(String prefix) {
        CacheKey[] vals = CacheKey.values();
        for (CacheKey val : vals) {
            if (val.getPrefix().equals(prefix)) {
                return val;
            }
        }
        return null;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
