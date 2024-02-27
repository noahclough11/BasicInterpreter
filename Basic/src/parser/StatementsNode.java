package parser;

import java.util.LinkedList;

public class StatementsNode extends Node{
	private LinkedList<StatementNode> statements;
	public StatementsNode(LinkedList<StatementNode> statements) {
		this.statements = statements;
	}
	@Override
	public String toString() {
		String s = "";
		for (StatementNode n: statements) {
			s += n.toString();
		}
		return s;
	}
	
}