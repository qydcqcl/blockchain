package com.blockchain.model;

import com.blockchain.security.CryptoUtil;
import com.blockchain.security.RSACoder;

import java.util.Map;

public class Wallet {

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

    public Wallet() {
    }

    public Wallet(String publicKey) {
        this.publicKey = publicKey;
    }

    public Wallet(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * 生成钱包
     * @return
     */
    public static Wallet generateWallet(){
        Map<String, Object> initKey;
        try {
            initKey = RSACoder.initKey();
            String publicKey = RSACoder.getPublicKey(initKey);
            String privateKey = RSACoder.getPrivateKey(initKey);
            return new Wallet(publicKey, privateKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String getAddress(){
        String publicKeyHash = hashPubKey(publicKey);
        return CryptoUtil.MD5(publicKeyHash);
    }

    public static String getAddress(String publicKey){
        String publicKeyHash = hashPubKey(publicKey);
        return CryptoUtil.MD5(publicKeyHash);
    }

    public String getHashPubKey(){
        return CryptoUtil.SHA256(publicKey);
    }

    private static String hashPubKey(String publicKey) {
        return CryptoUtil.SHA256(publicKey);
    }

}
