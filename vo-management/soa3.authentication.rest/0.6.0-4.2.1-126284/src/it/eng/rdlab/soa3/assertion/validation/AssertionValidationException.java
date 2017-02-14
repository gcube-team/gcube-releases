package it.eng.rdlab.soa3.assertion.validation;

public class AssertionValidationException extends Exception{

	/**
	 * Exception thrown for error handling during signature validation
	 */
	private static final long serialVersionUID = 1L;
	
	
	public AssertionValidationException(Exception e) {
		super(e.getMessage(), e);
	}

}
