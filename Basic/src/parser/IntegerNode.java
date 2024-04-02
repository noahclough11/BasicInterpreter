package parser;

//Represents an integer
public class IntegerNode extends Node {
	private NodeType type = NodeType.Integer;
	private int number;
	@Override
	public String toString() {
		return ""+number;
	}
	public int getNumber() {
		return number;
	}
	public IntegerNode(int num) {
		number = num;
	}
	public NodeType getNodeType() {
		return this.type;
	}
}