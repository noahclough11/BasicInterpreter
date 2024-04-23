package interpreter;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import lexer.InvalidCharacterException;
import lexer.Lexer;
import parser.FloatNode;
import parser.IntegerNode;
import parser.MathOpNode;
import parser.MathOpNode.MathOp;
import parser.Node;
import parser.Parser;
import parser.ProgramNode;
import parser.StringNode;
import parser.VariableNode;


public class InterpreterUnitTests{
	Lexer lex1 = new Lexer("variable = 4\n lineLabel: \n"
			+ "DATA(56.2, 78.3, 55.2)", 1);
	@Test
	//Test 1 asserts that the variable has been properly assigned to its hashmap
	public void interpreterTest1() throws InvalidCharacterException{
		var lex1List = lex1.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		assert inter1.intVariables.containsKey("variable");
	}
	@Test
	//Test 2 asserts that the label has been properly assigned to its hashmap
	public void interpreterTest2() throws InvalidCharacterException{
		var lex1List = lex1.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		assert inter1.labeledStatements.containsKey("lineLabel:");
	}
	@Test
	//Test 3 asserts that the data points have been properly added to the data list
	public void interpreterTest3() throws InvalidCharacterException{
		var lex1List = lex1.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		assert inter1.data.contains("56.2");
		assert inter1.data.contains("78.3");
		assert inter1.data.contains("55.2");
	}
	@Test
	//Tests functionality of the default functions
	public void defaultMethodsTest() {
		String str = "Albany";
		assertEquals("Alb", Interpreter.LEFT$(str, 3));
		assertEquals("any", Interpreter.RIGHT$(str, 3));
		assertEquals("ban", Interpreter.MID$(str, 2, 3));
		assertEquals("1", Interpreter.NUM$(1));
		assert 10 == Interpreter.VALINT("10");
		assert (float)10.1 ==  Interpreter.VALFLOAT("10.1");
	}
	@Test
	//InputTests test the input function's ability to map given input to the appropriate variables and store them
	public void inputTest1() throws InvalidCharacterException {
		var lex1List = lex1.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		LinkedList<Node> inputTestList = new LinkedList<Node>();
		inputTestList.add(new StringNode("enter three words:"));
		inputTestList.add(new VariableNode("first$"));
		inputTestList.add(new VariableNode("second$"));
		inputTestList.add(new VariableNode("third$"));
		LinkedList<String> inputTestInputs = new LinkedList<String>();
		inputTestInputs.add("cat");
		inputTestInputs.add("hat");
		inputTestInputs.add("rat");
		inter1.InputTest(inputTestList, inputTestInputs);
		assert inter1.stringVariables.get("first$") == "cat";
		assert inter1.stringVariables.get("second$") == "hat";
		assert inter1.stringVariables.get("third$") == "rat";
	}
	@Test
	public void inputTest2() throws InvalidCharacterException {
		var lex1List = lex1.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		LinkedList<Node> inputTestList = new LinkedList<Node>();
		inputTestList.add(new StringNode("enter three words:"));
		inputTestList.add(new VariableNode("first$"));
		inputTestList.add(new VariableNode("second$"));
		inputTestList.add(new VariableNode("third$"));
		LinkedList<String> inputTestInputs = new LinkedList<String>();
		inputTestInputs.add("dog");
		inputTestInputs.add("bog");
		inputTestInputs.add("cog");
		inter1.InputTest(inputTestList, inputTestInputs);
		assert inter1.stringVariables.get("first$") == "dog";
		assert inter1.stringVariables.get("second$") == "bog";
		assert inter1.stringVariables.get("third$") == "cog";
	}
	@Test
	public void inputTest3() throws InvalidCharacterException {
		var lex1List = lex1.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		LinkedList<Node> inputTestList = new LinkedList<Node>();
		inputTestList.add(new StringNode("enter three words:"));
		inputTestList.add(new VariableNode("first$"));
		inputTestList.add(new VariableNode("second$"));
		inputTestList.add(new VariableNode("third$"));
		LinkedList<String> inputTestInputs = new LinkedList<String>();
		inputTestInputs.add("pig");
		inputTestInputs.add("big");
		inputTestInputs.add("rig");
		inter1.InputTest(inputTestList, inputTestInputs);
		assert inter1.stringVariables.get("first$") == "pig";
		assert inter1.stringVariables.get("second$") == "big";
		assert inter1.stringVariables.get("third$") == "rig";
	}
	@Test
	//PrintTests check the interpretation of the print function, as well as evaluateInt and evaluateFloat
	public void printTest1() throws InvalidCharacterException {
		var lex1List = lex1.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		LinkedList<Node> printTestList = new LinkedList<Node>();
		printTestList.add(new MathOpNode( MathOp.ADD,new IntegerNode(2), new IntegerNode(5)));
		printTestList.add(new MathOpNode( MathOp.DIVIDE,new FloatNode(6), new IntegerNode(4)));
		printTestList.add(new MathOpNode( MathOp.MULTIPLY,new IntegerNode(3), new IntegerNode(4)));
		
		LinkedList<String> result = inter1.PrintTest(printTestList);
		String test = result.pop();
		assertEquals(test,"7");
		test = result.pop();
		assertEquals(test,"1.5");
		test = result.pop();
		assertEquals(test,"12");
	}
	//Sets the output to a different printstream instead of the console so that it can be tested against the correct result
	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

	@Before
	public void setUp() {
	    System.setOut(new PrintStream(outputStreamCaptor));
	}
	@Test
	//completeTests test the interpreters ability to lex, parse, and interpret a given BASIC program
	public void completeTest1() throws InvalidCharacterException {
		Lexer lexTest1 = new Lexer("FOR A = 7 TO 10\n"
				+ "PRINT A\n"
				+ "NEXT A\n"
				+ "END", 1);
		var lex1List = lexTest1.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		inter1.Interpret();
		assertEquals("78910", outputStreamCaptor.toString()
			      .trim());
	}
	@Test
	public void completeTest2() throws InvalidCharacterException {
		Lexer lexTest1 = new Lexer("IF 5 < 7 THEN True\n"
				+ "PRINT \"FALSE\"\n"
				+ "END\n"
				+ "True: PRINT \"TRUE\"\n"
				+ "END", 1);
		var lex1List = lexTest1.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		inter1.Interpret();
		assertEquals("TRUE", outputStreamCaptor.toString()
			      .trim());
	}
	@Test
	public void completeTest3() throws InvalidCharacterException {
		Lexer lexTest3 = new Lexer("FOR X = 2 TO 5\n"
				+ "PRINT X\n"
				+ "NEXT X\n"
				+ "END", 1);
		var lex1List = lexTest3.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		inter1.Interpret();
		assertEquals("2345", outputStreamCaptor.toString()
			      .trim());
	}
	@Test
	public void completeTest4() throws InvalidCharacterException {
		Lexer lexTest1 = new Lexer("IF 5 > 7 THEN True\n"
				+ "PRINT \"FALSE\"\n"
				+ "END\n"
				+ "True: PRINT \"TRUE\"\n"
				+ "END", 1);
		var lex1List = lexTest1.lex();
		Parser parser1 = new Parser(lex1List);
		ProgramNode program1 = parser1.parse();
		Interpreter inter1 = new Interpreter(program1);
		inter1.Interpret();
		assertEquals("FALSE", outputStreamCaptor.toString()
			      .trim());
	}
}