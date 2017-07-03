package com.tzg.tool.kit.security;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Filename:    SignatureUtil.java  
 * Description: 非对称加密、数据签名工具类（RSA加密签名、DSA数字签名和对应的验签）  
 * Copyright:   Copyright (c) 2012-2013 All Rights Reserved.
 * Company:     tzg-soft.com Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2013-11-5 下午03:33:11  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * 2013-11-5      heyiwu      1.0         1.0 Version  
 *
 */
public class SignatureUtil extends DigestUtil {
    private static final Logger logger = LoggerFactory.getLogger(SignatureUtil.class);

    /**
     *  获取公钥或私钥,需先生存密钥对：KeyPair keyPair = getKeyPair(RSA);
     * @param key 公钥或私钥:keyPair.getPrivate();keyPair.getPublic();
     * @return 
     */
    public static String getKey(Key key) {
        return encodeBase64(key.getEncoded());
    }

    /** 
     * RSA公钥加密 
     * @param data 明文
     * @param key 公钥:调用getKey方法获取
     * @return 
     * @throws Exception 
     */
    public static byte[] encryptRSAPubKey(byte[] data, String key) throws Exception {
        // 对公钥解密  
        byte[] keyBytes = decodeBase64(key);
        // 取得公钥  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /** 
     * RSA私钥加密
     * @param data ：RSA公钥加密后的数据
     * @param key ：私钥:调用getKey方法获取
     * @return 
     * @throws Exception 
     */
    public static byte[] encryptRSAPrivKey(byte[] data, String key) throws Exception {
        // 对密钥解密  
        byte[] keyBytes = decodeBase64(key);
        // 取得私钥  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据加密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /** 
     * 用RSA私钥解密 
     * @param data 解密数据
     * @param key 私钥:调用getKey方法获取
     * @return 
     * @throws Exception 
     */
    public static byte[] decryptRSAPrivKey(byte[] data, String key) throws Exception {
        // 对密钥解密  
        byte[] keyBytes = decodeBase64(key);
        // 取得私钥  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        // 对数据解密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /** 
     * 用RSA公钥解密 
     * @param data 
     * @param key 公钥:调用getKey方法获取
     * @return 
     * @throws Exception 
     */
    public static byte[] decryptRSAPubKey(byte[] data, String key) throws Exception {
        // 对密钥解密  
        byte[] keyBytes = decodeBase64(key);
        // 取得公钥  
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密  
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /** 
     * Return public RSA key exponent 
     *  
     * @param keyPair 
     *            RSA keys 
     * @return public exponent value as hex string 
     */
    public static String getPublicKeyExponent(RSAPublicKey publicKey) {
        return publicKey.getPublicExponent().toString(16);
    }

    /** 
     * Return public RSA key modulus 
     *  
     * @param keyPair 
     *            RSA keys 
     * @return modulus value as hex string 
     */
    public static String getPublicKeyModulus(RSAPublicKey publicKey) {
        return publicKey.getModulus().toString(16);
    }

    /** 
     * Max block size with given key length 
     *  
     * @param keyLength 
     *            length of key 
     * @return numeber of digits 
     */
    public static int getMaxDigits(int keyLength) {
        return ((keyLength * 2) / 16) + 3;
    }

    /** 
     * 用私钥对信息生成数字签名 
     * @param data  加密数据 
     * @param privateKey  私钥 :调用getKey方法获取
     * @return 
     * @throws Exception 
     */
    public static String signDSA(byte[] data, String privateKey) throws Exception {
        PrivateKey priKey = generatePrivate(privateKey, DSA);
        return encodeBase64(sign(data, priKey, priKey.getAlgorithm()));
    }

    /** 
     * 用私钥对信息生成数字签名 
     * @param data  加密数据 
     * @param privKey  私钥 :调用getKey方法获取
     * @return 
     * @throws Exception 
     */
    public static String signRSA(byte[] data, String privKey) throws Exception {
        PrivateKey privteKey = generatePrivate(privKey, RSA);
        return encodeBase64(sign(data, privteKey, MD5WITHRSA));
    }

    /** 
     * 校验数字签名 
     * @param data  加密数据 
     * @param sign   数字签名 
     * @param pubKey   公钥 :调用getKey方法获取
     * @return 校验成功返回true 失败返回false 
     * @throws Exception 
     *  
     */
    public static boolean verifyRSA(byte[] data, String sign, String pubKey) throws Exception {
        PublicKey publicKey = generatePublic(pubKey, RSA);
        return verify(data, decodeBase64(sign), publicKey, MD5WITHRSA);
    }

    /** 
     * 校验数字签名 
     * @param data 加密数据 
     * @param sign   数字签名 
     * @param publicKey  公钥 :调用getKey方法获取
     * @return 校验成功返回true 失败返回false 
     * @throws Exception 
     *  
     */
    public static boolean verifyDSA(byte[] data, String sign, String publicKey) throws Exception {
        PublicKey pubKey = generatePublic(publicKey, DSA);
        return verify(data, decodeBase64(sign), pubKey, DSA);
    }

    /** 
     * 利用私匙对信息进行签名  
     * @param text  要签名的信息
     * @param privKey 私钥
     */
    public static byte[] sign(byte[] data, PrivateKey privKey, String alg) {
        try {
            // Signature 对象可用来生成和验证数字签名  
            Signature signet = Signature.getInstance(alg);
            // 初始化签署签名的私钥  
            signet.initSign(privKey);
            // 更新要由字节签名或验证的数据  
            signet.update(data);
            // 签署或验证所有更新字节的签名，返回签名  
            return signet.sign();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    /** 
     * 读取数字签名文件 根据公匙，签名，信息验证信息的合法性 
     * @param data 密文
     * @param signed签名
     * @param pubKey 公钥
     * @param alg 算法
     * @return true 验证成功 false 验证失败 
     */
    public static boolean verify(byte[] data, byte[] signed, PublicKey pubKey, String alg) {
        try {
            // 初始一个Signature对象,并用公钥和签名进行验证  
            Signature signature = Signature.getInstance(alg);
            // 初始化验证签名的公钥  
            signature.initVerify(pubKey);
            // 使用指定的 byte 数组更新要签名或验证的数据  
            signature.update(data);
            // 验证传入的签名  
            return signature.verify(signed);
        } catch (Exception e) {
            logger.error("{}",e.getClass(),e);
        }
        return false;
    }

    /**
     * 为签名生成私钥对象：根据私钥字符和指定的算法生成私钥对象
     * @param privateKey 私钥
     * @param alg 算法:如DSA、MD5WITHRSA...
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static PrivateKey generatePrivate(String privateKey, String alg) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(alg);
        return keyFactory.generatePrivate(pkcs8KeySpec);
    }

    /**
     * 为验签生成公钥对象：根据公钥字符和指定的算法生成公钥对象
     * @param pubKey 公钥
     * @param alg 算法:如DSA、MD5WITHRSA...
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException 
     * @throws InvalidKeySpecException
     */
    private static PublicKey generatePublic(String pubKey, String alg) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = decodeBase64(pubKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(alg);
        return keyFactory.generatePublic(keySpec);
    }

    /** 
     * 用甲方DH公钥，初始化乙方DH密钥,得到乙方密钥对儿 
     * @param key  甲方DH公钥 
     * @return 乙方的密钥对儿:DHPublicKey/DHPrivateKey
     * @throws Exception 
     */
    public static KeyPair initDHKey(String key) throws Exception {
        // 解析甲方公钥  
        byte[] keyBytes = decodeBase64(key);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(DH);
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        // 由甲方公钥构建乙方密钥  
        DHParameterSpec dhParamSpec = ((DHPublicKey) pubKey).getParams();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyFactory.getAlgorithm());
        keyPairGenerator.initialize(dhParamSpec);
        return keyPairGenerator.generateKeyPair();
    }

    /** 
     * DH加密
     * @param data   待加密数据 
     * @param publicKey  甲方公钥/乙方公钥:调用getKey方法获取
     * @param privateKey  乙方私钥/甲方私钥:调用getKey方法获取
     * @return 
     * @throws Exception 
     */
    public static byte[] encryptDH(byte[] data, String publicKey, String privateKey) throws Exception {
        // 生成本地密钥  
        SecretKey secretKey = getSecretDHKey(publicKey, privateKey);
        // 数据加密  
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /** 
     * DH解密 
     * @param data  待解密数据 
     * @param publicKey   乙方公钥/甲方公钥:调用getKey方法获取
     * @param privateKey  甲方私钥/乙方私钥:调用getKey方法获取
     * @return 
     * @throws Exception 
     */
    public static byte[] decryptDH(byte[] data, String publicKey, String privateKey) throws Exception {

        // 生成本地密钥  
        SecretKey secretKey = getSecretDHKey(publicKey, privateKey);
        // 数据解密  
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /** 
     * 构建DH密钥 
     *  DH加密下需要一种对称加密算法对数据加密，这里我们使用DES3，也可以使用其他对称加密算法。 
     * @param publicKey 公钥 :调用getKey方法获取
     * @param privateKey  私钥 :调用getKey方法获取
     * @return 
     * @throws Exception 
     */
    private static SecretKey getSecretDHKey(String publicKey, String privateKey) throws Exception {
        // 初始化公钥  
        byte[] pubKeyBytes = decodeBase64(publicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(DH);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKeyBytes);
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        // 初始化私钥  
        byte[] priKeyBytes = decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKeyBytes);
        Key priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
        keyAgree.init(priKey);
        keyAgree.doPhase(pubKey, true);
        // 生成本地密钥  
        SecretKey secretKey = keyAgree.generateSecret(DES3);
        return secretKey;
    }
}
