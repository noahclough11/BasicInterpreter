package interpreter;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import lexer.InvalidCharacterException;
import lexer.Lexer;
import parser.Parser;
import parser.ProgramNode;


public class UnitTests{
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
		assert inter1.data.contains(56.2);
		assert inter1.data.contains(78.3);
		assert inter1.data.contains(55.2);
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
}