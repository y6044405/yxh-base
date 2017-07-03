package com.tzg.ex.mvc.web.filter.wrapper;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringEscapeUtils;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        return StringEscapeUtils.escapeHtml4(super.getHeader(name));
    }

    @Override
    public String getQueryString() {
        return StringEscapeUtils.escapeHtml4(StringEscapeUtils.escapeEcmaScript(super.getQueryString()));
    }

    @Override
    public String getParameter(String name) {
        return StringEscapeUtils.escapeHtml4(StringEscapeUtils.escapeEcmaScript(super.getParameter(name)));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null || values.length == 0) {
            return values;
        }
        int length = values.length;
        String[] escapseValues = new String[length];
        for (int i = 0; i < length; i++) {
            escapseValues[i] = StringEscapeUtils.escapeHtml4(StringEscapeUtils.escapeEcmaScript(values[i]));
        }
        return escapseValues;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parMap = new HashMap<String, String[]>();
        Map<String, String[]> map = super.getParameterMap();
        for (String name : map.keySet()) {
            parMap.put(name, this.getParameterValues(name));
        }
        return parMap;
    }
}