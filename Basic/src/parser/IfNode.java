package parser;
public class IfNode extends StatementNode {
	private Node booleanExp;
	private String label;
	public IfNode(Node booleanExp, String label) {
		this.booleanExp = booleanExp;
		this.label = label;
	}
	@Override
	public String toString() {
		return "IF(" + booleanExp.toString() + " THEN "+label+")";
	}
}
