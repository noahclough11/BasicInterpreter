package parser;
public class WhileNode extends StatementNode {
	private Node booleanExp;
	private String label;
	private NodeType type = NodeType.While;
	public WhileNode(Node booleanExp, String label) {
		this.booleanExp = booleanExp;
		this.label = label;
	}
	@Override
	public String toString() {
		return "WHILE(" + booleanExp.toString() + ") EndLabel: "+ label;
	}
	public NodeType getNodeType() {
		return this.type;
	}
}