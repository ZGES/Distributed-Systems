import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;


import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZooConnect {

    private ZooKeeper zookeeper;
    private CountDownLatch connSignal = new CountDownLatch(1);

    public ZooKeeper connect(String host, int timeout) throws InterruptedException, IOException {
        this.zookeeper = new ZooKeeper(host, timeout, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if(watchedEvent.getState() == KeeperState.SyncConnected)
                    connSignal.countDown();
            }
        });
        connSignal.await();
        System.out.println("Connected to the server");
        return zookeeper;
    }

    public void close(){
        try {
            zookeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
