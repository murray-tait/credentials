package uk.co.urbanfortress.aws;

public class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Exception exception) {
		super(exception);
	}

}
