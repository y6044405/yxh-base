package com.tzg.ex.mvc.web.servlet.handler;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncExceptionHandler.class);
    /**
     * 异常处理器进行捕获异步调度中的记录异常
     */
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        LOG.error("调用方法:{},方法参数：{},发生异常：{}", method.getName(), params, ex.getLocalizedMessage());
    }

}
