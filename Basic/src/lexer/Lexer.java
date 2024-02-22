package lexer;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Groups a given string into a list of words and numbers.
//Tracks line number and line position for generating helpful error messages.

public class Lexer{
	int lineNumber = 0;
	int position = 0;
	HashMap<String, TokenType> knownWords;
	HashMap<String, TokenType> singleSymbols;
	HashMap<String, TokenType> multiSymbols;
	CodeHandler codeHandler;
	public Lexer(String filename) throws IOException, InvalidCharacterException {
		initializeHashMap();
		this.codeHandler = new CodeHandler(filename);
	}
	//Alternate constructor for testing directly with a given string rather than reading a string from a file

    public Lexer(String testText, int test) {
    	initializeHashMap();
		this.codeHandler = new CodeHandler(testText, test);
	}
    
	Pattern letterPattern = Pattern.compile("[a-zA-Z]");
	Pattern digitPattern = Pattern.compile("[0-9]");
	public LinkedList<Token> lex() throws InvalidCharacterException{
		LinkedList<Token> tokenList = new LinkedList<Token>();
		//Loops through the document and creates tokens to add them to the list, 
		//as well as checking that each character in the document is valid.
		
		while(!codeHandler.isDone()){
			char next = codeHandler.GetChar();
			
			if(Pattern.matches("[^a-zA-Z_0-9\n\"<>=()+/* ]", ""+next)) {
				if(next != '-') {
				  throw new InvalidCharacterException("Invalid character <"+next+"> at line "+lineNumber+" position "+position);
				}
			}
			
			//Checks if the char is a letter or digit
			Matcher letterMatcher = letterPattern.matcher(""+next);
			Matcher digitMatcher = digitPattern.matcher(""+next);
			boolean isLetter = letterMatcher.matches();
			boolean isDigit = digitMatcher.matches();
			
			switch (next){
				case ' ':
					position++;
					break;
				case'\r':
					break;
				case '\n':
					tokenList.add(new Token(TokenType.ENDOFLINE, lineNumber, position));
					lineNumber++;
					position = 0;
					break;
				case '<','>','=','(',')','+','-','*','/':
					if(codeHandler.isDone()) {
						tokenList.add(new Token(singleSymbols.get(""+next), lineNumber, position,next+""));
					} else {
					    tokenList.add(processSymbol(codeHandler, next));
					}
					position++;
					break;
				case '"':
					tokenList.add(processStringLiteral(codeHandler, next));
					break;
				default:
				//Directly adds the character to the token list if it is the final character in the document,
				//otherwise collects the rest of the word/number and adds it to the list.
					if(isLetter) {
						if(codeHandler.isDone()) {
							tokenList.add(new Token(TokenType.WORD, lineNumber, position,next+""));
						} else {
						    tokenList.add(processWord(codeHandler, next));
						}
						position++;
					}
					if (isDigit) {
						if(codeHandler.isDone()) {
							tokenList.add(new Token(TokenType.NUMBER, lineNumber, position,next+""));
						} else {
							tokenList.add(processDigit(codeHandler, next));
						}
						position++;
					}
			}
		}
		tokenList.add(new Token(TokenType.ENDOFLINE, lineNumber, position));
		return tokenList;
	}
	//processWord and processNumber collect characters until they hit a non-word/non-number character,
	//and then skip ahead in the document equal to the length of the word/number and return the word/number as a Token
	//processWord returns a label token if the word ends in ':'
	private Token processWord(CodeHandler codeHandler, char next) {
		int length = 0;
		String word =  next+"";
		if (codeHandler.isDone()) {
			return new Token(TokenType.WORD, lineNumber, position, word);
		}
		while(Pattern.matches("[a-zA-Z_0-9]", ""+codeHandler.Peek(length))) {
			word += codeHandler.Peek(length);
			length++;
		}

		if (Pattern.matches("[%$]", ""+codeHandler.Peek(length))) {
			word += codeHandler.Peek(length);
			length++;
		}
		
		position += length;
		if (codeHandler.Peek(length)==':') {
			word+=":";
			length++;
			position++;
			codeHandler.Swallow(length);
			return new Token(TokenType.LABEL, lineNumber, position, word);
		}
		codeHandler.Swallow(length);
		if(knownWords.containsKey(word)) {
			return new Token(knownWords.get(word), lineNumber, position);
		}
		
		return new Token(TokenType.WORD, lineNumber, position, word);
	}
	private Token processDigit(CodeHandler codeHandler, char next) {
		int length = 0;

		String number =  next+"";
		
		while(Pattern.matches("[0-9]", ""+codeHandler.Peek(length))) {
			number += codeHandler.Peek(length);
			length++;
		}
		if (Pattern.matches("\\.", ""+codeHandler.Peek(length))) {
			number += ".";
			length++;
			while(Pattern.matches("[0-9]", ""+codeHandler.Peek(length))) {
				number += codeHandler.Peek(length);
				length++;
			}
		}
		position += length;
		codeHandler.Swallow(length);
		return new Token(TokenType.NUMBER, lineNumber, position, number);
	}
	//processStringLiteral collects the characters after the opening quote until it reaches the closing quote. 
	//It then returns the characters collected as a string and skips forward by 1 + length in order to skip over the closing quote in the codeHandler.
	private Token processStringLiteral(CodeHandler codeHandler, char next) {
		int length = 0;

		String stringLiteral = "";
		if (codeHandler.isDone()) {
			return new Token(TokenType.STRINGLITERAL, lineNumber, position, "");
		}
		while(Pattern.matches("[^\"]", ""+codeHandler.Peek(length))){
			//in case of escaped quotes (\"), skip over the backslash
			if (codeHandler.Peek(length)=='\\') {
				if (codeHandler.Peek(length+1)=='"') {
					length++;
				}
			}
			stringLiteral += codeHandler.Peek(length);
			length++;
		}
		
		position += length+1;
		codeHandler.Swallow(length+1);
		return new Token(TokenType.STRINGLITERAL, lineNumber, position, stringLiteral);
	}

