package parser;
import java.util.LinkedList;
import java.util.Optional;

import lexer.TokenType;
import parser.MathOpNode.MathOp;
import lexer.Token;
//Parses the list of tokens generated by the lexer into a list of instructions for a Basic program
public class Parser{
	TokenManager tokenManager;
	public Parser(LinkedList<Token> tokens) {
		this.tokenManager = new TokenManager(tokens);
	}
	//Checks for a newline token
	boolean AcceptSeperators() {
		if(tokenManager.Peek(0).isEmpty()) {
			return false;
		}
		if (tokenManager.Peek(0).get().getTokenType() == TokenType.ENDOFLINE) {
			return true;
		}
		return false;
	}
	//Removes newline tokens until a non-newline token is reached
	private void eatNewlines() {
		while(AcceptSeperators()) {
			tokenManager.MatchAndRemove(TokenType.ENDOFLINE);
		}
	}
	public ProgramNode parse() {
		//The list of nodes that will be passed to the ProgramNode
		LinkedList<Node> nodes = new LinkedList<Node>();
		
		//Loops through the list of tokens
		while (tokenManager.MoreTokens()) {			
			nodes.add(Statements());
		}
		return new ProgramNode(nodes);
	}
	//Collects statements into a linkedList and then passes the statement list up to the parser
	StatementsNode Statements() {
		 var statements =  new LinkedList<StatementNode>();
		 StatementNode state = null;
		 do {
			   state = Statement();
			   if (state != null) {
			  	 statements.add(state);
			   }
			   eatNewlines();
			   if(!tokenManager.MoreTokens()) {
				   state = null;
			   }
			   
		 } while(state != null);
		
		return new StatementsNode(statements);
	}
	//collects the different types of statements and returns the corresponding Node
	StatementNode Statement() {
		if(tokenManager.MoreTokens()) {
		  var t = tokenManager.MatchAndRemove(TokenType.LABEL);
		  if(t.isPresent()) {
			  return new LabeledStatementNode(t.get().getValue(), Statement());
		  }
		  t = tokenManager.MatchAndRemove(TokenType.FOR);
		  if(t.isPresent()) {
			  return ForStatement();
		  }
		  t = tokenManager.MatchAndRemove(TokenType.WHILE);
		  if(t.isPresent()) {
			  return WhileStatement();
		  }
		  t = tokenManager.MatchAndRemove(TokenType.END);
		  if(t.isPresent()) {
			  return new EndNode();
		  }
		  t = tokenManager.MatchAndRemove(TokenType.IF);
		  if(t.isPresent()) {
			  return IfStatement();
		  }
		  t = tokenManager.MatchAndRemove(TokenType.GOSUB);
		  if(t.isPresent()) {
			  return new GoSubStatementNode(tokenManager.MatchAndRemove(TokenType.LABEL).get().getValue());
		  }
		  t = tokenManager.MatchAndRemove(TokenType.RETURN);
		  if(t.isPresent()) {
			  return new ReturnNode();
		  }
		  t = tokenManager.MatchAndRemove(TokenType.PRINT);
		  if (t.isPresent()) {
		  	return PrintStatement();
		  }
		  t = tokenManager.Peek(0);
		  if (t.get().getTokenType() == TokenType.WORD) {
			  return Assignment();
		  }
		  t = tokenManager.MatchAndRemove(TokenType.READ);
		  if (t.isPresent()) {
			  return ReadStatement();
		  }
		  t = tokenManager.MatchAndRemove(TokenType.DATA);
		  if (t.isPresent()) {
			  return DataStatement();
		  }
		  t = tokenManager.MatchAndRemove(TokenType.INPUT);
		  if (t.isPresent()) {
			  return InputStatement();
		  }
		  t = tokenManager.MatchAndRemove(TokenType.NEXT);
		  if (t.isPresent()) {
			  return new NextNode(tokenManager.MatchAndRemove(TokenType.WORD).get().getValue());
		  }
		  return functionInvocation();
		}
		return null;
	}
	//Creates a boolean expression using two expressions and a boolean operator
	BooleanExpNode BooleanExp() {
		Node left = Expression();
		var op = tokenManager.Peek(0).get().getTokenType();
		tokenManager.MatchAndRemove(op);
		Node right = Expression();
		return new BooleanExpNode(left,op,right);
	}
	//Collects a boolean expression and the label for and IfNode
	IfNode IfStatement() {
		var booleanExp = BooleanExp();
		tokenManager.MatchAndRemove(TokenType.THEN);
		return new IfNode(booleanExp, tokenManager.MatchAndRemove(TokenType.WORD).get().getValue());
	}
	//Collects a boolean expression and end label for a While loop
	WhileNode WhileStatement() {
		var booleanExp = BooleanExp();
		return new WhileNode(booleanExp, tokenManager.MatchAndRemove(TokenType.WORD).get().getValue());
	}
	//Collects the counter variable and the limit for a For loop. Also collects the increment if given
	ForNode ForStatement() {
		Node increment;
		Node variable = Assignment();
		if (variable == null) {
			variable = new VariableNode(tokenManager.MatchAndRemove(TokenType.WORD).get().getValue());
		}
		tokenManager.MatchAndRemove(TokenType.TO);
		Node limit = Expression();
		var t = tokenManager.MatchAndRemove(TokenType.STEP);
		if(t.isPresent()) {
			increment = Expression();
		} else {
			return new ForNode(variable, limit);
		}
		return new ForNode(variable, limit, increment);
	}
	//Creates a ReadNode with the input token and the list of parameters
	ReadNode ReadStatement() {
		tokenManager.MatchAndRemove(TokenType.LPAREN);
		var t = tokenManager.MatchAndRemove(TokenType.RPAREN);
		if (t.isPresent()) {
			return null;
		}
		return new ReadNode(ReadList());
	}
	//Creates a DataNode with the input token and the list of parameters
	DataNode DataStatement() {
		tokenManager.MatchAndRemove(TokenType.LPAREN);
		var t = tokenManager.MatchAndRemove(TokenType.RPAREN);
		if (t.isPresent()) {
			return null;
		}
		return new DataNode(DataList());
	}
	//Creates an InputNode with the input token and the list of parameters
	InputNode InputStatement() {
		tokenManager.MatchAndRemove(TokenType.LPAREN);
		var t = tokenManager.MatchAndRemove(TokenType.RPAREN);
		if (t.isPresent()) {
			return null;
		}
		return new InputNode(InputList());
	}
	//Creates a printNode with the Print token and the list of tokens to be printed
	PrintNode PrintStatement() {
		tokenManager.MatchAndRemove(TokenType.LPAREN);
		var t = tokenManager.MatchAndRemove(TokenType.RPAREN);
		if (t.isPresent()) {
			return null;
		}
		return new PrintNode(PrintList());
	}
	//Checks if there are more items to be added to the comma separated list
	private boolean runCommaListAgain() {
		if(tokenManager.MoreTokens()) {
        	if(tokenManager.MatchAndRemove(TokenType.COMMA).isPresent()) {
        		return true;
        	} else {
        		return false;
        	}
        } else {
        	return false;
        }
	}
	//PrintList. ReadList, DataList, and InputList collect a comma separated list of nodes to pass to their respective Nodes
	
