### 2 exercises using middleware technologies - gRPC and ICE

1. To run gRPC exercise you need to:
  a) in java directory:
    - create directory: gen/sr/grpc/gen
    - run: 'protoc.exe -I=. --java_out=gen --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java.exe --grpc-java_out=gen protos/isalive.proto' 
      and 'protoc.exe -I=. --java_out=gen --plugin=protoc-gen-grpc-java=protoc-gen-grpc-java.exe --grpc-java_out=gen protos/subscription.proto'
  b) in python directory:
    - create python packages: gen/sr/grpc/gen
    - run: 'python -m grpc_tools.protoc -I. --python_out=gen --grpc_python_out=gen protos/isalive.proto'

2. To run ICE exericse you need to:
  a) in java directory:
    - create directory: gen/sr/ice/gen
    - run: 'slice2java  --output-dir gen/sr/ice/gen slice/camera.ice' and 'slice2java  --output-dir gen/sr/ice/gen slice/fridge.ice' 
  b) in python directory:
    - create python packages: gen/sr/ice/gen
    - run: 'slice2py  --output-dir gen/sr/ice/gen slice/camera.ice' and 'slice2py  --output-dir gen/sr/ice/gen slice/fridge.ice'
