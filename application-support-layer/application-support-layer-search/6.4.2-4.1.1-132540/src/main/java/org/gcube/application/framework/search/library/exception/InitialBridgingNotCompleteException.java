package org.gcube.application.framework.search.library.exception;

public class InitialBridgingNotCompleteException extends Exception{

	private static final long serialVersionUID = 1L;

	public InitialBridgingNotCompleteException(Throwable cause) {
		super("Initial bridging hasn't been completed yet.", cause);
	}

}
