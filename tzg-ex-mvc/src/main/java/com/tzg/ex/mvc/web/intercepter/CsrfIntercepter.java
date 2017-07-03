package com.tzg.ex.mvc.web.intercepter;

import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.tzg.ex.mvc.web.core.Constants;

public class CsrfIntercepter implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(CsrfIntercepter.class);

    /**
     * csrf的token名称
     */
    private String   tokenName = Constants.CSRF_PARAM_NAME;
    /**
     * 错误页面
     */
    private String   errorPage;
    /**
     * 排除拦截url，排除的url自行处理CSRF的安全检测
     */
    private String[] excludeUrls;

    /**
     * 判断请求是否合法：是否存在CSRF攻击
     * 登录或打开首页时，生成token放入cookie和session中
     * 判断请求、cookie、session中的token值是否一致，不一致视为不合法请求
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("GET")) {
            return true;
        }
        //优先检测请求参数、http请求头自定义属性、cookie
        String paramValue = request.getParameter(tokenName);
        if (StringUtils.isBlank(paramValue)) {
            paramValue = getCookieVal(request, tokenName);
        }
        if (StringUtils.isBlank(paramValue)) {
            paramValue = request.getHeader(tokenName);
        }
        Object sessionVal = request.getSession().getAttribute(Constants.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
        if (StringUtils.isNotBlank(paramValue) &&null!=sessionVal&& StringUtils.equals(paramValue, sessionVal.toString())) {
            return true;
        }
        String uri = request.getRequestURI().replace(request.getContextPath(),"");
        String[] eUrls = getExcludeUrls();
        String domain = request.getServerName();
        if (uri.equals("/")||uri.startsWith("/error")||ArrayUtils.contains(eUrls, uri)||ArrayUtils.contains(eUrls,domain) ) {
            logger.info("ignore uri:{},csrf check", uri);
            return true;
        }
        logger.error("bad request:{},csrf token paramVal=[{}],sessionVal=[{}]", request.getRequestURL(), paramValue, sessionVal);
        if (StringUtils.isBlank(getErrorPage())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "非法请求!");
        } else {
            response.sendRedirect(request.getContextPath() + errorPage);
        }
        return false;
    }

    private String getCookieVal(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "";
        }
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (tokenName.equals(name)) {
                return cookie.getValue();
            }
        }
        return "";
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getErrorPage() {
        if (null == errorPage || Constants.DEV_MODEL) {
            errorPage = Constants.CSRF_ERROR_PAGE;
        }
        return errorPage;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    public String[] getExcludeUrls() {
        if (null == excludeUrls || Constants.DEV_MODEL) {
            excludeUrls = Constants.CSRF_EXCLUDE_URLS;
        }
        return excludeUrls;
    }

    public void setExcludeUrls(String[] excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}