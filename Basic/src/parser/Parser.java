package parser;
import java.util.LinkedList;
import java.util.Optional;

import lexer.TokenType;
import parser.MathOpNode.MathOp;
import lexer.Token;

public class Parser{
	TokenManager tokenManager;
	public Parser(LinkedList<Token> tokens) {
		this.tokenManager = new TokenManager(tokens);
	}
	boolean AcceptSeperators() {
		if(tokenManager.Peek(0).isEmpty()) {
			return false;
		}
		if (tokenManager.Peek(0).get().getTokenType() == TokenType.ENDOFLINE) {
			return true;
		}
		return false;
	}
	public ProgramNode parse() {
		LinkedList<Node> nodes = new LinkedList<Node>();
		while (tokenManager.MoreTokens()) {
			while(AcceptSeperators()) {
				tokenManager.MatchAndRemove(TokenType.ENDOFLINE);
			}
			if(tokenManager.MoreTokens()) {
			  if((tokenManager.Peek(0).get().getTokenType() == (TokenType.NUMBER))
					|| (tokenManager.Peek(0).get().getTokenType() == TokenType.SUBTRACT)
					|| (tokenManager.Peek(0).get().getTokenType() == TokenType.LPAREN)){
			  nodes.add(Expression());
			  }
			}
		}
		return new ProgramNode(nodes);
	}
	Node Expression() {
		//if(tokenManager.MoreTokens()) {
		tokenManager.MatchAndRemove(TokenType.LPAREN);
		tokenManager.MatchAndRemove(TokenType.RPAREN);
		
		Node left = Term();
		MathOp op = null;
		
		if(tokenManager.MatchAndRemove(TokenType.ADD).isEmpty()) {
			if(tokenManager.MatchAndRemove(TokenType.SUBTRACT).isEmpty()) {
				return left;	
			}
			op = MathOp.SUBTRACT;
			
		} else {
			op = MathOp.ADD;
		}
		if(tokenManager.MoreTokens()) {
			var t = tokenManager.Peek(1).get().getTokenType();
			if(t == TokenType.ADD || t == TokenType.SUBTRACT) {
				return new MathOpNode(op,left,Expression());
			}
		}
		return new MathOpNode(op, left, Term());
		//}
		//return null;
	}
	Node Term() {
		Node left = Factor();
		MathOp op = null;
		if(tokenManager.MatchAndRemove(TokenType.MULTIPLY).isEmpty()) {
			if(tokenManager.MatchAndRemove(TokenType.DIVIDE).isEmpty()) {
				return left;
			}
			op = MathOp.DIVIDE;
		}else {
			op = MathOp.MULTIPLY;
		}
		if(tokenManager.MoreTokens()) {
			var t = tokenManager.Peek(1).get().getTokenType();
			if(t == TokenType.MULTIPLY || t == TokenType.DIVIDE) {
				return new MathOpNode(op,left,Term());
			}
		}
		
		return new MathOpNode(op, left, Factor());

	}
	Node Factor() {
		Optional<Token> t = tokenManager.MatchAndRemove(TokenType.NUMBER);
		if(t.isEmpty()) {
			t = tokenManager.MatchAndRemove(TokenType.LPAREN);
			if(!t.isEmpty()) {
				return Expression();
			}
			t = tokenManager.MatchAndRemove(TokenType.SUBTRACT);
			if(!t.isEmpty()) {
				t = tokenManager.MatchAndRemove(TokenType.NUMBER);
				String number = t.get().getValue();
				
				if(number.contains(".")) {
					return new FloatNode(Double.parseDouble("-"+number));
				} 
				return new IntegerNode(Integer.parseInt("-"+number));
			}
			
		}
		String number = t.get().getValue();
		//System.out.println(number);
		if(number.contains(".")) {
			return new FloatNode(Double.parseDouble(number));
		} 
		return new IntegerNode(Integer.parseInt(number));
		
	}

 }