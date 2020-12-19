package intepreter.helpers;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import intepreter.STreeNode;
import intepreter.enums.TreeNodeType;

import java.io.IOException;
import java.util.LinkedList;

public class NodeDeserializer extends StdDeserializer<STreeNode> {
    protected NodeDeserializer(Class<?> vc) {
        super(vc);
    }

    public NodeDeserializer() {
        this(null);
    }


    @Override
    public STreeNode deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        final ObjectCodec objectCodec = jsonParser.getCodec();
        int position = (Integer) ((IntNode) node.get("position")).numberValue();
        TreeNodeType type = TreeNodeType.findByType(node.get("type").asText());
        JsonNode children = node.get("children");
        LinkedList result = new LinkedList<>();
        for (JsonNode child : children) {
                result.add( child.isObject() ? objectCodec.treeToValue(child, STreeNode.class) : child.asText());
        }
        return new STreeNode(position, type, result);
    }
}