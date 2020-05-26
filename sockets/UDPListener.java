import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPListener extends Thread {

    private DatagramSocket udpSocket;
    private MessageCollector collector;

    public UDPListener(DatagramSocket udpSocket, MessageCollector collector){
        this.udpSocket = udpSocket;
        this.collector = collector;
    }

    @Override
    public void run() {
        byte[] receiveBuffer = new byte[1024];

        while(true){
            Arrays.fill(receiveBuffer, (byte)0);
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                udpSocket.receive(receivePacket);
            } catch (IOException e) {
                break;
            }
            System.out.println("Received ASCII art");
            collector.sendAllUDP(receivePacket);
        }
    }
}
