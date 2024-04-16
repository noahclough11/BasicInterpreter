package parser;

import java.util.LinkedList;

public class ReadNode extends StatementNode{
	private LinkedList<Node> readList;
	private NodeType type = NodeType.Read;
	public ReadNode(LinkedList<Node> list) {
		this.readList  = list;
	}
	@Override
	public String toString() {
		String s = "Read: (";
		for(Node n:readList) {
			s += n.toString()+", ";
		}
		return s+ ")\n";
	}
	public NodeType getNodeType() {
		return this.type;
	}
	public LinkedList<Node> getReadList(){
		return this.readList;
	}
}