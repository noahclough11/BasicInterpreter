package interpreter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

import lexer.TokenType;
import parser.AssignmentNode;
import parser.BooleanExpNode;
import parser.DataNode;
import parser.FloatNode;
import parser.ForNode;
import parser.FunctionNode;
import parser.GoSubStatementNode;
import parser.IfNode;
import parser.InputNode;
import parser.IntegerNode;
import parser.LabeledStatementNode;
import parser.MathOpNode;
import parser.MathOpNode.MathOp;
import parser.Node;
import parser.NodeType;
import parser.PrintNode;
import parser.ProgramNode;
import parser.ReadNode;
import parser.StatementNode;
import parser.StatementsNode;
import parser.StringNode;
import parser.VariableNode;

public class Interpreter{
	enum dataType { String, Int, Float};
	private ProgramNode program;
	LinkedList<String> data;
	LinkedList<dataType> dataTypes;
	HashMap<String, LabeledStatementNode> labeledStatements;
	HashMap<String,Integer> intVariables;
	HashMap<String,Double> floatVariables;
	HashMap<String,String> stringVariables;
	
	public Interpreter(ProgramNode program) {
		this.program = program;
		initializeHashMaps();
		LinkNodes();
		logData();
		logLabels();
		logVariables();
	}
	
	boolean advance; // Whether or not the loop should set current to next at the end of the loop. Turned off for cases that manually set the next statement (gosub, next, return, true if)
	Stack<StatementNode> stack = new Stack<StatementNode>();
	//Performs the functions of the given statements
	public void Interpret() {
		StatementsNode statements = (StatementsNode) this.program.instructions.peekFirst();
		StatementNode currentStatement = statements.getList().peekFirst();
		while((currentStatement.getNodeType() != NodeType.End)&&currentStatement!=null) {
		if(currentStatement.getNodeType() == null) {
			break;
		}
		advance = true;
		switch(currentStatement.getNodeType()) {
		case Read:
			ReadNode read = (ReadNode)currentStatement;
			LinkedList<Node> readList = read.getReadList();
			for (Node n: readList) {
				VariableNode varNode = (VariableNode)n;
				String name = varNode.getName();
				dataType type = dataTypes.pop();
				if (type == dataType.Float) {
					double value = Double.parseDouble(data.pop());
					if(floatVariables.containsKey(name)) {
						floatVariables.replace(name, value);
					} else {
						floatVariables.put(name, value);
					}
				} else {
					int value = Integer.parseInt(data.pop());
					if(intVariables.containsKey(name)) {
						intVariables.replace(name, value);
					} else {
						intVariables.put(name, value);
					}
				}
				break;
			}
		case Assignment:
			AssignmentNode assign = (AssignmentNode)currentStatement;
			String name = assign.getVariableName();
			NodeType varType = assign.getValue().getNodeType();
			if (varType == NodeType.Integer) {
				var intNode = (IntegerNode)assign.getValue();
				if(intVariables.containsKey(name)) {
					intVariables.replace(name, intNode.getNumber());
				} else {
					intVariables.put(name, intNode.getNumber());
				}
			} else if (varType == NodeType.Float) {
				var floatNode = (FloatNode)assign.getValue();
				if(floatVariables.containsKey(name)) {
					floatVariables.replace(name, floatNode.getNumber());
				} else {
					floatVariables.put(name, floatNode.getNumber());
				}
			} else if (varType == NodeType.MathOp){
				var mathNode = (MathOpNode)assign.getValue();
				if (containsFloats(mathNode)) {
					double num = EvaluateFloat(mathNode);
					if(floatVariables.containsKey(name)) {
						floatVariables.replace(name, num);
					} else {
						floatVariables.put(name, num);
					}
				} else {
					int numInt = EvaluateInt(mathNode);
					if(intVariables.containsKey(name)) {
						intVariables.replace(name, numInt);
					} else {
						intVariables.put(name, numInt);
					}
				}
			} else if(varType == NodeType.Variable) {
				if (containsFloats(assign.getValue())) {
					double num = EvaluateFloat(assign.getValue());
					if(floatVariables.containsKey(name)) {
						floatVariables.replace(name, num);
					} else {
						floatVariables.put(name, num);
					}
				} else {
					int numInt = EvaluateInt(assign.getValue());
					if(intVariables.containsKey(name)) {
						intVariables.replace(name, numInt);
					} else {
						intVariables.put(name, numInt);
					}
				}
			} else {
				var strNode = (StringNode)assign.getValue();
				String str = strNode.getValue();
				if(stringVariables.containsKey(name)) {
					stringVariables.replace(name, str);
				} else {
					stringVariables.put(name, str);
				}
			}
			break;
		case Input:
			Scanner scan = new Scanner(System.in);
			InputNode input = (InputNode)currentStatement;
			var inputList = input.getInputList();
			StringNode strNode = (StringNode)inputList.pop();
			String prompt = strNode.getValue();
			System.out.println(prompt);
			for (Node n: inputList) {
				var varNode = (VariableNode)n;
				String varName = varNode.getName();
				if (varName.contains("$")) {
					String userInputS = scan.next();
					stringVariables.put(varName, userInputS);
				} else if (varName.contains("%")) {
					double userInputD = scan.nextDouble();
					floatVariables.put(varName, userInputD);
				} else {
					int userInputI = scan.nextInt();
					intVariables.put(varName, userInputI);
				}
			scan.close();
				
			}
			break;
		case Print:
			PrintNode print = (PrintNode)currentStatement;
			var printList = print.getPrintList();
			for (Node n: printList) {
				NodeType printType = n.getNodeType();
				switch(printType) {
				case Integer,Float, Variable, MathOp, Function:
					if(containsFloats(n)) {
						System.out.print(EvaluateFloat(n));
					} else {
						System.out.print(EvaluateInt(n));
					}
				break; 
				case String:
					var stringNode = (StringNode)n;
					System.out.print(stringNode.getValue());
					break;
				default:
					System.out.print(n.toString());
				}
			}
			break;
		case If:
			double leftValue;
			double rightValue;
			IfNode ifNode = (IfNode)currentStatement;
			StatementNode nextState = labeledStatements.get(ifNode.getLabel()+":").getStatement();
			if (nextState == null) {
				System.out.println("label statement dne");
			}
			Node condition = ifNode.getBool();
			if (condition.getNodeType() == NodeType.BooleanExp) { // evaluates the given boolean expression and then goes to the specified label if true
				BooleanExpNode bool = (BooleanExpNode)condition;
				if (containsFloats(bool.getLeft())) {
					leftValue = EvaluateFloat(bool.getLeft());
				} else {
					leftValue = (double)EvaluateInt(bool.getLeft());
				}
				if (containsFloats(bool.getRight())) {
					rightValue = EvaluateFloat(bool.getRight());
				} else {
					rightValue = (double)EvaluateInt(bool.getRight());
				}
				var op = bool.getOp();
				switch(op) {
				case LESSTHAN:
					if(leftValue < rightValue) {
						currentStatement = (StatementNode)nextState;
						advance = false;
					}
				case LESSEQUAL:
					if(leftValue <= rightValue) {
						currentStatement = (StatementNode)nextState;
						advance = false;
					}
				case NOTEQUALS:
					if(leftValue != rightValue) {
						currentStatement = (StatementNode)nextState;
						advance = false;
					}
				case GREATERTHAN:
					if(leftValue > rightValue) {
						currentStatement = (StatementNode)nextState;
						advance = false;
					}
				case GREATEREQUAL:
					if(leftValue >= rightValue) {
						currentStatement = (StatementNode)nextState;
						advance = false;
					}
				default:
					break;
				}
			} else {
				currentStatement = (StatementNode)nextState;
				advance = false;
			}
			break;
		case Gosub:
			GoSubStatementNode gosub = (GoSubStatementNode)currentStatement;
			stack.push(currentStatement.next);
			currentStatement = labeledStatements.get(gosub.getLabel()).getStatement();
			advance = false;
			break;
		case Return:
			if(stack.isEmpty()) {
				break;
			}
			currentStatement = stack.pop();
			advance = false;
			break;
		case For: // Checks if the index variable is greater than the limit after incrementing. If it is, it skips forward to the end of the loop block. If not, it puts itself at the top of the stack, to be returned to when the next statement is reached.
			ForNode forNode = (ForNode)currentStatement;
			double indexFloat;
			int indexInt;
			int increment = forNode.getIncrement().getNumber();
			int limit = forNode.getLimit().getNumber();
			Node variable = forNode.getVariable();
			if (variable.getNodeType() == NodeType.Assignment) {
				assign = (AssignmentNode)variable;
				String varName = assign.getVariableName();
				if (floatVariables.containsKey(varName)) {
					indexFloat = floatVariables.get(varName) + increment;
					if (indexFloat > limit) {
						currentStatement = skipToNext(currentStatement);
					} else {
						stack.push(currentStatement);
					}
				} else if (intVariables.containsKey(varName)) {
					indexInt = intVariables.get(varName) + increment;
					intVariables.replace(varName, indexInt);
					if (indexInt > limit) {
						currentStatement = skipToNext(currentStatement);
					}else {
						stack.push(currentStatement);
					}
				} else {
					int isInt = assignVariable(assign);
					if (isInt == 1) {
						indexInt = intVariables.get(varName);
						if (indexInt > limit) {
							currentStatement = skipToNext(currentStatement);
						}else {
							stack.push(currentStatement);
						}
					} else {
						indexFloat = floatVariables.get(varName);
						if (indexFloat > limit) {
							currentStatement = skipToNext(currentStatement);
						} else {
							stack.push(currentStatement);
						}
					}
				}
				
			}
			break;
		case Next:
			if(stack.isEmpty()) {
				break;
			}
			advance = false;
			currentStatement = stack.pop();
			break;
		default:
			break;	
		}
		if (advance == true) {
		  currentStatement = currentStatement.next;
		}
		if(currentStatement == null) {
			break;
		}
		}
	}
	//returns the total value of all mathop, function, variable, and number nodes within the head node given.
	int EvaluateInt(Node node) {
		var type = node.getNodeType();
		if (type == NodeType.MathOp) {
			MathOpNode mathOp = (MathOpNode)node;
			MathOp operator = mathOp.getOp();
			switch (operator) {
			case ADD:
				return EvaluateInt(mathOp.getLeft()) + EvaluateInt(mathOp.getRight());
			case SUBTRACT:
				return EvaluateInt(mathOp.getLeft()) - EvaluateInt(mathOp.getRight());
			case MULTIPLY:
				return EvaluateInt(mathOp.getLeft()) * EvaluateInt(mathOp.getRight());
			case DIVIDE:
				return EvaluateInt(mathOp.getLeft()) / EvaluateInt(mathOp.getRight());
			default:
				break;
			}
		} else if (type == NodeType.Integer) {
			IntegerNode intNode = (IntegerNode)node;
			return intNode.getNumber();
		} else if (type == NodeType.Function) {
			FunctionNode func = (FunctionNode)node;
			var funcType = func.getFuncType();
			switch (funcType) {
			case RANDOM:
				return RANDOM();
			case VALINT:
				StringNode arg = (StringNode)func.args.get(0);
				return VALINT(arg.getValue());
			case VALFLOAT:
				return -1;
			default:
				return -1;
			
			}
		} else if (type == NodeType.Variable) {
			VariableNode varNode = (VariableNode)node;
			String varName = varNode.getName();
			return intVariables.get(varName);
		} else {
			return -1;
		}
		return -1;
	}
	double EvaluateFloat(Node node) {
			var type = node.getNodeType();
			if (type == NodeType.MathOp) {
				MathOpNode mathOp = (MathOpNode)node;
				MathOp operator = mathOp.getOp();
				switch (operator) {
				case ADD:
					return EvaluateFloat(mathOp.getLeft()) + EvaluateFloat(mathOp.getRight());
				case SUBTRACT:
					return EvaluateFloat(mathOp.getLeft()) - EvaluateFloat(mathOp.getRight());
				case MULTIPLY:
					return EvaluateFloat(mathOp.getLeft()) * EvaluateFloat(mathOp.getRight());
				case DIVIDE:
					return EvaluateFloat(mathOp.getLeft()) / EvaluateFloat(mathOp.getRight());
				default:
					break;
				}
			} else if (type == NodeType.Float) {
				FloatNode fNode = (FloatNode)node;
				return fNode.getNumber();
			} else if (type == NodeType.Integer) {
				IntegerNode intNode = (IntegerNode)node;
				return (double)intNode.getNumber();
			} else if (type == NodeType.Function) {
				FunctionNode func = (FunctionNode)node;
				var funcType = func.getFuncType();
				switch (funcType) {
				case RANDOM:
					return RANDOM();
				case VALFLOAT:
					StringNode arg = (StringNode)func.args.get(0);
					return VALFLOAT(arg.getValue());
				case VALINT:
					arg = (StringNode)func.args.get(0);
					return VALINT(arg.getValue());
				default:
					return -1;
				
				}
			} else if (type == NodeType.Variable) {
				VariableNode varNode = (VariableNode)node;
				String varName = varNode.getName();
				return floatVariables.get(varName);
			} else {
				return -1;
			}
			return -1;
		
	}
	//skips ahead in the node list until the next Next node is reached
	StatementNode skipToNext(StatementNode current) {
		while (current.getNodeType() != NodeType.Next) {
			current = current.next;
		}
		return current;
	}
	//assigns the given variable to the proper hashmap
	//returns 1 if the assigned variable was an Integer, 0 if float.
	int assignVariable(AssignmentNode assign) {
		Node value = assign.getValue();
		String varName = assign.getVariableName();
		if (value.getNodeType() == NodeType.Integer) {
			IntegerNode intNode = (IntegerNode)value;
			intVariables.put(varName, intNode.getNumber());
			return 1;
		} else {
			FloatNode floatNode = (FloatNode)value;
			floatVariables.put(varName, floatNode.getNumber());
			return 0;
		}
	}
	//variants of the input and print cases for testing
	void InputTest(LinkedList<Node> inputList, LinkedList<String> testList) {
		StringNode strNode = (StringNode)inputList.pop();
		String prompt = strNode.getValue();
		System.out.println(prompt);
		for (Node n: inputList) {
			var varNode = (VariableNode)n;
			String varName = varNode.getName();
			String userInputS = testList.pop();
			stringVariables.put(varName, userInputS);
		}
	}
	LinkedList<String> PrintTest(LinkedList<Node> printList) {
		LinkedList<String> outputList = new LinkedList<String>();
		for (Node n: printList) {
			NodeType printType = n.getNodeType();
			switch(printType) {
			case Integer,Float, Variable, MathOp, Function:
				if(containsFloats(n)) {
					outputList.add(String.valueOf(EvaluateFloat(n)));
				} else {
					outputList.add(String.valueOf(EvaluateInt(n)));
				}
				break;
			case String:
				var stringNode = (StringNode)n;
				outputList.add(stringNode.getValue());
				break;
			default:
				outputList.add(n.toString());
			}
		
	}
		return outputList;
		
	}
	//checks if an expression contains any floating point numbers, to determine whether EvaluateInt or EvaluateFloat should be used.
	boolean containsFloats(Node node) {
		var type = node.getNodeType();
		if (type == NodeType.MathOp) {
			MathOpNode mathOp = (MathOpNode)node;
			if (containsFloats(mathOp.getLeft()) || containsFloats(mathOp.getRight())) {
				return true;
			} else {
				return false;
			}
		} else if (type == NodeType.Integer) {
			return false;
		} else if (type == NodeType.Float) {
			return true;
		} else if (type == NodeType.Variable) {
			VariableNode varNode = (VariableNode)node;
			String name = varNode.getName();
			if (floatVariables.containsKey(name)) {
				return true;
			} else {
				return false;
			}
		}else if (type == NodeType.Function) {
			FunctionNode func = (FunctionNode)node;
			var funcType = func.getFuncType();
			if (funcType == TokenType.VALFLOAT) {
				return true;
			} else return false;
		} else {
			return false;
		}
		
	}
	//creates all necessary hasmaps
	void initializeHashMaps() {
		data = new LinkedList<String>();
		dataTypes = new LinkedList<dataType>();
		labeledStatements = new HashMap<String, LabeledStatementNode>();
		intVariables = new HashMap<String,Integer>();
		floatVariables = new HashMap<String,Double>();
		stringVariables = new HashMap<String,String>();
	}
	//looks through the program in advance and logs the data entries 
	void logData() {
		LinkedList<Node> statements = program.instructions;
		for(Node s: statements) {
			StatementsNode states = (StatementsNode)s;
		  for(Node n: states.getList()) {
			if (n.getNodeType() == NodeType.Data) {
				DataNode d = (DataNode) n;
				for(Node i: d.dataList) {
					if(i.getNodeType() == NodeType.Integer) {
						IntegerNode integer = (IntegerNode)i;
						dataTypes.add(dataType.Int);
						data.add(""+ integer.getNumber());
					}else {
						FloatNode floatData = (FloatNode)i;
						dataTypes.add(dataType.Float);
						data.add("" + floatData.getNumber());
					}
				}
			}
		  }
		}
	}
	//looks through the program in advance and logs the variables into hashmaps according to their data types
	void logVariables() {
		var statements = program.instructions;
		for(Node s: statements) {
			StatementsNode states = (StatementsNode)s;
		  for(Node n: states.getList()) {
			if(n.getNodeType() == NodeType.Assignment) {
				AssignmentNode assignment = (AssignmentNode)n;
				NodeType nodeType = assignment.getValue().getNodeType();
				switch (nodeType) {
				case Float:
					FloatNode floatNode = (FloatNode)assignment.getValue();
					floatVariables.put(assignment.getVariableName(),floatNode.getNumber());
					break;
				case Integer:
					IntegerNode intNode = (IntegerNode)assignment.getValue();
					intVariables.put(assignment.getVariableName(),intNode.getNumber());
					break;
				case String:
					StringNode strNode = (StringNode)assignment.getValue();
					stringVariables.put(assignment.getVariableName(),strNode.getValue());
					break;
				case Variable:
					VariableNode varNode = (VariableNode)assignment.getValue();
					String name = varNode.getName();
					if(floatVariables.containsKey(name)) {
						floatVariables.put(assignment.getVariableName(), null);
					} else if (intVariables.containsKey(name)) {
						intVariables.put(assignment.getVariableName(), null);
					} else {
						stringVariables.put(assignment.getVariableName(), null);
					}
				}
			}
		  }
		}
	}
	//looks through the program and logs all of the labeled statements into a hashmap
	void logLabels() {
		var statements = program.instructions;
		for(Node s: statements) {
			StatementsNode states = (StatementsNode)s;
		  for(Node n: states.getList()) {
			if(n.getNodeType() == NodeType.LabeledStatement) {
				LabeledStatementNode labeledStatement = (LabeledStatementNode)n;
				labeledStatements.put(labeledStatement.getLabel(),labeledStatement );
			}
		  }
		}
	}
	//loops through the StatementNodes and links each one to the next Node
	void LinkNodes() {
		var instructions = program.instructions;
		StatementsNode statementsNode = (StatementsNode) instructions.get(0);
		var statements = statementsNode.getList();
		Node n = statements.get(0);
		int i = 0;
		while(n != null) {
			StatementNode statement = (StatementNode)n;
			if(statement == (StatementNode)statements.getLast()) {
				statement.next = null;
				break;
			}
			statement.next = (StatementNode)statements.get(i+1);
			i++;
			n = statements.get(i);
		}
		
	}
	//Basic default functions
	static int RANDOM() {
		return (int) Math.random();
	}
	static String LEFT$(String str, int len) {
		int i = 0;
		String returnStr = "";
		for (i = 0; i < len; i++) {
			returnStr += str.charAt(i);
		}
		return returnStr;
	}
	static String RIGHT$(String str, int len) {
		int i;
		String returnStr = "";
		for (i = str.length()- len; i < str.length(); i++) {
			returnStr += str.charAt(i);
		}
		return returnStr;
	}
	static String MID$(String str, int start, int len) {
		int i;
		String returnStr = "";
		for(i = start; i < (start + len); i++) {
			returnStr += str.charAt(i);
		}
		return returnStr;
	}
	static String NUM$(int num){
		return ""+num;
	}
	static String NUM$(float num){
		return ""+num;
	}
	static int VALINT(String str) {
		return Integer.parseInt(str);
	}
	static float VALFLOAT(String str) {
		return Float.parseFloat(str);
	}
}