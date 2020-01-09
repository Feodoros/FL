package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParseTree {
    static int lastId = 0;

    private String node;
    private int id;
    private List<ParseTree> children;

    ParseTree(String node, ParseTree... children){
        this.id = lastId++;
        this.node = node;
        this.children = Arrays.asList(children);
    }

    ParseTree(String node){
        this.id = lastId++;
        this.node = node;
        this.children = new ArrayList<>();
    }

    public String getNode() {
        return node;
    }

    public List<ParseTree> getChildren() {
        return children;
    }

    private String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append('\t');
        }
        sb.append(node).append('\n');
        for (ParseTree pt : children) {
            sb.append(pt.toString(depth + 1));
        }
        return sb.toString();
    }

    private String combine(){
        StringBuilder sb = new StringBuilder();
        sb.append("node [label = \"").append(node).append("\"]; \"").append(node).append(id).append("\";\n");
        for (ParseTree pt : children){
            sb.append(pt.combine()).append('"').append(node).append(id).append("\"->\"").append(pt.node).append(pt.id).append("\"\n");
        }
        return sb.toString();
    }

    public String toGraph(){
        return "digraph tree {\n" + combine() + "}";
    }

    @Override
    public String toString() {
        return toString(0);
    }
}
