package gr.uoa.di.madgik.environment.state;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

public interface ILocalStateHandlingProvider {
	
	public void updateWSResource(String resourceId, String resourceType, Object resource, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public Object getWSResource(String resourceId, String forName, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public void deleteWSResource(String resourceId, String resourceType, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
}
