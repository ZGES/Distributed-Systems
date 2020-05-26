import java.util.List;

public class ChildrenTreeNode {
    
    private List<String> children;
    private String parent;
    private int level;
    
    public ChildrenTreeNode(List<String> children, String parent, int level){
        this.children = children;
        this.parent = parent;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public String getParent() {
        return parent;
    }

    public List<String> getChildren() {
        return children;
    }
}
