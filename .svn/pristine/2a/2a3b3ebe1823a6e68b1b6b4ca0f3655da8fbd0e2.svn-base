package com.tzg.tool.kit.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

public class FastjsonMapper {
    private static Logger LOG = LoggerFactory.getLogger(FastjsonMapper.class);

    /**
     * 
     * @Description:把JSON文本parse为JSONObject或者JSONArray 
     * @param json
     * @return
     */
    public static Object parse(String json) {
        return JSON.parse(json);
    }

    /**
     * 
     * @Description:把JSON文本parse成JSONObject    
     * @param json
     * @return
     */
    public static JSONObject parseObject(String json) {
        return JSON.parseObject(json);
    }

    /**
     * 
     * @Description:将json字符串解析为一个JavaBean
     * @param json
     * @param cls
     * @return
     */
    public static <T> T toBean(String json, Class<T> cls) {
        T t = null;
        try {
            t = JSON.parseObject(json, cls);
        } catch (Exception e) {
            LOG.error("json string=[{}] to class=[{}] bean,exception:{}", json, cls, e);
        }
        return t;
    }

    /**
     * @Description:把JSON文本parse成JSONArray 
     * @param json
     * @return
     */
    public static JSONArray parseArray(String json) {
        return JSON.parseArray(json);
    }

    public static <T> T parseObject(String json, Class<T> cls) {
        return JSONObject.parseObject(json, cls);
    }

    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        return JSONObject.parseObject(json.toString(), typeReference);
    }

    /**
     * 
     * @Description:将json字符串 解析成为一个 List<JavaBean> 及 List<String>
     * @param json
     * @param cls
     * @return
     */
    public static <T> List<T> toListBean(String json, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            list = JSON.parseArray(json, cls);
        } catch (Exception e) {
            LOG.error("json string=[{}] to list class=[{}] bean,exception:{}", json, cls, e);
        }
        return list;
    }

    /**
     * 用fastjson 将jsonString 解析成 List<Map<String,Object>>
     * 
     * @param json
     * @return
     */
    public static List<Map<String, Object>> toListMap(String json) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            // 两种写法
            //            list = JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
            //            }.getType());
            list = JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            LOG.error("json string=[{}] to list map bean,exception:{}", json, e);
        }
        return list;
    }

    /**
     * 
     * @Description:将javabean序列化为JSON文本
     * @param t:class
     * @param features: SerializerFeature.PrettyFormat
     * <p>
     * DisableCheckSpecialChar：一个对象的字符串属性中如果有特殊字符如双引号，将会在转成json时带有反斜杠转移符。如果不需要转义，可以使用这个属性。默认为false 
     * QuoteFieldNames———-输出key时是否使用双引号,默认为true 
     * WriteMapNullValue——–是否输出值为null的字段,默认为false 
     * WriteNullNumberAsZero—-数值字段如果为null,输出为0,而非null 
     * WriteNullListAsEmpty—–List字段如果为null,输出为[],而非null 
     * WriteNullStringAsEmpty—字符类型字段如果为null,输出为”“,而非null 
     * WriteNullBooleanAsFalse–Boolean字段如果为null,输出为false,而非null
    </p>
     * @return
     */
    public static String toJsonStr(Object object, SerializerFeature... features) {
        return JSONObject.toJSONString(object, features);
    }
    /**
     * 
     * @Description:将javabean序列化为JSON文本
     * @param object
     * @param config:序列化过程的特殊配置,示例：
     * SerializeConfig mapping = new SerializeConfig();
     * mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss")); 
     * @param features
     * @return
     */
    public static final String toJSONString(Object object, SerializeConfig config, SerializerFeature... features) {
        return JSONObject.toJSONString(object,config, features);
    }
    /**
     * 
     * @Description:指定日期格式将javabean序列化为JSON文本
     * @param object
     * @param dateFormat:日期格式，如:"yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String toJsonStr(Object object, String dateFormat) {
        return JSON.toJSONStringWithDateFormat(object, dateFormat);
    }

    /**
     * 
     * @Description:将JavaBean序列化为JSON文本 
     * @param object
     * @return
     */
    public static String toJsonStr(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * 
     * @Description:将JavaBean序列化为带格式的JSON文本 
     * @param object
     * @param prettyFormat：是否格式化
     * @return
     */
    public static String toJsonStr(Object object, boolean prettyFormat) {
        return JSON.toJSONString(object, prettyFormat);
    }

    /**
     * 
     * @Description:将JavaBean转换为JSONObject或者JSONArray
     * @param object
     * @return
     */
    public static Object toJson(Object object) {
        return JSON.toJSON(object);
    }
}
