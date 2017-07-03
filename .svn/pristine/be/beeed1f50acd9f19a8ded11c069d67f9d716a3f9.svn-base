package com.tzg.ex.mvc.web.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.tzg.tool.kit.file.FileUtils;

public class RestTemplateUtil {
    private static final Logger logger         = LoggerFactory.getLogger(RestTemplateUtil.class.getName());
    private static int          readTimeout    = 8000;
    private static int          connectTimeout = 5000;
    private static RestTemplate restTemplate;
    private final static Object syncLock       = new Object();

    public static RestTemplate getRestTemplate() {
        if (null == restTemplate) {
            synchronized (syncLock) {
                if (null == restTemplate) {
                    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
                    requestFactory.setReadTimeout(readTimeout);
                    requestFactory.setConnectTimeout(connectTimeout);
                    // 添加转换器
                    restTemplate = new RestTemplate(new ArrayList<HttpMessageConverter<?>>() {
                        private static final long serialVersionUID = 102455248343724262L;

                        {
                            add(new ByteArrayHttpMessageConverter());
                            add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
                            add(new ResourceHttpMessageConverter());
                            add(new SourceHttpMessageConverter<>());
                            add(new FormHttpMessageConverter());
//                            add(new MappingJackson2XmlHttpMessageConverter());
                            add(new MappingJackson2HttpMessageConverter());
                            add(new FastJsonHttpMessageConverter());
                        }
                    });
                    restTemplate.setRequestFactory(requestFactory);
                    restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
                }
            }

        }
        return restTemplate;
    }

    public static String doGet(String url) {
        return getRestTemplate().getForObject(url, String.class);
    }

  
    public static String doPost(String url) {
        return getRestTemplate().postForObject(url, HttpEntity.EMPTY, String.class);
    }

    /**
     * post请求
     * @param uri
     * @param params
     * @return
     * @throws Exception 
     */
    public static String doPost(String uri, Map<String, Object> params) throws Exception {
        return doPost(uri, params, null, String.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T doPost(String uri, Map<String, Object> params, HttpHeaders headers, Class<T> result) throws Exception {
        RestTemplate template = getRestTemplate();
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                logger.debug("trust  URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        });
        if (null == headers) {
            headers = getDefaultHeaders();
        }
        HttpEntity entity = new HttpEntity(params, headers);
        ResponseEntity<T> response = template.exchange(uri, HttpMethod.POST, entity, result);
        return response.getBody();
    }
    /**
     * 下载文件
     * @author:  heyiwu 
     * @param url
     * @param method HttpMethod.POST或HttpMethod.GET
     * @param headers 请求头
     * @param httpClient 可为null
     * @param file 下载的文件
     * @return
     */
    public static boolean download(String url, HttpMethod method, HttpHeaders headers, Map<String, Object> params, HttpClient httpClient, File file) {
        try {
            RestTemplate restTemplate = null;
            if (null == httpClient) {
                restTemplate = new RestTemplate();
            } else {
                HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
                factory.setConnectTimeout(connectTimeout);
                factory.setConnectionRequestTimeout(readTimeout);
                // 连接不够用的等待时间，不宜过长
                factory.setConnectionRequestTimeout(200);
                restTemplate = new RestTemplate(factory);
            }

            ResponseEntity<byte[]> response = restTemplate.exchange(url, method, new HttpEntity(params, headers), byte[].class);
            FileUtils.write(new ByteArrayInputStream(response.getBody()), new FileOutputStream(file));
            return true;
        } catch (FileNotFoundException e) {
            logger.error("下载文件:{}异常:", file);
        }
        return false;
    }

    /** 
     * 返回一个请求头 
     * 
     * @param mediaType 请求类型例如MediaType.APPLICATION_JSON 
     * @param headerMap 请求头信息 
     * @return 
     */
    public static HttpHeaders getHeaders(MediaType mediaType, Map<String, String> headerMap) {
        HttpHeaders httpHeaders = new HttpHeaders();
        //设置请求的类型  
        httpHeaders.setContentType(mediaType);
        //请求头信息  
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }
        return httpHeaders;
    }

    /** 
     * 
     * @param mediaType 请求类型例如MediaType.APPLICATION_JSON 
     * @param headerMap 请求头信息 
     * @param body      请求的参数 
     * @return 
     */
    public static HttpEntity getHttpEntity(MediaType mediaType, Map<String, String> headerMap, String body) {
        return new HttpEntity(body, getHeaders(mediaType, headerMap));
    }

    /** 
     * 请求的参数封装 
     * 
     * @param httpHeaders 请求头信息 
     * @param body        请求参数 
     * @return 
     */
    public static HttpEntity getHttpEntity(HttpHeaders httpHeaders, String body) {
        return new HttpEntity(body, httpHeaders);
    }

    public static HttpHeaders getJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Charset", "UTF-8");
        headers.set("contentType", "application/json");
        return headers;
    }

    public static HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Keep-Alive");
        headers.set("Accept-Charset", "UTF-8");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36");
        return headers;
    }

    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new MyTrustManager();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    static class MyTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) throws java.security.cert.CertificateException {
            return;
        }

    }

    public static int getReadTimeout() {
        return readTimeout;
    }

    public static void setReadTimeout(int readTimeout) {
        RestTemplateUtil.readTimeout = readTimeout;
    }

    public static int getConnectTimeout() {
        return connectTimeout;
    }

    public static void setConnectTimeout(int connectTimeout) {
        RestTemplateUtil.connectTimeout = connectTimeout;
    }

}
