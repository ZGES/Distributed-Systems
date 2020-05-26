import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executor implements Watcher, AsyncCallback.StatCallback {

    protected final static ExecutorService executorService = Executors.newCachedThreadPool();
    private ZooKeeper zooKeeper;
    private String zNode;
    private String program;
    private Process process;

    public Executor(ZooKeeper zooKeeper, String zNode, String program){
        this.zooKeeper = zooKeeper;
        this.zNode = zNode;
        this.program = program;

        ChildController ch = new ChildController(zooKeeper, zNode, zNode);
        zooKeeper.exists(zNode, this, this, null);
        zooKeeper.exists(zNode, this, ch, null);

    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getPath().equals(zNode)) {
            zooKeeper.exists(zNode, this, this, null);
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        switch (KeeperException.Code.get(rc)) {
            case OK:
                if (process == null) {
                    System.out.println("Starting external program...");
                    try {
                        process = Runtime.getRuntime().exec(program);
                        executorService.submit(() -> {
                            final Scanner input = new Scanner(process.getInputStream());
                            while (input.hasNextLine()) {
                                System.out.println(input.nextLine());
                            }
                        });
                        executorService.submit(() -> {
                            final Scanner input = new Scanner(process.getErrorStream());
                            while (input.hasNextLine()) {
                                System.out.println(input.nextLine());
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case NONODE:
                if (process.isAlive()) {
                    System.out.println("Stopping external program...");
                    process.destroy();
                    try {
                        process.waitFor();
                    } catch (InterruptedException ignored) {
                    }
                    process = null;
                }
                break;
            case SESSIONEXPIRED:
            case NOAUTH:
                break;
            default:
                zooKeeper.exists(zNode, this, this, null);
        }
    }
}
