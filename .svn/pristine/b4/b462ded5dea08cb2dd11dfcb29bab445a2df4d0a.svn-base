package com.tzg.tool.kit.adapter;

import javax.xml.bind.annotation.XmlElement;

/**
 * Jaxb  MapAdapter
 * @link com.tzg.tool.kit.adapter.MapAdapter.java
 * @link com.tzg.tool.kit.mapper.JaxbMapper.java
 */
public class MapEntry {
    @XmlElement
    public String key;
    @XmlElement
    public Object value;

    @SuppressWarnings("unused")
    private MapEntry() {
    } // Required by JAXB 

    public MapEntry(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}
