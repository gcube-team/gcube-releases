package org.gcube.searchsystem.planning.exception;

public class CQLTreeSyntaxException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
     * Default constructor.
     */
    public CQLTreeSyntaxException() {
        super();
    }

    /**
     * Constructs with message.
     * @param message - The message to throw in the exception
     */
    public CQLTreeSyntaxException(final String message) {
        super(message);
    }
    
}
