import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.HashSet;

public class ChatServer {

    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        int portNumber = 8888;
        HashSet<String> nicknameSet = new HashSet<>();
        HashSet<ConnectionHandler> connectionSet = new HashSet<>();

        try {
            System.out.println("CHAT SERVER");
            MessageCollector collector = new MessageCollector(nicknameSet, connectionSet);

            serverSocket = new ServerSocket(portNumber);

            DatagramSocket udpSocket = new DatagramSocket(portNumber);
            UDPListener udpListener = new UDPListener(udpSocket, collector);
            udpListener.start();

            while(true){
                ConnectionHandler handler = new ConnectionHandler(serverSocket.accept(), collector, udpSocket);
                connectionSet.add(handler);
                handler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
