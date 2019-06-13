package com.blockchain.block;

import com.alibaba.fastjson.JSON;
import com.blockchain.model.*;
import com.blockchain.security.CryptoUtil;

import java.util.*;

public class BlockService {

    /**
     * 区块链存储结构
     */
    private List<Block> blockchain = new ArrayList<Block>();

    /**
     * 当前节点钱包集合
     */
    private Map<String, Wallet> myWalletMap = new HashMap<String, Wallet>();

    /**
     * 其他节点钱包集合,只有公钥
     */
    private Map<String, Wallet> otherWalletMap = new HashMap<>();

    /**
     * 所有的交易
     */
    private List<Transaction> allTransaction = new ArrayList<Transaction>();

    /**
     * 已打包的交易
     */
    private List<Transaction> packedTransaction = new ArrayList<Transaction>();

    public BlockService() {
        //创世区块
        Block gensisBlock = new Block(1, "1", System.currentTimeMillis(), new ArrayList<>(), 1, "1");
        blockchain.add(gensisBlock);
        System.out.println("生成创世区块: " + JSON.toJSONString(gensisBlock));
    }

    /**
     * 获取最新区块
     * @return
     */
    public Block getLatestBlock(){
        return blockchain.size() > 0 ? blockchain.get(blockchain.size() - 1) : null;
    }

    /**
     * 创建钱包
     * @return
     */
    public Wallet createWallet() {
        Wallet wallet = Wallet.generateWallet();
        String address = wallet.getAddress();
        myWalletMap.put(address, wallet);
        return wallet;
    }

    /**
     * 获取钱包余额
     * @param address
     * @return
     */
    public int getWalletBalance(String address){
        List<Transaction> unspentTxs = findUnspentTransaction(address);
        int balance = 0;
        for(Transaction transaction : unspentTxs){
            balance += transaction.getTransactionOutput().getValue();
        }
        return balance;
    }

    /**
     * 创建交易
     * @return
     */
    public Transaction createTransaction(Wallet senderWallet, Wallet recipientWallet, int amout){
        List<Transaction> unspentTxs = findUnspentTransaction(senderWallet.getAddress());
        Transaction prevTx = null;
        for(Transaction transaction : unspentTxs){
            if(transaction.getTransactionOutput().getValue() == amout){
                prevTx = transaction;
                break;
            }
        }

        if(prevTx == null){
            return null;
        }

        TransactionInput txIn = new TransactionInput(prevTx.getId(), amout, null, senderWallet.getPublicKey());
        TransactionOutput txOut = new TransactionOutput(amout, recipientWallet.getHashPubKey());
        Transaction transaction = new Transaction(CryptoUtil.UUID(), txIn, txOut);
        transaction.sign(senderWallet.getPrivateKey(), prevTx);
        allTransaction.add(transaction);
        return transaction;
    }


    /**
     * 查找未被消费的交易
     * @param address
     * @return
     */
    private List<Transaction> findUnspentTransaction(String address) {
        List<Transaction> unspentTxs = new ArrayList<>();
        Set<String> spentTxs = new HashSet<>();
        for(Transaction tx : allTransaction){
            if(tx.coinbaseTx()){
                continue;
            }

            if(address.equals(Wallet.getAddress(tx.getTransactionInput().getPublicKey()))){
                spentTxs.add(tx.getTransactionInput().getTxId());
            }
        }

        for(Block block : blockchain){
            List<Transaction> transactions = block.getTransactions();
            for(Transaction transaction : transactions){
                if(address.equals(CryptoUtil.MD5(transaction.getTransactionOutput().getPublicKeyHash()))){
                    if(!spentTxs.contains(transaction.getId())){
                        unspentTxs.add(transaction);
                    }
                }
            }
        }
        return unspentTxs;
    }

    /**
     * 挖矿
     * @return
     */
    public Block mine(String toAddress){
        //创建系统交易
        allTransaction.add(newCoinbaseTx(toAddress));

        //去除已打包进区块的交易
        List<Transaction> blockTxs = new ArrayList<Transaction>(allTransaction);
        blockTxs.removeAll(packedTransaction);
        //验证所有的交易
        verifyAllTransactions(blockTxs);

        String newBlockHash = "";
        int nonce = 0;
        long start = System.currentTimeMillis();
        System.out.println("开始挖矿");
        while(true){
            //计算新区块的hash值
            newBlockHash = calculateHash(getLatestBlock().getHash(), blockTxs, nonce);
            //验证hash值
            if(isValidHash(newBlockHash)){
                System.out.println("挖矿完成,正确的hash值: " + newBlockHash);
                System.out.println("挖矿耗费的时间: " + (System.currentTimeMillis() - start) + "ms");
                break;
            }
            System.out.println("错误的hash值: " + newBlockHash);
            nonce++;
        }

        //创建新区块

        Block block = createNewBlock(nonce, getLatestBlock().getHash(), newBlockHash, blockTxs);
        return block;
    }

