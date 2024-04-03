package parser;
public class StringNode extends Node{
	private String value;
	private NodeType type = NodeType.String;
	public StringNode(String value) {
		this.value = value;	
		}	
	@Override
	public String toString() {
		return "string: "+ "\""+value+ "\"";
	}
	public NodeType getNodeType() {
		return this.type;
	}
	public String getValue() {
		return this.value;
	}
}