package com.tzg.tool.kit.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tzg.tool.kit.asserts.Assert;
import com.tzg.tool.kit.file.FileUtils;

/**
 * 封装了采用HttpClient发送HTTP请求的方法
 */
public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    public static final String  CONTENT_TYPE_JSON_CHARSET = "application/json;charset=gbk";
    public static final String  CONTENT_TYPE_XML_CHARSET  = "application/xml;charset=gbk";
    // 读取内容时使用的字符集
    public static final String  CONTENT_CHARSET           = "GBK";
    public static final Charset UTF_8                     = Consts.UTF_8;
    public static final Charset GBK                       = Charset.forName(CONTENT_CHARSET);
    // 连接超时时间
    public static int           connectTimeout            = 360000;
    // 读取数据超时时间
    public static int           socketTimeout             = 360000;

    private static int bufferSize = 1024;

    private static volatile HttpClientUtils instance;

    private static ConnectionConfig connConfig;

    private static SocketConfig socketConfig;

    private static ConnectionSocketFactory plainSF;

    private static KeyStore trustStore;

    private static SSLContext sslContext;

    private static LayeredConnectionSocketFactory sslSF;

    private static Registry<ConnectionSocketFactory> registry;

    private static PoolingHttpClientConnectionManager connManager;

    private volatile static HttpClient client;

    private volatile static BasicCookieStore cookieStore;

    public static String defaultEncoding = "utf-8";

    private static void init() {
        // 设置连接参数
        connConfig = ConnectionConfig.custom().setCharset(Charset.forName(defaultEncoding)).build();
        socketConfig = SocketConfig.custom().setSoTimeout(100000).build();
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create();
        plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        // 指定信任密钥存储对象和连接套接字工厂
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(trustStore, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslSF = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            registryBuilder.register("https", sslSF);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        registry = registryBuilder.build();
        // 设置连接管理器
        connManager = new PoolingHttpClientConnectionManager(registry);
        connManager.setDefaultConnectionConfig(connConfig);
        connManager.setDefaultSocketConfig(socketConfig);
        // 指定cookie存储对象
        cookieStore = new BasicCookieStore();
        // 构建客户端
        client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).setConnectionManager(connManager).build();
    }

    public static HttpClientUtils getInstance() {
        synchronized (HttpClientUtils.class) {
            if (instance == null) {
                instance = new HttpClientUtils();
            }
            return instance;
        }
    }

    private static CloseableHttpClient getHttpClient() {
        return buildHttpClient(false);
    }

    /**
     * 设置请求和传输超时时间
     * 
     * @return
     */
    public static RequestConfig buildRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
    }

    /**
     * 创建HttpClient
     * 
     * @param isMultiThread
     * @return
     */
    public static CloseableHttpClient buildHttpClient(boolean isMultiThread) {
        CloseableHttpClient client;
        if (isMultiThread)
            client = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
        else
            client = HttpClientBuilder.create().setDefaultRequestConfig(buildRequestConfig()).build();
        // 设置代理服务器地址和端口
        // client.getHostConfiguration().setProxy("proxy_host_addr",proxy_port);
        return client;
    }

    /**
     * 发送HTTP GET请求
     * 
     * @param url
     *            请求地址(含参数)
     * @return 响应内容
     */
    public static String doGet(String url) {
        return doGet(url, null);
    }

    public static InputStream getInputStream(String url) throws URISyntaxException, ClientProtocolException, IOException {
        HttpResponse response = getHttpResponse(url, null);
        return response != null ? response.getEntity().getContent() : null;
    }

    /**
     * 基本的Get请求
     * 
     * @param url
     *            请求url
     * @param queryParams
     *            请求头的查询参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static HttpResponse getHttpResponse(String url, Map<String, String> queryParams) throws URISyntaxException, ClientProtocolException, IOException {
        HttpGet gm = new HttpGet();
        URIBuilder builder = new URIBuilder(url);
        // 填入查询参数
        if (queryParams != null && !queryParams.isEmpty()) {
            builder.setParameters(toNameValuePair(queryParams));
        }
        gm.setURI(builder.build());
        return client.execute(gm);
    }

    /**
     * 发送HTTP GET请求
     * 
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String, String> params) {
        return doGet(url, params, CONTENT_CHARSET);
    }

    /**
     * 发送HTTP GET请求
     * 
     * @param url
     * @param params
     * @param charset
     * @return
     */
    public static String doGet(String url, Map<String, String> params, String charset) {
        HttpEntity entity = null;
        try {
            HttpClient client = buildHttpClient(false);
            HttpGet get = buildHttpGet(url, params);
            HttpResponse response = client.execute(get);
            assertStatus(response);
            entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            return EntityUtils.toString(entity, getCharset(entity, charset));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != entity)
                    EntityUtils.consume(entity);
            } catch (IOException e) {
            }
        }
    }

    /**
     * 获取响应字符集 1、获取响应消息头中Content-Type的charset值作为响应报文的解码字符集
     * 2、无Content-Type属性,则会使用指定的默认字符集
     * 3、没有指定默认字符集、则使用HttpClient内部默认的ISO-8859-1作为响应报文的解码字符集
     * 
     * @param entity
     * @param defaultCharset
     * @return
     */
    private static Charset getCharset(HttpEntity entity, String defaultCharset) {
        ContentType contentType = ContentType.get(entity);
        if (null != contentType) {
            return contentType.getCharset();
        }
        if (StringUtils.isBlank(defaultCharset)) {
            return ContentType.getOrDefault(entity).getCharset();
        }
        return Charset.forName(defaultCharset);
    }

    /**
     * 发送http post请求
     * 
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, Map<String, String> params) {
        return doPost(url, params, CONTENT_CHARSET);
    }

    /**
     * 发送http post请求
     * 
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, Map<String, String> params, String charset) {
        HttpEntity entity = null;
        try {
            HttpClient client = buildHttpClient(false);
            HttpPost postMethod = buildHttpPost(url, params, charset);
            HttpResponse response = client.execute(postMethod);
            assertStatus(response);
            entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, getCharset(entity, charset));
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (URISyntaxException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (ClientProtocolException e) {
            logger.error(e.getLocalizedMessage(), e);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            try {
                if (null != entity)
                    EntityUtils.consume(entity);
            } catch (IOException e) {
            }
        }

        return null;
    }

    /**
     * 构建httpPost对象
     * 
     * @param url
     * @param headers
     * @return
     * @throws UnsupportedEncodingException
     * @throws URISyntaxException
     */
    public static HttpPost buildHttpPost(String url, Map<String, String> params, String charset) throws UnsupportedEncodingException, URISyntaxException {
        Assert.notNull(url, "url不能为null");
        HttpPost post = new HttpPost(url);
        setCommonHttpMethod(post, charset);
        setHttpEntity(post, params, charset);
        // 在RequestContent.process中会自动写入消息体的长度，自己不用写入，写入反而检测报错
        // setContentLength(post, he);
        return post;

    }

    /**
     * 构建httpGet对象
     * 
     * @param url
     * @param headers
     * @return
     * @throws URISyntaxException
     */
    public static HttpGet buildHttpGet(String url, Map<String, String> params) throws URISyntaxException {
        Assert.notNull(url, "url不能为null");
        return new HttpGet(buildGetUrl(url, params));
    }

    /**
     * 组装HTTP get请求url
     * 
     * @param url
     * @param params
     * @return
     */
    private static String buildGetUrl(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }
        StringBuffer buffer = new StringBuffer(url);
        List<NameValuePair> ps = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            ps.add(new BasicNameValuePair(key, params.get(key)));
        }
        buffer.append("?");
        buffer.append(URLEncodedUtils.format(ps, UTF_8));
        return buffer.toString();
    }

    /**
     * 设置HttpMethod通用配置
     * 
     * @param httpMethod
     */
    public static void setCommonHttpMethod(HttpRequestBase httpMethod, String charset) {
        httpMethod.setHeader(HTTP.CONTENT_ENCODING, CONTENT_CHARSET);
        // httpMethod.setHeader(HTTP.CHARSET_PARAM, CONTENT_CHARSET);
        // httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_JSON_CHARSET);
        // httpMethod.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_XML_CHARSET);
        // httpMethod.setHeader(HTTP.CONTENT_TYPE,
        // "application/x-www-form-urlencoded; charset=" + charset);
    }

    /**
     * 设置成消息体的长度
     * 
     * @param httpMethod
     * @param entity
     */
    public static void setContentLength(HttpRequestBase httpMethod, HttpEntity entity) {
        if (entity == null) {
            return;
        }
        httpMethod.setHeader(HTTP.CONTENT_LEN, String.valueOf(entity.getContentLength()));
    }

    /**
     * 强验证必须是200状态否则报异常
     * 
     * @param res
     * @throws HttpException
     */
    static void assertStatus(HttpResponse res) throws IOException {
        Assert.notNull(res, "http响应对象为null");
        Assert.notNull(res.getStatusLine(), "http响应对象的状态为null");
        switch (res.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
                // case HttpStatus.SC_CREATED:
                // case HttpStatus.SC_ACCEPTED:
                // case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION:
                // case HttpStatus.SC_NO_CONTENT:
                // case HttpStatus.SC_RESET_CONTENT:
                // case HttpStatus.SC_PARTIAL_CONTENT:
                // case HttpStatus.SC_MULTI_STATUS:
                break;
            default:
                throw new IOException("服务器响应状态异常,失败.");
        }
    }

    public HttpClientUtils(final int connectTimeout, final int socketTimeout) {
        HttpClientUtils.connectTimeout = connectTimeout;
        HttpClientUtils.socketTimeout = socketTimeout;
    }

    public HttpClientUtils() {
    }

    /**
     * 发送HTTP GET请求
     * 
     * @param targetScheme
     *            请求的HTTP scheme:http/https
     * @param targetHost
     *            域名/IP
     * @param targetPort
     *            请求端口
     * @param proxyScheme
     *            代理服务器的HTTP访问协议scheme:http/https
     * @param proxyHost
     *            代理服务器的域名/IP
     * @param proxyPort
     *            代理服务器的服务端口
     * @return
     */
    public static String doProxyGet(String targetScheme, String targetHost, int targetPort, String proxyScheme, String proxyHost, int proxyPort) {
        String resp = "";
        try {
            HttpHost target = new HttpHost(targetHost, targetPort, targetScheme);
            HttpHost proxy = new HttpHost(proxyHost, proxyPort, proxyScheme);

            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            HttpGet request = new HttpGet("/");
            request.setConfig(config);

            logger.info("EXEC: target=[{}],proxy=[{}]", target, proxy);
            CloseableHttpResponse response = getHttpClient().execute(target, request);
            try {
                HttpEntity entity = response.getEntity();
                logger.info(response.getStatusLine().toString());
                Header[] headers = response.getAllHeaders();
                for (Header header : headers) {
                    logger.info(header.toString());
                }
                if (entity != null) {
                    resp = EntityUtils.toString(entity);
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            } finally {
                response.close();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return resp;
    }

    private static void setHttpEntity(HttpPost httpPost, Map<String, String> params, String charset) throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) {
            return;
        }
        httpPost.setEntity(new UrlEncodedFormEntity(toNameValuePair(params), charset));
    }

    /**
     * 发送HTTP_POST_SSL请求 默认请求443端口,也可在URL参数指定SSL端口
     * 
     * @param url
     *            请求地址
     * @param pars
     *            请求参数
     * @param charset
     *            编码字符集
     * @return 远程主机响应正文
     */
    public static String doSSLPost(String url, Map<String, String> pars, String charset) {
        String resp = null;
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(1000).setSocketTimeout(20000).setConnectTimeout(10000).build();

        // 创建TrustManager()
        // 用于解决javax.net.ssl.SSLPeerUnverifiedException: peer not authenticated
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        // 创建HostnameVerifier
        // 用于解决javax.net.ssl.SSLException: hostname in certificate didn't match:
        // <123.125.97.66> != <123.125.97.241>
        X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            @Override
            public void verify(String host, X509Certificate cert) throws SSLException {
            }

            @Override
            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            }

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        CloseableHttpResponse response = null;
        try {
            // TLS1.0与SSL3.0基本上没有太大的差别,可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext

            SSLContext sslContext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            // 使用TrustManager来初始化该上下文,TrustManager只是被SSL的Socket所使用
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            // 创建SSLSocketFactory
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpClients.custom().setSSLSocketFactory(sslsf).build();
            SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
            // 通过SchemeRegistry将SSLSocketFactory注册到HttpClient上
            getHttpClient().getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext, hostnameVerifier)).build();
            // 创建HttpPost
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(config);
            // 构建POST请求的表单参数
            if (null != pars) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : pars.entrySet()) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(formParams, charset));
            }
            response = getHttpClient().execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                resp = EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset());
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            logger.error("httpClient doSSLPost[{}] exception：", url, e);
        }
        return resp;
    }

    /**
     * 发起HTTP SSL GET请求
     * 
     * @param url
     * @param keyStore
     *            存放证书、密钥的证书库文件
     * @param passwd
     *            密码
     * @return
     */
    public static String doSSLGet(String url, File keyStore, String passwd) {
        String resp = null;
        CloseableHttpClient httpclient = null;
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream instream = new FileInputStream(keyStore);
            try {
                trustStore.load(instream, passwd.toCharArray());
            } catch (CertificateException e) {
                logger.error(e.getLocalizedMessage());
            } finally {
                try {
                    instream.close();
                } catch (Exception ignore) {
                }
            }
            // 相信自己的CA和所有自签名的证书
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
            // 只允许使用TLSv1协议
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            // new SSLConnectionSocketFactory(sslcontext,
            // SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            HttpGet httpget = new HttpGet(url);
            logger.info("exec request:{}", httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                logger.info("response status line:{}", response.getStatusLine());
                if (entity != null) {
                    logger.info("Response content length: {}", entity.getContentLength());
                    resp = EntityUtils.toString(entity);
                    EntityUtils.consume(entity);
                }
            } finally {
                response.close();
            }
        } catch (ParseException e) {
            logger.error(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        } catch (KeyManagementException e) {
            logger.error(e.getLocalizedMessage());
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getLocalizedMessage());
        } catch (KeyStoreException e) {
            logger.error(e.getLocalizedMessage());
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    logger.warn(e.getLocalizedMessage());
                }
            }
        }
        return resp;
    }

    /**
     * 发起HTTP SSL GET请求
     * 
     * @param url
     * @param host
     * @param port
     * @param userName
     *            用户名
     * @param pwd
     *            密码
     * @return
     */
    public static String doSSlGet(String url, String host, int port, String userName, String pwd) {
        String resp = "";
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(userName, pwd));
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        try {
            HttpGet httpget = new HttpGet(url);

            logger.info("exec:{}", httpget.getRequestLine());
            CloseableHttpResponse response = null;
            try {
                response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();

                logger.info(response.getStatusLine().toString());
                if (entity != null) {
                    logger.info("Response content length: " + entity.getContentLength());
                    resp = EntityUtils.toString(entity);
                }
                EntityUtils.consume(entity);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            } finally {
                try {
                    if (null != response)
                        response.close();
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
        return resp;
    }

    /**
     * 上传文件
     * 
     * @param url
     * @param file
     *            上传的文件
     * @return
     */
    public static long upload(String url, File file) {
        return upload(url, getHttpEntity(file));
    }

    /**
     * 上传文件
     * 
     * @param url
     * @param file
     *            文件路径
     * @return
     */
    public static long upload(String url, String file) {
        return upload(url, getHttpEntity(file));
    }

    /**
     * 上传文件
     * 
     * @param url
     * @param reqEntity
     * @return
     */
    private static long upload(String url, HttpEntity reqEntity) {
        long len = -1;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(reqEntity);
            logger.info("executing request " + httpPost.getRequestLine());
            CloseableHttpResponse response = getHttpClient().execute(httpPost);
            try {
                logger.info("response status line:{}", response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    len = resEntity.getContentLength();
                    logger.info("Response content length: {},chunked:", len, resEntity.isChunked());
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            logger.error(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        } finally {
            try {
                getHttpClient().close();
            } catch (IOException e) {
                logger.warn(e.getLocalizedMessage());
            }
        }
        return len;
    }

    private static HttpEntity getHttpEntity(Object file) {
        if (file instanceof String) {
            try {
                InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream((String) file), -1);
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true);
                return reqEntity;
            } catch (FileNotFoundException e) {
                logger.error("file[{}] not found:{}", file, e.getLocalizedMessage());
            }
        }
        FileBody fileBody = new FileBody((File) file);
        StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);
        return MultipartEntityBuilder.create().addPart("bin", fileBody).addPart("comment", comment).build();
    }

    /**
     * 获取目标url的本地cookie
     * 
     * @param url
     * @return
     */
    public static List<Cookie> getCookies(String url) {
        List<Cookie> cookies = null;
        try {
            CookieStore cookieStore = new BasicCookieStore();
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setCookieStore(cookieStore);
            HttpGet httpget = new HttpGet(url);
            logger.info("EXEC:{}", httpget.getURI());
            CloseableHttpResponse response = getHttpClient().execute(httpget, localContext);
            try {
                HttpEntity entity = response.getEntity();
                logger.info(response.getStatusLine().toString());
                if (entity != null) {
                    logger.info("Response content length: {}", entity.getContentLength());
                }
                cookies = cookieStore.getCookies();
                for (Cookie cookie : cookies) {
                    logger.info("local cookie:{}", cookie);
                }
                EntityUtils.consume(entity);
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            } finally {
                response.close();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        } finally {
            try {
                getHttpClient().close();
            } catch (IOException e) {
                logger.warn(e.getLocalizedMessage());
            }
        }
        return cookies;
    }

    /**
     * 批量执行HTTP get请求
     * 
     * @param urls
     *            请求地址数据
     * @param max
     *            最大连接数
     */
    public void doGet(String[] urls, final int max) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(max);
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(cm).build();
        try {
            HttpGetThread[] threads = new HttpGetThread[urls.length];
            for (int i = 0; i < threads.length; i++) {
                HttpGet httpget = new HttpGet(urls[i]);
                threads[i] = new HttpGetThread(httpclient, httpget, i + 1);
            }
            for (HttpGetThread httpGetThread : threads) {
                httpGetThread.start();
            }
            for (HttpGetThread httpGetThread : threads) {
                httpGetThread.join();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.warn(e.getLocalizedMessage());
            }
        }

    }

    static class HttpGetThread extends Thread {

        private final CloseableHttpClient httpClient;
        private final HttpContext         context;
        private final HttpGet             httpget;
        private final int                 id;

        public HttpGetThread(CloseableHttpClient httpClient, HttpGet httpget, int id) {
            this.httpClient = httpClient;
            this.context = new BasicHttpContext();
            this.httpget = httpget;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                logger.info("[{}] EXEC:", id, httpget.getURI());
                CloseableHttpResponse response = httpClient.execute(httpget, context);
                try {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        logger.info("[{}] resp:{}", id, EntityUtils.toString(entity));
                    }
                } finally {
                    response.close();
                }
            } catch (Exception e) {
                logger.info("[{}] error:{} ", id, e);
            }
        }

    }

    public static String doGetForString(String url) throws URISyntaxException, ClientProtocolException, IOException {
        return read(getInputStream(url), null);
    }

    public static InputStream doGetForStream(String url, Map<String, String> queryParams) throws URISyntaxException, ClientProtocolException, IOException {
        HttpResponse response = getHttpResponse(url, queryParams);
        return response != null ? response.getEntity().getContent() : null;
    }

    public static String doGetForString(String url, Map<String, String> queryParams) throws URISyntaxException, ClientProtocolException, IOException {
        return read(doGetForStream(url, queryParams), null);
    }

    public static InputStream doPostForStream(String url, Map<String, String> queryParams) throws URISyntaxException, ClientProtocolException, IOException {
        HttpResponse response = doPostHttpResponse(url, queryParams, null);
        return response != null ? response.getEntity().getContent() : null;
    }

    public static String doPostForString(String url, Map<String, String> queryParams) throws URISyntaxException, ClientProtocolException, IOException {
        return read(doPostForStream(url, queryParams), null);
    }

    public static InputStream doPostForStream(String url, Map<String, String> queryParams, Map<String, String> formParams) throws URISyntaxException, ClientProtocolException,
                                                                                                                           IOException {
        HttpResponse response = doPostHttpResponse(url, queryParams, formParams);
        return response != null ? response.getEntity().getContent() : null;
    }

    public static String doPostRetString(String url, Map<String, String> queryParams, Map<String, String> formParams) throws URISyntaxException, ClientProtocolException,
                                                                                                                      IOException {
        return read(doPostForStream(url, queryParams, formParams), null);
    }

    /**
     * 基本的Post请求
     * 
     * @param url
     *            请求url
     * @param queryParams
     *            请求头的查询参数
     * @param formParams
     *            post表单的参数
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static HttpResponse doPostHttpResponse(String url, Map<String, String> queryParams, Map<String, String> formParams) throws URISyntaxException, ClientProtocolException,
                                                                                                                               IOException {
        HttpPost pm = new HttpPost();
        URIBuilder builder = new URIBuilder(url.replaceAll(" ", "%20"));
        // 填入查询参数
        if (queryParams != null && !queryParams.isEmpty()) {
            builder.setParameters(toNameValuePair(queryParams));
        }
        pm.setURI(builder.build());
        // 填入表单参数
        if (formParams != null && !formParams.isEmpty()) {
            pm.setEntity(new UrlEncodedFormEntity(toNameValuePair(formParams)));
        }
        return client.execute(pm);
    }

    /**
     * 多块Post请求
     * 
     * @param url
     *            请求url
     * @param queryParams
     *            请求头的查询参数
     * @param formParts
     *            post表单的参数,支持字符串-文件(FilePart)和字符串-字符串(StringPart)形式的参数
     * @param maxCount
     *            最多尝试请求的次数
     * @return
     * @throws URISyntaxException
     * @throws ClientProtocolException
     * @throws HttpException
     * @throws IOException
     */
    public HttpResponse multipartPost(String url, Map<String, String> queryParams, List<FormBodyPart> formParts) throws URISyntaxException, ClientProtocolException, IOException {
        HttpPost pm = new HttpPost();
        URIBuilder builder = new URIBuilder(url);
        // 填入查询参数
        if (queryParams != null && !queryParams.isEmpty()) {
            builder.setParameters(toNameValuePair(queryParams));
        }
        pm.setURI(builder.build());
        // 填入表单参数
        if (formParts != null && !formParts.isEmpty()) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder = entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            for (FormBodyPart formPart : formParts) {
                entityBuilder = entityBuilder.addPart(formPart.getName(), formPart.getBody());
            }
            pm.setEntity(entityBuilder.build());
        }
        return client.execute(pm);
    }

    /**
     * 获取当前Http客户端状态中的Cookie
     * 
     * @param domain
     *            作用域
     * @param port
     *            端口 传null 默认80
     * @param path
     *            Cookie路径 传null 默认"/"
     * @param useSecure
     *            Cookie是否采用安全机制 传null 默认false
     * @return
     */
    public Map<String, Cookie> getCookie(String domain, Integer port, String path, Boolean useSecure) {
        if (domain == null) {
            return null;
        }
        if (port == null) {
            port = 80;
        }
        if (path == null) {
            path = "/";
        }
        if (useSecure == null) {
            useSecure = false;
        }
        List<Cookie> cookies = cookieStore.getCookies();
        if (cookies == null || cookies.isEmpty()) {
            return null;
        }

        CookieOrigin origin = new CookieOrigin(domain, port, path, useSecure);
        BestMatchSpec cookieSpec = new BestMatchSpec();
        Map<String, Cookie> retVal = new HashMap<String, Cookie>();
        for (Cookie cookie : cookies) {
            if (cookieSpec.match(cookie, origin)) {
                retVal.put(cookie.getName(), cookie);
            }
        }
        return retVal;
    }

    /**
     * 批量设置Cookie
     * 
     * @param cookies
     *            cookie键值对图
     * @param domain
     *            作用域 不可为空
     * @param path
     *            路径 传null默认为"/"
     * @param useSecure
     *            是否使用安全机制 传null 默认为false
     * @return 是否成功设置cookie
     */
    public boolean setCookie(Map<String, String> cookies, String domain, String path, Boolean useSecure) {
        synchronized (cookieStore) {
            if (domain == null) {
                return false;
            }
            if (path == null) {
                path = "/";
            }
            if (useSecure == null) {
                useSecure = false;
            }
            if (cookies == null || cookies.isEmpty()) {
                return true;
            }
            Set<Entry<String, String>> set = cookies.entrySet();
            String key = null;
            String value = null;
            for (Entry<String, String> entry : set) {
                key = entry.getKey();
                if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                    throw new IllegalArgumentException("cookies key and value both can not be empty");
                }
                BasicClientCookie cookie = new BasicClientCookie(key, value);
                cookie.setDomain(domain);
                cookie.setPath(path);
                cookie.setSecure(useSecure);
                cookieStore.addCookie(cookie);
            }
            return true;
        }
    }

    /**
     * 设置单个Cookie
     * 
     * @param key
     *            Cookie键
     * @param value
     *            Cookie值
     * @param domain
     *            作用域 不可为空
     * @param path
     *            路径 传null默认为"/"
     * @param useSecure
     *            是否使用安全机制 传null 默认为false
     * @return 是否成功设置cookie
     */
    public boolean setCookie(String key, String value, String domain, String path, Boolean useSecure) {
        Map<String, String> cookies = new HashMap<String, String>();
        cookies.put(key, value);
        return setCookie(cookies, domain, path, useSecure);
    }

    private static List<NameValuePair> toNameValuePair(Map<String, String> params) {
        List<NameValuePair> list = new LinkedList<NameValuePair>();
        Set<Entry<String, String>> paramsSet = params.entrySet();
        for (Entry<String, String> paramEntry : paramsSet) {
            list.add(new BasicNameValuePair(paramEntry.getKey(), paramEntry.getValue()));
        }
        return list;
    }

    public static String read(InputStream in, String encoding) {
        if (in == null) {
            return null;
        }
        InputStreamReader inReader = null;
        try {
            if (encoding == null) {
                inReader = new InputStreamReader(in, defaultEncoding);
            } else {
                inReader = new InputStreamReader(in, encoding);
            }
            char[] buffer = new char[bufferSize];
            int readLen = 0;
            StringBuffer sb = new StringBuffer();
            while ((readLen = inReader.read(buffer)) != -1) {
                sb.append(buffer, 0, readLen);
            }
            return sb.toString();
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        } finally {
            if (null == inReader) {
                try {
                    inReader.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /**
     * get请求
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public static String read(String url) throws Exception {
        return read(url, 5, 8);
    }

    /**
     * 执行get请求
     * 
     * @param url
     * @param connectTimeout
     *            连接超时时间,单位秒
     * @param readTimeout
     *            读取超时时间,单位秒
     * @return
     * @throws Exception
     */
    public static String read(String url, int connectTimeout, int readTimeout) throws Exception {
        if (StringUtils.isBlank(url)) {
            logger.warn("get request url is :{}", url);
            return null;
        }
        logger.info("do get request:{}", url);
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        StringBuilder builder = new StringBuilder();
        try {
            // 转义url参数值中的空格
            conn = (HttpURLConnection) new URL(url.replaceAll(" ", "%20")).openConnection();
            conn.setConnectTimeout(connectTimeout * 1000);
            conn.setReadTimeout(readTimeout * 1000);
            conn.setRequestProperty("Accept-Charset", defaultEncoding);
            conn.setRequestProperty("contentType", defaultEncoding);
            conn.connect();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), defaultEncoding));
            String line;
            String enter = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null) {
                builder.append(line + enter);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (null != conn) {
                conn.disconnect();
            }
        }
        return builder.toString();
    }

    public static String post(String url, String params) throws Exception {
        return post(url, params, 5, 8);
    }

    /**
     * 执行post请求
     * 
     * @param url 请求地址
     * @param params 参数
     * @param connectTimeout 连接超时
     * @param readTimeout 读超时
     * @return
     * @throws Exception
     */
    public static String post(String url, String params, int connectTimeout, int readTimeout) throws Exception {
        if (StringUtils.isBlank(url)) {
            logger.warn("request url is:{}", url);
            return null;
        }
        logger.info("do post request:{}", url);
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("请求的目标地址url不能为空!");
        }
        if (StringUtils.isBlank(params)) {
            int index = url.indexOf("?");
            params = (index != -1) ? url.substring(index) : "";
            url = (index == -1) ? url : url.substring(0, index);
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("Accept-Charset", defaultEncoding);
        conn.setRequestProperty("contentType", defaultEncoding);
        conn.setRequestMethod("POST");
        // 连接超时 单位毫秒
        conn.setConnectTimeout(connectTimeout * 1000);
        // 读取超时 单位毫秒
        conn.setReadTimeout(readTimeout * 1000);
        conn.setDoOutput(true);

        if (StringUtils.isNotBlank(params)) {
            byte[] bypes = params.getBytes(defaultEncoding);
            conn.getOutputStream().write(bypes);
        }
        return read(conn.getInputStream(), defaultEncoding);
    }

    public static CloseableHttpClient createSSLClient(KeyStore truststore, String password) {
        return createSSLClient(truststore, password, null, null);
    }

    public static CloseableHttpClient createSSLClient(KeyStore truststore, String password, final Integer maxTotal, final Integer maxPerRoute) {
        SSLContext sslContext;
        try {
            TrustStrategy trustStrategy = new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            };
            if (StringUtils.isBlank(password)) {
                sslContext = new SSLContextBuilder().loadTrustMaterial(truststore, trustStrategy).build();
            } else {
                sslContext = new SSLContextBuilder().loadTrustMaterial(trustStrategy).loadKeyMaterial(truststore, password.toCharArray()).build();
            }
            if (null == maxTotal || maxTotal <= 0) {
                return HttpClients.custom().setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext)).build();
            }
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", socketFactory).build();
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
            connectionManager.setMaxTotal(maxTotal);
            connectionManager.setDefaultMaxPerRoute(maxPerRoute);
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            httpClientBuilder.setConnectionManager(connectionManager);
            return httpClientBuilder.build();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
           logger.error("{}:",e.getClass(),e);;
        }
        return HttpClients.createDefault();
    }

    private static HttpEntity getHttpEntity(Map<String, Object> pars) {
        if (null == pars || pars.isEmpty()) {
            return null;
        }

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        for (String name : pars.keySet()) {
            System.out.println(name + "=" + pars.get(name).toString());
            entity.addTextBody(name, pars.get(name).toString());
        }
        return entity.build();
    }
    /**
     * SSL post下载文件
     * @author:  heyiwu 
     * @param url
     * @param pars
     * @param header
     * @param file
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     * @throws FileNotFoundException
     */
    public static boolean download(String url, Map<String, Object> pars, Map<String, String> header, File file) throws IOException, ClientProtocolException, FileNotFoundException {
        return download(url, pars, header, null, null, file);
    }

    /**
     * SSL post下载文件
     * @author:  heyiwu 
     * @param url 
     * @param pars  请求参数
     * @param header 请求头
     * @param keyStore 密钥库或带密钥的证书
     * @param password 密钥库或证书密钥/密码
     * @param file 下载文件
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     * @throws FileNotFoundException
     */
    public static boolean download(String url, Map<String, Object> pars, Map<String, String> header, KeyStore keyStore, String password,
                                   File file) throws IOException, ClientProtocolException, FileNotFoundException {
        HttpPost request = new HttpPost(url);
        setHeader(request, header);
        request.setEntity(getHttpEntity(pars));
        HttpResponse resp = createSSLClient(keyStore, password).execute(request);
        StatusLine statusLine = resp.getStatusLine();
        if (statusLine.getStatusCode() != 200) {
            logger.info("download status:{}", statusLine);
            return false;
        }
        logger.info("download file:{}", file);
        FileUtils.write(resp.getEntity().getContent(), new FileOutputStream(file));
        logger.info("save file:{}", file);
        return true;
    }

    private static void setHeader(HttpRequestBase request, Map<String, String> header) {
        if (null == header) {
            return;
        }
        for (String name : header.keySet()) {
            request.setHeader(name, header.get(name));
        }
    }

}