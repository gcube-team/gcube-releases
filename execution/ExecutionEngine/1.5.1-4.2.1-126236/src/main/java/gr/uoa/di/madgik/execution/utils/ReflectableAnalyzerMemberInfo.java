package gr.uoa.di.madgik.execution.utils;

import java.lang.reflect.Method;

public class ReflectableAnalyzerMemberInfo
{
	public Method getter=null;
	public Method setter=null;
	public String name=null;
	public Class<?> type=null;
	
	public ReflectableAnalyzerMemberInfo(){}
	public ReflectableAnalyzerMemberInfo(String name,Class<?> type,Method getter,Method setter)
	{
		this.getter=getter;
		this.setter=setter;
		this.name=name;
		this.type=type;
	}
}
