package p2pdemo;

public class Main {

    public static void main(String[] args) {
        P2PServer p2pServer = new P2PServer();
        P2PClient p2pClient = new P2PClient();

        Integer p2pPort = Integer.valueOf(args[0]);
        p2pServer.initP2PServer(p2pPort);
        if(args.length == 2 && args[1] != null){
            p2pClient.connectToPeer(args[1]);
        }
    }
}
