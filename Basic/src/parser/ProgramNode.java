package parser;
import java.util.LinkedList;
//Represents a Basic program, using a list of nodes to represent the instructions of the program
public class ProgramNode extends Node{
	LinkedList<Node> instructions;
	public ProgramNode(LinkedList<Node> nodes) {
		this.instructions = nodes;
		
	}
	@Override
	public String toString() {
		String s = "";
		for(Node n: instructions) {
			if (n != null) {
			s += n.toString();
			}
		}
		return s;
	}
	
}