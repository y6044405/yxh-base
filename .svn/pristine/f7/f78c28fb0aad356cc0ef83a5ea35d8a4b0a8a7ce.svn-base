package com.tzg.ex.mvc.web.core;

import javax.servlet.ServletContext;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class Context {
	public static ServletContext servletContext;

	/**
	 * springMVC获取servletContext:
	 * 
	 * @return
	 */
	public static ServletContext getServletContext() {
		if (null == servletContext) {
			WebApplicationContext webApplicationContext = ContextLoader
					.getCurrentWebApplicationContext();
			if(null!=webApplicationContext){
				servletContext = webApplicationContext.getServletContext();
			}
		}
		return servletContext;
	}

	public static void setServletContext(ServletContext servletContext) {
		Context.servletContext = servletContext;
	}
}
