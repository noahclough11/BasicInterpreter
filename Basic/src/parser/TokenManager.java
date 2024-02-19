package parser;
import java.util.LinkedList;
import java.util.Optional;
import lexer.TokenType;
import lexer.Token;

public class TokenManager{
	private LinkedList<Token> tokenList;
	
	public TokenManager(LinkedList<Token> tokens) {
		this.tokenList = tokens;
	}
	Optional<Token> Peek(int j){
		if (j < tokenList.size()) {
			return Optional.of(tokenList.get(j));
		}
		return Optional.empty();
	}
	boolean MoreTokens() {
		return (tokenList.size() > 0);
	}
	Optional<Token> MatchAndRemove(TokenType t){
		if(tokenList.peek().getTokenType() == t) {
			return Optional.of(tokenList.removeFirst());
		}
		return Optional.empty();
	}
}