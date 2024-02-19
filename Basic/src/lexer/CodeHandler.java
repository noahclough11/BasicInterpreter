package lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//CodeHandler takes a file and converts into a string, and can 
//read through it character by character.
public class CodeHandler{
	
	private String document;
	private int index;
	
	//Constructor takes a given file and converts it into a String using readAllBytes
	public CodeHandler(String filename) throws IOException {
		Path myPath = Paths.get(filename);
		document = new String(Files.readAllBytes(myPath));
	}
	// Alternate constructor for testing directly with a given string rather than reading a string from a file
	public CodeHandler(String text, int test) {
		document = text;
	}
	
	//Peeks ahead a given number of characters in the document. Returns a newline if the peek goes past the end of the document
	public char Peek(int i) {
		if (index+i >= document.length()) {
			return '\n';
		}
		return document.charAt(index +i);
	}

	public String PeekString(int i) {
		String documentCopy = document;
		documentCopy = documentCopy.substring(index,index+i);
		return documentCopy;
	}
	//Returns the current character and then moves the index onto the next character
	public char GetChar() {
		return document.charAt(index++);
	}
	//Skips forward in the document
	public void Swallow(int i) {
		index += i;
	}

	public boolean isDone() {
		return index >= document.length();
	}
	public boolean isOnLastCharacter() {
		return (index == document.length());
	}
	public String Remainder() {
		String documentCopy = document;
		return documentCopy.substring(index);
	}
	//getIndex returns the index for testing that it is in the correct place after using other methods
	public int getIndex() {
		return this.index;
	}

}