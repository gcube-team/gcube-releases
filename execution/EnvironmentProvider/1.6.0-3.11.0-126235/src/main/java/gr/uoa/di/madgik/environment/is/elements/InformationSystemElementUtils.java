//package gr.uoa.di.madgik.environment.is.elements;
//
//import gr.uoa.di.madgik.commons.utils.XMLUtils;
//import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
//import org.w3c.dom.Element;
//
//public class InformationSystemElementUtils
//{
//	public static IInformationSystemElement GetElement(Element xml) throws EnvironmentInformationSystemSerializationException
//	{
//		try
//		{
//			IInformationSystemElement elem=null;
//			if(!XMLUtils.AttributeExists(xml, "id") || !XMLUtils.AttributeExists(xml, "type")) throw new EnvironmentInformationSystemSerializationException("Serialization not valid");
//			IInformationSystemElement.ElementType t=IInformationSystemElement.ElementType.valueOf(XMLUtils.GetAttribute(xml, "type"));
//			switch(t)
//			{
//				case BoundaryListerner:
//				{
//					elem=new BoundaryListenerInfo();
//					elem.FromXML(xml);
//					break;
//				}
//				case Node:
//				{
//					elem=new NodeInfo();
//					elem.FromXML(xml);
//					break;
//				}
//				default: throw new EnvironmentInformationSystemSerializationException("Unrecognized element type");
//			}
//			return elem;
//		}catch(Exception ex)
//		{
//			throw new EnvironmentInformationSystemSerializationException("Could not deserialize information system element",ex);
//		}
//	}
//}
