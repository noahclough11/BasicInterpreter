package lexer;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UnitTests {
	
	Lexer lex1 = new Lexer("56 cat 7.84\ndog 42", 1);
	Lexer lex2 = new Lexer("car hat% 72", 1);
	Lexer lex3 = new Lexer("8.921\n 9.5 hello", 1);
	Lexer lex4 = new Lexer("", 1);
	Lexer lex5 = new Lexer("87 dog ! 98", 1);
	Lexer lex6 = new Lexer("FOR IF hello 9.32 PRINT(32+6)", 1);
	Lexer lex7 = new Lexer("Line: \"hello\" they said", 1);
	
	
	//Tests 1-4 verify that the lex method returns the correct tokens given a valid input
	@Test
    public void lexerTest1() throws Exception {
    	String lex1Result = "";
    	var lex1List = lex1.lex();
    	for(Token t: lex1List) {
    		lex1Result += t.toString();
    	}
    	
    	assertEquals("NUMBER(56) WORD(cat) NUMBER(7.84) ENDOFLINE WORD(dog) NUMBER(42) ENDOFLINE ", lex1Result);
    };
    @Test
    public void lexerTest2() throws Exception {
    	String lex2Result = "";
    	var lex2List = lex2.lex();
    	for(Token t: lex2List) {
    		lex2Result += t.toString();
    	}
    	
    	assertEquals("WORD(car) WORD(hat%) NUMBER(72) ENDOFLINE ", lex2Result);
    };
    @Test
    public void lexerTest3() throws Exception {
    	String lex3Result = "";
    	var lex3List = lex3.lex();
    	for(Token t: lex3List) {
    		lex3Result += t.toString();
    	}
    	
    	assertEquals("NUMBER(8.921) ENDOFLINE NUMBER(9.5) WORD(hello) ENDOFLINE ", lex3Result);
    };
    @Test
    public void lexerTest4() throws Exception {
    	String lex4Result = "";
    	var lex4List = lex4.lex();
    	for(Token t: lex4List) {
    		lex4Result += t.toString();
    	}
    	
    	assertEquals("ENDOFLINE ", lex4Result);
    };
    CodeHandler codeHandler1 = new CodeHandler("56 cat 7.84\ndog 42", 1);
	CodeHandler codeHandler2 = new CodeHandler("car hat% 72", 1);
	CodeHandler codeHandler3 = new CodeHandler("8.921\n 9.5 hello", 1);
	
	//Test 5 verifies that the Lexer throws an InvalidCharacterException when given an invalid character
	 @Test
	    public void lexerTest5Invalid() throws Exception {
	    	Object o = null;
	    	try {
	    		o = lex5.lex();
	    	} catch (Exception e) {
	    		o = e;
	    	}
	    	assertEquals(lex5.new InvalidCharacterException("s").getClass(), o.getClass());
	    };
	    
	@Test
	//Test 6 checks that keywords are properly handled as their unique token types and not as words, as well as checking that symbols are properly handled
	public void lexerTest6() throws Exception {
	    	String lex6Result = "";
	    	var lex6List = lex6.lex();
	    	for(Token t: lex6List) {
	    		lex6Result += t.toString();
	    	}
	    	
	    	assertEquals("FOR IF WORD(hello) NUMBER(9.32) PRINT LPAREN NUMBER(32) ADD NUMBER(6) RPAREN ENDOFLINE ", lex6Result);
	    }; 
	@Test
	//Test 7 checks that the label is properly handled and that the string literal is handled properly
	public void lexerTest7() throws Exception {
	    	String lex7Result = "";
	    	var lex7List = lex7.lex();
	    	for(Token t: lex7List) {
	    		lex7Result += t.toString();
	    	}
	    	
	    	assertEquals("LABEL(Line:) STRINGLITERAL(hello) WORD(they) WORD(said) ENDOFLINE ", lex7Result);
	    };
	   
	//The CodeHandler tests verify that the CodeHandler functions return the correct characters and move the index properly
	@Test
	public void codeHandlerTestPeek() {
	assertEquals('6', codeHandler1.Peek(1));
	assertEquals(' ', codeHandler1.Peek(2));
	assertEquals('t', codeHandler1.Peek(5));
	assertEquals('r', codeHandler2.Peek(2));
	assertEquals('.', codeHandler3.Peek(1));
	
	
	assertEquals(0, codeHandler1.getIndex());
	assertEquals(0, codeHandler2.getIndex());
	assertEquals(0, codeHandler3.getIndex());
	}
	@Test
	public void codeHandlerTestGetChar() {
		assertEquals('5', codeHandler1.GetChar());
		assertEquals('6', codeHandler1.GetChar());
		assertEquals(' ', codeHandler1.GetChar());
		assertEquals('c', codeHandler2.GetChar());
		assertEquals('8', codeHandler3.GetChar());
		
		assertEquals(3, codeHandler1.getIndex());
		assertEquals(1, codeHandler2.getIndex());
		assertEquals(1, codeHandler3.getIndex());
	}
	@Test
	public void codeHandlerTestSwallow() {
	    codeHandler1.Swallow(2);
	    codeHandler2.Swallow(4);
	    codeHandler3.Swallow(8);
	    
	    assertEquals(2, codeHandler1.getIndex());
		assertEquals(4, codeHandler2.getIndex());
		assertEquals(8, codeHandler3.getIndex());
	    
	}
	
}
