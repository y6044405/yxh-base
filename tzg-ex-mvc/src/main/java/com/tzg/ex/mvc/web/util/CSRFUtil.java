package com.tzg.ex.mvc.web.util;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import com.tzg.ex.mvc.web.core.Constants;

public class CSRFUtil {

    public static String getTokenForSession(HttpSession session) {
        String token = null;
        synchronized (session) {
            token = (String) session.getAttribute(Constants.CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
            if (null == token) {
                token = UUID.randomUUID().toString().replace("-", "");
                session.setAttribute(Constants.CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
            }
        }
        return token;
    }
}