	LinkedList<Node> PrintList(){
		var list = new LinkedList<Node>();
		boolean runAgain = false;
		do {
		  var t = tokenManager.Peek(0).get().getTokenType();
		  if((t == (TokenType.NUMBER)|| (t == TokenType.SUBTRACT)|| (t == TokenType.LPAREN)|| (t == TokenType.WORD))){     
	          list.add(Expression());
	          runAgain = runCommaListAgain();
		  } else if(t == TokenType.STRINGLITERAL){
			  list.add(new StringNode(tokenManager.MatchAndRemove(TokenType.STRINGLITERAL).get().getValue()));
		      runAgain = runCommaListAgain();
		  }
		  else {
			  return null;
		  }
		  } while (runAgain);
		if(tokenManager.MoreTokens()){
	      tokenManager.MatchAndRemove(TokenType.RPAREN);
		}
		
		 return list;
	}
	LinkedList<Node> ReadList(){
		var list = new LinkedList<Node>();
		boolean runAgain = false;
		do {
		  var t = tokenManager.Peek(0).get().getTokenType();
		  if(t == TokenType.WORD){     
	          list.add(new VariableNode(tokenManager.MatchAndRemove(TokenType.WORD).get().getValue()));
	          runAgain = runCommaListAgain();
		  } else {
			  return null;
		  }
		  } while (runAgain);
		if(tokenManager.MoreTokens()){
	      tokenManager.MatchAndRemove(TokenType.RPAREN);
		}
		
		 return list;
	}
	LinkedList<Node> DataList(){
		var list = new LinkedList<Node>();
		boolean runAgain = false;
		do {
		  var t = tokenManager.Peek(0).get().getTokenType();
		  if(t == TokenType.STRINGLITERAL){     
	          list.add(new StringNode(tokenManager.MatchAndRemove(TokenType.STRINGLITERAL).get().getValue()));
	          runAgain = runCommaListAgain();
		  } else if(t == TokenType.NUMBER){
			  String num = tokenManager.MatchAndRemove(TokenType.NUMBER).get().getValue();
			  if (num.contains(".")) {
				   list.add(new FloatNode(Double.parseDouble(num)));
			  } else {
				  list.add(new IntegerNode(Integer.parseInt(num)));
			  }
		      runAgain = runCommaListAgain();
		  }
		  else {
			  return null;
		  }
		  } while (runAgain);
		if(tokenManager.MoreTokens()){
	      tokenManager.MatchAndRemove(TokenType.RPAREN);
		}
		
		 return list;
	}
	LinkedList<Node> InputList(){
		var list = new LinkedList<Node>();
		boolean runAgain = true;
		if(tokenManager.Peek(0).get().getTokenType() == TokenType.STRINGLITERAL) {
			list.add(new StringNode(tokenManager.MatchAndRemove(TokenType.STRINGLITERAL).get().getValue()));
			runAgain = runCommaListAgain();
		}
		while (runAgain){
			  var t = tokenManager.Peek(0).get().getTokenType();
			  if(t == TokenType.WORD){     
		          list.add(new VariableNode(tokenManager.MatchAndRemove(TokenType.WORD).get().getValue()));
		          runAgain = runCommaListAgain();
			  } else {
				  return null;
			  }
		} 
		if(tokenManager.MoreTokens()){
	      tokenManager.MatchAndRemove(TokenType.RPAREN);
		}
		
		 return list;
	}
	//Takes a word as a variable and assigns an expression to it
	AssignmentNode Assignment() {
		String word;
		if(tokenManager.Peek(0).isPresent() 
				&& tokenManager.Peek(1).isPresent()
				&& tokenManager.Peek(2).isPresent()) 
		{
			if ((tokenManager.Peek(0).get().getTokenType() == TokenType.WORD)&&(tokenManager.Peek(1).get().getTokenType() == TokenType.EQUALS)) {
				word = tokenManager.MatchAndRemove(TokenType.WORD).get().getValue();
				tokenManager.MatchAndRemove(TokenType.EQUALS);
				return new AssignmentNode(new VariableNode(word), Expression());
			} else {
				return null;
			}
			
		} else {
			return null;
		}
	}
	//collects arguments for a function, which can be Strings or numbers.
	LinkedList<Node> parameterList(){
		var list = new LinkedList<Node>();
		tokenManager.MatchAndRemove(TokenType.LPAREN);
		var rparen = tokenManager.MatchAndRemove(TokenType.RPAREN);
		if (rparen.isPresent()) {
			return null;
		}
		boolean runAgain = false;
		do {
		  var t = tokenManager.Peek(0).get().getTokenType();
		  if((t == (TokenType.NUMBER)|| (t == TokenType.SUBTRACT)|| (t == TokenType.LPAREN)|| (t == TokenType.WORD))){     
	          list.add(Expression());
	          runAgain = runCommaListAgain();
		  } else if(t == TokenType.STRINGLITERAL){
			  list.add(new StringNode(tokenManager.MatchAndRemove(TokenType.STRINGLITERAL).get().getValue()));
		      runAgain = runCommaListAgain();
		  }
		  else {
			  return null;
		  }
		  } while (runAgain);
		
		tokenManager.MatchAndRemove(TokenType.RPAREN);
		return list;
	}
	//Checks for known functions and the collects the arguments
	FunctionNode functionInvocation() {
		var t = tokenManager.Peek(0).get().getTokenType();
		switch(t) {
		case RANDOM:
			tokenManager.MatchAndRemove(TokenType.RANDOM);
			return new FunctionNode(t, parameterList());
		case LEFT$:
			tokenManager.MatchAndRemove(TokenType.LEFT$);
			return new FunctionNode(t, parameterList());
		case RIGHT$:
			tokenManager.MatchAndRemove(TokenType.RIGHT$);
			return new FunctionNode(t, parameterList());
		case MID$:
			tokenManager.MatchAndRemove(TokenType.MID$);
			return new FunctionNode(t, parameterList());
		case NUM$:
			tokenManager.MatchAndRemove(TokenType.NUM$);
			return new FunctionNode(t, parameterList());
		case VALINT:
			tokenManager.MatchAndRemove(TokenType.VALINT);
			return new FunctionNode(t, parameterList());
		case VALFLOAT:
			tokenManager.MatchAndRemove(TokenType.VALFLOAT);
			return new FunctionNode(t, parameterList());
		default:
			return null;
		}
	}
	//Expression returns Term if it does not find a + or -, or a functionNode if given one
	//If it finds a + or -, it returns a MathOpNode with the + or -, the first return of Term, and another return of Term
	//If it finds multiple + or -, it continues calling itself until it can't find any more
	Node Expression() {
		var function = functionInvocation();
		if (function != null) {
			return function;
		}
		Node left = Term();
		MathOp op = null;
		if(tokenManager.MoreTokens()) {
			if(tokenManager.Peek(0).get().getTokenType() == (TokenType.COMMA)||tokenManager.Peek(0).get().getTokenType() == (TokenType.RPAREN)) {
				return left;
			}
		}
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
				return new MathOpNode(op,left,Expression());
			}
		}
		return new MathOpNode(op, left, Term());
		
	}
	//Term collects factors and operators until there are no more and then creates a list of MathOpNodes with each one containing the last in order to preserve the proper order.
	//Returns the last MathOpNode in the list (which contains all of the previous MathOpNodes)
	Node Term() {
		LinkedList<Node> numbers = new LinkedList<Node>();
		LinkedList<MathOp> ops = new LinkedList<MathOp>();
		LinkedList<Node> mathNodes = new LinkedList<Node>();
		Node left = Factor();
		if(tokenManager.MoreTokens()) {
			if(tokenManager.Peek(0).get().getTokenType() == (TokenType.COMMA)||tokenManager.Peek(0).get().getTokenType() == (TokenType.RPAREN)) {
				return left;
			}
		} else {
			return left;
		}
		
		if(tokenManager.MatchAndRemove(TokenType.RPAREN).isPresent()) {
			eatNewlines();
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
		//Loops, collecting factors and operators until it cant find any more * or / operators
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
		//Creates a list of MathOpNodes with the collected factors and operators
		mathNodes.add(new MathOpNode(ops.pop(),numbers.pop(),numbers.pop()));
		while(numbers.size() > 0) {			
			mathNodes.add(new MathOpNode(ops.pop(),mathNodes.getLast(),numbers.pop()));
		}
		
		if(ops.size() == 0) {
			return mathNodes.getLast();
		} 
		return new MathOpNode(ops.pop(),mathNodes.getLast(),Factor());
	}
	//Factor returns a FloatNode or IntegerNode if it has a number token, or returns a new expression if it has an lparen token
	Node Factor() {
		eatNewlines();
		Optional<Token> t = tokenManager.MatchAndRemove(TokenType.NUMBER);
		if(t.isEmpty()) {
			t = tokenManager.MatchAndRemove(TokenType.LPAREN);
			if(!t.isEmpty()) {
				return Expression();
			}
			t = tokenManager.MatchAndRemove(TokenType.WORD);
			if(!t.isEmpty()) {
				return new VariableNode(t.get().getValue());
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