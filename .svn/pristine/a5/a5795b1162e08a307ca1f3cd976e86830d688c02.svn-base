package com.tzg.tool.kit.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Random;
import java.util.zip.CRC32;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import com.tzg.tool.kit.Encodes;
import com.tzg.tool.kit.file.FileUtils;
import com.tzg.tool.kit.string.StringUtil;

/**
 * 
 * Filename:    DigestUtil.java  
 * Description: 摘要和加密/解密工具类 扩展apache
 * comons的摘要工具类、集成常见的加密、解密方法 更多相关工具类：
 * {@link org.springframework.util.DigestUtils}
 *<p>
 * 基本的单向加密算法(单向加密的用途主要是为了校验数据在传输过程中是否被修改)：<br>
 * BASE64 严格地说，属于编码格式，而非加密算法<br>
 * MD5(Message Digest algorithm 5，信息摘要算法)<br>
 * SHA(Secure Hash Algorithm，安全散列算法) <br>
 * HMAC(Hash Message Authentication Code，散列消息鉴别码)<br>
 * </p>
 * <p>
 * 双向加密分:对称和非对称加密 <br>
 * 对称加密算法:<br>
 * DES、PBE、3DES、Blowfish、RC2、RC4(ARCFOUR)...区别在于算法不同、密钥长度不同<br>
 * 对称加密调用：encrypt(String text, String alg)<br>
 * 对称解密调用: decrypt(String text, String alg)
 * 
 * 非对称加密算法：<br>
 * RSA(算法的名字以发明者的名字命名：Ron Rivest, AdiShamir 和Leonard Adleman)<br>
 * DH(Diffie-Hellman算法，密钥一致协议)<br>
 * DSA(Digital Signature Algorithm，数字签名)<br>
 * ECC(Elliptic Curves Cryptography，椭圆曲线密码编码学)
 * 
 * 
 * </p>
 * 
 * <pre>
 * 支持 DES、DESede(TripleDES,就是3DES)、AES、Blowfish、RC2、RC4(ARCFOUR) 
 * DES                  key size must be equal to 56 
 * DESede(TripleDES)    key size must be equal to 112 or 168 
 * AES                  key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available 
 * Blowfish             key size must be multiple of 8, and can only range from 32 to 448 (inclusive) 
 * RC2                  key size must be between 40 and 1024 bits 
 * RC4(ARCFOUR)         key size must be between 40 and 1024 bits 
 * 具体内容关注JDK6 :http://docs.oracle.com/javase/6/docs/technotes/guides/security/SunProviders.html
 * 
 * </pre>  
 * Copyright:   Copyright (c) 2015-2018 All Rights Reserved.
 * Company:     tzg.cn Inc.
 * @author:     heyiwu 
 * @version:    1.0  
 * Create at:   2015年12月19日 下午4:31:37  
 *
 */
public class DigestUtil extends org.apache.commons.codec.digest.DigestUtils {
    private static final Logger   logger       = LoggerFactory.getLogger(DigestUtil.class);

    public final static String    DES          = "DES";
    public final static String    DES3         = "DESede";
    // AES取代DES(美国国家标准局倡导)
    public final static String    AES          = "AES";
    // 类似算法:PBEWithMD5AndDES、PBEWithMD5AndTripleDES、PBEWithSHA1AndDESede、
    // PBEWithSHA1AndRC2_40
    public final static String    PBE          = "PBEWITHMD5andDES";

    // 非对称加密
    public final static String    RSA          = "RSA";
    public final static String    DSA          = "DSA";
    public final static String    DH           = "DH";

    // 单向加密/摘要算法
    public final static String    MD5          = "MD5";
    public final static String    MD5WITHRSA   = "MD5withRSA";

