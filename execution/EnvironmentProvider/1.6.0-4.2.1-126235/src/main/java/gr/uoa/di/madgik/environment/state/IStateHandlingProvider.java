package gr.uoa.di.madgik.environment.state;

import java.util.List;

import gr.uoa.di.madgik.environment.IEnvironmentProvider;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.state.elements.Endpoint;
import gr.uoa.di.madgik.environment.state.elements.StateResource;

public interface IStateHandlingProvider extends IEnvironmentProvider{

	public String CreateNewWSResource(String ForName, Object Resource, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public void UpdateWSResource(String ID, String ForName, Object resource, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public Object GetWSResourceByIDForServiceClass(String ID, String ForName, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public List<Object> GetWSResourcesForServiceClass(String ForName, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public StateResource GetXMLWSResourceByIDForServiceClass(String ID, String ForName, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	public List<StateResource> GetXMLWSResourcesForServiceClass(String ForName, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public void DeleteWSResource(String ID, String ForName, EnvHintCollection Hints) throws EnvironmentInformationSystemException;
	
	public Endpoint newEndpoint(String endpoint) throws EnvironmentInformationSystemException;
}
