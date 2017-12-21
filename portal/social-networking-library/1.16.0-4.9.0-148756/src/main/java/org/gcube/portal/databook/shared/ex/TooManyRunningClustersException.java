package org.gcube.portal.databook.shared.ex;

@SuppressWarnings("serial")
public class TooManyRunningClustersException extends Exception {
	public TooManyRunningClustersException(String message) {
		super(message);
	}
}