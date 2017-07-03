package com.tzg.ex.mvc.web.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.util.WebUtils;

import sun.net.util.IPAddressUtil;

@SuppressWarnings("unchecked")
public class HttpUtils extends WebUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    // -- header 常量定义 --//
    private static final String HEADER_ENCODING = "encoding";

    private static final String HEADER_NOCACHE = "no-cache";

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final boolean DEFAULT_NOCACHE = true;

    private static final String TEXT_TYPE = "text/plain";

    // 工程名称
    public static final String DEFAULT_WEB_APP_NAME = "webapp.name";
    // 工程路径
    public static final String DEFAULT_WEB_APP_PATH = "webapp.path";
    // 应用所在的jboss服务日志路径
    public static final String WEB_APP_LOG_PATH     = "webapp.log.path";

    // 工程集群参数名
    public static final String WEB_APP_CLUSTERS = "webApp.clusters";

    /**
     * 获取应用的日志路径
     * 
     * @param webAppName
     * @return
     */
    public static String getWebAppLogPath(String webAppName) {
        return System.getProperty(webAppName + WEB_APP_LOG_PATH);
    }

    public static String setWebAppLogPath(String webAppName, String path) {
        return System.setProperty(webAppName + WEB_APP_LOG_PATH, path);
    }

    /**
     * 获取WEB工程安装路径（绝对路径）
     * 
     * @return <br>
     *         -----------------------------------------------------<br>
     * @author flotage
     * @create 2012-12-21 上午08:57:08
     * @note
     */
    public static String getWebAppRealPath() {
        URL url = HttpUtils.class.getResource("/");
        String path = url.getPath();
        if (url.getProtocol().startsWith("vfs")) {
            path = new File(HttpUtils.class.getResource("/com").getPath()).getParent();
        }
        return new File(path).getParentFile().getParentFile().getPath();

    }

    /**
     * 通过项目路径解析WEBAPP目录名称
     * 
     * @return <br>
     *         -----------------------------------------------------<br>
     * @author flotage
     * @create 2012-12-21 上午09:24:38
     * @note
     */
    public static String getWebAppName() {
        String path = getWebAppRealPath();
        return path.substring(path.lastIndexOf(File.separator) + 1);
    }

    /**
     * 获取web应用的名称 1、优先获取在web.xml中配置的webAppRootKey的值, 2、没有的话返回ServletContext的名称
     * 3、根据应用路径,获取war包文件夹名称
     */
    public static String getInitWebAppName(ServletContext context) {
        String sDefault = getInitParameter(context, HttpUtils.DEFAULT_WEB_APP_ROOT_KEY, "");
        String webappName = getInitParameter(context, HttpUtils.WEB_APP_ROOT_KEY_PARAM, sDefault);
        if (StringUtils.isBlank(webappName)) {
            webappName = context.getServletContextName();
        }
        if (StringUtils.isBlank(webappName)) {
            webappName = getWebAppName(context);
        }
        return webappName;
    }

    /**
     * 获取web应用的名称 根据应用路径,获取war包文件夹名称
     * 
     * @param context
     * @return
     */
    public static String getWebAppName(ServletContext context) {
        String root = context.getRealPath("/");
        int index = root.lastIndexOf(".");
        if (index != -1) {
            root = root.substring(0, index);
        }
        return root.substring(root.lastIndexOf(File.separator) + 1);
    }

    /**
     * 获取web应用的初始参数(context-param)
     * 
     * @param context
     * @param name
     *            参数名称
     * @param sDefault
     *            默认值
     * @return
     */
    public static String getInitParameter(ServletContext context, String name, String sDefault) {
        String value = context.getInitParameter(name);
        if (StringUtils.isBlank(value)) {
            return sDefault;
        }
        return value;
    }

    public static void setWebAppName(String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        System.setProperty(DEFAULT_WEB_APP_NAME, value.replace(".root", ""));
    }

    public static <T> T convert(Object o, Class<T> clas) {
        try {
            return (T) ConvertUtils.convert(o.toString(), clas);
        } catch (Exception e) {
            return (T) o;
        }
    }

    public static void renderText(final String text, final String... headers) {
        render(TEXT_TYPE, text, headers);
    }

    /**
     * eg. render("text/plain", "hello", "encoding:GBK"); render("text/plain",
     * "hello", "no-cache:false"); render("text/plain", "hello", "encoding:GBK",
     * "no-cache:false");
     * 
     * @param headers
     *            可变的header数组，目前接受的值为"encoding:"或"no-cache:",默认值分别为UTF-8和true.
     */
    public static void render(final String contentType, final String content, final String... headers) {
        HttpServletResponse response = initResponseHeader(contentType, headers);
        try {
            response.getWriter().write(content);
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 分析并设置contentType与headers.
     */
    private static HttpServletResponse initResponseHeader(final String contentType, final String... headers) {
        String encoding = DEFAULT_ENCODING;
        boolean noCache = DEFAULT_NOCACHE;
        for (String header : headers) {
            String headerName = StringUtils.substringBefore(header, ":");
            String headerValue = StringUtils.substringAfter(header, ":");
            if (StringUtils.equalsIgnoreCase(headerName, HEADER_ENCODING)) {
                encoding = headerValue;
            } else if (StringUtils.equalsIgnoreCase(headerName, HEADER_NOCACHE)) {
                noCache = Boolean.parseBoolean(headerValue);
            } else {
                throw new IllegalArgumentException(headerName + "不是一个合法的header类型");
            }
        }
        HttpServletResponse response = getResponse();
        // 设置headers参数
        String fullContentType = contentType + ";charset=" + encoding;
        response.setContentType(fullContentType);
        if (noCache) {
            setNoCacheHeaders(response);
        }
        return response;
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getResponse() {
        return ((ServletWebRequest) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    public static void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
    }

    /**
     * 获取访问者有效IP地址
     * 一般使用Request.getRemoteAddr()即可,但经过apache,nginx等软件反向代理后,得到的不是真实有效的IP。
     * 1、先从Header中获取X-Real-IP(nginx自定义属性值为$remote_addr)
     * 2、如果不存在再从X-Forwarded-For获得第一个IP(用,分割)...注意：可伪造,存在SQL注入和XSS潜在风险
     * 3、依次从header属性：Proxy-Client-IP、WL-Proxy-Client-IP、host获取,都不存在时则调用Request.getRemoteAddr()。
     * 
     * @param request
     * @return
     */
    @SuppressWarnings("finally")
    public static String getIpAddr(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        try {
            if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                // 多次反向代理后会有多个IP值，第一个为真实IP。
                int index = ip.indexOf(',');
                if (index != -1) {
                    return ip.substring(0, index);
                }
                return ip;
            }
            ip = req.getHeader("X-Real-IP");
            //X-Real-IP为nginx转发的$remote_addr一般为用户的真实IP,实际网络情况比较复杂(多层代理)获取的是阿里的IP,调整为优先获取X-Forwarded-For
            if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = req.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = req.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = req.getHeader("host");
            }
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = req.getRemoteAddr();
            }
            return ip;
        } finally {
            //内网环境可能带端口号,去端口号
            return  StringUtils.substringBefore(StringUtils.substringBefore(ip, ","), ":");
        }
    }

    public static Hashtable parseQueryString(final String s) {
        String valArray[] = null;
        if (s == null) {
            throw new IllegalArgumentException("queryString must not null");
        }
        Hashtable ht = new Hashtable();
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(s, "&");
        while (st.hasMoreTokens()) {
            String pair = (String) st.nextToken();
            if (pair.trim().length() == 0)
                continue;
            int pos = pair.indexOf('=');
            if (pos == -1) {
                throw new IllegalArgumentException("cannot parse queryString:" + s);
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1, pair.length()), sb);
            if (ht.containsKey(key)) {
                String oldVals[] = (String[]) ht.get(key);
                valArray = new String[oldVals.length + 1];
                for (int i = 0; i < oldVals.length; i++)
                    valArray[i] = oldVals[i];
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[1];
                valArray[0] = val;
            }
            ht.put(key, valArray);
        }
        return fixValueArray2SingleStringObject(ht);

    }

    private static Hashtable fixValueArray2SingleStringObject(Hashtable ht) {
        Hashtable result = new Hashtable();
        for (Iterator it = ht.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            String[] valueArray = (String[]) entry.getValue();
            if (valueArray == null)
                result.put(entry.getKey(), valueArray);
            else
                result.put(entry.getKey(), valueArray.length == 1 ? valueArray[0] : valueArray);
        }
        return result;
    }

    private static String parseName(final String s, StringBuffer sb) {
        sb.setLength(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                    } catch (NumberFormatException e) {
                        // need to be more specific about illegal arg
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException e) {
                        String rest = s.substring(i);
                        sb.append(rest);
                        if (rest.length() == 2)
                            i++;
                    }

                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    public static String getQueryString(HttpServletRequest req) {
        StringBuffer buffer = new StringBuffer();
        Enumeration names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String[] values = req.getParameterValues(name);
            String value = values[0];
            if (values.length > 1) {
                StringBuffer builder = new StringBuffer();
                for (String s : values) {
                    builder.append(s + ",");
                }
                value = builder.deleteCharAt(builder.length() - 1).toString();
            }
            buffer.append("&" + name + "=" + value);
        }
        return buffer.toString();
    }

    /**
     * 获取公网(WAN)ip
     * 
     * @return
     */
    public static String getInternetIp() {
        return getInternetIp("http://city.ip138.com/ip2city.asp");
    }

    /**
     * 获取公网(WAN)ip
     * 
     * @param url
     * @return
     */
    public static String getInternetIp(String url) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            String s = "";
            StringBuffer sb = new StringBuffer("");
            String content = "";
            while ((s = br.readLine()) != null) {
                sb.append(s + "\r\n");
            }
            content = sb.toString();
            int start = content.indexOf("[") + 1;
            int end = content.indexOf("]");
            return content.substring(start, end);
        } catch (Exception e) {
            logger.error("error open url:{}", url);
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /**
     * 是否内网ip
     * 
     * @param ip
     * @return
     */
    public static boolean isInternalIp(String ip) {
        byte[] addr = IPAddressUtil.textToNumericFormatV4(ip);

        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        // 172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        // 192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }
}
