package gr.uoa.di.madgik.environment.jms;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

public interface IJMSProvider {
	public String getJMSPRovider(EnvHintCollection Hints) throws EnvironmentInformationSystemException;
}
