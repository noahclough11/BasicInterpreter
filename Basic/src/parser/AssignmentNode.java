package parser;

public class AssignmentNode extends StatementNode{
	private VariableNode variable;
	private Node value;
	public AssignmentNode(VariableNode variable, Node value) {
		this.variable = variable;
		this.value = value;
	}
	@Override
	public String toString() {
		return "variable("+variable.toString() + ") = " + value.toString();
	}
	
}