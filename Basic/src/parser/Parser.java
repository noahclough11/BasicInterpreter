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
			//System.out.println(tokenManager.Peek(0).get().getTokenType());
			var t = tokenManager.Peek(0).get().getTokenType();
		    if((t == (TokenType.NUMBER)|| (t == TokenType.SUBTRACT)|| (t == TokenType.LPAREN))){
		      nodes.add(Expression());
		      System.out.println("endAdd");
					      
			while(tokenManager.Peek(0).isPresent()) {
				tokenManager.MatchAndRemove(TokenType.RPAREN);
				while(AcceptSeperators()) {
					tokenManager.MatchAndRemove(TokenType.ENDOFLINE);
				}
			}
			    
			}
		}
		System.out.println("endParse");
		return new ProgramNode(nodes);
	}
	Node Expression() {
		System.out.println("startExp");
		Node left = Term();
		MathOp op = null;
		
		if(tokenManager.MatchAndRemove(TokenType.ADD).isEmpty()) {
			if(tokenManager.MatchAndRemove(TokenType.SUBTRACT).isEmpty()) {
				return left;	
			}
			op = MathOp.SUBTRACT;
			eatNewlines();
		} else {
			eatNewlines();
			op = MathOp.ADD;
		}
		if(tokenManager.MoreTokens()) {
			var t = tokenManager.Peek(1).get().getTokenType();
			if(t == TokenType.ADD || t == TokenType.SUBTRACT) {
				System.out.println("endExp1");
				return new MathOpNode(op,left,Expression());
			}
		}
		System.out.println("endExp2");
		return new MathOpNode(op, left, Term());
		
	}
	Node Term() {
		System.out.println("startTerm");
		LinkedList<Node> numbers = new LinkedList<Node>();
		LinkedList<MathOp> ops = new LinkedList<MathOp>();
		LinkedList<Node> mathNodes = new LinkedList<Node>();
		Node left = Factor();
		if(tokenManager.MatchAndRemove(TokenType.RPAREN).isPresent()) {
			while(AcceptSeperators()) {
				tokenManager.MatchAndRemove(TokenType.ENDOFLINE);
			}
			return left;
		}
		MathOp op = null;
		if(tokenManager.MatchAndRemove(TokenType.MULTIPLY).isPresent()) {
			eatNewlines();
			op = MathOp.MULTIPLY;
		}
		if(tokenManager.MatchAndRemove(TokenType.DIVIDE).isPresent()) {
			eatNewlines();
			op = MathOp.DIVIDE;
		}
		if(op == null) {
			return left;
		}
		numbers.add(left);
		ops.add(op);
		if(tokenManager.Peek(1).isEmpty()) {
			return left;
		}
		while(tokenManager.Peek(1).get().getTokenType() == TokenType.MULTIPLY || tokenManager.Peek(1).get().getTokenType() == TokenType.DIVIDE||(tokenManager.Peek(0).get().getTokenType() == TokenType.SUBTRACT)||tokenManager.Peek(0).get().getTokenType()==TokenType.LPAREN) {
			if(tokenManager.MatchAndRemove(TokenType.RPAREN).isPresent()){
				break;
			}
			if(tokenManager.Peek(0).get().getTokenType() == TokenType.LPAREN) {
				left = Factor();
				numbers.add(left);
			} else {
			if(tokenManager.Peek(1).get().getTokenType() == TokenType.MULTIPLY || tokenManager.Peek(1).get().getTokenType() == TokenType.DIVIDE||tokenManager.Peek(0).get().getTokenType() == TokenType.SUBTRACT) {
				left = Factor();
				if(tokenManager.MatchAndRemove(TokenType.MULTIPLY).isPresent()) {
					numbers.add(left);
					ops.add(MathOp.MULTIPLY);
				}else if(tokenManager.MatchAndRemove(TokenType.DIVIDE).isPresent()) {
					numbers.add(left);
					ops.add(MathOp.DIVIDE);
				}else {			
				numbers.add(left);
				
				}
			} else {
				left = Factor();
				numbers.add(left);
			}
			}
			if (tokenManager.Peek(1).isEmpty()) {
				tokenManager.MatchAndRemove(TokenType.RPAREN);
				break;
			}
		}
		if(numbers.size() == 1) {
			return new MathOpNode(op, left, Factor());
		}
		for(MathOp m: ops) {
			System.out.println(m);
		}
		for(Node n: numbers) {
			System.out.println(n);
		}
		mathNodes.add(new MathOpNode(ops.pop(),numbers.pop(),numbers.pop()));
		while(numbers.size() > 0) {			
			mathNodes.add(new MathOpNode(ops.pop(),mathNodes.getLast(),numbers.pop()));
		}
		System.out.println("endTerm");
		if(ops.size() == 0) {
			return mathNodes.getLast();
		} 
		return new MathOpNode(ops.pop(),mathNodes.getLast(),Factor());
	

	}
	Node Factor() {
		eatNewlines();
		System.out.println("startFactor");
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
	private void eatNewlines() {
		while(AcceptSeperators()) {
			tokenManager.MatchAndRemove(TokenType.ENDOFLINE);
		}
	}
 }