package com.tzg.ex.mvc.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CharacterEncodingFilter extends
		org.springframework.web.filter.CharacterEncodingFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		ServletContext application = req.getSession().getServletContext();
		application.setAttribute("ctx", req.getContextPath());
		super.doFilterInternal(request, response, filterChain);
	}

}
