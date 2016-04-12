//package gr.uoa.di.madgik.environment.is.elements;
//
//import gr.uoa.di.madgik.commons.utils.XMLUtils;
//import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
//import java.util.UUID;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//
//public class BoundaryListenerInfo implements IInformationSystemElement
//{
//	private static final long serialVersionUID = 786451786824805162L;
//	public int Port=0;
//
//	public BoundaryListenerInfo() {}
//	
//	public BoundaryListenerInfo(int Port) 
//	{
//		this.Port=Port;
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	public String ID=UUID.randomUUID().toString();
//	public String NodeID=null;
//
//	public BoundaryListenerInfo(String NodeID,int Port)
//	{
//		this.NodeID=NodeID;
//		this.Port=Port;
//	}
//	
//	public IInformationSystemElement.ElementType GetElementType()
//	{
//		return ElementType.BoundaryListerner;
//	}
//	
//	public String ToXML() throws EnvironmentInformationSystemSerializationException
//	{
//		StringBuilder buf=new StringBuilder();
//		buf.append("<element id=\""+this.ID+"\" type=\""+this.GetElementType().toString()+"\">");
//		buf.append("<node id=\""+this.NodeID+"\"/>");
//		buf.append("<port value=\""+this.Port+"\"/>");
//		buf.append("</element>");
//		return buf.toString();
//	}
//	
//	public void FromXML(String xml) throws EnvironmentInformationSystemSerializationException
//	{
//		try
//		{
//			Document doc=XMLUtils.Deserialize(xml);
//			this.FromXML(doc.getDocumentElement());
//		}
//		catch(Exception ex)
//		{
//			throw new EnvironmentInformationSystemSerializationException("Could not parse provided info",ex);
//		}
//	}
//	
//	public void FromXML(Element xml) throws EnvironmentInformationSystemSerializationException
//	{
//		try
//		{
//			if(!XMLUtils.AttributeExists(xml, "id")) throw new EnvironmentInformationSystemSerializationException("Invalid serialization provided");
//			this.ID=XMLUtils.GetAttribute(xml, "id");
//			if(!XMLUtils.AttributeExists(xml, "type")) throw new EnvironmentInformationSystemSerializationException("Invalid serialization provided");
//			if(!XMLUtils.GetAttribute(xml, "type").equals(this.GetElementType().toString())) throw new EnvironmentInformationSystemSerializationException("Invalid serialization provided");
//			Element nodeElem=XMLUtils.GetChildElementWithName(xml, "node");
//			if(nodeElem==null) throw new EnvironmentInformationSystemSerializationException("Invalid serialization provided");
//			if(!XMLUtils.AttributeExists(nodeElem, "id")) throw new EnvironmentInformationSystemSerializationException("Invalid serialization provided");
//			this.NodeID=XMLUtils.GetAttribute(nodeElem, "id");
//			Element portElem=XMLUtils.GetChildElementWithName(xml, "port");
//			if(portElem==null) throw new EnvironmentInformationSystemSerializationException("Invalid serialization provided");
//			if(!XMLUtils.AttributeExists(portElem, "value")) throw new EnvironmentInformationSystemSerializationException("Invalid serialization provided");
//			this.Port=Integer.parseInt(XMLUtils.GetAttribute(portElem, "value"));
//		}
//		catch(Exception ex)
//		{
//			throw new EnvironmentInformationSystemSerializationException("Could not parse provided info",ex);
//		}
//	}
//}
