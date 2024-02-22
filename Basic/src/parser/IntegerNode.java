package parser;

//Represents an integer
public class IntegerNode extends Node {
	private int number;
	@Override
	public String toString() {
		return ""+number;
	}
	public int getNumber() {
		return number;
	}
	public IntegerNode(int num) {
		number = num;
	}
}