package gr.uoa.di.madgik.workflow.utils;

public class KeyUtils
{
	public static String KeyOfEnvironmentVariable(String VariableName)
	{
		return VariableName;
	}
	
	public static String KeyOfEnvironmentFile(String FileName)
	{
		return FileName;
	}
	
	public static String KeyOfStdIn()
	{
		return "StdIn";
	}
	
	public static String KeyOfStdOut()
	{
		return "StdOut";
	}
	
	public static String KeyOfStdErr()
	{
		return "StdErr";
	}
	
	public static String KeyOfStdExit()
	{
		return "StdExit";
	}
	
	public static String KeyOfEndPoint()
	{
		return "EndPoint";
	}
	
	public static String KeyOfShellArgument(String ArgumentName)
	{
		return ArgumentName;
	}
	
	public static String KeyOfPojoArgument(int MethodOrder,String ArgumentName)
	{
		return Integer.toString(MethodOrder)+"#"+ArgumentName;
	}
	
	public static String KeyOfWSArgument(int MethodOrder)
	{
		return Integer.toString(MethodOrder);
	}
	
	public static String KeyOfInvocationReturn(int InvocationOrder)
	{
		return Integer.toString(InvocationOrder);
	}
}
