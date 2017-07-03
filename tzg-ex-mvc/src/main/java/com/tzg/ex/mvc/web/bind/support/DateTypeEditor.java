package com.tzg.ex.mvc.web.bind.support;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

/**
 * 日期编辑器
 * 
 * 根据日期字符串长度判断是长日期还是短日期。只支持yyyy-MM-dd，yyyy-MM-dd HH:mm:ss两种格式。
 * 扩展支持yyyy,yyyy-MM日期格式
 * PropertyEditorSupport(spring3以前)只支持String与对象之间的类型转换,不支持高级转换逻辑
 * spring3之后添加了一个通用的类型转换模块，类型转换支持任意对象类型之间的转换,ConversionService是转换体系的核心接口 
 * 简单的类型转换建议使用PropertyEditor(自动查找bean同包同名的Editor类)
 * org.springframework.core.convert.support
 * org.springframework.validation
 */
public class DateTypeEditor extends PropertyEditorSupport {
	public static final String DF_LONG = "yyyy-MM-dd HH:mm:ss";
	public static final String DF_SHORT = "yyyy-MM-dd";
	public static final String DF_YEAR = "yyyy";
	public static final String DF_MONTH = "yyyy-MM";
	/**
	 * 短类型日期长度
	 */
	public static final int SHORT_DATE = 10;
	
	public static final int YEAR_DATE = 4;
	
	public static final int MONTH_DATE = 7;

	public void setAsText(String text) throws IllegalArgumentException {
		text = text.trim();
		if (!StringUtils.hasText(text)) {
			setValue(null);
			return;
		}
		try {
			if (text.length() <= YEAR_DATE) {
				setValue(new java.sql.Date(new SimpleDateFormat(DF_YEAR).parse(text).getTime()));
			}else  if (text.length() <= MONTH_DATE) {
				setValue(new java.sql.Date(new SimpleDateFormat(DF_MONTH).parse(text).getTime()));
			}else if (text.length() <= SHORT_DATE) {
				setValue(new java.sql.Date(new SimpleDateFormat(DF_SHORT).parse(text).getTime()));
			} else {
				setValue(new java.sql.Timestamp(new SimpleDateFormat(DF_LONG).parse(text).getTime()));
			}
		} catch (ParseException ex) {
			IllegalArgumentException iae = new IllegalArgumentException(
					"Could not parse date: " + ex.getMessage());
			iae.initCause(ex);
			throw iae;
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 */
	public String getAsText() {
		Date value = (Date) getValue();
		return (value != null ? new SimpleDateFormat(DF_LONG).format(value) : "");
	}
}
