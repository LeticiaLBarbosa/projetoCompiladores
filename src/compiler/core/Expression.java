package compiler.core;


public class Expression {
	private Type type;
	private String value;
	private String context;
	
	public Expression(Type t) {
		this.type = t;
	}
	
	public Expression(Type t, String value) {
		this.type = t;
		this.value = value;
		this.context = "";
	}
	
	public Type getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getContext(){
		return this.context;
	}
	
	public void setContext(String context){
		this.context = context;
	}
	public boolean isNumeric() {
		return    getType().getName().equals("int")
				||getType().getName().equals("float")
				||getType().getName().equals("long")
				||getType().getName().equals("double");
	}
	
	public String toString(){
		return "Expression of type; " + getType();
	}
}
