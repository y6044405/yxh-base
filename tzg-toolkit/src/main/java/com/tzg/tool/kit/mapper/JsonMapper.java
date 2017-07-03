package com.tzg.tool.kit.mapper;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 简单封装Jackson，实现JSON String<->Java Object的Mapper.
 * 封装不同的输出风格, 使用不同的builder函数创建实例.
 * 
 * 被fastjson取代，效率更高
 */
@Deprecated
public class JsonMapper {

    private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

    private ObjectMapper  mapper;

    public JsonMapper() {
        this(Include.ALWAYS);
    }

    public JsonMapper(Include include) {
        mapper = new ObjectMapper();
        //设置输出时包含属性的风格
        mapper.setSerializationInclusion(include);
        //设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     */
    public static JsonMapper nonEmptyMapper() {
        return new JsonMapper(Include.NON_EMPTY);
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     */
    public static JsonMapper nonDefaultMapper() {
        return new JsonMapper(Include.NON_DEFAULT);
    }

    /**
     * Object可以是POJO，也可以是Collection或数组。
     * 如果对象为Null, 返回"null".
     * 如果集合为空集合, 返回"[]".
     */
    public String toJson(Object object) {
        try {
            return writeValueAsString(object);
        } catch (IOException e) {
            return new Gson().toJson(object);
        }
    }

    /**
     * 输出JSON格式
     * @param object
     * @return
     * @throws IOException
     * @throws JsonGenerationException
     * @throws JsonMappingException
     */
    public String writeValueAsString(Object object) throws IOException, JsonGenerationException, JsonMappingException {
        return mapper.writeValueAsString(object);
    }

    /**
     * 输出JSONP格式数据.
     */
    public String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object));
    }

    /**
     * 输出json格式数据
     * @param writer
     * @param bean
     */
    public void writeValue(Writer writer, Object bean) {
        try {
            mapper.writeValue(writer, bean);
        } catch (JsonGenerationException e) {
            logger.warn("write bean:{} to json string error:{}", bean, e.getLocalizedMessage());
        } catch (JsonMappingException e) {
            logger.warn("write bean:{} to json string error:{}", bean, e.getLocalizedMessage());
        } catch (IOException e) {
            logger.warn("write bean:{} to json string error:{}", bean, e.getLocalizedMessage());
        }
    }

    /**
     * 输出json格式数据
     * @param writer
     * @param bean
     */
    public void writeObject(Writer writer, Object bean) {
        try {
            com.fasterxml.jackson.core.JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(writer);
            generator.writeObject(bean);
        } catch (IOException e) {
            logger.warn("write bean:{} to json string error:{}", bean, e.getLocalizedMessage());
        }
    }

    /**
     * 反序列化POJO或简单Collection如List<String>.
     * 
     * 如果JSON字符串为Null或"null"字符串, 返回Null.
     * 如果JSON字符串为"[]", 返回空集合.
     * 
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String,JavaType)
     * @see #fromJson(String, JavaType)
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }

        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            logger.warn("parse json string=[{}] error:{}", jsonString, e);
            return null;
        }
    }

    /**
     * 反序列化复杂Collection如List<Bean>, 先使用函數createCollectionType构造类型,然后调用本函数.
     * @see #createCollectionType(Class, Class...)
     */
    public <T> T fromJson(String jsonString, JavaType javaType) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }

        try {
            return (T) mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 构造泛型的Collection Type如:
     * ArrayList<MyBean>, 则调用constructCollectionType(ArrayList.class,MyBean.class)
     * HashMap<String,MyBean>, 则调用(HashMap.class,String.class, MyBean.class)
     */
    public JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 当JSON里只含有Bean的部分属性性时，更新一個已存在Bean，只覆蓋该部分的属性.
     */
    public <T> T update(String jsonString, T object) {
        try {
            return (T) mapper.readerForUpdating(object).readValue(jsonString);
        } catch (JsonProcessingException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        } catch (IOException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
        return null;
    }

    /**
     * 设定是否使用Enum的toString方法来读取Enum,
     * 为False时使用Enum的name()函數來讀寫Enum, 默認為False.
     * 注意本函數一定要在Mapper創建後, 所有的讀寫動作之前調用.
     */
    public void enableEnumUseToString() {
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * 判断是否json格式的字符串
     * @param json 字符串
     * @return
     */
    public static boolean isJson(String json) {
        try {
            if (StringUtils.isBlank(json) || StringUtils.length(json) <= 1)
                return false;
            new com.google.gson.JsonParser().parse(json);
            new JSONObject(json);
        } catch (Exception e) {
            try {
                new JSONArray(json);
            } catch (JSONException e1) {
                return false;
            }
        }
        return true;
    }

    /**
     * new GsonBuilder对象
     * @return
     */
    public static GsonBuilder getGsonBuilder() {
        return new GsonBuilder();
    }

    /**
     * 过滤/排除属性
     * 实现ExclusionStrategy接口即可
     * @param strategies
     */
    public static void setExclusionStrategies(GsonBuilder gsonBuilder, ExclusionStrategy... strategies) {
        gsonBuilder.setExclusionStrategies(strategies);
    }

    /**
     * 注册TypeAdapter处理枚举类
     * @param gsonBuilder 
     * @param enums 枚举类class
     * @param typeAdapter 实现JsonSerializer<T>(对象转json), JsonDeserializer<T>(json转对象)接口的实现类 或继承TypeAdapter<T>类
     */
    public void registerTypeAdapter(GsonBuilder gsonBuilder, Class<?> enums, Object typeAdapter) {
        gsonBuilder.registerTypeHierarchyAdapter(enums, typeAdapter);

    }
}
