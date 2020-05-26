package grpc;

import io.grpc.stub.StreamObserver;
import sr.grpc.gen.Check;
import sr.grpc.gen.IsAliveServiceGrpc;
import sr.grpc.gen.Response;

public class IsAliveImpl extends IsAliveServiceGrpc.IsAliveServiceImplBase {

    @Override
    public void isAlive(Check request, StreamObserver<Response> responseObserver) {

        Response response = Response.newBuilder().setAlive(true).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
