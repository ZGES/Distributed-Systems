package grpc;

import io.grpc.stub.ServerCallStreamObserver;
import sr.grpc.gen.Reply;

import sr.grpc.gen.SubType;
import sr.grpc.gen.SubscribeMessage;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubscribeManager {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private final ConcurrentHashMap<String, Client> clients;
    private ArrayList<String> cities;

    public SubscribeManager(){
        this.clients = new ConcurrentHashMap<>();
        this.cities = new ArrayList<>();
        cities.add("Krakow");
        cities.add("Warszawa");
        cities.add("Gdansk");
        this.runGenerator();
    }

    public void addClient(SubscribeMessage request){
        System.out.println(request);
        String clientName = request.getClientName();
        Client client = this.clients.getOrDefault(clientName, null);

        if(client == null){
            client = new Client(clientName);
            this.clients.put(clientName, client);
        }
        client.subscribeOn(request);
        if(!cities.contains(request.getCityName()))
            cities.add(request.getCityName());
        System.out.println("NEW CLIENT SUBSCRIBED: " + clientName + " FOR CITY: " + request.getCityName());
    }

    public boolean startListen(String clientName, ServerCallStreamObserver<Reply> observer){
        Client client = this.clients.getOrDefault(clientName, null);
        if(client == null)
            return false;

        observer.setOnCancelHandler(() -> {
            System.out.println("CLIENT " + clientName + " DISCONECTED");
            this.clients.remove(clientName);
        });
        client.setStreamObserver(observer);
        return true;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void generate(){
        while(true) {
            Random rand = new Random();

            String city = cities.get(rand.nextInt(cities.size()));
            SubType type = SubType.forNumber(rand.nextInt(4));

            if(type != null && city != null) {

                Reply reply = generateReply(city, type);
                this.clients.values().forEach((client) -> {
                    if (client.getSubTypesByCity(city).contains(type) && client.getObserver() != null) {
                        client.getObserver().onNext(reply);
                    }
                });
                try {
                    Thread.sleep(rand.nextInt(1000) + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("GENERATED " + reply.getReply());
            }
        }

    }

    public Reply generateReply(String city, SubType type){
        String message;
        if(type == SubType.WEATHER)
            message = generateWeather(city);
        else if(type == SubType.WORLD_NEWS)
            message = generateNews();
        else if (type == SubType.LOCAL_NEWS)
            message = generateLocals(city);
        else
            message = generateEvents(city);

        return Reply.newBuilder().setReply(message).build();
    }

    public String generateWeather(String city){
        Random rand = new Random();
        String[] schemas = {"windy", "rainy", "storm", "sunny", "foggy"};
        String message = "In city: " + city + " Temperature: " + (rand.nextInt(20) + 11) + " Celcius degree; Sky: " + schemas[rand.nextInt(5)];
        return message;
    }

    public String generateNews(){
        Random rand = new Random();
        String[] schemas = {"fly accident", "terrorist attack", "anniversary celebration", "forest fire"};
        String[] countries = {"Poland", "USA", "UK", "Germany", "China", "Brazil", "Australia"};
        String message = "There was " + schemas[rand.nextInt(4)] + " in " + countries[rand.nextInt(7)];
        return message;

    }

    public String generateLocals(String city){
        Random rand = new Random();
        String[] schemas = {"traffic difficulties", "car accident", "student with the highest grade in country", "new restaurant opened"};
        String message = "NEWS for " + city + ": " + schemas[rand.nextInt( 4)];
        return message;
    }

    public String generateEvents(String city){
        Random rand = new Random();
        String[] schemas =  {"sport", "concert", "film", "theater play"};
        String message = "New " + schemas[rand.nextInt( 4)] + " event in city: " + city;
        return message;
    }

    public void runGenerator(){
        System.out.println("GENERATING MESSAGES");
        executor.submit(this::generate);
    }
}
