package parser;

import lexer.TokenType;

public class BooleanExpNode extends Node{
	private Node left;
	private TokenType operator;
	private Node right;
	private NodeType type;
	
	public BooleanExpNode(Node left, TokenType operator, Node right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
		this.type = NodeType.BooleanExp;
	}
	public String toString() {
		return left.toString() +" " +operator + " "+right.toString();
	}
	public NodeType getNodeType() {
		return this.type;
	}
	public Node getLeft() {
		return this.left;
	}
	public Node getRight() {
		return this.right;
	}
	public TokenType getOp() {
		return this.operator;
	}
}
