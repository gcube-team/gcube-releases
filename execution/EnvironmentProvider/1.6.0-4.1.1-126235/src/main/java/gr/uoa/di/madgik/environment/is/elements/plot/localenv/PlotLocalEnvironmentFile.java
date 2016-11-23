package gr.uoa.di.madgik.environment.is.elements.plot.localenv;

public class PlotLocalEnvironmentFile
{
	public enum LocalEnvironmentFileDirection
	{
		In,
		Out
	}
	
	public LocalEnvironmentFileDirection Direction=LocalEnvironmentFileDirection.In;
	public String Name=null;
	public String Location=null;
	public boolean CleanUp=true;
	public boolean IsExecutable=false;
}
