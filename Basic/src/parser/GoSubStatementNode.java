package parser;
public class GoSubStatementNode extends StatementNode{
	private String label;
	public GoSubStatementNode(String label) {
		this.label = label;
	}
	public String toString(){
		return "GOSUB(" + label + ")";
	}
}