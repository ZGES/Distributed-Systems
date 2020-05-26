import org.apache.zookeeper.ZooKeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class Main {

    public static void main(String[] args) {

        //parse arguments
        if(args.length < 2) {
            System.err.println("You nee to pass 2 arguments - host port and program to execute");
            System.exit(1);
        }
        String host = args[0];
        String program = args[1];

        //initialize connection class
        ZooConnect zooConnect = new ZooConnect();

        //connect to zookeeper server
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = zooConnect.connect(host, 5000);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        //initialize support classes
        String zNode = "/z";
        TreePrinter tPrinter = new TreePrinter(zooKeeper);
        if (zooKeeper != null) {
            Executor executor = new Executor(zooKeeper, zNode, program);
        }

        //read commands
        System.out.println("q - exit program;  print tree - print tree of '/z'");
        System.out.println("Insert a command:");
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        boolean run = true;
        while(run){
            try {
                String line = stdIn.readLine();

                switch (line){
                    case "q":
                        run = false;
                        break;
                    case "print tree":
                        tPrinter.printZNodeTree(zNode);
                        break;
                    case "":
                        break;
                    default:
                        System.out.println("Invalid command");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Exiting program...");
        if (zooKeeper != null) {
            zooConnect.close();
        }
    }
}
