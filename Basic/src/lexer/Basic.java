package lexer;

import java.io.IOException;
import java.util.LinkedList;

import interpreter.Interpreter;
import parser.Parser;
import parser.ProgramNode;


public class Basic{
	
//  Translates given file into a list of Tokens
//  and then prints out the list for debugging.
//  Parses the token list into a list of nodes which represent
//  instructions for a Basic program.
//  prints the instructions node list for debugging.
	public static void main(String[] args) throws IOException, lexer.InvalidCharacterException{

		if(args.length != 1)
			throw new IOException();
		String filename = args[0];

		
		Lexer lexer = new Lexer(filename);
		LinkedList<Token> tokensList = lexer.lex();

		for(Token t: tokensList) {
			System.out.print(t.toString());
		}
		System.out.println();
		Parser parser = new Parser(tokensList);
		ProgramNode program = parser.parse();
		System.out.println(program.toString());
		Interpreter interpreter = new Interpreter(program);
		interpreter.Interpret();
		

	}

}