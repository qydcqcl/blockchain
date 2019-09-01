package com.blockchain;

import com.blockchain.block.BlockService;
import com.blockchain.http.HTTPService;
import com.blockchain.p2p.P2PClient;
import com.blockchain.p2p.P2PServer;
import com.blockchain.p2p.P2PService;

public class Blockchain {

    public static void main(String[] args) {
        if(args != null && (args.length == 1 || args.length == 2 || args.length == 3)){
            try {
                BlockService blockService = new BlockService();
                P2PService p2pService = new P2PService(blockService);
                startP2PServer(args, p2pService);
                HTTPService httpService = new HTTPService(blockService, p2pService);
                Integer httpPort = Integer.valueOf(args[0]);
                httpService.initHTTPService(httpPort);
            }catch (Exception e){
                System.out.println("startup is error: " + e.getMessage());
                e.printStackTrace();
            }
        }else{
            System.out.println("startup is error: no param");
        }
    }

    private static void startP2PServer(String[] args, P2PService p2pService) {
        P2PServer p2pServer = new P2PServer(p2pService);
        P2PClient p2pClient = new P2PClient(p2pService);
        Integer p2pPort = Integer.valueOf(args[1]);
        //启动p2p服务端
        p2pServer.initP2PServer(p2pPort);
        if(args.length == 3 && args[2] != null){
            //作为p2p客户端连接盘p2p服务器
            p2pClient.connectToPeer(args[2]);
        }
    }
}
