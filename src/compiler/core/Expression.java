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
	public Expression(String name) {
		type = new Type("UNKNOWN");
	}
	
	public Type getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}

	public void setType(Type type){
        this.type = type;
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

    public boolean isString() {
        System.out.println(getType().getName());
        return getType().getName().equalsIgnoreCase("String");
    }

	public String toString(){
		return "Expression of type; " + getType();
	}
	public String getAssemblyValue() {
		return this.value;
	}
}
