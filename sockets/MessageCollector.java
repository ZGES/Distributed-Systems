import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashSet;

public class MessageCollector  {

    private HashSet<String> nicknames;
    private HashSet<ConnectionHandler> connections;

    public MessageCollector(HashSet<String> nicknames, HashSet<ConnectionHandler> connections){
        this.nicknames = nicknames;
        this.connections = connections;
    }

    public boolean addClient(String nickname){
        if(nickname.equals(""))
            return false;
        return nicknames.add(nickname);
    }

    public void removeClient(String nickname, ConnectionHandler client){
        nicknames.remove(nickname);
        connections.remove(client);
    }

    private String findNickname(InetAddress address, int portNumber){
        for (ConnectionHandler client : connections) {
            InetAddress clientAddress = client.getTcpSocket().getInetAddress();
            int clientPort = client.getTcpSocket().getPort();

            if(address.equals(clientAddress) && portNumber == clientPort)
                return client.getNickname();
        }
        return "";
    }

    public void sendAll(String message, String nickname){
        for (ConnectionHandler client : connections) {
            if(!client.getNickname().equals(nickname) && !client.getNickname().equals(""))
                client.send(message, nickname);
        }
    }
    public void sendAllUDP(DatagramPacket receivePacket){
        InetAddress address = receivePacket.getAddress();
        int portNumber = receivePacket.getPort();
        String nickname = findNickname(address, portNumber);
        for (ConnectionHandler client : connections) {
            if(!client.getNickname().equals(nickname) && !client.getNickname().equals("")) {
                    client.sendUDP(receivePacket);
            }
        }
    }

}
