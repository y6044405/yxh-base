
package com.tzg.ex.mvc.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.janino.IClass.IField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.tzg.ex.mvc.web.core.Constants;
import com.tzg.ex.mvc.web.core.ThreadVariable;
import com.tzg.ex.mvc.web.util.HttpUtils;

/**
 * 处理器拦截适配器,对处理器拦截器(HandlerInterceptor)的适配
    处理器拦截器，简称拦截器,拦截上下文公共信息,如：
 *      通用行为：多个处理器都需要的公共信息（如用户信息、Locale、Theme等）放入请求,方便后续处理器流程使用
 *      日志记录：记录请求信息的日志，以便进行信息监控、信息统计、计算PV（Page View）等。
 *      登陆、权限检查：检测是否登录,是否有权限.基于通用考虑(不依赖mvc的实现springmvc/struts),推荐使用servlet规范中的过滤器filter实现
 *      性能监控：在进入处理器之前记录开始时间,在处理完后记录结束时间,从而得到该请求的处理时间(反向代理,如apache可以自动记录) 
 */
public class ContextInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ContextInterceptor.class);
    /**
     * 慢请求的时长
     */
    public long                 reqSlowTime;
    /**
     * 用户
     */
    private String              userIdSessionKey;

    /**
     * 预处理回调方法，实现处理器的预处理 
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 响应的处理器
     * @return true表示继续流程(如调用下一个拦截器或处理器);false表示中断流程,不会继续调用其他的拦截器或处理器，需要通过response来产生响应
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //拦截器是单例的即线程不安全,使用线程局部变量(ThreadLocal)绑定/保存变量(变量只有当前线程可见)。
        ThreadVariable.setProcessStartTime(System.currentTimeMillis());
        HttpSession session = request.getSession();
        Object userName = session.getAttribute(getUserIdSessionKey());
        MDC.put(Constants.SESSION_ID_MDC_KEY, session.getId());
        MDC.put(Constants.IP_ADDR_MDC_KEY, HttpUtils.getIpAddr(request));
        MDC.put(Constants.USER_ID_MDC_KEY, null==userName?"":userName.toString());
        String uri = request.getRequestURI().replace(request.getContextPath(), "");
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod hm = (HandlerMethod) handler;
        logger.info("EXEC:{}.{},{} URI:{}", hm.getBeanType().getName(), hm.getMethod().getName(),request.getMethod(), uri);
        return true;
    }

    /**
     * 后处理回调方法
     *  在渲染视图之前实现处理器的后处理,可以通过模型和视图对象(modelAndView可能为null)对模型数据进行处理或对视图进行处理。
     *@param req 请求对象
     *@param response 响应对象
     *@param handler 响应的处理器
     *@param mav  模型和视图对象(modelAndView可能为null) 
     */
    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse response, Object handler, ModelAndView mav) throws Exception {
        if (null == mav) {
            processTime(req);
            return;
        }
        String viewName = mav.getViewName();
        logger.info("{} to location :{}", viewName.startsWith("redirect:") ? "redirect" : "forward", viewName);
    }

    /**
     * 整个请求处理完毕回调方法,在视图渲染完毕时回调:
     *   性能监控中可以在此记录结束时间并输出消耗时间
     *   进行一些资源清理,仅调用处理器执行链中preHandle返回true的请求。
     *@param req 请求对象
     *@param response 响应对象
     *@param handler 响应的处理器
     *@param ex 异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        processTime(request);
    }

    /**
     * 记录请求处理时间
     *  此拦截器必须在拦截器链的顶端(第一个)记录的时间才准确
     * @param req
     * @return
     */
    protected void processTime(HttpServletRequest req) {
        Long start = ThreadVariable.getProcessStartTime();
        if (null == start) {
            return;
        }
        ThreadVariable.removeProcessStartTime();
        long processTime = System.currentTimeMillis() - start;
        String uri = req.getRequestURI().replace(req.getContextPath(), "");
        if (processTime > getReqSlowTime()) {
            //请求处理时间超过设定的时间,则认为是比较慢的请求即系统瓶颈
            logger.warn("process in {} : {} mills", uri, processTime);
        } else {
            logger.info("process in {} : {} mills", uri, processTime);
        }
    }

    public long getReqSlowTime() {
        if (0 == reqSlowTime) {
            reqSlowTime = Constants.REQ_SLOW_TIME;
        }
        return reqSlowTime;
    }

    public void setReqSlowTime(long reqSlowTime) {
        this.reqSlowTime = reqSlowTime;
    }

    public String getUserIdSessionKey() {
        if(StringUtils.isBlank(userIdSessionKey)){
            userIdSessionKey=Constants.USER_ID_SESSION_KEY;
        }
        return userIdSessionKey;
    }

    public void setUserNameSessionKey(String userNameSessionKey) {
        this.userIdSessionKey = userNameSessionKey;
    }

}
