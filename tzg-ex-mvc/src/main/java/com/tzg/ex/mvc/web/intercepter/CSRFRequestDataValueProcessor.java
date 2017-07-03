package com.tzg.ex.mvc.web.intercepter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.support.RequestDataValueProcessor;

import com.tzg.ex.mvc.web.core.Constants;
import com.tzg.ex.mvc.web.util.CSRFUtil;

public class CSRFRequestDataValueProcessor implements RequestDataValueProcessor {
    @Override
    public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
        Map<String, String> hiddenFields = new HashMap<String, String>();
        hiddenFields.put(Constants.CSRF_PARAM_NAME, CSRFUtil.getTokenForSession(request.getSession()));
        return hiddenFields;
    }


    @Override
    public String processFormFieldValue(HttpServletRequest request, String name, String value, String type) {
        return null;
    }

    @Override
    public String processUrl(HttpServletRequest request, String url) {
        return url;
    }

    @Override
    public String processAction(HttpServletRequest request, String action, String httpMethod) {
        return null;
    }

   
}