    public final static String    SHA          = "SHA";
    public final static String    SHA1         = "SHA-1";
    // MAC算法可选以下多种算法: HmacMD5、HmacSHA1、HmacSHA256、HmacSHA384、HmacSHA512
    public static final String    MAC          = "HmacMD5";
    // 默认密钥字节数 Default Keysize 1024,Keysize must be a multiple of 64, ranging
    // from 512 to 1024 (inclusive).
    protected static final int    KEY_SIZE     = 1024;
    // 默认种子
    protected static final String DEFAULT_SEED = "0f22507a10bbddd07d8a3082122966e3";

    public static void main(String[] args) throws Exception {
        //		Cannot find a free socket for the debugger
        //		netsh winsock reset
        /*		String path = "D:/system";
        		File[] files = new File(path).listFiles();
        		for (File file : files) {
        			if (file.isDirectory()) {
        				continue;
        			}
        			System.out.println(file);
        			long size = FileUtils.getFileSize(file);
        			String fileSize = FileUtils.getFileSize(size);
        			String ds = FileUtils.byteCountToDisplaySize(size);
        			System.out.println("\t length:" + file.length() + ",size:"
        					+ fileSize + "," + ds);

        			long s = System.currentTimeMillis();
        			String md5Hash = getMd5Hash(file);
        			System.out.println("\t IO HASH:" + md5Hash + ",耗时："
        					+ (System.currentTimeMillis() - s) + "ms ");
        			System.out.println();
        		}*/
      
    }

    public static String getMd5Hash(File f) {
        long size = FileUtils.getFileSize(f);
        if (size < FileUtils.KB) {
            return getMd5HashIO(f, 1);
        }
        if (size < FileUtils.MB) {
            return getMd5HashNio(f, (int) ((size % FileUtils.KB) / 2));
        }
        return String.valueOf(f.lastModified());
    }

