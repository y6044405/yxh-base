package com.tzg.ex.mvc.web.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.util.backoff.ExponentialBackOff;

/**
 * 
 * Filename:    BackOffUtil.java  
 * Description: 退避算法工具类   
 * Copyright:   Copyright (c) 2015-2018 All Rights Reserved.
 * Company:     tzg.cn Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2016年1月12日 上午11:24:34  
 *
 */
public class BackOffUtil {
    //初始间隔 
    public static long   initialInterval = 500;
    //最大间隔  
    public static long   maxInterval     = 10 * 1000L;
    //最大时间间隔  
    public static long   maxElapsedTime  = 50 * 1000L;
    //递增倍数（即下次间隔是上次的多少倍）
    public static double multiplier      = 1.5;

    public static List<Long> backoff() {
        return backoff(initialInterval, multiplier, maxInterval, maxElapsedTime);
    }

    /**
     * 退避算法 
     * @author:  heyiwu 
     * @return
     */
    public static List<Long> backoff(long initialInterval, double multiplier, long maxInterval, long maxElapsedTime) {
        ExponentialBackOff backOff = new ExponentialBackOff(initialInterval, multiplier);
        backOff.setMaxInterval(maxInterval);
        backOff.setMaxElapsedTime(maxElapsedTime);
        BackOffExecution execution = backOff.start();
        List<Long> list = new ArrayList<Long>();
        while (true) {
            long value = execution.nextBackOff();
            if (-1 == value) {
                break;
            }
            list.add(value);
        }
        return list;
    }
}
