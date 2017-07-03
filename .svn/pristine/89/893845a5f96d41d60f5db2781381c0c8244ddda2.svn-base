package com.tzg.tool.kit.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.DateTimeConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.beanutils.converters.SqlTimeConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;

/**
 * 注册Converter, 用于apache commons BeanUtils.copyProperties()方法中的class类型转换;
 * 可以修改此处代码以添加新的Converter
 */
public class ConvertRegisterHelper {

    private ConvertRegisterHelper() {
    }

    public static void registerConverters() {
        Map<Converter, Class> map = getConverMap();
        for (Converter o : map.keySet()) {
            ConvertUtils.register(o, map.get(o));
        }
    }

    //	public static void registerConverters(ConvertUtilsBean convertUtils) {
    //		registerConverters(convertUtils,new String[] {"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm:ss.SSS","HH:mm:ss"});
    //	}

    public static void registerConverters(ConvertUtilsBean convertUtils, String[] datePatterns) {
        convertUtils.register(new StringConverter(), String.class);
        //date 
        convertUtils.register(ConvertRegisterHelper.setPatterns(new DateConverter(null), datePatterns), java.util.Date.class);
        convertUtils.register(ConvertRegisterHelper.setPatterns(new SqlDateConverter(null), datePatterns), java.sql.Date.class);
        convertUtils.register(ConvertRegisterHelper.setPatterns(new SqlTimeConverter(null), datePatterns), Time.class);
        convertUtils.register(ConvertRegisterHelper.setPatterns(new SqlTimestampConverter(null), datePatterns), Timestamp.class);
        //number
        convertUtils.register(new BooleanConverter(null), Boolean.class);
        convertUtils.register(new ShortConverter(null), Short.class);
        convertUtils.register(new IntegerConverter(null), Integer.class);
        convertUtils.register(new LongConverter(null), Long.class);
        convertUtils.register(new FloatConverter(null), Float.class);
        convertUtils.register(new DoubleConverter(null), Double.class);
        convertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
        convertUtils.register(new BigIntegerConverter(null), BigInteger.class);
    }

    public static <T extends DateTimeConverter> T setPatterns(T converter, String... patterns) {
        converter.setPatterns(patterns);
        return converter;
    }

    @SuppressWarnings("unchecked")
    public static Map<Converter, Class> getConverMap() {
        Map<Converter, Class> map = new LinkedHashMap<Converter, Class>();
        map.put(new StringConverter(), String.class);
        //date 
        map.put(new DateConverter(null), java.util.Date.class);
        map.put(new SqlDateConverter(null), java.sql.Date.class);
        map.put(new SqlTimeConverter(null), Time.class);
        map.put(new SqlTimestampConverter(null), Timestamp.class);
        //number
        map.put(new BooleanConverter(null), Boolean.class);
        map.put(new ShortConverter(null), Short.class);
        map.put(new IntegerConverter(null), Integer.class);
        map.put(new LongConverter(null), Long.class);
        map.put(new FloatConverter(null), Float.class);
        map.put(new DoubleConverter(null), Double.class);
        map.put(new BigDecimalConverter(null), BigDecimal.class);
        map.put(new BigIntegerConverter(null), BigInteger.class);
        return map;
    }
}
