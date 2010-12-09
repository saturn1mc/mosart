package dependent.gui;

public class MosArtException extends Exception {

	/**
	 * Auto-generated SVUID
	 */
	private static final long serialVersionUID = -5884631524663525543L;

	private String message;
	
	public MosArtException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return "MosArtException : " + message;
	}
}
