import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class DatagramReader extends Thread {

    private DatagramSocket datagramSocket;

    public DatagramReader(DatagramSocket datagramSocket){
        this.datagramSocket = datagramSocket;
    }


    @Override
    public void run() {
        byte[] receiveBuffer = new byte[1024];
        String message;

        while(true){
            Arrays.fill(receiveBuffer, (byte)0);
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                datagramSocket.receive(receivePacket);
            } catch (IOException e) {
                break;
            }
            message = new String(receivePacket.getData());
            System.out.print(message.trim());
        }
    }
}
