package com.tzg.ex.mvc.web.view.velocity;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.view.ViewToolContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;

import com.tzg.ex.mvc.web.view.velocity.tools.ToolManager;

/**
 * 1、重写方法以支持tools2.x加载：
 * Spring3默认的 createVelocityContext方法中采用的是 tools-1.x 的 ToolboxManager,
 * ServletToolboxManager等类 加载toolbox，但是 tools 2.x 中已经废弃了这些类，导致了无法加载tools 2.x。
 * 所以，这里采用tools 2.x中新的 ToolManager方式重写此方法加载toolbox2.x。
 * 2、优化解析速度
 * 3、toolbox配置文件支持classpath和servlet contextpath两种加载方式
 */
public class VelocityToolbox2View extends VelocityLayoutView {
    private static final Logger logger = LoggerFactory.getLogger(VelocityToolbox2View.class.getName());

    private static ToolManager toolManager;
    private boolean            configured = false;

    static{
        getToolManager();
    }

    @Override
    protected Context createVelocityContext(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        VelocityEngine ve = this.getVelocityEngine();
        ViewToolContext ctx = new ViewToolContext(ve, request, response, this.getServletContext());
        String location = this.getToolboxConfigLocation();
        if (location != null) {
            toolManager = getToolManager();
            toolManager.setVelocityEngine(ve);
            configure(location);
            for (String scope : Scope.values()) {
                ctx.addToolbox(toolManager.getToolboxFactory().createToolbox(scope));
            }
        }
        if (model != null && !model.isEmpty()) {
            ctx.putAll(model);
        }
        return ctx;
    }

    private void configure(String location) {
        if (configured) {
            return;
        }
        configured = true;
        try {
            if (location.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
                toolManager.configure(new PathMatchingResourcePatternResolver().getResource(location).getURL());
            } else {
                toolManager.configure(this.getServletContext().getRealPath(location));
            }
        } catch (Exception e) {
            logger.error("toolboxConfigLocation:{} not found!", location, e);
        }
    }

    public static ToolManager getToolManager() {
        if (null == toolManager) {
            toolManager = new ToolManager();
        }
        return toolManager;
    }

}
