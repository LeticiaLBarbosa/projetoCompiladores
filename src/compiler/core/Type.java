package compiler.core;

public class Type{

	private String typeName;

	public Type(String typeName) {
		this.typeName = typeName;
	}

	public String getName() {
		return this.typeName;
	}

	public void setName(String typeName){
		this.typeName = typeName;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Type))
			return false;
		return this.getName().toLowerCase().equals(((Type) obj).getName().toLowerCase());
	}
	
	@Override
	public String toString(){
		return getName();
	}

}