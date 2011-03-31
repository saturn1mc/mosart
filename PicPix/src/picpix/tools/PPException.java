package picpix.tools;

public class PPException extends Exception {

	/**
	 * Auto-generated SVUID
	 */
	private static final long serialVersionUID = -5884631524663525543L;

	private String message;
	
	public PPException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return "MosArtException : " + message;
	}
}
