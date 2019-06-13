package com.blockchain.block;

import com.alibaba.fastjson.JSON;
import com.blockchain.model.Block;
import com.blockchain.model.Transaction;
import com.blockchain.security.CryptoUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BolckServiceTest {

    @Before
    public void setUp(){

    }

    @Test
    public void testBlockMine(){
        //创建空的区块链
        List<Block> blockchain = new ArrayList<Block>();
        
        //创世区块
        Block block = new Block(0, "1", System.currentTimeMillis(), new ArrayList<Transaction>(), 1, "1");

        blockchain.add(block);

        System.out.println(JSON.toJSONString(blockchain));

        //创建一个空的交易集合
        List<Transaction> txs = new ArrayList<Transaction>();
        Transaction tx1 = new Transaction();
        Transaction tx2 = new Transaction();
        Transaction tx3 = new Transaction();
        txs.add(tx1);
        txs.add(tx2);
        txs.add(tx3);
        //加入系统奖励的交易
        Transaction sysTx = new Transaction();
        txs.add(sysTx);

        //获取最后一个区块
        Block lastblock = blockchain.get(blockchain.size() - 1);

        int nonce = 1;
        String hash = "";
        while(true){
            hash = CryptoUtil.SHA256(lastblock.getHash() + JSON.toJSONString(txs) + nonce);
            if(hash.startsWith("0000")){
                System.out.println("========计算结果正确,计算次数为: " + nonce + ",hash: " + hash);
                break;
            }
            nonce++;
            System.out.println("=======计算错误,hash: " + hash);
        }

        //添加的区块链
        Block newBlock = new Block(lastblock.getIndex() + 1, hash, System.currentTimeMillis(), txs, nonce, lastblock.getHash());
        blockchain.add(newBlock);
    }
}
