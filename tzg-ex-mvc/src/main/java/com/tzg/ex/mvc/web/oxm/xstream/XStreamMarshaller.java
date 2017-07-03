package com.tzg.ex.mvc.web.oxm.xstream;

import java.util.LinkedHashMap;
import java.util.Map;

public class XStreamMarshaller extends
		org.springframework.oxm.xstream.XStreamMarshaller {

	public void setAliasAttribute(Map<String, Map<String, String>> aliasesAttr)
			throws ClassNotFoundException {
		Map<Class<?>, Map<String, String>> result = toClassMap(aliasesAttr);
		for (Map.Entry<Class<?>, Map<String, String>> entry : result.entrySet()) {
			Map<String, String> map = entry.getValue();
			for (Map.Entry<String, String> attr : map.entrySet()) {
				this.getXStream().aliasAttribute(entry.getKey(), attr.getKey(),
						attr.getValue());
			}
		}
	}

	private Map<Class<?>, Map<String, String>> toClassMap(
			Map<String, Map<String, String>> aliasesAttr)
			throws ClassNotFoundException {
		Map<Class<?>, Map<String, String>> result = new LinkedHashMap<Class<?>, Map<String, String>>(
				aliasesAttr.size());

		for (Map.Entry<?, Map<String, String>> entry : aliasesAttr.entrySet()) {
			Object key = entry.getKey();
			Map<String, String> value = entry.getValue();
			Class type;
			if (key instanceof Class) {
				type = (Class) key;
			} else if (key instanceof String) {
				type = Class.forName((String) key);
			} else {
				throw new IllegalArgumentException("Unknown value [" + value
						+ "], expected String or Class");
			}
			result.put(type, value);
		}
		return result;
	}
}
