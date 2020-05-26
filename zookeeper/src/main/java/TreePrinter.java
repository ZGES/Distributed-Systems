import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;

public class TreePrinter {

    private ZooKeeper zooKeeper;

    public TreePrinter(ZooKeeper zooKeeper){
        this.zooKeeper = zooKeeper;
    }

    private ArrayList<ChildrenTreeNode> getZNodeTree(String path, int level) throws KeeperException, InterruptedException {
        List<String> children;
        ArrayList<ChildrenTreeNode> tree = new ArrayList<>();
        if(zooKeeper.exists(path, true) != null){
            children = zooKeeper.getChildren(path, true);

            if(children.size() > 0) {
                ChildrenTreeNode childrenTreeNode = new ChildrenTreeNode(children, path, level);
                tree.add(childrenTreeNode);
                level += 1;
                for (String child : children) {
                    String newPath = path + "/" + child;
                    if(path.equals("/"))
                        newPath = "/" + child;
                    ArrayList<ChildrenTreeNode> newChildren = getZNodeTree(newPath, level);
                    if(newChildren != null)
                        tree.addAll(newChildren);
                }
                return tree;
            }
        }
        return null;
    }

    public void printZNodeTree(String path){
        try {
            int level = 1;
            ArrayList<ChildrenTreeNode> tree = getZNodeTree(path, level);
            if (tree != null) {
                tree.sort(new ChildrenTreeNodeComparator());

                System.out.print("ROOT: " + path);
                level = 0;
                for (ChildrenTreeNode childrenTreeNode : tree) {
                    if (childrenTreeNode.getLevel() != level) {
                        level = childrenTreeNode.getLevel();
                        System.out.print("\nLEVEL" + level + ": ");
                    }
                    System.out.print("PARENT->" + childrenTreeNode.getParent() + ": [");
                    for (String child : childrenTreeNode.getChildren()) {
                        System.out.print(child + ", ");
                    }
                    System.out.print("]; ");

                }
                System.out.print("\n");
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
