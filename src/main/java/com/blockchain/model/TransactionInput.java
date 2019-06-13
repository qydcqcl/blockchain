package com.blockchain.model;

public class TransactionInput {

    /**
     * 前一次交易id
     */
    private String txId;

    /**
     * 交易金额
     */
    private int value;

    /**
     * 交易签名
     */
    private String signnature;

    /**
     * 交易发送方钱包公钥
     */
    private String publicKey;

    public TransactionInput() {
    }

    public TransactionInput(String txId, int value, String signnature, String publicKey) {
        this.txId = txId;
        this.value = value;
        this.signnature = signnature;
        this.publicKey = publicKey;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getSignnature() {
        return signnature;
    }

    public void setSignnature(String signnature) {
        this.signnature = signnature;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "TransactionInput{" +
                "txId='" + txId + '\'' +
                ", value=" + value +
                ", signnature='" + signnature + '\'' +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }
}
