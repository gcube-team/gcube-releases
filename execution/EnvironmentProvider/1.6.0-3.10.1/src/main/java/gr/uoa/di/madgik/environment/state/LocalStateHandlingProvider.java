package gr.uoa.di.madgik.environment.state;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

public class LocalStateHandlingProvider implements ILocalStateHandlingProvider {
	
	private static ILocalStateHandlingProvider StaticProvider = null;
	private static final Object lockMe = new Object();
	
	private ILocalStateHandlingProvider Provider = null;
	private EnvHintCollection InitHints = null;
	
	public static ILocalStateHandlingProvider Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException
	{
		try
		{
			synchronized(LocalStateHandlingProvider.lockMe)
			{
				if(LocalStateHandlingProvider.StaticProvider==null)
				{
					if(ProviderName.equals(LocalStateHandlingProvider.class.getName())) throw new EnvironmentValidationException("Class "+StateHandlingProvider.class.getName()+" cannot be defined as provider");
					Class<?> c=Class.forName(ProviderName);
					Object o=c.newInstance();
					if(!(o instanceof ILocalStateHandlingProvider)) throw new EnvironmentValidationException("");
					LocalStateHandlingProvider prov=new LocalStateHandlingProvider();
					prov.Provider=(ILocalStateHandlingProvider)o;
					prov.InitHints=Hints;
					LocalStateHandlingProvider.StaticProvider=prov;
				}
			}
			return LocalStateHandlingProvider.StaticProvider;
		}catch(Exception ex)
		{
			throw new EnvironmentValidationException("Could not initialize Information System Provider", ex);
		}
	}
	
	public static boolean IsInit()
	{
		synchronized(LocalStateHandlingProvider.lockMe)
		{
			return (LocalStateHandlingProvider.StaticProvider!=null);
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
	public void updateWSResource(String resourceId, String resourceType,
			Object resource, EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		this.Provider.updateWSResource(resourceId, resourceType, resource, Hints);
	}

	@Override
	public Object getWSResource(String resourceId, String forName,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		return this.Provider.getWSResource(resourceId, forName, Hints);
	}

	@Override
	public void deleteWSResource(String resourceId, String resourceType,
			EnvHintCollection Hints)
			throws EnvironmentInformationSystemException {
		this.Provider.deleteWSResource(resourceId, resourceType, Hints);
	}

}
