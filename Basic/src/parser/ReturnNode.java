package parser;
public class ReturnNode extends StatementNode{
	private NodeType type = NodeType.Return;
	@Override
	public String toString() {
		return "RETURN";
	}
	public NodeType getNodeType() {
		return this.type;
	}
}