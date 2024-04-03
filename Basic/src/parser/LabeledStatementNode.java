package parser;
public class LabeledStatementNode extends StatementNode{
	private String label;
	private Node statement;
	private NodeType type = NodeType.LabeledStatement;
	
	public LabeledStatementNode(String label, Node statement) {
		this.label = label;
		this.statement = statement;
	}

	@Override
	public String toString() {
		return label + statement.toString();
	}
	public NodeType getNodeType() {
		return this.type;
	}
	public String getLabel() {
		return this.label;
	}
}