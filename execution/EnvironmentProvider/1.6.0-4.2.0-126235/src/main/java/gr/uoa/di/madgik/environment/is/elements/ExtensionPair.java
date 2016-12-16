package gr.uoa.di.madgik.environment.is.elements;

import java.io.Serializable;

public class ExtensionPair implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final String ORIGINAL_GLOBUS_LOCATION="ORIGINAL_GLOBUS_LOCATION";
	public static final String HADOOP_LOCATION="HADOOP_LOCATION";
	public static final String GLITE_LOCATION="GLITE_LOCATION";
	public static final String CONDOR_LOCATION="CONDOR_LOCATION";
	
	public String Key;
	public String Value;
	
	public ExtensionPair(){}
	public ExtensionPair(String Key, String Value)
	{
		this.Key=Key;
		this.Value=Value;
	}
}
