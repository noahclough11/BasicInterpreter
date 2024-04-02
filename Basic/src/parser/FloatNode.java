package parser;
//Represents a floating point number
public class FloatNode extends Node{
	private NodeType type;
	private double number;
	@Override
	public String toString() {
		return ""+number;
	}
	public double getNumber() {
		return number;
	}
	public FloatNode(double num) {
		number = num;
		this.type = NodeType.Float;
	}
	public NodeType getNodeType() {
		return this.type;
	}
}	