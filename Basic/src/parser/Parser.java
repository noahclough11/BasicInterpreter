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
				var t = tokenManager.Peek(0).get().getTokenType();
			    if((t == (TokenType.NUMBER)|| (t == TokenType.SUBTRACT)|| (t == TokenType.LPAREN))){
			      nodes.add(Expression());
			  }
			}
		}
		
		return new ProgramNode(nodes);
	}
	Node Expression() {
		
		
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
		
	}
	Node Term() {
		
		LinkedList<Node> numbers = new LinkedList<Node>();
		LinkedList<MathOp> ops = new LinkedList<MathOp>();
		LinkedList<Node> mathNodes = new LinkedList<Node>();
		Node left = Factor();
		if(tokenManager.MatchAndRemove(TokenType.RPAREN).isPresent()) {
			System.out.println(1);
			return left;
		}
		MathOp op;
		if(tokenManager.MatchAndRemove(TokenType.MULTIPLY).isEmpty()) {
			if(tokenManager.MatchAndRemove(TokenType.DIVIDE).isEmpty()) {	
				
				return left;
			}
			op = MathOp.DIVIDE;
		}else {
			
			op = MathOp.MULTIPLY;
		}
		numbers.add(left);
		ops.add(op);
		if(tokenManager.Peek(1).isEmpty()) {
			return left;
		}
		var next = tokenManager.Peek(1).get().getTokenType();
		var current = tokenManager.Peek(0).get().getTokenType();
		while(next == TokenType.MULTIPLY || next == TokenType.DIVIDE||current == TokenType.SUBTRACT||current==TokenType.LPAREN) {
			next = tokenManager.Peek(1).get().getTokenType();
			current = tokenManager.Peek(0).get().getTokenType();
			
			if(next == TokenType.MULTIPLY || next == TokenType.DIVIDE||current == TokenType.SUBTRACT||current==TokenType.LPAREN) {
				left = Factor();
				if(tokenManager.MatchAndRemove(TokenType.MULTIPLY).isEmpty()) {
					if(tokenManager.MatchAndRemove(TokenType.DIVIDE).isEmpty()) {	
						numbers.add(left);
					}
					op = MathOp.DIVIDE;
				}else {				
					op = MathOp.MULTIPLY;
				}
				
				numbers.add(left);
				ops.add(op);
			} 
		}
		if(numbers.size() == 1) {
			return new MathOpNode(op, left, Factor());
		}
		for(MathOp m: ops) {
			System.out.println(m);
		}
		mathNodes.add(new MathOpNode(ops.pop(),numbers.pop(),numbers.pop()));
		while(numbers.size() > 0) {			
			mathNodes.add(new MathOpNode(ops.pop(),mathNodes.getLast(),numbers.pop()));
		}
		return new MathOpNode(ops.pop(),mathNodes.getLast(),Factor());
		

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
		
		if(number.contains(".")) {
			
			return new FloatNode(Double.parseDouble(number));
		} 

		return new IntegerNode(Integer.parseInt(number));
		
	}

 }