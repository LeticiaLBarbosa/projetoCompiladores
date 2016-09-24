package compiler.core;

public class Variable implements Parameter{
	
	Type type;
	String identifier;
	Expression value;
	
	public Variable(String identifier, Type type){
		this.type = type;
		this.identifier = identifier;
	}
	
	public Variable(String identifier, Type type, Expression value){
		this.type = type;
		this.identifier = identifier;
		this.value = value;
	}

	public Type getType() {
		return this.type;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Expression getValue() {
		return value;
	}

	public void setValue(Expression value) {
		this.value = value;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	
	
	
	@Override
	public String toString(){
		return this.identifier + " of type: " + getType().getName();
	}

}
