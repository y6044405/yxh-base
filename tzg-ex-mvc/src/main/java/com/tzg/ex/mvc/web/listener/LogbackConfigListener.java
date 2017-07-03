package com.tzg.ex.mvc.web.listener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.web.util.WebUtils;

import com.tzg.ex.mvc.web.core.Constants;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.util.StatusPrinter;
import ch.qos.logback.ext.spring.web.WebLogbackConfigurer;

/**
 * 
 * Filename:    LogbackConfigListener.java  
 * Description: logback初始化监听器.@WebListener注解用于将类声明为监听器。被@WebListener标注的类必须实现以下至少一个接口：
 * ServletContextListener、ServletContextAttributeListener、 ServletRequestListener、ServletRequestAttributeListener、HttpSessionListener、HttpSessionAttributeListener  
 * Copyright:   Copyright (c) 2015-2018 All Rights Reserved.
 * Company:     tzg.cn Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2016年4月23日 下午4:52:44  
 *
 */
//@WebListener("logback初始化监听器")
public class LogbackConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        com.tzg.ex.mvc.web.core.Context.setServletContext(context);
        Properties props = loadConf();
        for (Object key : props.keySet()) {
            if (null == key || !key.toString().startsWith("log.")) {
                continue;
            }
            System.setProperty(key.toString(), props.getProperty(key.toString()));
        }
        //设置dubbo日志为slf4j
        System.setProperty(Constants.DUBBO_APPLICATION_NAME, "slf4j");
        String appName = getAppName(context, props);
        //调试输出
        System.out.println("set " + Constants.DUBBO_APPLICATION_NAME + "=" + appName);
        System.setProperty(Constants.DUBBO_APPLICATION_NAME, appName);
        System.setProperty(Constants.LOG_DIR_NAME, appName);
        //获取日志根目录
        System.setProperty(Constants.LOG_ROOT_DIR, getLogRootDir(props));
        // jul to slf4j  
        SLF4JBridgeHandler.install();
        URL url = getLogbackConf(event);
        if (null == url) {
            context.log("logback.xml file not found in classpath");
            return;
        }
        try {
            LoggerContext loggerContext = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
            loggerContext.reset();
            new ContextInitializer(loggerContext).configureByResource(url);
            StatusPrinter.print(loggerContext);
        } catch (Exception e) {
            context.log("load logback configure file from :" + url, e);
        }

        //手动初始化
        //LogbackConfigurer.initLogging(location);
    }

    /**
     * 获取logback配置文件:logback.xml
     * 优先加载工程下的配置文件，找不到是读取jar中的默认配置
     * @param event
     * @return
     */
    private URL getLogbackConf(ServletContextEvent event) {
        String logbackConf = event.getServletContext().getInitParameter(Constants.LOGBACK_CONFIG_KEY);
        if (org.apache.commons.lang3.StringUtils.isEmpty(logbackConf)) {
            logbackConf = Constants.LOGBACK_CONFIG_FILE;
        }
        URL url = null;
        try {
            url = getClass().getResource(logbackConf);
        } catch (Exception e) {
            event.getServletContext().log("loading  logback configure file from " + logbackConf, e);
        }
        return url;
    }

    /**
     * 加载配置自定义配置(可选项:可不配置),覆盖默认配置
     * @author:  heyiwu 
     * @return
     * @throws IOException
     */
    private Properties loadConf() {
        Properties props = new Properties();
        try {
            URL url = getClass().getResource(Constants.LOG_CONF_FILE);
            if (url != null) {
                props.load(url.openStream());
            }
        } catch (Exception ignore) {
        }
        return props;
    }

    /**
     * 获取应用名称(日志子目录名),用于设置dubbo的应用名(dubbo.application.name)以及日志子目录名(log.dir.name)
     * 1、从web.xml中获取context-param（webAppRootKey的值）
     * 2、从应用conf/conf.properties中获取(dubbo.application.name或log.dir.name)
     * 3、从应用路径解析获取应用目录名
     * @author:  heyiwu 
     * @param context
     * @param props 
     * @return
     */
    private String getAppName(ServletContext context, Properties props) {
        String appName = context.getInitParameter(WebUtils.WEB_APP_ROOT_KEY_PARAM);
        String key = (appName != null ? appName : WebUtils.DEFAULT_WEB_APP_ROOT_KEY);
        String root = context.getRealPath("/");
        System.setProperty(key, root);
        if (StringUtils.isNotBlank(appName)) {
            return appName.replace(".root", "");
        }
        appName = props.getProperty(Constants.DUBBO_APPLICATION_NAME);
        appName = StringUtils.isBlank(appName) ? props.getProperty(Constants.LOG_DIR_NAME) : appName;
        if (StringUtils.isNotBlank(appName)) {
            return appName;
        }
        appName = root;
        if (StringUtils.endsWith(appName, File.separator)) {
            appName = appName.substring(0, appName.length() - 1);
        } else if (StringUtils.endsWith(appName, "webapp")) {
            appName = new File(appName).getParentFile().getParentFile().getParent();
        }
        return appName.substring(appName.lastIndexOf(File.separator) + 1, appName.length());
    }

    /**
     *  获取日志根目录
     * @author:  heyiwu 
     * @param props 
     * @return
     * @throws IOException
     */
    private String getLogRootDir(Properties props) {
        String dir = System.getProperty(Constants.LOG_ROOT_DIR);
        if (StringUtils.isNotBlank(dir)) {
            return dir;
        }
        dir = props.getProperty(Constants.LOG_ROOT_DIR);
        return StringUtils.isBlank(dir) ? "." : dir;
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        String param = servletContext.getInitParameter(WebUtils.WEB_APP_ROOT_KEY_PARAM);
        String key = (param != null ? param : WebUtils.DEFAULT_WEB_APP_ROOT_KEY);
        System.getProperties().remove(key);
        WebLogbackConfigurer.shutdownLogging(event.getServletContext());
    }
}
