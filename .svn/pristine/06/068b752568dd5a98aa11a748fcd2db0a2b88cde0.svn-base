package com.tzg.ex.mvc.web.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Filename:    AddHeaderFilter.java  
 * Description: 静态压缩过滤器：将所有以gzjs结尾的文件增加设置header Content-Encoding=gzip。
 * 1、将js利用gzip压缩成.gzjs文件(合并多个js文件之后在压缩)
 * 2、在web.xml中增加过滤器配置：初始化参数headers=Content-Encoding=gzip,映射规则*.gzjs
 * 3、页面js引用改成：<!-- type="text/javascript"不可少,有些浏览器缺少这个不能运行 --><script src="prototype.gzjs" type="text/javascript"></script>   
 * Copyright:   Copyright (c) 2015-2018 All Rights Reserved.
 * Company:     tzg.cn Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2016年10月11日 上午11:28:29  
 *
 */
public class AddHeaderFilter implements Filter {
	Map headers = new HashMap();
	
	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		if(req instanceof HttpServletRequest) {
			doFilter((HttpServletRequest)req, (HttpServletResponse)res, chain);
		}else {
			chain.doFilter(req, res);
		}
	}

	public void doFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
			for(Iterator it = headers.entrySet().iterator();it.hasNext();) {
				Map.Entry entry = (Map.Entry)it.next();
				response.addHeader((String)entry.getKey(),(String)entry.getValue());
			}
			chain.doFilter(request, response);
	}

	public void init(FilterConfig config) throws ServletException {
		String headersStr = config.getInitParameter("headers");
		String[] headers = headersStr.split(",");
		for(int i = 0; i < headers.length; i++) {
			String[] temp = headers[i].split("=");
			this.headers.put(temp[0].trim(), temp[1].trim());
		}
	}

}
