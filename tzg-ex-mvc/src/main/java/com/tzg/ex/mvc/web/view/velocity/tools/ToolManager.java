package com.tzg.ex.mvc.web.view.velocity.tools;

import java.net.URL;

import org.apache.velocity.tools.config.ConfigurationUtils;
import org.apache.velocity.tools.config.FactoryConfiguration;

/**
 * 重写2.x的ToolManager支持配置文件classpath加载
 */
public class ToolManager extends org.apache.velocity.tools.ToolManager {

    public void configure(URL url) {
        FactoryConfiguration config = findConfig(url);
        if (config == null) {
            throw new RuntimeException("Could not find any configuration at " + url);
        }
        configure(config);
    }

    protected FactoryConfiguration findConfig(URL url) {
        return ConfigurationUtils.read(url);
    }

}
