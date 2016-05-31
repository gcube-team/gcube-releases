package gr.uoa.di.madgik.environment.reporting;

import java.util.Map;

import gr.uoa.di.madgik.environment.IEnvironmentProvider;
import gr.uoa.di.madgik.environment.exception.EnvironmentReportingException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

public interface IReportingFrameworkProvider extends IEnvironmentProvider {

	public void SessionInit(EnvHintCollection Hints) throws EnvironmentReportingException;
	
	public void Send(String messageType, Map<String, Object> messageParameters, EnvHintCollection hints) throws EnvironmentReportingException;
}
