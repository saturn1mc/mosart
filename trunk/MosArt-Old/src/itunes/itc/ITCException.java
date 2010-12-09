package itunes.itc;

public class ITCException extends Exception {
	/**
	 * Generated SVUID
	 */
	private static final long serialVersionUID = 3092953896661061360L;
	
	private String cause;
	
	public ITCException(String cause) {
		this.cause = cause;
	}
	
	@Override
	public String getMessage() {
		return "ITLException : " + cause;
	}
}
