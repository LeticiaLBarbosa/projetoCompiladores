package compiler.core;

import compiler.exceptions.InvalidFunctionException;

import java.util.ArrayList;
import java.util.List;


public class Function extends ScopedEntity{
	

	Type declaredReturnType;
	Type returnType;
	List<Parameter> params;
	
	public Function(String name, ArrayList<Parameter>params){
        super(name);
        if(params != null){
			this.params = params;
		}else{
			this.params = new ArrayList<Parameter>();
		}
	}

	public boolean isReturnValid(){
		return this.declaredReturnType.equals(this.returnType);
	}
	public Type getDeclaredReturnType() {
		return declaredReturnType;
	}

	public Type getReturnType(){
		return returnType;
	}

	public List<Parameter> getParams() {
		return this.params;
	}
	
	public void setDeclaredReturnedType(Type type) {
		this.declaredReturnType = type;
	}

	public void validateReturnedType() throws InvalidFunctionException { // Checks if the function returned what it was supposed to..
		if (!returnType.equals(declaredReturnType))
			throw new InvalidFunctionException("Function " + getName() + " was supposed to return " + declaredReturnType);
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Function)) return false;
		Function f= (Function) obj;
		if(!f.getName().equals(getName()))return false;
		if(f.getParams().size() != getParams().size()) return false;
		
		for(int i=0;i<getParams().size();i++){
			if(! f.getParams().get(i).getType().equals(getParams().get(i).getType())) return false;
		}
		
		return true;
	}


}
