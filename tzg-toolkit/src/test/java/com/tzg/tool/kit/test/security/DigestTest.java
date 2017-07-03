package com.tzg.tool.kit.test.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyPair;
import java.util.Arrays;

import org.apache.commons.math.stat.descriptive.SynchronizedMultivariateSummaryStatistics;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tzg.tool.kit.file.FileUtils;
import com.tzg.tool.kit.security.CertificateUtil;
import com.tzg.tool.kit.security.Digest;
import com.tzg.tool.kit.security.DigestUtil;

/*
 * 安全加密、解密、证书、SSL单元测试用例
 * 对称加密、解密(DigestUtil)工具类、
 * 非对称加密、解密、签名、验签等工具类(SignatureUtil)、
 * 证书签名、验签、加密、解密、SSL等工具类(certificateUtil)的
 */
public class DigestTest extends CertificateUtil {
    private static final Logger logger  = LoggerFactory.getLogger(DigestTest.class);
    //要加密的明文测试数据
    private String              input   = "要被加密的明文数据";
    private String              charset = "UTF-8";

    @Test
    public void digestTest() throws Exception {
        logger.info("明文:{}", input);

        /*        try {
         *          String password = "0123456789";
                    byte[] bytes = encryptAES(input, password);
                    String hex = toHexString(bytes);
                    logger.info("密文:{}", hex);
                    logger.info("明文:{}", new String(decryptAES(hex2byte(hex), password)));
                    Digest digest = encryptDES(input);
                    logger.info("密文:{}", digest.getHex());
                    logger.info("明文:{}", digest.decrypt());
                } catch (Exception e) {
                    
                }*/
        Field[] fields = DigestUtil.class.getDeclaredFields();
        for (Field field : fields) {
            if (!field.getType().equals(String.class)) {
                continue;
            }
            String alg = (String) field.get(DigestUtil.class);
            Digest digest = DigestUtil.encrypt(input.getBytes(charset), alg);
            logger.info("{}加密后的密文:{}", alg, digest.getHex());
            logger.info("{}解密后的明文:{}", alg, digest.decrypt());
        }
        String str = DigestUtil.encodeBase64(input.getBytes());
        String out = DigestUtil.toString(DigestUtil.decodeBase64(str));
        logger.info("BASE64编码:{}", str);
        logger.info("BASE64解码:{}\t{}", out, input.equals(out));
        assertEquals(input, out);

        String key = DigestUtil.createHMACKey();
        out = DigestUtil.toString(DigestUtil.encryptHMAC(input.getBytes(), key));
        logger.info("BASE64解码:{}\t{}", out, input.equals(out));
        assertEquals(input, out);
    }

    @Test
    public void dhTest() throws Exception {
        //生成甲方公钥对儿
        KeyPair aKeyPair = getKeyPair(DH);
        String aPubKey = getKey(aKeyPair.getPublic());
        String aPrivKey = getKey(aKeyPair.getPrivate());
        logger.info("原文:{}", input);
        logger.info("\n甲方公钥:{}\n,甲方私钥:{}", aPubKey, aPrivKey);

        //由甲方公钥产生本地密钥对儿
        KeyPair bKeyPair = initDHKey(aPubKey);
        String bPubKey = getKey(bKeyPair.getPublic());
        String bPrivKey = getKey(bKeyPair.getPrivate());
        logger.info("\n乙方公钥:{}\n,乙方私钥:{}", bPubKey, bPrivKey);

        // 由甲方公钥，乙方私钥构建密文  
        byte[] aEncode = encryptDH(input.getBytes(), aPubKey, bPrivKey);
        // 由乙方公钥，甲方私钥解密  
        byte[] aDecode = decryptDH(aEncode, bPubKey, aPrivKey);
        logger.info("\n密文:{}\n解密:{}\n与原文相等:{}", toHexString(aEncode), toString(aDecode), toString(aDecode).equals(input));
        assertEquals(input, toString(aDecode));

        // 由乙方公钥，甲方私钥构建密文  
        byte[] bEncode = encryptDH(input.getBytes(), bPubKey, aPrivKey);
        byte[] bDecode = decryptDH(bEncode, aPubKey, bPrivKey);
        logger.info("\n密文:{}\n解密:{}\n与原文相等:{}", toHexString(bEncode), toString(bDecode), toString(bDecode).equals(input));
        assertEquals(input, toString(bDecode));
    }

