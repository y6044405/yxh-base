package com.tzg.ex.mvc.web.view.freemarker;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tzg.ex.mvc.web.core.Constants;


/**
 * 扩展spring的FreemarkerView，加上公用变量属性:
 *      contextPath部署路径,默认名称ctx
 * 支持jsp标签，Application、Session、Request、RequestParameters属性
 */
public class FreeMarkerView extends org.springframework.web.servlet.view.freemarker.FreeMarkerView {
 
	/**
	 * 在model中增加部署路径,方便处理部署路径问题。
	 */
	@SuppressWarnings("unchecked")
	protected void exposeHelpers(Map model, HttpServletRequest request)
			throws Exception {
		super.exposeHelpers(model, request);
		model.put(Constants.CONTEXT_PATH_KEY, request.getContextPath());
	}
}