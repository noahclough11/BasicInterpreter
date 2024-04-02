package parser;
public class EndNode extends StatementNode{
	private NodeType type;
	
	@Override
	public String toString() {
		return "END";
	}
	public NodeType getNodeType() {
		return this.type;
	}
	
}