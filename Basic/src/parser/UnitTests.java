package parser;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import lexer.Lexer;
import lexer.InvalidCharacterException;

public class UnitTests{
	Lexer lex1 = new Lexer("56+5/3", 1);
	Lexer lex2 = new Lexer("(10+5)/(6*3)", 1);
	Lexer lex3 = new Lexer("9*(1+4/8)", 1);
	Lexer lex4 = new Lexer("10/-9+2*3", 1);
	Lexer lex5 = new Lexer("2*4/2*6+9-7+6", 1);

@Test
public void orderOfOpTest1() throws InvalidCharacterException {
	var lex1List = lex1.lex();
	Parser parser1 = new Parser(lex1List);
	ProgramNode program1 = parser1.parse();
	String s = program1.toString();
	System.out.println(program1.toString());
	assertEquals("(56 ADD (5 DIVIDE 3))", s);
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
	assertEquals("(9 MULTIPLY (1 ADD (4 DIVIDE 8)))", s);
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
	assertEquals("((((2 MULTIPLY 4) DIVIDE 2) MULTIPLY 6) ADD (9 SUBTRACT (7 ADD 6)))", s);
}
}