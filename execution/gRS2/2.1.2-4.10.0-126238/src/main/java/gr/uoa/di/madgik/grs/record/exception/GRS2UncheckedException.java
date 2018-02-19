package gr.uoa.di.madgik.grs.record.exception;


public class GRS2UncheckedException extends RuntimeException{
	
	public GRS2UncheckedException() {
		super();
	}

	public GRS2UncheckedException(String message) {
		super(message);
	}

	public GRS2UncheckedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	private static final long serialVersionUID = 1L;

}
