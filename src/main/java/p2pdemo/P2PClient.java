package p2pdemo;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class P2PClient {

    private List<WebSocket> sockets = new ArrayList<>();

    public List<WebSocket> getSockets(){
        return sockets;
    }

    public void connectToPeer(String peer){
        try {
            final WebSocketClient socketClient = new WebSocketClient(new URI(peer)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    write(this, "客户端连接成功");
                    sockets.add(this);
                }

                @Override
                public void onMessage(String s) {
                    System.out.println("收到服务器发送的消息: " + s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("connection failed");
                    sockets.remove(this);
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("connection failed");
                    sockets.remove(this);
                }
            };
            socketClient.connect();

        }catch (Exception e){
            System.out.println("p2p connection is error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void write(WebSocket webSocket, String msg) {
        System.out.println("发送给" + webSocket.getRemoteSocketAddress().getPort() + "的p2p消息: " + msg);
        webSocket.send(msg);
    }

    public void broatcast(String message){
        if(sockets.size() == 0){
            return;
        }
        System.out.println("=====广播消息开始: ");
        for(WebSocket socket : sockets){
            this.write(socket, message);
        }
        System.out.println("=====广播消息接收");
    }
}
