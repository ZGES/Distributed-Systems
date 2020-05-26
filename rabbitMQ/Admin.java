import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Admin {

    public static void main(String[] argv) throws Exception {

        // initialize
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome in Administrator Panel");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME = "ACA_exchange";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        // listening thread
        AdminListener listener = new AdminListener(channel, EXCHANGE_NAME);
        listener.start();

        // main loop
        while(connection.isOpen()){
            String mode = stdIn.readLine();
            String key = "";

            switch (mode) {
                case "help":
                    System.out.println("Type number to select to whom send message: 1-'both agency and carrier', 2-'agency', 3-'carrier'. " +
                            "Then type the message. If want to close Administrator Panel type 'kill'");
                    break;
                case "kill":
                    System.out.println("Administrator Panel is being stopped...");
                    listener.join();
                    channel.close();
                    connection.close();
                    break;
                case "1":
                    key = "carrier.admin.agency";
                    break;
                case "2":
                    key = "admin.agency";
                    break;
                case "3":
                    key = "carrier.admin";
                    break;
                default:
                    System.out.println("This is not valid mode. Type help to get list of available modes.");
                    break;
            }

            if(!key.equals("")) {
                String message = stdIn.readLine();
                channel.basicPublish(EXCHANGE_NAME, key, null, message.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
