package gr.uoa.di.madgik.environment.jms;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

public class JMSProvider implements IJMSProvider {
	
	private static IJMSProvider StaticProvider = null;
	private static final Object lockMe = new Object();
	private static String fallback;
	
	private IJMSProvider Provider = null;
	private static EnvHintCollection InitHints = null;
	
	public static IJMSProvider Init(String ProviderName, EnvHintCollection Hints, String fallback) throws EnvironmentValidationException {
		JMSProvider.fallback = fallback;
		try {
			synchronized(JMSProvider.lockMe) {
				if (JMSProvider.StaticProvider == null) {
					if(ProviderName.equals(JMSProvider.class.getName())) throw new EnvironmentValidationException("Class "+JMSProvider.class.getName()+" cannot be defined as provider");
					Class<?> c=Class.forName(ProviderName);
					Object o=c.newInstance();
					if(!(o instanceof IJMSProvider)) throw new EnvironmentValidationException("");
					JMSProvider prov=new JMSProvider();
					prov.Provider=(IJMSProvider)o;
					prov.InitHints=Hints;
					JMSProvider.StaticProvider=prov;
				}
			}
			return JMSProvider.StaticProvider;
		}catch(Exception ex)
		{
			if (fallback == null || fallback.isEmpty())
				throw new EnvironmentValidationException("Could not initialize JMS Provider", ex);
			return null;
		}
	}
	
	public static boolean IsInit()
	{
		synchronized(JMSProvider.lockMe)
		{
			return (JMSProvider.StaticProvider!=null);
		}		
	}
	
	private EnvHintCollection MergeHints(EnvHintCollection Hints)
	{
		if(this.InitHints==null && Hints==null) return new EnvHintCollection();
		if(this.InitHints==null) return Hints;
		else if(Hints==null) return this.InitHints;
		else return this.InitHints.Merge(Hints);
	}

	public String getJMSPRovider(EnvHintCollection Hints) throws EnvironmentInformationSystemException {
		if (Provider == null)
			return fallback;
		return this.Provider.getJMSPRovider(InitHints);
	}
	
	public static String getJMSPRovider() throws EnvironmentInformationSystemException {
		if (StaticProvider == null)
			return fallback;
		return StaticProvider.getJMSPRovider(InitHints);
	}

}
