package com.tzg.tool.kit.test.http.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientTest.class);

    public static void login(String page, String url, String user, String pwd) {
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        try {
            HttpGet httpget = new HttpGet(page);

            CloseableHttpResponse response1 = null;
            try {
                response1 = httpclient.execute(httpget);
                HttpEntity entity = response1.getEntity();

                logger.info("login page: {}", response1.getStatusLine());
                EntityUtils.consume(entity);

                logger.info("Initial set of cookies:");
                List<Cookie> cookies = cookieStore.getCookies();
                if (!cookies.isEmpty()) {
                    for (int i = 0; i < cookies.size(); i++) {
                        logger.info("- " + cookies.get(i).toString());
                    }
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            } finally {
                try {
                    response1.close();
                } catch (IOException e) {
                    logger.warn(e.getLocalizedMessage());
                }
            }

            HttpPost httpost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("IDToken1", user));
            nvps.add(new BasicNameValuePair("IDToken2", pwd));

            httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            CloseableHttpResponse response2 = null;
            try {
                response2 = httpclient.execute(httpost);
                HttpEntity entity = response2.getEntity();

                logger.info("Login form get: " + response2.getStatusLine());
                EntityUtils.consume(entity);

                logger.info("Post logon cookies:");
                List<Cookie> cookies = cookieStore.getCookies();
                for (int i = 0; i < cookies.size(); i++) {
                    logger.info("- " + cookies.get(i).toString());
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            } finally {
                try {
                    if (null != response2)
                        response2.close();
                } catch (IOException e) {
                    logger.warn(e.getLocalizedMessage());
                }
            }
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.warn(e.getLocalizedMessage());
            }
        }
    }
}
