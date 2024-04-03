package parser;

import java.util.LinkedList;

public class StatementsNode extends Node{
	private NodeType type;
	public LinkedList<StatementNode> statements;
	public StatementsNode(LinkedList<StatementNode> statements) {
		this.statements = statements;
	}
	@Override
	public String toString() {
		String s = "";
		for (StatementNode n: statements) {
			s += n.toString() + "\n";
		}
		return s;
	}
	public NodeType getNodeType() {
		return this.type;
	}
	public LinkedList<StatementNode> getList(){
		return statements;
	}
}