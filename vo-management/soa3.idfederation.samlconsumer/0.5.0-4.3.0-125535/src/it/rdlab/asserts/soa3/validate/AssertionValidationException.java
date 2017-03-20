package it.rdlab.asserts.soa3.validate;

public class AssertionValidationException extends Exception{

	/**
	 * Exception thrown for error handling during signature validation
	 */
	private static final long serialVersionUID = 1L;
	
	
	public AssertionValidationException(Exception e) {
		super(e.getMessage(), e);
	}

}
