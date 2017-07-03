package com.tzg.tool.kit.security;

import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Digest {
    //密钥对 ：公钥、私钥
    private KeyPair   keyPair;
    //SecretKey 负责保存对称密钥  .  
    private SecretKey secretKey;
    //负责加密、解密
    private Cipher    cipher;
    //密文、明文
    private byte[]    bytes;

    public Digest(KeyPair keyPair, Cipher cipher, byte[] bytes) {
        this.keyPair = keyPair;
        this.cipher = cipher;
        this.bytes = bytes;
    }

    public Digest(SecretKey key, Cipher cipher, byte[] bytes) {
        this.secretKey = key;
        this.cipher = cipher;
        this.bytes = bytes;
    }

    /**
     * 得到密文或明文数组(bytes)的字符串
     * @return
     */
    public String getHex() {
        return DigestUtil.toHexString(bytes);
    }

    /**
     * 得到密文或明文数组(bytes)的字符串
     * @return
     */
    public String getText() {
        return DigestUtil.toString(bytes);
    }

    /**
     * 解密
     * @return
     */
    public String decrypt() {
        return decrypt(null);
    }

    /**
     * 解密
     * @return
     */
    public String decrypt(SecureRandom sr) {
        if (DigestUtil.isAsymmetric(getCipher().getAlgorithm())) {
            byte[] b = DigestUtil.asymmetricDecrypt(bytes, (RSAPrivateKey) keyPair.getPrivate(), getCipher());
            return DigestUtil.toString(b);
        }
        return DigestUtil.decrypt(getBytes(), getSecretKey(), getCipher(), sr).getText();
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

}
