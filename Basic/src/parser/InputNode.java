package parser;

import java.util.LinkedList;

public class InputNode extends StatementNode{
	private LinkedList<Node> inputList;
	public InputNode(LinkedList<Node> inputList) {
		this.inputList = inputList;
	}
	@Override
	public String toString() {
		String s = "Input: (";
		for(Node n: inputList) {
			s += n.toString()+ ", ";
		}
		return s+ ")\n";
	}
}