    private Transaction newCoinbaseTx(String toAddress) {
        TransactionInput transactionInput = new TransactionInput("0", -1, null, null);
        Wallet wallet = myWalletMap.get(toAddress);
        //指定生成区块的奖励为10BTC
        TransactionOutput transactionOutput = new TransactionOutput(10, wallet.getHashPubKey());
        Transaction transaction = new Transaction(CryptoUtil.UUID(), transactionInput, transactionOutput);
        return transaction;
    }

    /**
     * 验证所有交易是否有效
     * @param blockTxs
     */
    private void verifyAllTransactions(List<Transaction> blockTxs) {
        List<Transaction> invalidTxs = new ArrayList<>();
        for(Transaction tx : blockTxs){
            if(!verifyTransactions(tx)){
                invalidTxs.add(tx);
            }
        }

        blockTxs.removeAll(invalidTxs);
        //去除无效的交易
        allTransaction.removeAll(invalidTxs);

    }

    private boolean verifyTransactions(Transaction tx) {
        if(tx.coinbaseTx()){
            return true;
        }

        Transaction prevTx = findTransaction(tx.getTransactionInput().getTxId());
        return tx.verify(prevTx);
    }

    private Transaction findTransaction(String txId) {
        for(Transaction tx : allTransaction){
            if(txId.equals(tx.getId())){
                return tx;
            }
        }
        return null;
    }

    private Block createNewBlock(int nonce, String previousHash, String newBlockHash, List<Transaction> blockTxs) {
        Block block = new Block(blockchain.size() + 1, newBlockHash, System.currentTimeMillis(), blockTxs, nonce, previousHash);
        if(addBlock(block)){
            return block;
        }
        return null;
    }

    /**
     * 添加新区块
     * @param newBlock
     * @return
     */
    public boolean addBlock(Block newBlock) {
        if(isValidNewBlock(newBlock, getLatestBlock())){
            blockchain.add(newBlock);
            //新区块的交易添加到已打包交易集合
            packedTransaction.addAll(newBlock.getTransactions());
            return true;
        }
        return false;
    }

    /**
     * 验证新区块是否有效
     * @param newBlock
     * @param previousBlock
     * @return
     */
    public boolean isValidNewBlock(Block newBlock, Block previousBlock){
        if(!previousBlock.getHash().equals(newBlock.getPreviousHash())){
            System.out.println("新区块的前一个区块hash验证不通过");
            return false;
        }else{
            //验证新区块hash值的正确性
            String hash = calculateHash(newBlock.getPreviousHash(), newBlock.getTransactions(), newBlock.getNonce());
            if(!hash.equals(newBlock.getHash())){
                System.out.println("新区块的hash无效: " + hash + " " + newBlock.getHash());
                return false;
            }
            if(!isValidHash(newBlock.getHash())){
                return false;
            }
        }
        return true;
    }

    /**
     * 验证hash值是否满足系统条件
     * @param hash
     * @return
     */
    public boolean isValidHash(String hash){
        return hash.startsWith("0000");
    }

    /**
     * 计算区块的hash
     * @param previousHash
     * @param currentTransaction
     * @param nonce
     * @return
     */
    public String calculateHash(String previousHash, List<Transaction> currentTransaction, int nonce){
        return CryptoUtil.SHA256(previousHash + JSON.toJSONString(currentTransaction) + nonce);
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(List<Block> blockchain) {
        this.blockchain = blockchain;
    }

    public Map<String, Wallet> getMyWalletMap() {
        return myWalletMap;
    }

    public void setMyWalletMap(Map<String, Wallet> myWalletMap) {
        this.myWalletMap = myWalletMap;
    }

    public Map<String, Wallet> getOtherWalletMap() {
        return otherWalletMap;
    }

    public void setOtherWalletMap(Map<String, Wallet> otherWalletMap) {
        this.otherWalletMap = otherWalletMap;
    }

    public List<Transaction> getAllTransaction() {
        return allTransaction;
    }

    public void setAllTransaction(List<Transaction> allTransaction) {
        this.allTransaction = allTransaction;
    }

    public List<Transaction> getPackedTransaction() {
        return packedTransaction;
    }

    public void setPackedTransaction(List<Transaction> packedTransaction) {
        this.packedTransaction = packedTransaction;
    }

    /**
     * 替换本地区块链
     * @param newBlocks
     */
    public void replaceChain(List<Block> newBlocks) {
        if(isValidChain(newBlocks) && newBlocks.size() > blockchain.size()){
            blockchain = newBlocks;
            //更新已打包交易集合
            packedTransaction.clear();
            blockchain.forEach(block -> {
                packedTransaction.addAll(block.getTransactions());
            });

        }else{
            System.out.println("接收的区块无效");
        }
    }

    /**
     * 验证整个区块链是否有效
     * @param chain
     * @return
     */
    private boolean isValidChain(List<Block> chain) {
        Block block = null;
        Block lastBlock = chain.get(0);
        int currentIndex = 1;
        while(currentIndex < chain.size()){
            block = chain.get(currentIndex);
            if(!isValidNewBlock(block, lastBlock)){
                return false;
            }
            lastBlock = block;
            currentIndex++;
        }
        return true;
    }
}
