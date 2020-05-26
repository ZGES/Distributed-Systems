package grpc;

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import sr.grpc.gen.Reply;
import sr.grpc.gen.SubType;
import sr.grpc.gen.SubscribeMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Client {
    private ServerCallStreamObserver<Reply> streamObserver;
    private final HashMap<String, Set<SubType>> subscriptions;
    private final String name;

    public Client(String name){
        this.name = name;
        this.subscriptions = new HashMap<>();
        this.streamObserver = null;
    }

    public void subscribeOn(SubscribeMessage request){
        Set<SubType> subSetForCity = subscriptions.getOrDefault(request.getCityName(), new HashSet<>());;
        subSetForCity.addAll(request.getSubTypeList());
        this.subscriptions.put(request.getCityName(), subSetForCity);
    }

    public Set<SubType> getSubTypesByCity(String cityName){
        return this.subscriptions.getOrDefault(cityName, new HashSet<>());
    }

    public void setStreamObserver(ServerCallStreamObserver<Reply> streamObserver) {
        if(this.streamObserver == null)
            this.streamObserver = streamObserver;
    }
    public StreamObserver<Reply> getObserver(){
        return this.streamObserver;
    }
}
