package intepreter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import intepreter.enums.TreeNodeType;
import intepreter.helpers.NodeDeserializer;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


@JsonDeserialize(using = NodeDeserializer.class)
public class STreeNode<T> {

    private int position;
    private TreeNodeType type;
    private LinkedList<T> children = new LinkedList<>();

    public STreeNode() {
    }

    @JsonCreator
    public STreeNode(@JsonProperty("position")int position, @JsonProperty("type")TreeNodeType type, @JsonProperty("children")LinkedList<T> children) {
        this.position = position;
        this.type = type;
        this.children = children;
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
