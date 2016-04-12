package org.gcube.searchsystem.planning.exception;

public class CQLUnsupportedException extends Exception{

	private static final long serialVersionUID = 1L;

	/**
     * Default constructor.
     */
    public CQLUnsupportedException() {
        super();
    }

    /**
     * Constructs with message.
     * @param message - The message to throw in the exception
     */
    public CQLUnsupportedException(final String message) {
        super(message);
    }
    
}
