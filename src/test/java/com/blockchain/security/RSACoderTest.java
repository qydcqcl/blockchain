package com.blockchain.security;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class RSACoderTest {

    private String publicKey;
    private String privateKey;

    @Before
    public void setUp() throws Exception {

        Map<String, Object> keyMap = RSACoder.initKey();
        publicKey = RSACoder.getPublicKey(keyMap);
        privateKey = RSACoder.getPrivateKey(keyMap);
        System.out.println("公钥: \r\n" + publicKey);
        System.out.println("私钥: \r\n" + privateKey);
    }

    @Test
    public void testEncry() throws Exception {
        System.out.println("非对称加密 公钥加密 私钥解密");
        String inputStr = "abc";
        byte[] data = inputStr.getBytes();


        byte[] encodeData = RSACoder.encryptByPublicKey(data, publicKey);
        byte[] decodeData = RSACoder.decryptByPrivateKey(encodeData, privateKey);

        String outputStr = new String(decodeData);
        System.out.println("加密前: " + inputStr + "解密后: " + outputStr);
    }

    @Test
    public void testSign() throws Exception {
        System.out.println("私钥签名 公钥验证签名");
        String inputStr = "sign";
        byte[] data = inputStr.getBytes();

        //产生签名
        String sign = RSACoder.sign(data, privateKey);
        System.out.println("签名: \r\n" + sign);

        //验证签名
        boolean b = RSACoder.verify(data, publicKey, sign);
        System.out.println("状态: \r\n" + b);
    }
}
