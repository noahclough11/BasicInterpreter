package parser;
import java.util.LinkedList;
import lexer.TokenType;
import lexer.Token;

public class Parser{
	TokenManager tokenManager;
	public Parser(LinkedList<Token> tokens) {
		this.tokenManager = new TokenManager(tokens);
	}
	boolean AcceptSeperators() {
		if (tokenManager.Peek(0).get().getTokenType() == TokenType.ENDOFLINE) {
			return true;
		}
		return false;
	}
	public ProgramNode parse() {
		while (tokenManager.MoreTokens()) {
			
		}
	}
	Node Expression() {
		
	}
	Node Term() {
		
	}
	Node Factor() {
		if (tokenManager.Peek(1).get().getTokenType() == TokenType.ENDOFLINE) {
			
		}
	}

 }