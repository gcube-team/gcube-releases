package gr.uoa.di.madgik.environment.reporting;

import gr.uoa.di.madgik.environment.exception.EnvironmentReportingException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

import java.util.Map;

/**
 * A dummy implementation used in case no reporting provider is available for a specific environment
 * 
 * 
 * @author gerasimos.farantatos
 *
 */
public class NoReportingFrameworkProvider implements IReportingFrameworkProvider {

	public void SessionInit(EnvHintCollection Hints) throws EnvironmentReportingException {
		throw new EnvironmentReportingException("Could not initialize session: This environment does not support a reporting provider");
	}
	
	public void Send(String messageType, Map<String, Object> messageParameters, EnvHintCollection hints) throws EnvironmentReportingException {
		throw new EnvironmentReportingException("Could not send reporting message: This environment does not support a reporting provider");
	}

}
