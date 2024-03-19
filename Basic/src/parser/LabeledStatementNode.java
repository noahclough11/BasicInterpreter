package parser;
public class LabeledStatementNode extends StatementNode{
	private String label;
	private Node statement;
	
	public LabeledStatementNode(String label, Node statement) {
		this.label = label;
		this.statement = statement;
	}

	@Override
	public String toString() {
		return label + statement.toString();
	}
	
}