package parser;
public class GoSubStatementNode extends StatementNode{
	private NodeType type = NodeType.Gosub;
	private String label;
	public GoSubStatementNode(String label) {
		this.label = label;
	}
	public String toString(){
		return "GOSUB(" + label + ")";
	}
	public NodeType getNodeType() {
		return this.type;
	}
}
