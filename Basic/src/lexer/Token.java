package lexer;

public class Token{
	

	TokenType token;
	private int lineNumber;
	private int position;
	private String value;

	public Token(TokenType tokenType, int lineNumber, int position) {
		this.token = tokenType;
		this.lineNumber = lineNumber;
		this.position = position;
	}
	public Token(TokenType tokenType, int lineNumber, int position, String value) {
		this.token = tokenType;
		this.lineNumber = lineNumber;
		this.position = position;
		this.value = value;
	}
	//Prints the value and type of the token if it is a word, number, string, or label
	//otherwise prints only the type
	public String toString() {
		if ((this.token == TokenType.WORD) || (this.token == TokenType.NUMBER) || (this.token == TokenType.STRINGLITERAL)||(this.token == TokenType.LABEL)) {
		  return this.token + "("+this.value+") ";
		} 
		return this.token + " ";
		
	}
	public TokenType getTokenType() {
		return token;
	}
	

}
