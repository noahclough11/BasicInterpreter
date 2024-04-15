package interpreter;

import java.util.HashMap;
import java.util.LinkedList;

import parser.AssignmentNode;
import parser.DataNode;
import parser.FloatNode;
import parser.FunctionNode;
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
	private ProgramNode program;
	LinkedList<Double> data;
	HashMap<String, LabeledStatementNode> labeledStatements;
	HashMap<String,Integer> intVariables;
	HashMap<String,Double> floatVariables;
	HashMap<String,String> stringVariables;
	
	public Interpreter(ProgramNode program) {
		this.program = program;
		this.data = new LinkedList<Double>();
		initializeHashMaps();
		logData();
		logLabels();
		logVariables();
	}
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
				return null;
			default:
				return null;
			
			}
		} else if (type == NodeType.Variable) {
			VariableNode varNode = (VariableNode)node;
			String varName = varNode.getName();
			return intVariables.get(varName);
		} else {
			return null;
		}
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
					return null;
				default:
					return null;
				
				}
			} else if (type == NodeType.Variable) {
				VariableNode varNode = (VariableNode)node;
				String varName = varNode.getName();
				return floatVariables.get(varName);
			} else {
				return null;
			}
		
	}
	void Interpret(StatementNode statement) {
		switch(statement.getNodeType()) {
		case Read:
			ReadNode read = (ReadNode)statement;
		case Assignment:
			AssignmentNode assign = (AssignmentNode)statement;
		case Input:
			InputNode input = (InputNode)statement;
		case Print:
			PrintNode print = (PrintNode)statement;
		default:
			break;	
		}
	}
	void initializeHashMaps() {
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
						data.add((double) integer.getNumber());
					}else {
						FloatNode floatData = (FloatNode)i;
						data.add(floatData.getNumber());
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