package independent.itunes.itl;

public class ITLException extends Exception {

	/**
	 * Generated SVUID
	 */
	private static final long serialVersionUID = 3032059647076593550L;

	private String message;
	
	public ITLException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return "ITLException : " + message;
	}
}
