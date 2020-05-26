import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class Client {

    private String nickname;
    private int serverPortNumber;
    private String hostName;
    private String multicastGroup;

    public Client(String hostName, int serverPortNumber, String multicastGroup){
        this.hostName = hostName;
        this.serverPortNumber = serverPortNumber;
        this.multicastGroup = multicastGroup;
    }

    public void setNickname(String nickname){this.nickname = nickname;}

    public static void main(String[] args) {

        Client client = new Client("localhost", 8888, "239.0.1.0");
        Socket socket = null;

        DatagramSocket udpSocket = null;
        MulticastSocket multiSocket = null;

        try {
            socket = new Socket(client.hostName, client.serverPortNumber);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            udpSocket = new DatagramSocket(socket.getLocalPort());

            multiSocket = new MulticastSocket(7777);
            multiSocket.joinGroup(InetAddress.getByName(client.multicastGroup));
            multiSocket.setTimeToLive(0);

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String nickname;

            System.out.println("Enter your nickname: ");
            nickname = stdIn.readLine();
            out.println(nickname);
            while(in.readLine().equals("bad nickname")) {
                System.out.println("Your nickname is not valid or already in use. Try another one:");
                nickname = stdIn.readLine();
                out.println(nickname);
            }

            client.setNickname(nickname);
            System.out.println(client.nickname + " has joined the room");

            Reader reader = new Reader(in, udpSocket, multiSocket);
            Writer writer = new Writer(out, udpSocket, client.hostName, client.serverPortNumber, multiSocket, client.multicastGroup);

            reader.start();
            writer.start();

            reader.join();
            writer.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(socket != null)
                    socket.close();
                if(udpSocket != null)
                    udpSocket.close();
                if(multiSocket != null) {
                    multiSocket.leaveGroup(InetAddress.getByName(client.multicastGroup));
                    multiSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
