package grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;


public class SubscriptionServer {

    private static final int PORT = 7777;
    private Server server;

    private void start(){
        try {
            server = ServerBuilder.forPort(PORT)
                    .addService(new SubscriptionImpl())
                    .addService(new IsAliveImpl())
                    .build()
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitToTerminate(){
        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        System.out.println("Server is up!");
        final SubscriptionServer server = new SubscriptionServer();
        server.start();
        server.waitToTerminate();
    }
}

