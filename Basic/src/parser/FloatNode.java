package parser;

public class FloatNode extends Node{
	private double number;
	@Override
	public String toString() {
		return ""+number;
	}
	public double getNumber() {
		return number;
	}
	public FloatNode(double num) {
		number = num;
	}
}	