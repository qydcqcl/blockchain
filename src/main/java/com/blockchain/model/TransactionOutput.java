package com.blockchain.model;

public class TransactionOutput {

    /**
     * 交易金额
     */
    private int value;

    /**
     * 交易接收方的钱包公钥的hash
     */
    private String publicKeyHash;

    public TransactionOutput() {
    }

    public TransactionOutput(int value, String publicKeyHash) {
        this.value = value;
        this.publicKeyHash = publicKeyHash;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getPublicKeyHash() {
        return publicKeyHash;
    }

    public void setPublicKeyHash(String publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
    }

    @Override
    public String toString() {
        return "TransactionOutput{" +
                "value=" + value +
                ", publicKeyHash='" + publicKeyHash + '\'' +
                '}';
    }
}
