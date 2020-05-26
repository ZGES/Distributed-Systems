from datetime import datetime
from threading import Thread

import time
import grpc

from gen.sr.grpc.gen import subscription_pb2, subscription_pb2_grpc, isalive_pb2, isalive_pb2_grpc

PORT = "7777"
HOST = "localhost"
CONNECTED = False
SUBSCRIBED = False
connection_stub = None


class Client:
    def __init__(self, name):
        self.name = name

    def subscribe(self, stub, cityName):
        global SUBSCRIBED

        print("Choose event type:")
        for x in choice:
            print(x)
        selected_types = handle_sub_types_selection()

        pack = subscription_pb2.SubscribeMessage(clientName=self.name, cityName=cityName, subType=selected_types)
        res = stub.Subscribe(pack, wait_for_ready=True)

        if res.acceptation == "Connected":
            print("Connected to server")

        if not SUBSCRIBED:
            SUBSCRIBED = True
            print("Subscribed to server")
            self._streamSub(stub)

    def _streamSub(self, stub):
        thread = Thread(target=callback, args=(stub, self.name))
        print("Start listening thread...")
        thread.start()

    def run(self, host=HOST, port=PORT):
        global connection_stub
        global CONNECTED
        association = HOST + ":" + PORT
        with grpc.insecure_channel(association) as channel:
            connection_stub = isalive_pb2_grpc.IsAliveServiceStub(channel)
            stub = subscription_pb2_grpc.SubscribeServiceStub(channel)
            print("Write city you want to subscribe on:")
            try:
                while True:
                    user_input = input("> ")
                    if user_input == 'kill':
                        print("Finishing...")
                        CONNECTED = True
                        break
                    self.subscribe(stub, user_input)
            except KeyboardInterrupt:
                CONNECTED = True
                print("Got SIGINT... Exiting")


def parse_sub_type(sub_type):
    cases = {
        0: 'WEATHER',
        1: 'WORLD_NEWS',
        2: 'LOCAL_NEWS',
        3: 'EVENTS'
    }
    return cases[sub_type]


sub_types = [subscription_pb2.SubType.Value('WEATHER'),
             subscription_pb2.SubType.Value('WORLD_NEWS'),
             subscription_pb2.SubType.Value('LOCAL_NEWS'),
             subscription_pb2.SubType.Value('EVENTS')]
choice = [str(i + 1) + ": " + parse_sub_type(sub_type) for i, sub_type in enumerate(sub_types)]


def write_name():
    client_input = input("Client name: ")
    return client_input


def handle_sub_types_selection():
    option = input("> ")
    if ',' in option:
        choices = option.split(',')
        tmp = set([sub_types[int(x) - 1] for x in choices])
        return list(tmp)
    elif '-' in option:
        choices = option.split('-')
        start = int(choices[0]) - 1
        end = int(choices[1])
        return sub_types[start:end]
    else:
        return [sub_types[int(option) - 1]]


def notification_printer(notification):
    now = str(datetime.now())
    print("Got new message")

    print("[")
    srv_msg = notification.reply
    print("     Message:", srv_msg)
    print("     Date:", now)
    print("]")


def server_fault_handler():
    global SUBSCRIBED
    if not connection_stub:
        raise ValueError("Server error")

    SUBSCRIBED = False

    retries = 2
    while True:
        try:
            response = connection_stub.checkConnection(isalive_pb2.Check())
            print("Server is alive")
            print("Message:", response.message)
            break
        except:
            if CONNECTED:
                print("Connection thread exited")
                exit(0)
            print("Pulling exponentially. Server is down")
            time.sleep((2 ** retries) / 100)
            retries += 1


def callback(stub, name):
    try:
        print("Listening for events...")
        res = stub.StreamSub(subscription_pb2.Request(clientName=name), wait_for_ready=True)

        for reply in res:
            notification_printer(reply)
    except:
        if CONNECTED:
            print(" Callback thread exited")
            exit(0)
        print("Server closed")
        server_fault_handler()


if __name__ == "__main__":
    client_name = write_name()
    client = Client(client_name)
    client.run()
