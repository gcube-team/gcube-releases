package gr.uoa.di.madgik.environment.reporting;

import java.util.Map;

import gr.uoa.di.madgik.environment.exception.EnvironmentReportingException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

public class ReportingFrameworkProvider implements IReportingFrameworkProvider {

	private static IReportingFrameworkProvider StaticProvider = null;
	private static final Object lockMe = new Object();
	
	private IReportingFrameworkProvider Provider = null;
	private EnvHintCollection InitHints = null;
	
	public static IReportingFrameworkProvider Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException {
		try {
			synchronized(ReportingFrameworkProvider.lockMe) {
				if(ReportingFrameworkProvider.StaticProvider == null) {
					Object o;
					if(ProviderName.equals(ReportingFrameworkProvider.class.getName())) throw new EnvironmentValidationException("Class " + ReportingFrameworkProvider.class.getName() + " cannot be defined as reporting provider");
					if(ProviderName == null || ProviderName.trim().equals(""))
						o = new NoReportingFrameworkProvider();
					else {
						Class<?> c = Class.forName(ProviderName);
						o = c.newInstance();
					}
					if(!(o instanceof IReportingFrameworkProvider)) throw new EnvironmentValidationException("Class" + ProviderName + " is not a reporting provider");
					ReportingFrameworkProvider prov = new ReportingFrameworkProvider();
					prov.Provider = (IReportingFrameworkProvider)o;
					prov.InitHints = Hints;
					prov.SessionInit(Hints);
					ReportingFrameworkProvider.StaticProvider = prov;
				}
			}
			return ReportingFrameworkProvider.StaticProvider;
		}catch(Exception ex) {
			throw new EnvironmentValidationException("Could not initialize Reporting Provider", ex);
		}
	}
	
	public static boolean IsInit() {
		synchronized(ReportingFrameworkProvider.lockMe) {
			return (ReportingFrameworkProvider.StaticProvider != null);
		}		
	}

	public void SessionInit(EnvHintCollection Hints) throws EnvironmentReportingException {
		if(this.Provider == null) throw new EnvironmentReportingException("Reporting Provider not initialized");
		this.Provider.SessionInit(this.MergeHints(Hints));
	}
	
	public void Send(String messageType, Map<String, Object> messageParameters, EnvHintCollection hints) throws EnvironmentReportingException  {
		if(this.Provider == null) throw new EnvironmentReportingException("Reporting Provider not initialized");
		this.Provider.Send(messageType, messageParameters, this.MergeHints(hints));
		
	}
	
	private EnvHintCollection MergeHints(EnvHintCollection Hints)
	{
		if(this.InitHints==null && Hints==null) return new EnvHintCollection();
		if(this.InitHints==null) return Hints;
		else if(Hints==null) return this.InitHints;
		else return this.InitHints.Merge(Hints);
	}
}
