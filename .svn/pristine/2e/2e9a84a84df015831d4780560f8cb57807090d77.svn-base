package com.tzg.tool.kit.mapper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * 
 * Filename:    XstreamMapper.java  
 * Description: java bean对象和XML之间互相转换  
 * Copyright:   Copyright (c) 2012-2013 All Rights Reserved.
 * Company:     tzg-soft.com Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2012-10-10 上午10:01:36  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2012-10-10      heyiwu      1.0         1.0 Version  
 *
 */
public class XstreamMapper {
    private static XstreamMapper xstreamMapper = getInstance();
    private static XStream       xStream;

    private XstreamMapper() {
    }

    public static XstreamMapper getInstance() {
        if (null == xstreamMapper) {
            xstreamMapper = new XstreamMapper();
            //             new XStream(new DomDriver())需要xpp3 jar;
            //            // new XStream()不需要xpp3 jar;
            xStream = new XStream(new XppDriver(new XmlFriendlyNameCoder("_-", "_")));
        }
        return xstreamMapper;
    }

    /**
     * XStream结合JettisonMappedXmlDriver驱动，转换Java对象到JSON.
     * 需要添加jettison jar
     * 2种驱动:
     * JettisonMappedXmlDriver、JsonHierarchicalStreamDriver
     * @return
     */
    public XStream getJsonXstream() {
        XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);

        //      xstream = new XStream(new JsonHierarchicalStreamDriver());

        //删除根节点
        //        xstream = new XStream(new JsonHierarchicalStreamDriver() {
        //            public HierarchicalStreamWriter createWriter(Writer out) {
        //                return new JsonWriter(out, JsonWriter.DROP_ROOT_MODE);
        //            }
        //        });
        return xstream;
    }

    public static String toXml(Object bean) {
        return toXml(bean, bean.getClass());
    }

    /**
     * 将对象转为XML
     * @param bean
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String toXml(Object bean, Class clazz) {
        return toXml(bean, new ImmutablePair<String, Class>("result", clazz));
    }

    /**
     * 将对象转为XML
     * @param bean
     * @return
     */
    @SafeVarargs
    @SuppressWarnings("rawtypes")
    public static String toXml(Object bean, Pair<String, Class>... alias) {
        return toXml(bean, new ImmutablePair<String, String>(null, "class"), alias);
    }

    @SafeVarargs
    @SuppressWarnings("rawtypes")
    public static String toXml(Object bean, Pair<String, String> sysAttr, Pair<String, Class>... alias) {
        XStream xStream = getXStream();
        //注解支持
        xStream.processAnnotations(bean.getClass());
        if (null == alias || alias.length == 0) {
            //类重命名
            //        xStream.alias(bean.getClass().getSimpleName(), bean.getClass());
            //        //包重命名
            xStream.aliasPackage("", bean.getClass().getPackage().getName());
        } else {
            for (Pair<String, Class> pair : alias) {
                xStream.alias(pair.getLeft(), pair.getRight());
            }
        }
        if (null != sysAttr) {
            xStream.aliasSystemAttribute(sysAttr.getLeft(), sysAttr.getRight());
        }
        return xStream.toXML(bean);
    }

    /**
     * 将XML转为对象
     * @param xml:xml或json字符串
     * @param bean
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Object toBean(String xml, Class<?> clazz) {
        return toBean(xml, clazz, new ImmutablePair<String, String>(null, "class"), new ImmutablePair<String, Class>("result", clazz));
    }

    @SafeVarargs
    @SuppressWarnings("rawtypes")
    public static Object toBean(String xml, Class<?> clas, Pair<String, String> sysAttr, Pair<String, Class>... alias) {
        XStream xStream = getXStream();
        //注解支持
        xStream.processAnnotations(clas);
        if (null == alias || alias.length == 0) {
            //类重命名
            //        getXStream().alias(clas.getSimpleName(), clas);
            //        //包重命名
            xStream.aliasPackage("", clas.getPackage().getName());
        } else {
            for (Pair<String, Class> pair : alias) {
                xStream.alias(pair.getLeft(), pair.getRight());
            }
        }
        if (null != sysAttr) {
            xStream.aliasSystemAttribute(sysAttr.getLeft(), sysAttr.getRight());
        }
        return xStream.fromXML(xml);
    }

    /**
     * bean写入输出流
     * @param outputStream
     * @param bean
     * @throws IOException
     */
    public static void writeObject(OutputStream outputStream, Object bean) throws IOException {
        if (outputStream == null || bean == null) {
            return;
        }
        ObjectOutputStream out = null;
        try {
            out = getXStream().createObjectOutputStream(outputStream);
            out.writeObject(bean);
            out.flush();
        } catch (Exception e) {
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * xml to bean对象
     * @param xml
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object readObject(String xml) throws IOException, ClassNotFoundException {
        StringReader reader = new StringReader(xml);
        ObjectInputStream in = getXStream().createObjectInputStream(reader);
        Object object = in.readObject();
        in.close();
        return object;
    }

    public static XStream getXStream() {
        if (null == xStream) {
            getInstance();
        }
        return xStream;
    }

    public static void setXStream(XStream stream) {
        xStream = stream;
    }

}

/**
 *类型转换器
 */
class SingleValueCalendarConverter implements Converter {

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Calendar calendar = (Calendar) source;
        writer.setValue(String.valueOf(calendar.getTime().getTime()));
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(Long.parseLong(reader.getValue())));
        return calendar;
    }

    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return type.equals(GregorianCalendar.class);
    }
}