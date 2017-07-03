package com.tzg.tool.kit.test.mapper.entity;

import info.monitorenter.util.Entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.tzg.tool.kit.file.FileUtils;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)  
public class Property {
    private String             comment;
    @XmlElement(name = "entry", required = true)  
    private static List<Entry> entries = new LinkedList<Entry>();

    public Property() {
    }

    public static void main(String[] args) throws FileNotFoundException {

        //        XstreamMapper.toBean(xml, bean)
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        if (StringUtils.isBlank(value)) {
            value = System.getProperty(key);
            if (StringUtils.isBlank(value)) {
                value = System.getenv(key);
            }
        }
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        if (value == null || "".equals(value.trim())) {
            throw new IllegalStateException("required property is blank by key=" + key);
        }
        return value;
    }

    public Integer getInt(String key) {
        String value = getRequiredProperty(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Integer.parseInt(value);
    }

    public int getInt(String key, int defaultValue) {
        if (getProperty(key) == null) {
            return defaultValue;
        }
        return Integer.parseInt(getRequiredProperty(key));
    }

    public int getRequiredInt(String key) {
        return Integer.parseInt(getRequiredProperty(key));
    }

    public Boolean getBoolean(String key) {
        if (getProperty(key) == null) {
            return null;
        }
        return Boolean.parseBoolean(getRequiredProperty(key));
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (getProperty(key) == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(getRequiredProperty(key));
    }

    public boolean getRequiredBoolean(String key) {
        return Boolean.parseBoolean(getRequiredProperty(key));
    }

    public String getNullIfBlank(String key) {
        String value = getProperty(key);
        if (value == null || "".equals(value.trim())) {
            return null;
        }
        return value;
    }


    public static List<Entry> getEntries() {
        return entries;
    }

    public static void setEntries(List<Entry> entries) {
        Property.entries = entries;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
