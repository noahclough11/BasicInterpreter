package parser;
public class ForNode extends StatementNode{
	private Node increment;
	private Node variable;
	private Node limit;
	public ForNode(Node variable, Node limit) {
		this.variable = variable;
		this.limit = limit;
		this.increment = new IntegerNode(1);
	}
	public ForNode(Node variable, Node limit, Node increment) {
		this.variable = variable;
		this.limit = limit;
		this.increment = increment;
	}
	@Override
	public String toString() {
		return "FOR "+variable.toString() + " TO "+ limit.toString() + " STEP " + increment.toString();
	}
}