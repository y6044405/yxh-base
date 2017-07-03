package com.tzg.ex.mvc.web.core;

/**
 *系统常量
 */
public class Constants {

    // logback日志配置文件
    public static String       LOGBACK_CONFIG_FILE    = "/logback.xml";
    // 系统公共配置文件
    public static String       GLOBAL_SYS_CONFIG_FILE = "/conf/base-conf.properties";
    // jdbc配置文件
    public static String       JDBC_CONFIG_FILE       = "/config/jdbc.properties";
    // UTF-8编码
    public static final String UTF8                   = "UTF-8";
    // logback初始化的参数名
    public static final String LOGBACK_CONFIG_KEY     = "logbackConfigLocation";
    //日志自定义配置文件,可选项,在工程目录下，可有可无,用来覆盖默认配置(默认配置在logback.xml中)
    public static final String LOG_CONF_FILE          = "/conf/conf.properties";
    //日志根目录
    public static final String LOG_ROOT_DIR           = "log.root.dir";
    //日志子目录名
    public static final String LOG_DIR_NAME           = "log.dir.name";
    //dubbo应用名 
    public static final String DUBBO_APPLICATION_NAME = "dubbo.application.name";

    // locale存放的key
    public static final String LOCALE_KEY       = "locale";
    // contextPath的key
    public static final String CONTEXT_PATH_KEY = "ctx";

    public static final String DEFAULT_LOCALE = "zh_CN";

    // HTTP POST请求
    public static final String POST          = "POST";
    // HTTP GET请求
    public static final String GET           = "GET";
    // 请求瓶颈时间(处理时间超过此时间就认为是比较慢的请求),单位毫秒
    public static final long   REQ_SLOW_TIME = 1500;

    // 请求开始时间
    public static final String REQ_START_TIME = "req_start_time";

    //请求中CSRF的token参数名
    public static final String CSRF_PARAM_NAME                  = "CSRFToken";
    //session中CSRF的token键名
    public static final String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CSRF_PARAM_NAME + ".tokenval";
  
    //CSRF拦截器排除地址:排除的url自行处理CSRF的安全检测
    public static String[]     CSRF_EXCLUDE_URLS;
    //CSRF拦截不通过跳转页面:为空时直接返回错误状态码(HttpServletResponse.SC_BAD_REQUEST)
    public static String       CSRF_ERROR_PAGE                  = "";
    //是否开发模式:开发模式会一直重新加载配置
    public static boolean      DEV_MODEL                        = true;

    /**
     * 铜掌柜域名
     */
    public static final String TZG_DOMAIN_NAME = "tzg.cn";

    /**
     * 用户名session键名
     */
    public static final String USER_ID_SESSION_KEY = "userId";

    /**
     * 用户名session键名
     */
    public static final String USER_ID_MDC_KEY = "userId";

    /**
     * sessionid的mdc键名
     */
    public static final String SESSION_ID_MDC_KEY = "_sessionId";
    public static final String IP_ADDR_MDC_KEY    = "_ip";

}
