package parser;

public class VariableNode extends Node{
	private String name;
	private NodeType type = NodeType.Variable;
	public VariableNode(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "variable: "+name;
	}
	public NodeType getNodeType() {
		return this.type;
	}
}