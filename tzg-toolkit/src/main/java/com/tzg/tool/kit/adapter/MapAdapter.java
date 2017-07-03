package com.tzg.tool.kit.adapter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Jaxb  MapAdapter
 * @link com.tzg.tool.kit.mapper.JaxbMapper.java
 */
public class MapAdapter extends XmlAdapter<MapEntry[], Map<String, Object>> {
    public MapEntry[] marshal(Map<String, Object> map) throws Exception {
        if (null == map || map.isEmpty()) {
            return null;
        }
        MapEntry[] mapElements = new MapEntry[map.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet())
            mapElements[i++] = new MapEntry(entry.getKey(), entry.getValue());
        return mapElements;
    }

    public Map<String, Object> unmarshal(MapEntry[] map) throws Exception {
        Map<String, Object> unmarshal = new HashMap<String, Object>();
        for (MapEntry entry : map)
            unmarshal.put(entry.key, entry.value);
        return unmarshal;
    }
}