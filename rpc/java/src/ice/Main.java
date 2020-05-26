package ice;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String... args) {
        Server server = new Server();
        server.run(Stream.of(
                "--Ice.Config=config.server"
        ).collect(Collectors.joining()));
    }
}