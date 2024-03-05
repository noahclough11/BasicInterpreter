package parser;

import java.util.LinkedList;

public class PrintNode extends StatementNode{
	private LinkedList<Node> toPrint;
	
	public PrintNode(LinkedList<Node> toPrint) {
		this.toPrint = toPrint;
	}
	@Override
	public String toString() {
		String s = "Print: (";
		for (Node n: toPrint) {
			s += n.toString()+", ";
		}
		s += ")\n";
		return s;
	}
	
}