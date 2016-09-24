package compiler.exceptions;

public class InvalidTypeAssignmentException extends Exception {

	private static final long serialVersionUID = 1L;
	private String msg;
	
	public InvalidTypeAssignmentException(String msg) {
		this.setMsg(msg);
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}


}
