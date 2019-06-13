package com.blockchain.model;

import java.util.List;

public class Block {

    /**
     * 区块索引
     */
    private int index;

    /**
     * 当前区块的hash值，区块的唯一标识
     */
    private String hash;

    /**
     * 生成区块的时间戳
     */
    private long timetamp;

    /**
     * 当前区块的交易集合
     */
    private List<Transaction> transactions;

    /**
     * 工作量证明，计算正确hash值的次数
     */
    private int nonce;

    /**
     * 上一区块的hash值
     */
    private String previousHash;

    public Block() {
    }

    public Block(int index, String hash, long timetamp, List<Transaction> transactions, int nonce, String previousHash) {
        this.index = index;
        this.hash = hash;
        this.timetamp = timetamp;
        this.transactions = transactions;
        this.nonce = nonce;
        this.previousHash = previousHash;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getTimetamp() {
        return timetamp;
    }

    public void setTimetamp(long timetamp) {
        this.timetamp = timetamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", hash='" + hash + '\'' +
                ", timetamp=" + timetamp +
                ", transactions=" + transactions +
                ", nonce=" + nonce +
                ", previousHash='" + previousHash + '\'' +
                '}';
    }
}