    @Test
    public void rsaTest() throws Exception {
        KeyPair keyPair = getKeyPair(RSA);
        String pubKey = getKey(keyPair.getPublic());
        String privKey = getKey(keyPair.getPrivate());
        logger.info("\n公钥:{}\n,私钥:{}", pubKey, privKey);
        input = "公钥加密:\n私钥解密 :\n,测试数据中带有回车换行符";
        byte[] data = input.getBytes();
        byte[] eData = encryptRSAPubKey(data, pubKey);
        byte[] bytes="123".getBytes();
        System.out.println(Arrays.toString(encryptRSAPubKey(bytes, pubKey)));
        System.out.println(Arrays.toString(encryptRSAPubKey(bytes, pubKey)));
        
        System.out.println(encryptRSA(bytes).getHex());
        System.out.println(encryptRSA(bytes).getHex());
        
        System.out.println(encryptRSA(bytes).getText());
        System.out.println(encryptRSA(bytes).getText());
        
        byte[] out = decryptRSAPrivKey(eData, privKey);
        logger.info("\n原文:{}\n密文:{}\n解密:{}\n与原文相等:{}", input, toHexString(eData), toString(out), toString(out).equals(input));
        assertEquals(input, toString(out));

        input = "私钥加密:\n公钥解密 :\n,我顶顶顶顶顶顶顶顶顶顶顶顶顶顶";
        eData = input.getBytes();
        eData = encryptRSAPrivKey(eData, privKey);
        out = decryptRSAPubKey(eData, pubKey);
        logger.info("\n原文:{}\n密文:{}\n解密:{}\n与原文相等:{}", input, toHexString(eData), toString(out), toString(out).equals(input));
        assertEquals(input, toString(out));

        String sign = signRSA(eData, privKey);
        boolean flag = verifyRSA(eData, sign, pubKey);
        logger.info("\n私钥签名:{}\n公钥验签:{}", sign, flag);
        assertTrue(flag);
    }

    @Test
    public void dsaTest() throws Exception {
        KeyPair keyPair = getKeyPair(DSA, DEFAULT_SEED);
        String pubKey = getKey(keyPair.getPublic());
        String privKey = getKey(keyPair.getPrivate());
        logger.info("原文：{}", input);
        logger.info("\n公钥:{}\n,私钥:{}", pubKey, privKey);
        String sign = signDSA(input.getBytes(), privKey);
        boolean flag = verifyDSA(input.getBytes(), sign, pubKey);
        logger.info("签名:{},验签:{}", sign, flag);
        assertTrue(flag);

       

    }

    @Test
    public void dsaTest2() throws IOException, Exception {
        boolean flag;
        //原文：数据文件 
        File dataFile = new File("c:/tmp/data.xml");
        byte[] data = FileUtils.readFile(dataFile);
        File pubKeyFile = new File("c:/tmp/publicKey");
        File signFile = new File("c:/tmp/signFile");
        //发送方：1、 生成密钥对 2、对原文进行签名

        //先运行一次生成密钥对和签名文件,然后修改文件模拟数据在传送过程中被篡改,注释此段代码在此运行验签将不会通过
        //生成密钥对:私钥、公钥
        KeyPair keys = getKeyPair(DSA);
        FileUtils.write(pubKeyFile, getKey(keys.getPublic()));
        //用私钥对原文进行签名、生成签名文件
        FileUtils.write(signFile, signDSA(data, getKey(keys.getPrivate())));
        //发送方 发送数据给接收方,数据包括:公钥、签名、原文

        //接收方验证签名是否有效可得知文件是否被篡改
        flag = verifyDSA(data, FileUtils.readLine(signFile), FileUtils.readLine(pubKeyFile));
        assertTrue(flag);
        System.out.println(dataFile + (flag ? "未修改" : "已被篡改!"));
    }

    /* @Test
     public void eccTest() throws Exception {
         Key[] keyPair = initECCKey();
         String pubKey = getKey(keyPair[0]);
         String privKey = getKey(keyPair[1]);
         input="xyz";
         byte[] encode = encryptECC(input.getBytes(), privKey);
         byte[] decode = decryptECC(encode, pubKey);
         logger.info("\n原文:{}\n密文:{}\n解密:{}",input,encode,toString(decode));
         assertEquals(input, toString(decode));
     }*/

    @Test
    public void certificateTest() throws Exception {
        String pass = "tzg";
        String alias = "www.tzg-soft.com";
        String certificatePath = "c:/tmp/tzg.cer";
        String keyStorePath = "c:/tmp/tzg.keystore";
        String input = "公钥加密-私钥解密";
        byte[] encode = encryptByPublicKey(input.getBytes(), certificatePath);
        byte[] decode = decryptByPrivateKey(encode, keyStorePath, alias, pass);
        boolean flag = verifyCertificate(certificatePath);
        logger.info("\n原文:{} \n密文:{}\n解密:{}\n证书是否有效:{}", input, encode, toString(decode), flag);
        assertEquals(input, toString(decode));
        assertTrue(flag);

        input = "私钥加密——公钥解密";
        encode = encryptByPrivateKey(input.getBytes(), keyStorePath, alias, pass);
        decode = decryptByPrivateKey(encode, keyStorePath, alias, pass);
        String sign = sign(encode, keyStorePath, alias, pass);
        flag = verifyCertificate(encode, sign, certificatePath);
        logger.info("\n原文:{} \n密文:{}\n解密:{}\n签名:{}\n证书是否有效:{}", input, encode, toString(decode), sign, flag);
        assertEquals(input, toString(decode));
        assertTrue(flag);
    }

}
