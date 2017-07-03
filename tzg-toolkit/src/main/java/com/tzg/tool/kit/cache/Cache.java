package com.tzg.tool.kit.cache;

import java.util.Map;

/**
 * cache 接口
 * Filename:    Cache.java  
 * Description: cache 接口  
 * Copyright:   Copyright (c) 2012-2013 All Rights Reserved.
 * Company:     tzg-soft.com Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2012-5-11 上午09:27:19  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2012-5-23      heyiwu      1.0         添加注释说明 
 */
public interface Cache {
    /**
     * 根据key得到一个元素
     * @param key
     * @return
     * @throws CacheException
     */
    Object get(Object key) throws CacheException;

    /**
     * 批量查找元素
     * @param keys
     * @return
     * @throws CacheException
     */
    public Map<Object, Object> get(Object[] keys) throws CacheException;

    /**
     *  添加元素,有异常则抛出
     * @param key
     * @param value
     * @throws CacheException
     */
    void put(Object key, Object value) throws CacheException;

    /**
     * 添加元素指定时长,有异常则抛出
     * @param key
     * @param value
     * @param ttl 时长
     * @throws CacheException
     */
    void put(Object key, Object value, long ttl) throws CacheException;

    /**
     * 更新元素,有异常则抛出
     * @param key
     * @param value
     * @throws CacheException
     */
    void update(Object key, Object value) throws CacheException;

    /**
     * 移除元素,有异常则抛出
     * @param key
     * @throws CacheException
     */
    boolean remove(Object key) throws CacheException;

    /**
     * 清理,有异常则抛出
     * @throws CacheException
     */
    void clear() throws CacheException;

    /**
     * 清理退出,有异常则抛出
     * @throws CacheException
     */
    void destroy() throws CacheException;

    /***
     * 判断元素是否存在 
     * @param key
     * @return
     * @throws CacheException
     */
    boolean containsKey(Object key) throws CacheException;

    boolean flushAll();

    /**
     * 是否已连接
     * @return
     */
    boolean isConnected();
}
