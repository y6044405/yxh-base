package com.tzg.tool.kit.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tzg.tool.kit.string.StringUtil;

/**
 * 
 * Filename:    Certificate.java  
 * Description: 证书工具类<br>
 * 1、利用keytool命令生成keyStroe文件并设置密码:
 *      keytool -genkey -validity 36000 -alias www.tzg-soft.com -keyalg RSA -keystore d:\tzg.keystore
 * 其中:
 *      -genkey表示生成密钥 
 *      -validity指定证书有效期，这里是36000天 
 *      -alias指定别名 
 *      -keyalg指定RSA算法 
 *      -keystore文件存储位置  
 * 2、根据keystore文件生成自签名证书:
 *      keytool -export -keystore d:\tzg.keystore -alias www.tzg-soft.com -file d:\tzg.cer -rfc
 *      其中 
        -export指定为导出操作 
        -keystore指定keystore文件 
        -alias指定导出keystore文件中的别名 
        -file指向导出路径 
        -rfc以文本格式输出，也就是以BASE64编码输出 
   3、使用方需导入证书   
   通过工具JarSigner可以完成代码签名:
   jarsigner -storetype jks -keystore zlex.keystore -verbose tools.jar www.tzg-soft.com   
   验证tools.jar，命令如下： 
   jarsigner -verify -verbose -certs tools.jar  
   代码签名认证的用途主要是对发布的软件做验证，支持 Sun Java .jar (Java Applet) 文件(J2SE)和 J2ME MIDlet Suite 文件。  
 * Copyright:   Copyright (c) 2012-2013 All Rights Reserved.
 * Company:     tzg-soft.com Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2013-11-6 下午01:28:48  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-11-6      heyiwu      1.0         1.0 Version  
 *
 */
public class CertificateUtil extends SignatureUtil {
    private static final Logger logger = LoggerFactory.getLogger(CertificateUtil.class.getName());

    //Java密钥库(Java Key Store，JKS)KEY_STORE 
    public static final String KEY_STORE = "JKS";
    public static final String X509      = "X.509";
    public static final String SunX509   = "SunX509";
    public static final String SSL       = "SSL";
    public static final String PKCS12    = "PKCS12";

    /** 
     * 由KeyStore获得私钥 
     * @param keyStorePath  KeyStore文件路径
     * @param alias  生成KeyStore文件时设定的alias别名
     * @param password  生成KeyStore文件时设定的密码
     * @return 
     * @throws Exception 
     */
    private static PrivateKey getPrivateKey(String keyStorePath, String alias, String password) throws Exception {
        KeyStore ks = getKeyStore(keyStorePath, password);
        return (PrivateKey) ks.getKey(StringUtil.isBlank(alias) ? getKeyAlias(ks) : alias, password.toCharArray());
    }

    /** 
     * 由Certificate获得公钥 
     * @param certificatePath 证书路径
     * @return 
     * @throws Exception 
     */
    private static PublicKey getPublicKey(String certificatePath) throws Exception {
        Certificate certificate = getCertificate(certificatePath);
        return certificate.getPublicKey();
    }

