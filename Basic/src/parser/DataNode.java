package parser;

import java.util.LinkedList;

public class DataNode extends StatementNode{
	public LinkedList<Node> dataList;
	private NodeType type;
	public DataNode(LinkedList<Node> dataList) {
		this.dataList = dataList;
		this.type = NodeType.Data;
	}
	@Override
	public String toString() {
		String s = "Data: (";
		for (Node n: dataList) {
			s += n.toString()+ ", ";
		}
		return s + ")\n";
	}
	public NodeType getNodeType() {
		return this.type;
	}
}