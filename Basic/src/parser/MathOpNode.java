package parser;
//Represents a math operation, with an operator and two operands,
//numbers or other math operations which must be computed first
public class MathOpNode extends Node{
	enum MathOp{ADD, SUBTRACT, MULTIPLY, DIVIDE};
	private Node left;
	private Node right;
	private MathOp operator;
	private NodeType type = NodeType.MathOp;
	@Override
	public String toString() {
		return "(" +left.toString() +" "+ operator +" "+ right.toString() + ")";
	}
	public  MathOpNode(MathOp op, Node leftNode,  Node rightNode) {
		operator = op;
		left = leftNode;
		right = rightNode;
	}
	public NodeType getNodeType() {
		return this.type;
	}
	
}