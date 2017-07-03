package com.tzg.ex.mvc.web.servlet.handler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.tzg.ex.mvc.web.core.Constants;

/**
 * 本地化信息拦截器
 *      Spring MVC缺省使用AcceptHeaderLocaleResolver来根据request header中的Accept-Language来确定访客的local
 *      设置LOCALE信息,locale设置过一次就可以了,不需要每个连接后面都加上locale参数
 *      本地化变更拦截器：org.springframework.web.servlet.i18n.LocaleChangeInterceptor
 *      页面使用标签<spring:message>从resource文件中获取的文字的动态加载功能。 
 */
public class LocaleInterceptor extends org.springframework.web.servlet.i18n.LocaleChangeInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LocaleInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        try {
            super.preHandle(request, response, handler);
        } catch (Exception e) {
            logger.warn(e.getLocalizedMessage());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
//        localeResolver.setLocale(request, response, org.springframework.util.StringUtils.parseLocaleString(locale));
        if (modelAndView != null&&null!=localeResolver) {
            modelAndView.getModelMap().addAttribute(Constants.LOCALE_KEY,
                localeResolver.resolveLocale(request).toString());
        }
    }
}