    public static String getMd5HashIO(File f, int size) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            byte[] buffer = new byte[1024 * size];
            int length;
            MessageDigest md5 = MessageDigest.getInstance(MD5);
            while ((length = fis.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            return toHexString(md5.digest());
        } catch (FileNotFoundException e) {
            logger.error("md5 file " + f.getAbsolutePath() + " failed:" + e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error("md5 file " + f.getAbsolutePath() + " failed:" + e.getLocalizedMessage());
        } catch (NoSuchAlgorithmException e) {
            logger.error("md5 file " + f.getAbsolutePath() + " failed:" + e.getLocalizedMessage());
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return null;
    }

    /**
     * 计算文件MD5值 NIO实现
     * 
     * @param file
     * @return
     */
    public static String getMd5HashNio(File file, int size) {
        String md5Hash = "";
        FileInputStream is = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance(MD5);
            is = new FileInputStream(file);
            /*
             * MappedByteBuffer byteBuffer = fis.getChannel().map(
             * FileChannel.MapMode.READ_ONLY, 0, f.length());
             * md5.update(byteBuffer); BigInteger bi = new BigInteger(1,
             * md5.digest()); return bi.toString(16);
             */

            FileChannel fChannel = is.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(size * 1024);
            for (int count = fChannel.read(buffer); count != -1; count = fChannel.read(buffer)) {
                buffer.flip();
                md5.update(buffer);
                if (!buffer.hasRemaining()) {
                    buffer.clear();
                }
            }
            md5Hash = toHexString(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getLocalizedMessage());
        } catch (FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
        } finally {
            IOUtils.closeQuietly(is);
        }
        return md5Hash;
    }

    /**
     * 对字符串进行DES加密
     * 
     * @param str
     *            明文
     * @return 密文数组,byte2String得到密文字符串
     * @throws Exception
     */
    public static Digest encryptDES(byte[] bytes) {
        return encrypt(bytes, DES);
    }

    /**
     * 根据密匙进行DES加密
     * 
     * @param text
     *            要加密的信息
     * @param key
     *            密匙
     * @return String 加密后的信息
     */
    public static Digest encryptDES(byte[] text, SecretKey key) {
        return encrypt(text, DES, key, new SecureRandom());
    }

    /**
     * 对字符串进行3DES加密
     * 
     * @param bytes
     * @return
     */
    public Digest encrypt3DES(byte[] bytes) {
        return encrypt(bytes, DES3);
    }

    /**
     * 进行AES加密
     * 
     * @param bytes
     *            被加密的明文
     * @return
     */
    public Digest encryptAES(byte[] bytes) {
        return encrypt(bytes, AES);
    }

    /**
     * 盐初始化
     * 
     * @return
     * @throws Exception
     */
    public static byte[] createPBESalt() throws Exception {
        byte[] salt = new byte[8];
        Random random = new Random();
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 加密
     * 
     * @param data
     *            数据,示例:"data123".getBytes()
     * @param password
     *            密码 ,示例: "123456"
     * @param salt
     *            盐 :createPBESalt()
     * @return bytes : encryptBASE64(bytes)
     * @throws Exception
     */
    public static byte[] encryptPBE(byte[] data, String password, byte[] salt) throws Exception {
        Key key = toKey(password, PBE);
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance(PBE);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     * 
     * @param data
     *            数据 ：encryptPBE返回的数组
     * @param password
     *            密码,示例: "123456"
     * @param salt
     *            盐 :createPBESalt()
     * @return new String(bytes)
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String password, byte[] salt) throws Exception {
        Key key = toKey(password, PBE);
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance(PBE);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        return cipher.doFinal(data);

    }

    /**
     * MD5加密 通常将MD5产生的字节数组交给BASE64再加密
     * 注意：避免使用String，避免使用String的getBytes()方法,此方法依赖系统编码(字符串转byte数组采用系统默认编码,不同的编码会导致加密不同)
     * @param bytes
     * @return
     */
    public static byte[] encryptMD5(byte[] bytes) {
        return digest(bytes, MD5);
    }

    /**
     * 进行MD5加密
     * @author:  heyiwu 
     * @param bytes
     * @return
     */
    public static String getMd5Digest(byte[] bytes) {
        BigInteger number = new BigInteger(1, digest(bytes, MD5));
        StringBuffer sb = new StringBuffer('0');
        sb.append(number.toString(16));
        return sb.toString();
    }

    /**
     * 进行SHA(安全散列算法)加密 虽然SHA与MD5通过碰撞法都被破解了，但是SHA仍然是公认的安全加密算法，较之MD5更为安全.推荐SHA2
     * 
     * @param bytes
     *            被加密的信息
     * @return
     */
    @Deprecated
    public static byte[] encryptSHA(byte[] bytes) {
        return digest(bytes, SHA);
    }

    /**
     * SHA1加密
     * 推荐SHA2算法
     * @param bytes
     * @return
     */
    @Deprecated
    public static String encryptSHA1(byte[] bytes) {
        return toHexString(digest(bytes, SHA1));
    }

    /**
     * 
     * @author:  heyiwu 
     * @param bytes
     * @param alg :SHA-224、SHA-256、SHA-384，和SHA-512并称为SHA-2。
     * @return
     */
    public static String encryptSH2(byte[] bytes, String alg) {
        return toHexString(digest(bytes, alg));
    }

    /**
     * 进行非对称RSA加密 默认采用公钥加密,用私钥解密（大多采用的方式）,当然也可以用私钥加密，用公钥解密。
     * 
     * @param bytes
     *            明文
     * @return
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     */
    public static Digest encryptRSA(byte[] bytes) {
        return asymmetricEncrypt(bytes, RSA);
    }

    /**
     * HMAC密钥
     * 
     * @return
     * @throws Exception
     */
    public static String createHMACKey() throws Exception {
        return encodeBase64(getSecretKey(MAC).getEncoded());
    }

    /**
     * HMAC加密
     * 
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(decodeBase64(key), MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(data);

    }

    /**
     * 非对称加密
     * 
     * @param bytes
     * @param alg
     * @return
     */
    public static Digest asymmetricEncrypt(byte[] bytes, String alg) {
        KeyPair keyPair = getKeyPair(alg);
        if (null == keyPair) {
            return null;
        }
        Cipher cipher = getCipher(alg);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        } catch (InvalidKeyException e) {
            logger.error("非对称加密{},初始化异常:{}", alg, e.getLocalizedMessage());
        }
        try {
            return new Digest(keyPair, cipher, cipher.doFinal(bytes));
        } catch (IllegalBlockSizeException e) {
            logger.error("非对称加密异常:{}", e.getLocalizedMessage());
        } catch (BadPaddingException e) {
            logger.error("非对称加密异常:{}", e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 根据指定算法进行加密
     * 
     * @param bytes
     *            明文
     * @param alg
     *            算法:DES、AES...
     * @return
     */
    public static Digest encrypt(byte[] bytes, String alg) {
        if (isAsymmetric(alg)) {
            return asymmetricEncrypt(bytes, alg);
        }
        return encrypt(bytes, alg, getSecretKey(alg), null);
    }

    /**
     * 根据指定算法,加密字符串
     * 
     * @param text
     *            被加密的明文字符串
     * @param alg
     *            加密算法
     * @return
     */
    public static Digest encrypt(byte[] text, String alg, SecretKey key, SecureRandom sr) {
        // Security.addProvider(new com.sun.crypto.provider.SunJCE());
        // Cipher负责完成加密或解密工作 .生成Cipher对象,指定其支持的DES算法
        Cipher cipher = getCipher(key.getAlgorithm());
        // 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式
        try {
            if (null == sr) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key, sr);
            }
        } catch (InvalidKeyException e1) {
            logger.error(e1.getLocalizedMessage());
        }
        try {
            return new Digest(key, cipher, cipher.doFinal(text));
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getLocalizedMessage());
        } catch (BadPaddingException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * AES加密
     * 
     * @param text
     *            明文
     * @param password
     *            明文的加密密码
     * @return
     * @throws Exception
     */
    public static byte[] encryptAES(byte[] bytes, String password) throws Exception {
        return doFinal(bytes, password, AES, Cipher.ENCRYPT_MODE);
    }

    /**
     * AES解密
     * 
     * @param text
     *            密文
     * @param password
     *            明文的解密密码
     * @return
     * @throws Exception
     */
    public static byte[] decryptAES(byte[] bytes, String password) throws Exception {
        return doFinal(bytes, password, AES, 128, Cipher.DECRYPT_MODE);
    }

    /**
     * 执行加密、解密操作
     * 
     * @param bytes
     *            明文或密文,和操作(optMode)对应
     * @param password
     *            密码
     * @param alg
     *            算法
     * @param optMode
     *            操作：加密或解密 见Cipher
     * @return
     * @throws Exception
     */
    public static byte[] doFinal(byte[] bytes, String password, String alg, int optMode) throws Exception {
        return doFinal(bytes, password, alg, 128, optMode);
    }

    /**
     * 执行加密、解密操作
     * 
     * @param bytes明文或密文
     *            ,和操作(optMode)对应
     * @param password
     *            密码
     * @param alg算法
     * @param keySize
     *            位数128 192 256
     * @param optMode
     *            操作：加密或解密 见Cipher
     * @return
     * @throws Exception
     */
    public static byte[] doFinal(byte[] bytes, String password, String alg, int keySize, int optMode) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(alg);
        // AES可以使用128、192、和256位密钥

        // kgen.init(keySize, new SecureRandom(password.getBytes()));
        // SecureRandom实现和系统相关，为兼容linux和solaris
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(password.getBytes());
        kgen.init(keySize, secureRandom);

        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, alg);
        Cipher cipher = Cipher.getInstance(alg);
        cipher.init(optMode, key);
        return cipher.doFinal(bytes);
    }

    /**
     * RSA解密
     * 
     * @param bytes
     *            ：密文
     * @param privKey
     *            :私匙 getKeyPair("RSA").getPrivate()
     * @return
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] asymmetricDecrypt(byte[] bytes, RSAPrivateKey privKey, Cipher cipher) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, privKey);
        } catch (InvalidKeyException e) {
            logger.error("非对称解密{},初始化异常(密钥不正确?):{}", privKey.getAlgorithm(), e.getLocalizedMessage());
        }
        try {
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException e) {
            logger.error("非对称解密{}异常:{}", privKey.getAlgorithm(), e.getLocalizedMessage());
        } catch (BadPaddingException e) {
            logger.error("非对称解密{}异常:{}", privKey.getAlgorithm(), e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 根据指定算法进行解密
     */
    public static Digest decrypt(byte[] bytes, SecretKey key, Cipher cipher, SecureRandom sr) {
        try {
            if (null == sr) {
                cipher.init(Cipher.DECRYPT_MODE, key);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key, sr);
            }
        } catch (InvalidKeyException e) {
            logger.error(e.getLocalizedMessage());
        }
        try {
            bytes = cipher.doFinal(bytes);
            return new Digest(key, cipher, bytes);
        } catch (IllegalBlockSizeException e) {
            logger.error(e.getLocalizedMessage());
        } catch (BadPaddingException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 根据指定单向的算法进行单向加密
     * 
     * @param bytes
     *            明文
     * @param alg
     *            单向算法：MD5、SHA、SHA1、SHA-256、SHA-384、SHA-512
     * @return
     */
    public static byte[] digest(byte[] bytes, String alg) {
        MessageDigest msgDigest;
        try {
            msgDigest = MessageDigest.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
        // 更新摘要
        msgDigest.update(bytes);
        // 完成哈希计算
        return msgDigest.digest();

    }

    /**
     * 创建密匙
     * 
     * @return
     */
    public SecretKey createSecretKey() {
        return createSecretKey(DES);
    }

    /**
     * 根据指定的算法创建密匙
     * 
     * @param alg
     *            加密算法,可用 DES,DESede,Blowfish
     * @return SecretKey 秘密（对称）密钥
     */
    public SecretKey createSecretKey(String alg) {
        try {
            // 提供对称密钥生成器的功能,支持各种算法.返回生成指定算法的秘密密钥的 KeyGenerator对象
            KeyGenerator keygen = KeyGenerator.getInstance(alg);
            // SecretKey负责保存对称密钥 .生成一个密钥
            return keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            logger.error("没有{}算法 :{}", alg, e.getLocalizedMessage());
        }
        // 返回密匙
        return null;
    }

    /**
     *根据指定算法产生密匙组:公匙、私匙
     * 
     * @param alg
     *            非对称算法:RSA、DSA
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair getKeyPair(String alg) {
        return getKeyPair(alg, null);
    }

    /**
     *根据指定算法产生密匙组:公匙、私匙
     * 
     * @param alg
     *            非对称算法:RSA、DSA
     * @param seed
     *            种子
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair getKeyPair(String alg, String seed) {
        KeyPairGenerator keygen = null;
        try {
            keygen = KeyPairGenerator.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            logger.error("没有{}算法:{}", alg, e.getLocalizedMessage());
            return null;
        }
        if (StringUtils.isBlank(seed)) {
            keygen.initialize(KEY_SIZE);
        } else {
            // 初始化随机产生器
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.setSeed(seed.getBytes());
            keygen.initialize(KEY_SIZE, secureRandom);
        }
        return keygen.generateKeyPair();
    }

    private static Cipher getCipher(String alg) {
        try {
            return Cipher.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getLocalizedMessage());
        } catch (NoSuchPaddingException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    private static SecretKey getSecretKey(String alg) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(alg);
            return keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            logger.error("没有或不支持{}算法：{}", alg, e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 创建密匙组:私匙、公匙
     * 
     * @param file
     *            密匙组存放文件
     * @return [私匙,公匙]
     */
    public static Key[] createPairKey() {
        try {
            // 根据特定的算法一个密钥对生成器
            KeyPairGenerator keygen = KeyPairGenerator.getInstance(DSA);
            // 加密随机数生成器 (RNG)
            SecureRandom random = new SecureRandom();
            // 重新设置此随机对象的种子
            random.setSeed(1000);
            // 使用给定的随机源（和默认的参数集合）初始化确定密钥大小的密钥对生成器
            keygen.initialize(512, random);// keygen.initialize(512);
            // 生成密钥组
            KeyPair keys = keygen.generateKeyPair();
            return new Key[] { keys.getPrivate(), keys.getPublic() };
        } catch (NoSuchAlgorithmException e) {
            logger.error("创建密匙组异常:{}", e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 转换密钥<br>
     * 
     * @param key
     * @param alg
     *            算法
     * @return
     * @throws Exception
     */
    public static Key toKey(byte[] key, String alg) throws Exception {
        // 当使用其他对称加密算法时，如AES、Blowfish等算法时，可用下述代码替换后三行代码
        if (AES.equalsIgnoreCase(alg)) {
            return new SecretKeySpec(key, alg);
        }
        if(RSA.equalsIgnoreCase(alg)){
            // 取得私钥  
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return keyFactory.generatePrivate(pkcs8KeySpec);
        }
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(alg);
        return keyFactory.generateSecret(dks);
    }

    /**
     * 转换密钥<br>
     * 
     * @param password
     * @return
     * @throws Exception
     */
    public static Key toKey(String password, String alg) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(alg);
        return keyFactory.generateSecret(keySpec);
    }

    /**
     * BASE64编码
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static String encodeBase64(byte[] key) {
        return Encodes.encodeBase64(key);
    }

    /**
     * BASE64加密 常见于邮件、http加密，截取http信息， 用户名、密码字段BASE64加密
     * BASE64Encoder和BASE64Decoder是非官方JDK实现类, JRE 中 sun 和 com.sun
     * 开头包的类都是未被文档化的，属于 java, javax 类库的基础，其中的实现大多数与底层平台有关，一般来说是不推荐使用的。
     * 
     * 推荐@see encodeBase64
     * 
     * @param key
     * @return 加密后产生的字节位数是8的倍数，如果不够位数以=符号填充
     * @throws Exception
     */
    public static String encryptBASE64(byte[] key) throws Exception {
        return  Base64.encodeBase64String(key);
    }

    /**
     * BASE64解码
     * 
     * @param key
     * @return
     * @return
     * @throws Exception
     */
    public static byte[] decodeBase64(String key) {
        return Encodes.decodeBase64(key);
    }

    /**
     * BASE64解密
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decodeBase64(key);
    }

    public static long getCRC32Digest(byte[] bytes) {
        CRC32 crc = new CRC32();
        crc.update(bytes);
        return crc.getValue();
    }

    /**
     * 将二进制转化为16进制字符串
     * 
     * @param bytes
     *            二进制字节数组
     */
    public static String toHexString(byte[] bytes) {
        return StringUtil.toHexString(bytes);
    }

    /**
     * 十六进制字符串转化为2进制
     * 
     * @param hex
     * @return
     */
    public static byte[] hex2byte(String hex) {
        return StringUtil.hex2byte(hex);
    }

    public static String toString(byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        return new String(bytes);
    }

    /**
     * 判断算法是否是非对称算法
     * 
     * @param alg
     *            算法
     * @return
     */
    public static boolean isAsymmetric(String alg) {
        if (StringUtils.isBlank(alg)) {
            return false;
        }
        alg = alg.toUpperCase();
        return alg.equals(RSA);// || alg.equals(DSA);
    }

    /**
     * 根据键值进行加密
     * 
     * @param key
     *            加密键byte数组
     * @param data
     * @return
     * @throws Exception
     * @see encodeBase64
     */
    public static String encryptBase64DES(String key, String data) throws Exception {
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, new SecureRandom());
        byte[] bytes = cipher.doFinal(data.getBytes());
        return encodeBase64(bytes);
    }

    /**
     * 根据键值进行解密
     * 
     * @param key
     *            加密键byte数组
     * @param data
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decryptBase64DES(String key, String data) throws IOException, Exception {
        if (data == null)
            return null;
        byte[] buf = decodeBase64(data);
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, new SecureRandom());
        return new String(cipher.doFinal(buf));
    }
}
