import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Writer extends Thread {

    private PrintWriter out;
    private DatagramSocket udpSocket;
    private MulticastSocket multiSocket;
    private String hostname;
    private int portNumber;
    private String multiGroup;

    public Writer(PrintWriter out, DatagramSocket udpSocket, String hostname, int portNumber, MulticastSocket multiSocket, String multiGroup){
        this.out = out;
        this.udpSocket = udpSocket;
        this.hostname = hostname;
        this.portNumber = portNumber;
        this.multiSocket = multiSocket;
        this.multiGroup = multiGroup;
    }


    @Override
    public void run() {
        try {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            InetAddress address = InetAddress.getByName(hostname);
            DatagramPacket sendPacket;

            String message;
            do{
                message = stdIn.readLine();

                //Sending ASCII art by UDP
                if(message.equals("U")){
                    byte[] sendBuffer = new byte[1024];
                    FileInputStream fIn = new FileInputStream(new File(".").getAbsolutePath() + "\\ASCIIART.txt");
                    int i = 0;
                    while((sendBuffer[i] = (byte)fIn.read()) != -1){
                        if(i == sendBuffer.length-1){
                            sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
                            udpSocket.send(sendPacket);
                            i = -1;
                            sendBuffer = new byte[1024];
                        }
                        i++;
                    }
                    fIn.close();
                    sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
                    udpSocket.send(sendPacket);
                }

                //Multicast communication
                if(message.equals("M")){
                    String multiMessage = stdIn.readLine();
                    String toSend = "Multicast: ".concat(multiMessage);
                    byte[] sendBuffer = toSend.getBytes();
                    sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(multiGroup), 7777);
                    multiSocket.send(sendPacket);
                }

                if(!(message.equals("") || message.equals("U") || message.equals("M")))
                    out.println(message);
            }while(!message.equals("ok"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
