package intepreter;

import intepreter.enums.TreeNodeType;

import java.util.Collection;
import java.util.LinkedList;


public class STreeNode<T> {
    private int position;
    private TreeNodeType type;
    private LinkedList<T> children = new LinkedList<>();

    public STreeNode() {
    }

    public STreeNode(int position, TreeNodeType type) {
        this.position = position;
        this.type = type;
    }

    public STreeNode(TreeNodeType type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public TreeNodeType getType() {
        return type;
    }

    public void setType(TreeNodeType type) {
        this.type = type;
    }

    public LinkedList<T> getChildren() {
        return children;
    }

    public void addChild(T child) {
        this.children.add(child);
    }

    public void addAll(Collection<T> children){
        this.children.addAll(children);
    }


    @Override
    public String toString(){
        return "\nSTreeNode{" +
                "position=" + position +
                ", type=" + type +
                ",children=" + children +
                '}';
    }
}
