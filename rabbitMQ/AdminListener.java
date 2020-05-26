import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AdminListener extends Thread {

    private Channel channel;
    private String EXCHANGE_NAME;

    public AdminListener(Channel channel, String EXCHANGE_NAME){
        this.channel = channel;
        this.EXCHANGE_NAME = EXCHANGE_NAME;
    }

    @Override
    public void run() {
        try {
            // queue & bind
            String agencyQueueName = "ALL_Agency";
            String agencyQueueKey = "agency.*";
            channel.queueDeclare(agencyQueueName, false, false, false, null);
            channel.queueBind(agencyQueueName, EXCHANGE_NAME, agencyQueueKey);

            String serviceQueueName = "All_Service";
            String serviceQueueKey = "service.*";
            channel.queueDeclare(serviceQueueName, false, false, false, null);
            channel.queueBind(serviceQueueName, EXCHANGE_NAME, serviceQueueKey);


            // message handling
            Consumer listener = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    System.out.println("MESSAGE: " + message);

                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };


            channel.basicConsume(agencyQueueName, false, listener);
            channel.basicConsume(serviceQueueName , false, listener);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
