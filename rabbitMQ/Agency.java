import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;

public class Agency {

    private String name;
    private long orderID;

    public Agency(String name){
        this.name = name;
        this.orderID = 0;
    }

    public String getName() {
        return name;
    }

    public void incrementOrderID() {
        this.orderID += 1;
    }

    public long getOrderID() {
        return orderID;
    }

    public static void main(String[] argv) throws Exception {

        // initialize
        String JVM_ID = ManagementFactory.getRuntimeMXBean().getName();
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Set agency name: ");
        String agencyName = "";

        while(agencyName.equals("")){
            agencyName = stdIn.readLine();
            if(agencyName.equals(""))
                System.out.println("Wrong name. Try another one.");
        }

        Agency agency = new Agency(agencyName);

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME = "ACA_exchange";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        System.out.println("AGENCY " + agency.getName() + " is working...");

        // listening thread
        AgencyListener listener = new AgencyListener(agency, channel, EXCHANGE_NAME, JVM_ID);
        listener.start();

        // main loop
        while(connection.isOpen()){
            System.out.print("> ");
            String service = stdIn.readLine();

            switch (service) {
                case "help":
                    System.out.println("Choose form: 'load', 'person', 'satellite'. If want to close Agency type 'kill'");
                    break;
                case "kill":
                    System.out.println("AGENCY " + agency.getName() + " is being stopped...");
                    listener.join();
                    channel.close();
                    connection.close();
                    break;
                case "load":
                case "person":
                case "satellite":
                    String message = agency.getName() + JVM_ID + ":" + agency.getOrderID();
                    String key = "service." + service;
                    channel.basicPublish(EXCHANGE_NAME, key, null, message.getBytes(StandardCharsets.UTF_8));
                    System.out.println("> Order: " + agency.getOrderID() + " has been sent");
                    agency.incrementOrderID();
                    break;
                default:
                    System.out.println("This is not valid order. Type help to get list of current services.");
                    break;
            }

        }
    }
}
