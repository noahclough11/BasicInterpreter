package parser;

import java.util.LinkedList;

public class DataNode extends StatementNode{
	private LinkedList<Node> dataList;
	public DataNode(LinkedList<Node> dataList) {
		this.dataList = dataList;
	}
	@Override
	public String toString() {
		String s = "Data: (";
		for (Node n: dataList) {
			s += n.toString()+ ", ";
		}
		return s + ")\n";
	}
}