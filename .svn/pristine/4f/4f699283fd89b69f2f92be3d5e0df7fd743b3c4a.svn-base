package com.tzg.ex.mvc.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.tzg.tool.kit.file.FileUtils;

/**
 * Properties文件载入工具类. 可载入多个properties文件, 相同的属性在最后载入的文件中的值将会覆盖之前的值.
 */
public class PropertiesLoader {

    private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    private static ResourceLoader resourceLoader = new DefaultResourceLoader();

    private Properties properties;

    public static void main(String[] args) {
        System.out.println(new PropertiesLoader("classpath*:/conf/*conf.properties").getProperty("catalina.base"));
    }

    public PropertiesLoader(String... resourcesPaths) {
        properties = loadProperties(resourcesPaths);
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * 取出Property，但以System的Property优先.
     */
    public String getProperty(String key) {
        String result = System.getProperty(key);
        if (result != null) {
            return result;
        }
        return properties.getProperty(key);
    }

    /**
     * 取出Property，但以System的Property优先.
     */
    public String getProperty(String key, String defaultValue) {
        String result = getProperty(key);
        if (result != null) {
            return result;
        } else {
            return defaultValue;
        }
    }

    /**
     * 取出Property，但以System的Property优先.
     */
    public Integer getInteger(String key) {
        return Integer.valueOf(getProperty(key));
    }

    /**
     * 取出Property，但以System的Property优先.
     */
    public Integer getInteger(String key, Integer defaultValue) {
        return Integer.valueOf(getProperty(key, String.valueOf(defaultValue)));
    }

    /**
     * 取出Property，但以System的Property优先.
     */
    public Boolean getBoolean(String key) {
        return Boolean.valueOf(getProperty(key));
    }

    /**
     * 取出Property，但以System的Property优先.
     */
    public Boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.valueOf(getProperty(key, String.valueOf(defaultValue)));
    }

    /**
     * 载入多个文件, 文件路径使用Spring Resource格式.
     */
    private Properties loadProperties(String... resourcesPaths) {
        Properties props = new Properties();
        for (String location : resourcesPaths) {
            logger.debug("Loading properties file:{}", location);
            InputStream is = null;
            try {
                Resource resource = resourceLoader.getResource(location);
                if (location.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
                    Resource[] resources = new PathMatchingResourcePatternResolver().getResources(location);
                    if(null!=resources&&resources.length==1){
                        resource= resources[0];
                    }
                    if(null!=resources&&resources.length>1){
                        for (Resource r : resources) {
                           try{
                               is = r.getInputStream();
                               String encoding = FileUtils.getEncoding(r.getFile());
                               props.load(new BufferedReader(new InputStreamReader(is, encoding)));
                           }catch (IOException ex) {
                               logger.error("Could not load properties from path=[{}],exception:{}", r, ex);
                           }finally{
                               IOUtils.closeQuietly(is);
                           }
                        }
                    }
                }
                if (!resource.exists()) {
                    resource = new FileSystemResourceLoader().getResource(location);
                }
                if (!resource.exists()) {
                    logger.warn("Could not load properties resource=[{}] is not exists", resource);
                    continue;
                }
                is = resource.getInputStream();
                String encoding = FileUtils.getEncoding(resource.getFile());
                props.load(new BufferedReader(new InputStreamReader(is, encoding)));
            } catch (IOException ex) {
                logger.error("Could not load properties from path=[{}],exception:{}", location, ex);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return props;
    }
}
