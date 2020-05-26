package grpc;

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import sr.grpc.gen.*;

public class SubscriptionImpl extends SubscribeServiceGrpc.SubscribeServiceImplBase {

    private final SubscribeManager manager;

    public SubscriptionImpl(){
        this.manager = new SubscribeManager();
    }

    @Override
    public void subscribe(SubscribeMessage request, StreamObserver<Accept> responseObserver) {
        final Accept accept = Accept.newBuilder().setAcceptation("Connected").build();
        manager.addClient(request);
        responseObserver.onNext(accept);
        responseObserver.onCompleted();
    }

    @Override
    public void streamSub(Request request, StreamObserver<Reply> responseObserver) {
        String clientName = request.getClientName();
        ServerCallStreamObserver<Reply> newObserver = (ServerCallStreamObserver<Reply>) responseObserver;
        if(!manager.startListen(clientName,newObserver)){
            Reply reply = Reply.newBuilder().setReply("Client not subscribed").build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
