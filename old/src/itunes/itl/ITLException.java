package itunes.itl;

public class ITLException extends Exception {

	/**
	 * Generated SVUID
	 */
	private static final long serialVersionUID = 3032059647076593550L;

	private String cause;
	
	public ITLException(String cause) {
		this.cause = cause;
	}
	
	@Override
	public String getMessage() {
		return "ITLException : " + cause;
	}
}
