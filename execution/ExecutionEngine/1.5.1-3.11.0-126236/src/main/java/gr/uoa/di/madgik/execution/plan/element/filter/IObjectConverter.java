package gr.uoa.di.madgik.execution.plan.element.filter;

public interface IObjectConverter
{
	public Object Convert(String serialization) throws Exception;
	
	public String Convert(Object o) throws Exception;
}
