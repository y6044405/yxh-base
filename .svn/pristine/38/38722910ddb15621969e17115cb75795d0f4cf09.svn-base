package com.tzg.ex.mvc.web.listener;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *   HttpSessionBindingListen
 * Filename:    SessionListener.java  
 * Description: session监听 session销毁时移除用户信息  
 * Copyright:   Copyright (c) 2012-2013 All Rights Reserved.
 * Company:     golden-soft.com Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2012-10-26 下午12:44:32  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2012-10-26      heyiwu      1.0         1.0 Version  
 *
 */
public class SessionListener implements HttpSessionListener {
    //session失效时间,单位分钟
    private int sessionExpiredTime = 30;

    private int getSessionExpiredTime(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();
        String time = context.getInitParameter("sessionExpiredTime");
        try {
            return Integer.valueOf(time);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int time = getSessionExpiredTime(se);
        se.getSession().setMaxInactiveInterval((time < 0 ? sessionExpiredTime : time) * 60);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        Enumeration names = session.getAttributeNames();
        while (names.hasMoreElements()) {
            session.removeAttribute(names.nextElement().toString());
        }
        session.invalidate();
    }

}
