import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;

public class Carrier {

    private String firstService;
    private String secondService;

    public Carrier(Integer firstIndex, Integer secondIndex){
        this.firstService = Services.values()[firstIndex-1].getService();
        this.secondService = Services.values()[secondIndex-1].getService();
    }

    public String getFirstService() {
        return firstService;
    }

    public String getSecondService() {
        return secondService;
    }

    public static void main(String[] argv) throws Exception {
        // initialize
        String JVM_ID = ManagementFactory.getRuntimeMXBean().getName();
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Select two services you provide. To choose, type two of numbers: 1 - load, 2 - person, 3 - satellite, for example '1 2'");
        String services  = stdIn.readLine();
        while(!(services.equals("1 2") || services.equals("2 1") || services.equals("1 3") || services.equals("3 1") || services.equals("2 3")
                || services.equals("3 2"))) {
            System.out.println("Passed wrong services. Try again!");
            services = stdIn.readLine();
        }

        String[] twoServices = services.split(" ");
        Integer firstIndex = Integer.parseInt(twoServices[0]);
        Integer secondIndex = Integer.parseInt(twoServices[1]);
        Carrier carrier;
        if(firstIndex < secondIndex)
            carrier = new Carrier(firstIndex, secondIndex);
        else
            carrier = new Carrier(secondIndex, firstIndex);

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // exchange
        String EXCHANGE_NAME = "ACA_exchange";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        // queue & bind
        for (String service : twoServices) {
            String queueName = Services.values()[Integer.parseInt(service) - 1].getService();
            String queueKey = "service." + queueName;
            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, queueKey);
        }
        String queueName = "Admin_Carrier-" + JVM_ID;
        String queueKey = "carrier.admin.#";
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, EXCHANGE_NAME, queueKey);


        // message handling
        Consumer orders = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                String[] messageParts = message.split(":");
                String queueKey = "agency." + messageParts[0];

                System.out.println("Received order: " + messageParts[1] + " from AGENCY: " + messageParts[0]);
                String newMessage = "Order: " + messageParts[1] + " has been received";
                channel.basicPublish(envelope.getExchange(), queueKey, null, newMessage.getBytes(StandardCharsets.UTF_8));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        Consumer admin = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println("ADMIN MESSAGE: " + message);

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        System.out.println("Carrier" + JVM_ID + " is ready for orders...");
        channel.basicQos(1);
        channel.basicConsume(carrier.getFirstService(), false, orders);
        channel.basicConsume(carrier.getSecondService(), false, orders);
        channel.basicConsume(queueName, false, admin);
    }
}
