package compiler.exceptions;

public class InvalidVariableException extends Exception {
	
	private static final long serialVersionUID = 4621490192670023409L;

	public InvalidVariableException(String message){
		super(message);
	}
}
