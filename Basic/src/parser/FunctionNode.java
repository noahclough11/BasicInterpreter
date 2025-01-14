package parser;

import java.util.LinkedList;
import lexer.TokenType;

public class FunctionNode extends StatementNode{
	private NodeType type = NodeType.Function;
	private TokenType functionName;
	public LinkedList<Node> args;
	public FunctionNode(TokenType functionName, LinkedList<Node> args) {
		this.functionName = functionName;
		this.args = args;
	}
	@Override
	public String toString() {
		String s = "void";
		if(args != null) {
			s = " ";
		  for(Node n: args) {
			  s += "("+n.toString() + ") ";
		  }
		}
		return "function " + functionName + "(" + s + ")";
	}
	public NodeType getNodeType() {
		return this.type;
	}
	public TokenType getFuncType() {
		return this.functionName;
	}
}