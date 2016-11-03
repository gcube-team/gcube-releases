package gr.uoa.di.madgik.environment.exception;

public class EnvironmentReportingException extends EnvironmentException {

	private static final long serialVersionUID = -8424385337535984162L;
	
	public EnvironmentReportingException(String Message) {
		super(Message);
	}

	public EnvironmentReportingException(String Message,Throwable cause) {
		super(Message,cause);
	}
}