	//processSymbol first checks if the symbol is part of a valid multi-character symbol, then returns a token of the given symbol.
	private Token processSymbol(CodeHandler codeHandler, char next) {

		String symbol =  ""+next;
		if (Pattern.matches("[<>]",symbol)){
			if(Pattern.matches("[=>]",""+codeHandler.Peek(0))) {	
				if(!(next == '>'&&(codeHandler.Peek(0) == '>'))) {
					symbol += codeHandler.Peek(0);
					codeHandler.Swallow(1);
					return new Token(multiSymbols.get(symbol), lineNumber, position);
				}
			}
		}
		return new Token(singleSymbols.get(symbol), lineNumber, position);
		
	}
	//initializeHashMap creates HashMaps and populates them with the known words and symbols so that
	//the words and symbols read by the lex method can be checked with them
	private void initializeHashMap() {
		this.knownWords = new HashMap<String, TokenType>();
		this.singleSymbols = new HashMap<String, TokenType>();
		this.multiSymbols = new HashMap<String, TokenType>();
		multiSymbols.put("<=", TokenType.LESSEQUAL);
		multiSymbols.put(">=", TokenType.GREATEREQUAL);
		multiSymbols.put("<>", TokenType.NOTEQUALS);
		singleSymbols.put("=", TokenType.EQUALS);
		singleSymbols.put("<", TokenType.LESSTHAN);
		singleSymbols.put(">", TokenType.GREATERTHAN);
		singleSymbols.put("(", TokenType.LPAREN);
		singleSymbols.put(")", TokenType.RPAREN);
		singleSymbols.put("+", TokenType.ADD);
		singleSymbols.put("-", TokenType.SUBTRACT);
		singleSymbols.put("*", TokenType.MULTIPLY);
		singleSymbols.put("/", TokenType.DIVIDE);
		knownWords.put("PRINT", TokenType.PRINT);
		knownWords.put("READ", TokenType.READ);
		knownWords.put("INPUT", TokenType.INPUT);
		knownWords.put("DATA", TokenType.DATA);
		knownWords.put("GOSUB", TokenType.GOSUB);
		knownWords.put("FOR", TokenType.FOR);
		knownWords.put("TO", TokenType.TO);
		knownWords.put("STEP", TokenType.STEP);
		knownWords.put("NEXT", TokenType.NEXT);
		knownWords.put("RETURN", TokenType.RETURN);
		knownWords.put("IF", TokenType.IF);
		knownWords.put("THEN", TokenType.THEN);
		knownWords.put("FUNCTION", TokenType.FUNCTION);
		knownWords.put("WHILE", TokenType.WHILE);
		knownWords.put("END", TokenType.END);
	}
	
	}
