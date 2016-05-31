package gr.uoa.di.madgik.workflow.adaptor.search.utils.converters;

import org.w3c.dom.Document;
import gr.uoa.di.madgik.execution.plan.element.filter.IObjectConverter;
import gr.uoa.di.madgik.commons.utils.XMLUtils;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class EnumConverter implements IObjectConverter 
{
	public Object Convert(String serialization) throws Exception
	{
		try
		{
			if(serialization==null || serialization.trim().length()==0) throw new Exception("Cannot convert null or empty value ("+serialization+")");
			Document doc=XMLUtils.Deserialize(serialization);
			Class enumType = Class.forName(XMLUtils.GetAttribute(doc.getDocumentElement(), "type"));
			if(!enumType.isEnum())
				throw new Exception("Cannot convert arbitrary class to enum (" + serialization + ")");
			Enum<?> value = Enum.valueOf(enumType, XMLUtils.GetAttribute(doc.getDocumentElement(), "value"));
		return value;
		}catch(Exception ex)
		{
			throw new Exception(serialization,ex);
		}
	}

	public String Convert(Object o) throws Exception
	{
		if(!o.getClass().isEnum()) throw new Exception("Can not handle provided object");
		return "<enumeration type=\""+o.getClass().getName()+"\" value=\""+((Enum<?>)o).toString()+"\"/>";
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
