package lexer;

import java.io.IOException;
import java.util.LinkedList;
import parser.Parser;


public class Basic{
	
//  Translates given file into a list of Tokens
//  and then prints out the list for debugging.
	public static void main(String[] args) throws IOException, lexer.Lexer.InvalidCharacterException{

		if(args.length != 1)
			throw new IOException();
		String filename = args[0];

		
		Lexer lexer = new Lexer(filename);
		LinkedList<Token> tokensList = lexer.lex();

		for(Token t: tokensList) {
			System.out.print(t.toString());
		}
		Parser parser = new Parser(tokensList);
		parser.parse();
	}

}