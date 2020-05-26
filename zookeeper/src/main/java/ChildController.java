import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;

public class ChildController implements Watcher, AsyncCallback.StatCallback {

    private ZooKeeper zooKeeper;
    private String root;
    private String zNode;
    private List<String> children = new ArrayList<>();

    public ChildController(ZooKeeper zooKeeper, String root, String zNode){
        this.zooKeeper = zooKeeper;
        this.root = root;
        this.zNode = zNode;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getPath().equals(zNode)) {
            zooKeeper.exists(zNode, true, this, null);
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (KeeperException.Code.get(rc) == KeeperException.Code.OK) {
            try {
                children = updateChildren(zooKeeper, root, zNode, children, this);
            } catch (KeeperException | InterruptedException ignored) {
            }
        } else {
            zooKeeper.exists(zNode, true, this, null);
        }
    }

    private List<String> updateChildren(ZooKeeper zooKeeper, String root, String zNode, List<String> children, Watcher watcher) throws KeeperException, InterruptedException {
        List<String> newChildren = zooKeeper.getChildren(zNode, watcher);
        newChildren.removeAll(children);
        if (newChildren.size() == 1) {
            for (String child : newChildren) {
                final String newChild = zNode + "/" + child;
                zooKeeper.exists(newChild, true, new ChildController(zooKeeper, root, newChild), null);
                System.out.println("Current children: " + (getChildrenNumber(zooKeeper, root, 0)));
            }
        }
        return newChildren;
    }

    public int getChildrenNumber(ZooKeeper zooKeeper, String zNode, int counter) throws KeeperException, InterruptedException {
        List<String> children;
        if (zooKeeper.exists(zNode, true) != null) {
            children = zooKeeper.getChildren(zNode, true);
            counter += children.size();

            for (String child: children) {
                counter += getChildrenNumber(zooKeeper, zNode + "/" + child, 0);
            }
        }
        return counter;
    }
}
