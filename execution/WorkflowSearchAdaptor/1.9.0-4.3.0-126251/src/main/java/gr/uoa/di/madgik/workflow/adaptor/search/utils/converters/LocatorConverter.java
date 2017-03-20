package gr.uoa.di.madgik.workflow.adaptor.search.utils.converters;

import java.net.URI;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.plan.element.filter.IObjectConverter;

import org.w3c.dom.Document;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class LocatorConverter implements IObjectConverter 
{

		public Object Convert(String serialization) throws Exception
		{
			try
			{
				if(serialization==null || serialization.trim().length()==0) throw new Exception("Cannot convert null or empty value ("+serialization+")");
				Document doc=XMLUtils.Deserialize(serialization);
				URI value = new URI(XMLUtils.GetAttribute(doc.getDocumentElement(), "value"));
				return value;
			}catch(Exception ex)
			{
				throw new Exception(serialization,ex);
			}
		}

		public String Convert(Object o) throws Exception
		{
			if(!(o instanceof URI)) throw new Exception("Can not handle provided object");
			return "<locator value=\""+((URI)o).toString()+"\"/>";
		}
		
//		public static void main(String[] args) throws Exception {
//			URI a = new URI("http://www.example.com");
//			LocatorConverter conv = new LocatorConverter();
//			String ser = conv.Convert(a);
//			System.out.println(ser);
//			Object o = conv.Convert(ser);
//			System.out.println(o.getClass().getName());
//			System.out.println((URI)o);
//		}
}
