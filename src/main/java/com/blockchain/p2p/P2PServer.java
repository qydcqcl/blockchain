package com.blockchain.p2p;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class P2PServer {

    private P2PService p2pService;

    public P2PServer(P2PService p2pService) {
        this.p2pService = p2pService;
    }

    public void initP2PServer(int port){
        final WebSocketServer socketServer = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                p2pService.getSockets().add(webSocket);
            }

            @Override
            public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                System.out.println("connection fail to peer: " + webSocket.getRemoteSocketAddress());
                p2pService.getSockets().remove(webSocket);
            }

            @Override
            public void onMessage(WebSocket webSocket, String msg) {
                p2pService.handleMessage(webSocket, msg, p2pService.getSockets());
            }

            @Override
            public void onError(WebSocket webSocket, Exception e) {
                System.out.println("connection failed to peer: " + webSocket.getRemoteSocketAddress());
                p2pService.getSockets().remove(webSocket);
            }

            @Override
            public void onStart() {

            }
        };
        socketServer.start();
        System.out.println("listening webSocket p2p port on: " + port);
    }
}
