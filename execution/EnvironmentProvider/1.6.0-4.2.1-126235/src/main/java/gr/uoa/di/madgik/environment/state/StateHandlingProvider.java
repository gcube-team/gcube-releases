package gr.uoa.di.madgik.environment.state;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.state.elements.Endpoint;
import gr.uoa.di.madgik.environment.state.elements.StateResource;

public class StateHandlingProvider implements IStateHandlingProvider {
	
	private static IStateHandlingProvider StaticProvider=null;
	private static final Object lockMe=new Object();

	private IStateHandlingProvider Provider=null;
	private EnvHintCollection InitHints=null;

	public static IStateHandlingProvider Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException
	{
		try
		{
			synchronized(StateHandlingProvider.lockMe)
			{
				if(StateHandlingProvider.StaticProvider==null)
				{
//					StringBuffer classpath = new StringBuffer();
//					ClassLoader applicationClassLoader = StateHandlingProvider.class.getClassLoader();
//				     if (applicationClassLoader == null) {
//				         applicationClassLoader = ClassLoader.getSystemClassLoader();
//				     }
//				     URL[] urls = ((URLClassLoader)applicationClassLoader).getURLs();
//				      for(int i=0; i < urls.length; i++) {
//				          classpath.append(urls[i].getFile()).append("\r\n");
//				      }  
//				      
//				      System.out.println("Classpath: " + classpath.toString());
					if(ProviderName.equals(StateHandlingProvider.class.getName())) throw new EnvironmentValidationException("Class "+StateHandlingProvider.class.getName()+" cannot be defined as provider");
					System.out.println("Provider name iss: " + ProviderName);
					Class<?> c=Class.forName(ProviderName);
					Object o=c.newInstance();
					if(!(o instanceof IStateHandlingProvider)) throw new EnvironmentValidationException("");
					StateHandlingProvider prov=new StateHandlingProvider();
					prov.Provider=(IStateHandlingProvider)o;
					prov.InitHints=Hints;
					StateHandlingProvider.StaticProvider=prov;
				}
			}
			return StateHandlingProvider.StaticProvider;
		}catch(Exception ex)
		{
			ex.printStackTrace();
			throw new EnvironmentValidationException("Could not initialize Information System Provider", ex);
		}
	}
	
	public static boolean IsInit()
	{
		synchronized(StateHandlingProvider.lockMe)
		{
			return (StateHandlingProvider.StaticProvider!=null);
		}		
	}
	
	private EnvHintCollection MergeHints(EnvHintCollection Hints)
	{
		if(this.InitHints==null && Hints==null) return new EnvHintCollection();
		if(this.InitHints==null) return Hints;
		else if(Hints==null) return this.InitHints;
		else return this.InitHints.Merge(Hints);
	}

	@Override
	public String CreateNewWSResource(String ForName, Object Resource,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		return this.Provider.CreateNewWSResource(ForName, Resource, this.MergeHints(Hints));
	}

	@Override
	public void UpdateWSResource(String ID, String ForName, Object resource,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		this.Provider.UpdateWSResource(ID, ForName, resource, this.MergeHints(Hints));
		
	}

	@Override
	public Object GetWSResourceByIDForServiceClass(String ID, String ForName,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		return this.Provider.GetWSResourceByIDForServiceClass(ID, ForName, this.MergeHints(Hints));
	}
	
	@Override
	public List<Object> GetWSResourcesForServiceClass(String ForName, EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		return this.Provider.GetWSResourcesForServiceClass(ForName, this.MergeHints(Hints));
	}
	
	@Override
	public StateResource GetXMLWSResourceByIDForServiceClass(String ID, String ForName, EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		return this.Provider.GetXMLWSResourceByIDForServiceClass(ID, ForName, this.MergeHints(Hints));
	}
	
	@Override
	public List<StateResource> GetXMLWSResourcesForServiceClass(String ForName, EnvHintCollection Hints) throws EnvironmentInformationSystemException
	{
		return this.Provider.GetXMLWSResourcesForServiceClass(ForName, this.MergeHints(Hints));
	}

	@Override
	public void DeleteWSResource(String ID, String ForName,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		this.Provider.DeleteWSResource(ID, ForName, this.MergeHints(Hints));
	}
	
	@Override
	public Endpoint newEndpoint(String endpoint) throws EnvironmentInformationSystemException
	{
		return this.Provider.newEndpoint(endpoint);
	}
}
