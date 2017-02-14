package gr.uoa.di.madgik.workflow.adaptor.search.utils.converters;

import gr.uoa.di.madgik.execution.plan.element.filter.IObjectConverter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

/**
 * 
 * @author gerasimos.farantatos  - DI NKUA
 *
 */
public class StatsConverter implements IObjectConverter 
{
	public Object Convert(String serialization) throws Exception
	{
		try
		{
			if(serialization==null || serialization.trim().length()==0) throw new Exception("Cannot convert null or empty value ("+serialization+")");
			StatsContainer stats = new StatsContainer();
			stats.fromXML(serialization);
			return stats;
		}catch(Exception ex)
		{
			throw new Exception(serialization,ex);
		}
	}

	public String Convert(Object o) throws Exception
	{
		if(!(o instanceof StatsContainer)) throw new Exception("Can not handle provided object");
		return ((StatsContainer)o).toXML();
	}
	
//	public static void main(String[] args) throws Exception {
//		RecordGenerationPolicy a = RecordGenerationPolicy.Concatenate;
//		EnumConverter conv = new EnumConverter();
//		String ser = conv.Convert(a);
//		System.out.println(ser);
//		Object o = conv.Convert(ser);
//		System.out.println(o.getClass().getName());
//		System.out.println((RecordGenerationPolicy)o);
//	}
}
