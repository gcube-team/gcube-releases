package gr.uoa.di.madgik.environment.is.elements.invocable.context;

public class InvocableContext
{
	public enum ProgressReportingProvider
	{
		Local,
		TCP
	}
	
	public boolean Supported=false;
	public InvocableContextgRSProxy ProxygRS=new InvocableContextgRSProxy();
	public boolean ReportsProgress=false;
	public ProgressReportingProvider ProgressProvider=ProgressReportingProvider.Local;
	public boolean KeepAlive=false;
}
