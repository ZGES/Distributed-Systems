import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MulticastSocket;


public class Reader extends Thread {

    private BufferedReader in;
    private DatagramSocket udpSocket;
    private DatagramSocket multiSocket;
    DatagramReader udpReader;
    DatagramReader multiReader;

    public Reader(BufferedReader in, DatagramSocket udpSocket, MulticastSocket multiSocket){
        this.in = in;
        this.udpSocket = udpSocket;
        this.multiSocket = multiSocket;
    }

    @Override
    public void run() {
        try {
            // UDP message read thread
            udpReader = new DatagramReader(udpSocket);
            udpReader.start();

            //Multicast message read thread
            multiReader = new DatagramReader(multiSocket);
            multiReader.start();

            //TCP message read
            String message;
            while((message = in.readLine()) != null && !message.equals("ok")){
                System.out.println(message.trim());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
