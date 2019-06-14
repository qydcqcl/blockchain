package com.blockchain.model;

import com.alibaba.fastjson.JSON;
import com.blockchain.security.CryptoUtil;
import com.blockchain.security.RSACoder;

import java.io.Serializable;

public class Transaction implements Serializable {

    private String id;

    private TransactionInput transactionInput;

    private TransactionOutput transactionOutput;

    public Transaction() {
    }

    public Transaction(String id, TransactionInput transactionInput, TransactionOutput transactionOutput) {
        this.id = id;
        this.transactionInput = transactionInput;
        this.transactionOutput = transactionOutput;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionInput getTransactionInput() {
        return transactionInput;
    }

    public void setTransactionInput(TransactionInput transactionInput) {
        this.transactionInput = transactionInput;
    }

    public TransactionOutput getTransactionOutput() {
        return transactionOutput;
    }

    public void setTransactionOutput(TransactionOutput transactionOutput) {
        this.transactionOutput = transactionOutput;
    }

    public boolean verify(Transaction prevTx){
        if(coinbaseTx()){
            return true;
        }

        if(!prevTx.getId().equals(transactionInput.getTxId())){
            System.out.println("验证交易签名失败: 当前交易输入引用的前一笔交易与传入的前一笔交易不匹配");
        }

        Transaction txClone = cloneTx();
        txClone.getTransactionInput().setPublicKey(prevTx.getTransactionOutput().getPublicKeyHash());

        boolean result = false;
        try {
            result = RSACoder.verify(txClone.hash().getBytes(), transactionInput.getPublicKey(), transactionInput.getSignnature());
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 用私钥生成交易签名
     * @param privateKey
     * @param prevTx
     */
    public void sign(String privateKey, Transaction prevTx ){
        if(coinbaseTx()){
            return;
        }

        if(!prevTx.getId().equals(transactionInput.getTxId())){
            System.err.println("交易签名失败: 当前交易输入引用的前一笔交易与传入的的前一笔交易不匹配!");
            return;
        }

        Transaction txClone = cloneTx();
        txClone.getTransactionInput().setPublicKey(prevTx.getTransactionOutput().getPublicKeyHash());
        String sign = "";
        try {
            sign = RSACoder.sign(txClone.hash().getBytes(), privateKey);
        }catch (Exception e){
            e.printStackTrace();
        }
        transactionInput.setSignnature(sign);
    }

    /**
     * 生成交易hash值
     * @return
     */
    private String hash(){
        return CryptoUtil.SHA256(JSON.toJSONString(this));
    }

    /**
     * 生成用于交易签名的交易记录副本
     * @return
     */
    private Transaction cloneTx() {
        TransactionInput txIn = new TransactionInput(transactionInput.getTxId(), transactionInput.getValue(), null, null);
        TransactionOutput txOut = new TransactionOutput(transactionOutput.getValue(), transactionOutput.getPublicKeyHash());
        return new Transaction(id, txIn, txOut);
    }

    /**
     * 是否是系统生成区块的奖励交易
     * @return
     */
    public boolean coinbaseTx() {
        return transactionInput.getTxId().equals("0") && getTransactionInput().getValue() == -1;
    }

}


