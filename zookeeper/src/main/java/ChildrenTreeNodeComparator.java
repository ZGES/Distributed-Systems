import java.util.Comparator;

public class ChildrenTreeNodeComparator implements Comparator<ChildrenTreeNode> {

    @Override
    public int compare(ChildrenTreeNode ch1, ChildrenTreeNode ch2) {
        return (ch1.getLevel() - ch2.getLevel());
    }
}
