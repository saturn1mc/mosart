package dependent.gui;

public class MosArtException extends Exception {

	/**
	 * Auto-generated SVUID
	 */
	private static final long serialVersionUID = -5884631524663525543L;

	private String cause;
	
	public MosArtException(String cause) {
		this.cause = cause;
	}
	
	@Override
	public String getMessage() {
		return "ITLException : " + cause;
	}
}
