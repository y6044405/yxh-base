package com.tzg.ex.mvc.web.core;


/**
 * 公共线程变量
 *  ThreadLocal线程局部变量保存的变量只有当前线程可见,可保证线程安全.spring提供ThreadLocal的实现NamedThreadLocal
 */
public class ThreadVariable {
	/**
	 * 处理时间变量
	 */
	private static ThreadLocal<Long> processStartTimeVariable = new ThreadLocal<Long>();

	
	/**
	 * 获取处理开始时间
	 * @return 
	 */
	public static Long getProcessStartTime() {
	    return processStartTimeVariable.get();
	}
	public static void removeProcessStartTime() {
	    processStartTimeVariable.remove();
	}
	/**
	 * 设置处理时间
	 * @param time   
	 */
	public static void setProcessStartTime(Long time) {
	    processStartTimeVariable.set(time);
	}
 
}