    /** 
     * 获得Certificate 
     * @param certificatePath 证书路径
     * @return 
     * @throws FileNotFoundException 
     * @throws CertificateException 
     * @throws Exception 
     */
    private static Certificate getCertificate(String certificatePath) throws FileNotFoundException, CertificateException {
        FileInputStream is = null;
        try {
            is = new FileInputStream(certificatePath);
        } catch (FileNotFoundException e1) {
            throw e1;
        }
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(X509);
            return certificateFactory.generateCertificate(is);
        } catch (CertificateException e) {
            logger.error("{}", e.getClass(), e);
            throw e;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /** 
     * 获得Certificate 
     * @param keyStorePath keyStore文件路径
     * @param alias 别名
     * @param password  密码
     * @return 
     * @throws Exception 
     */
    public static Certificate getCertificate(String keyStorePath, String alias, String password) throws Exception {
        KeyStore ks = getKeyStore(keyStorePath, password);
        if (StringUtil.isBlank(alias)) {
            alias = getKeyAlias(ks);
        }
        Certificate certificate = ks.getCertificate(alias);
        return certificate;
    }

    public static KeyStore getKeyStore(File file, String password) throws FileNotFoundException {
        return getKeyStore(file.getAbsolutePath(), password);
    }

    /** 
     * 获得KeyStore 
     * @param keyStorePath KeyStore文件路径
     * @param password  密码
     * @return 
     */
    public static KeyStore getKeyStore(String keyStorePath, String password) throws FileNotFoundException {
        if (StringUtil.isBlank(keyStorePath)) {
            throw new FileNotFoundException(keyStorePath);
        }
        FileInputStream is = null;
        try {
            is = new FileInputStream(keyStorePath);
        } catch (FileNotFoundException e) {
            logger.error("file:{} not found!", keyStorePath);
            throw e;
        }
        String type = getType(keyStorePath);
        return getKeyStore(is, password, type);
    }

    private static String getType(String filePath) {
        return getType(filePath, KEY_STORE);
    }

    private static String getType(String keyStorePath, String sDefault) {
        String type = sDefault;
        if (keyStorePath.endsWith(".p12")) {
            type = PKCS12;
        }
        return type;
    }

    public static KeyStore getKeyStore(FileInputStream is, String password, String type) {
        try {
            KeyStore ks = null;
            try {
                ks = KeyStore.getInstance(type);
                ks.load(is, password.toCharArray());
            } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
                logger.error("{}:", e.getClass(), e);
            }
            return ks;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /** 
     * 私钥加密 
     * @param data 明文
     * @param keyStorePath keyStore文件路径
     * @param alias 别名
     * @param password 密码
     * @return 
     * @throws Exception 
     */
    public static byte[] encryptByPrivateKey(byte[] data, String keyStorePath, String alias, String password) throws Exception {
        PrivateKey privateKey = getPrivateKey(keyStorePath, alias, password);
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /** 
     * 公钥加密 
     * @param data 
     * @param certificatePath 
     * @return 
     * @throws Exception 
     */
    public static byte[] encryptByPublicKey(byte[] data, String certificatePath) throws Exception {
        PublicKey publicKey = getPublicKey(certificatePath);
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /** 
     * 私钥解密 
     * @param data  密文
     * @param keyStorePath keyStore文件路径
     * @param alias 别名
     * @param password 密码
     * @return 
     * @throws Exception 
     */
    public static byte[] decryptByPrivateKey(byte[] data, String keyStorePath, String alias, String password) throws Exception {
        PrivateKey privateKey = getPrivateKey(keyStorePath, alias, password);
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);

    }

    /** 
     * 公钥解密 
     * @param data 
     * @param certificatePath 
     * @return 
     * @throws Exception 
     */
    public static byte[] decryptByPublicKey(byte[] data, String certificatePath) throws Exception {
        PublicKey publicKey = getPublicKey(certificatePath);
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }


    public static String sign(byte[] data, String keyStorePath, String password) throws Exception {
        return sign(data, keyStorePath, "", password, null);
    }

    public static String sign(byte[] data, String keyStorePath, String alias, String password) throws Exception {
        return sign(data, keyStorePath, alias, password, null);
    }

    /** 
     * 签名 <br>
     * 获得证书、提取私钥、进行签名 
     * @param data 明文
     * @param keyStorePath  密钥库文件
     * @param alias 别名,支持空值从密钥库文件中提取
     * @param password 密钥库密码
     * @param algorithm 签名算法，用于证书的签名算法与实际签名算法不一致的情况
     * @return 
     * @throws Exception 
     */
    public static String sign(byte[] data, String keyStorePath, String alias, String password, String algorithm) throws Exception {
        X509Certificate x509Certificate = (X509Certificate) getCertificate(keyStorePath, alias, password);
        KeyStore ks = getKeyStore(keyStorePath, password);
        if (StringUtil.isBlank(alias)) {
            alias = getKeyAlias(ks);
        }
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
        return encodeBase64(sign(data, privateKey, StringUtil.isBlank(algorithm) ? x509Certificate.getSigAlgName() : algorithm));
    }

    public static String getKeyAlias(KeyStore ks) throws KeyStoreException {
        Enumeration<String> enuml = ks.aliases();
        String keyAlias = null;
        if (enuml.hasMoreElements()) {
            keyAlias = (String) enuml.nextElement();
            if (ks.isKeyEntry(keyAlias)) {
                return keyAlias;
            }
        }
        return null;
    }

    /** 
     * 验证Certificate 
     * @param certificatePath 
     * @return 
     */
    public static boolean verifyCertificate(String certificatePath) {
        return verifyCertificate(new Date(), certificatePath);
    }

    /**
     * 验证签名 
     * @author:  heyiwu 
     * @param data 密文
     * @param sign 签名
     * @param certificatePath 证书
     * @return
     * @throws Exception
     */
    public static boolean verifyCertificate(byte[] data, String sign, String certificatePath) throws Exception {
        return verifyCertificate(data, sign, certificatePath, null);
    }

    /** 
     * 验证签名 <br>
     * 获得证书、获得公钥 、验签
     * @param data 密文
     * @param sign 签名
     * @param certificatePath 证书 
     * @param alg 签名算法,支持为空从证书上提取(优先),用于证书的签名算法与实际算法不一致的情况
     * @return 
     * @throws Exception 
     */
    public static boolean verifyCertificate(byte[] data, String sign, String certificatePath, String alg) throws Exception {
        X509Certificate x509Cert = (X509Certificate) getCertificate(certificatePath);
        PublicKey publicKey = x509Cert.getPublicKey();
        return verify(data, decodeBase64(sign), publicKey, StringUtil.isBlank(alg) ? x509Cert.getSigAlgName() : alg);
    }

    /** 
     * 验证Certificate是否过期或无效 
     * @param date 
     * @param certificatePath 
     * @return 
     */
    public static boolean verifyCertificate(Date date, String certificatePath) {
        boolean status = true;
        try {
            // 取得证书  
            Certificate certificate = getCertificate(certificatePath);
            // 验证证书是否过期或无效  
            status = verifyCertificate(date, certificate);
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    /** 
     * 验证证书是否过期或无效 
     *  
     * @param date 
     * @param certificate 
     * @return 
     */
    private static boolean verifyCertificate(Date date, Certificate certificate) {
        boolean status = true;
        try {
            X509Certificate x509Certificate = (X509Certificate) certificate;
            x509Certificate.checkValidity(date);
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    /** 
     * 验证Certificate 
     * @param keyStorePath 
     * @param alias 
     * @param password 
     * @return 
     */
    public static boolean verifyCertificate(Date date, String keyStorePath, String alias, String password) {
        boolean status = true;
        try {
            Certificate certificate = getCertificate(keyStorePath, alias, password);
            status = verifyCertificate(date, certificate);
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    /** 
     * 验证Certificate 
     * @param keyStorePath 
     * @param alias 
     * @param password 
     * @return 
     */
    public static boolean verifyCertificate(String keyStorePath, String alias, String password) {
        return verifyCertificate(new Date(), keyStorePath, alias, password);
    }

    /** 
     * 获得SSLSocektFactory 
     * 为HttpsURLConnection配置SSLSocketFactory,就可以通过HttpsURLConnection的getInputStream、getOutputStream，像使用HttpURLConnection做操作
     * 未配置SSLSocketFactory前,HttpsURLConnection的getContentLength()获得值永远都是-1
     * @param password  密码 
     * @param keyStorePath  密钥库路径 
     * @param trustKeyStorePath  信任库路径 
     * @return 
     * @throws Exception 
     */
    private static SSLSocketFactory getSSLSocketFactory(String password, String keyStorePath, String trustKeyStorePath) throws Exception {
        // 初始化密钥库  
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(SunX509);
        KeyStore keyStore = getKeyStore(keyStorePath, password);
        keyManagerFactory.init(keyStore, password.toCharArray());
        // 初始化信任库  
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(SunX509);
        KeyStore trustkeyStore = getKeyStore(trustKeyStorePath, password);
        trustManagerFactory.init(trustkeyStore);
        // 初始化SSL上下文  
        SSLContext ctx = SSLContext.getInstance(SSL);
        ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return ctx.getSocketFactory();
    }

    /** 
     * 为HttpsURLConnection配置SSLSocketFactory 
     *  
     * @param conn   HttpsURLConnection 
     * @param password   密码 
     * @param keyStorePath   密钥库路径 
     * @param trustKeyStorePath  信任库路径 
     * @throws Exception 
     */
    public static void configSSLSocketFactory(HttpsURLConnection conn, String password, String keyStorePath, String trustKeyStorePath) throws Exception {
        conn.setSSLSocketFactory(getSSLSocketFactory(password, keyStorePath, trustKeyStorePath));
    }
}
