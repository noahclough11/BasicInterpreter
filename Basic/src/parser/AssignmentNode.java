package parser;

public class AssignmentNode extends StatementNode{
	private VariableNode variable;
	private Node value;
	private NodeType type;
	
	public AssignmentNode(VariableNode variable, Node value) {
		this.variable = variable;
		this.value = value;
		this.type = NodeType.Assignment;
	}
	@Override
	public String toString() {
		return variable.toString() +" = " + value.toString();
	}
	public NodeType getNodeType() {
		return this.type;
	}
	public Node getValue() {
		return this.value;
	}
	public String getVariableName() {
		return variable.getName();
	}
}