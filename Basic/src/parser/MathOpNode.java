package parser;

public class MathOpNode extends Node{
	enum MathOp{ADD, SUBTRACT, MULTIPLY, DIVIDE};
	private Node left;
	private Node right;
	private MathOp operator;
	@Override
	public String toString() {
		return "(" +left.toString() +" "+ operator +" "+ right.toString() + ")";
	}
	public  MathOpNode(MathOp op, Node leftNode,  Node rightNode) {
		operator = op;
		left = leftNode;
		right = rightNode;
	}
	
}