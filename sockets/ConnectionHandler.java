import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;


public class ConnectionHandler extends Thread{

    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private PrintWriter out;
    BufferedReader in;
    private String nickname;
    private MessageCollector collector;


    public ConnectionHandler(Socket tcpSocket, MessageCollector collector, DatagramSocket udpSocket){
        this.tcpSocket = tcpSocket;
        this.collector = collector;
        this.nickname = "";
        this.udpSocket = udpSocket;
    }

    public void send(String message, String user){
            out.println(user + ": " + message);
    }

    public void sendUDP(DatagramPacket receivePacket) {
        byte[] data = receivePacket.getData();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, tcpSocket.getInetAddress(), tcpSocket.getPort());
        try {
            udpSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String  getNickname(){
        return this.nickname;
    }

    public Socket getTcpSocket() {return  this.tcpSocket;}

    @Override
    public void run() {
        try {
            out = new PrintWriter(tcpSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

            boolean added = false;
            while(!added) {
                nickname = in.readLine();
                if(!(added = collector.addClient(nickname)))
                    out.println("bad nickname");
            }
            out.println("Joined chat");
            collector.sendAll(nickname + " has joined the room", nickname);
            System.out.println(nickname + " has joined the room");

            String received;

            while((received = in.readLine()) != null){
                if(received.equals("ok")){
                    break;
                }
                System.out.println("Message received from: " + nickname);
                collector.sendAll(received, nickname);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                String byeMessage = nickname + " left room";
                System.out.println(byeMessage);
                collector.sendAll(byeMessage, nickname);
                out.println("ok");
                collector.removeClient(nickname, this);
                if(tcpSocket != null)
                    tcpSocket.close();
                if(udpSocket != null)
                    udpSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
