package parser;
public class NextNode extends StatementNode{
	String varName;
	public NextNode(String varName) {
		this.varName = varName;
	}
	public String toString() {
		
		return "NEXT " + varName;
	}

	@Override
	public NodeType getNodeType() {
		
		return NodeType.Next;
	}
	
}