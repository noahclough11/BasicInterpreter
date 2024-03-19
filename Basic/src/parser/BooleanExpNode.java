package parser;

import lexer.TokenType;

public class BooleanExpNode extends Node{
	private Node left;
	private TokenType operator;
	private Node right;
	
	public BooleanExpNode(Node left, TokenType operator, Node right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	public String toString() {
		return left.toString() +" " +operator + " "+right.toString();
	}
}