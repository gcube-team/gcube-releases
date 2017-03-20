package gr.uoa.di.madgik.reporting;

import java.util.Map;

import gr.uoa.di.madgik.environment.exception.EnvironmentReportingException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.reporting.IReportingFrameworkProvider;
import gr.uoa.di.madgik.environment.reporting.ReportingFrameworkProvider;

public class ReportingFramework
{
	private static IReportingFrameworkProvider Provider=null;
	private static Object lockMe=new Object();

	public static void Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException
	{
		synchronized (ReportingFramework.lockMe)
		{
			if(ReportingFramework.Provider==null) ReportingFramework.Provider = ReportingFrameworkProvider.Init(ProviderName, Hints);
		}
	}
	
	public static void Send(String MessageType, Map<String, Object> MessageParameters, EnvHintCollection hints) throws EnvironmentReportingException
	{
		ReportingFramework.Provider.Send(MessageType, MessageParameters, hints);
	}
	
}
