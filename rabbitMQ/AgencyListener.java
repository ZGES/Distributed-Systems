import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AgencyListener extends Thread {

    private Agency agency;
    private Channel channel;
    private String EXCHANGE_NAME;
    private String JVM_ID;

    public AgencyListener(Agency agency, Channel channel, String EXCHANGE_NAME, String JVM_ID){
        this.agency = agency;
        this.channel = channel;
        this.EXCHANGE_NAME= EXCHANGE_NAME;
        this.JVM_ID = JVM_ID;
    }

    @Override
    public void run() {
        // queue & binds
        String queueName = agency.getName() + JVM_ID;
        String queueKey = "agency." + queueName;
        String fromAdminQueueName = "Admin_Agency-" + agency.getName() + JVM_ID;
        String fromAdminQueueKey = "#.admin.agency";
        try {
            channel.queueDeclare(fromAdminQueueName, false, false, false, null);
            channel.queueBind(fromAdminQueueName, EXCHANGE_NAME, fromAdminQueueKey);

            channel.queueDeclare(queueName, false, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, queueKey);

            // message handling
            Consumer replier = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.print(message + "\n> ");

                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };

            Consumer admin = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.print("ADMIN MESSAGE: " + message + "\n> ");

                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };

            channel.basicConsume(queueName, false, replier);
            channel.basicConsume(fromAdminQueueName, false, admin);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
