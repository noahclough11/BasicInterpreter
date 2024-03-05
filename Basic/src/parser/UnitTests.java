package parser;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import lexer.Lexer;
import lexer.InvalidCharacterException;

public class UnitTests{
	Lexer lex1 = new Lexer("56.9+\n5/3", 1);
	Lexer lex2 = new Lexer("(10+5)\n/\n(6*3)", 1);
	Lexer lex3 = new Lexer("9*(1+4/8.2)", 1);
	Lexer lex4 = new Lexer("10/\n\n-9+2*3", 1);
	Lexer lex5 = new Lexer("2*4/2*6.4+9-\n\n7+6", 1);
	Lexer lex6 = new Lexer("PRINT(1+2,2+1, \"hello\")", 1);
	Lexer lex7 = new Lexer("hello = 5+3", 1);
	Lexer lex8 = new Lexer("READ(cat, hat, rat)", 1);
	Lexer lex9 = new Lexer("DATA(56.2, 78, \"hello\")", 1);
	Lexer lex10 = new Lexer("INPUT(\"hello\", cat, hat)", 1);
//orderOfOpTests verify that various expressions return the correct order of operations and skip newlines
//  !!  OrderOfOpTests currently do not work as the main method was changed to not  directly call expression
/*
	@Test
public void orderOfOpTest1() throws InvalidCharacterException {
	var lex1List = lex1.lex();
	Parser parser1 = new Parser(lex1List);
	ProgramNode program1 = parser1.parse();
	String s = program1.toString();
	System.out.println(program1.toString());
	assertEquals("(56.9 ADD (5 DIVIDE 3))", s);
}
@Test
public void orderOfOpTest2() throws InvalidCharacterException {
	var lex2List = lex2.lex();
	Parser parser2 = new Parser(lex2List);
	ProgramNode program2 = parser2.parse();
	String s = program2.toString();
	System.out.println(program2.toString());
	assertEquals("((10 ADD 5) DIVIDE (6 MULTIPLY 3))", s);
}@Test
public void orderOfOpTest3() throws InvalidCharacterException {
	var lex3List = lex3.lex();
	Parser parser3 = new Parser(lex3List);
	ProgramNode program3 = parser3.parse();
	String s = program3.toString();
	System.out.println(program3.toString());
	assertEquals("(9 MULTIPLY (1 ADD (4 DIVIDE 8.2)))", s);
}@Test
public void orderOfOpTest4() throws InvalidCharacterException {
	var lex4List = lex4.lex();
	Parser parser4 = new Parser(lex4List);
	ProgramNode program4 = parser4.parse();
	String s = program4.toString();
	System.out.println(program4.toString());
	assertEquals("((10 DIVIDE -9) ADD (2 MULTIPLY 3))", s);
}@Test
public void orderOfOpTest5() throws InvalidCharacterException {
	var lex5List = lex5.lex();
	Parser parser5 = new Parser(lex5List);
	ProgramNode program5 = parser5.parse();
	String s = program5.toString();
	System.out.println(program5.toString());
	assertEquals("((((2 MULTIPLY 4) DIVIDE 2) MULTIPLY 6.4) ADD (9 SUBTRACT (7 ADD 6)))", s);
}
*/
//Test 6 tests the Statements() function's ability to process PrintNodes
@Test
public void printNodeTest() throws InvalidCharacterException {
	var lex6List = lex6.lex();
	Parser parser6 = new Parser(lex6List);
	ProgramNode program6 = parser6.parse();
	String s = program6.toString();
	System.out.println(program6.toString());
	assertEquals("Print: ((1 ADD 2), (2 ADD 1), string: \"hello\", )\n", s);
}
//Test 7 tests the Statements() function's ability to process AssignmentNodes
@Test
public void assignmentNodeTest() throws InvalidCharacterException {
	var lex7List = lex7.lex();
	Parser parser7 = new Parser(lex7List);
	ProgramNode program7 = parser7.parse();
	String s = program7.toString();
	System.out.println(program7.toString());
	assertEquals("variable: hello = (5 ADD 3)", s);
}
//Test 8 tests the Statements() function's ability to process ReadNode
@Test
public void readNodeTest() throws InvalidCharacterException {
	var lex8List = lex8.lex();
	Parser parser8 = new Parser(lex8List);
	ProgramNode program8 = parser8.parse();
	String s = program8.toString();
	System.out.println(program8.toString());
	assertEquals("Read: (variable: cat, variable: hat, variable: rat, )\n", s);
}
//Test 9 tests the Statements() function's ability to process DataNode
@Test
public void dataNodeTest() throws InvalidCharacterException {
	var lex9List = lex9.lex();
	Parser parser9 = new Parser(lex9List);
	ProgramNode program9 = parser9.parse();
	String s = program9.toString();
	System.out.println(program9.toString());
	assertEquals("Data: (56.2, 78, string: \"hello\", )\n", s);
}
//Test 10 tests the Statements() function's ability to process InputNode
@Test
public void inputNodeTest() throws InvalidCharacterException {
	var lex10List = lex10.lex();
	Parser parser10 = new Parser(lex10List);
	ProgramNode program10 = parser10.parse();
	String s = program10.toString();
	System.out.println(program10.toString());
	assertEquals("Input: (string: \"hello\", variable: cat, variable: hat, )\n", s);
}
}