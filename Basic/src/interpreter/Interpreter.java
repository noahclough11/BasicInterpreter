package interpreter;

import java.util.HashMap;
import java.util.LinkedList;

import parser.AssignmentNode;
import parser.DataNode;
import parser.FloatNode;
import parser.IntegerNode;
import parser.LabeledStatementNode;
import parser.Node;
import parser.ProgramNode;
import parser.StatementsNode;
import parser.VariableNode;

public class Interpreter{
	private ProgramNode program;
	private LinkedList<Double> data;
	private HashMap<String, LabeledStatementNode> labeledStatements;
	private HashMap<String,Integer> intVariables;
	private HashMap<String,Float> floatVariables;
	private HashMap<String,String> stringVariables;
	
	public Interpreter(ProgramNode program) {
		this.program = program;
		this.data = new LinkedList<Double>();
		initializeHashMaps();
		logData();
		logLabels();
		logVariables();
	}
	void initializeHashMaps() {
		labeledStatements = new HashMap<String, LabeledStatementNode>();
		intVariables = new HashMap<String,Integer>();
		floatVariables = new HashMap<String,Float>();
		stringVariables = new HashMap<String,String>();
	}
	void logData() {
		var statements = program.instructions;
		var testList = new LinkedList<Node>();
		DataNode dataNode = new DataNode(testList);
		IntegerNode intNode = new IntegerNode(0);
		FloatNode floatNode = new FloatNode(1.0);
		for(Node n: statements) {
			var nodeType = n.getClass();
			if (nodeType == dataNode.getClass()) {
				DataNode d = (DataNode) n;
				for(Node i: d.dataList) {
					nodeType = i.getClass();
					if(nodeType == intNode.getClass()) {
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
	void logVariables() {
		var statements = program.instructions;
		AssignmentNode testNode = new AssignmentNode(new VariableNode("test"), new IntegerNode(0));
		for(Node n: statements) {
			var nodeType = n.getClass();
			if(nodeType == testNode.getClass()) {
				AssignmentNode assigment = (AssignmentNode)n;
				if
			}
		}
	}
	void logLabels() {
		
	}
}