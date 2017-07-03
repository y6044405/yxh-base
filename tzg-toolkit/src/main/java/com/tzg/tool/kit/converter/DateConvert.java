package com.tzg.tool.kit.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

/** 
 * 日期转换（用于BeanUtils的日期转换插件，应用程序一般不要调用此方法，若要做日期转换，请调用Format）
 * Filename:    DateConvert.java  
 * Description:   
 * Copyright:   Copyright (c) 2012-2013 All Rights Reserved.
 * Company:     tzg-soft.com Inc.
 * @author:     flotage
 * @version:    1.0  
 * Create at:   2012-7-16 上午09:00:44  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2012-7-16      flotage     1.0         1.0 Version  
 *
 */
@SuppressWarnings("unchecked")
public class DateConvert implements Converter {

    public Object convert(Class arg0, Object arg1) {
        String p = StringUtils.trim(getDate(arg1));
        if (StringUtils.isBlank(p)) {
            return p;
        }
        String[] patterns = new String[] { "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd", "yyyyMMdd" };
        Date date = null;
        try {
            date = DateUtils.parseDate(p, patterns);
        } catch (ParseException e) {
            try {
                SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                return df.parse(p);
            } catch (Exception e2) {
               throw new IllegalArgumentException(arg1+" not date or not support date ");
            }
        }
        return date;
    }

    private String getDate(Object arg1) {
        if (null == arg1 || "".equals(arg1))
            return null;
        try {
            Date d = (Date) arg1;
            return d.toString();
        } catch (Exception e) {
            return (String) arg1;
        }
    }
}
