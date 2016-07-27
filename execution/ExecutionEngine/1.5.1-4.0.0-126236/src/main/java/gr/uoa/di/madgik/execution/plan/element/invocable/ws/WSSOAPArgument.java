package gr.uoa.di.madgik.execution.plan.element.invocable.ws;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.ArgumentBase;
import gr.uoa.di.madgik.execution.plan.element.variable.IInputParameter;
import gr.uoa.di.madgik.execution.utils.ParameterUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class WSSOAPArgument extends ArgumentBase
{
//	public QName ArgumentNamespace;
	
	private static final long serialVersionUID = 1L;

	public void Validate() throws ExecutionValidationException
	{
		super.Validate();
//		if(this.ArgumentNamespace!=null && (this.ArgumentNamespace.getNamespaceURI()==null || 
//				this.ArgumentNamespace.getNamespaceURI().trim().length()==0 ||
//				this.ArgumentNamespace.getLocalPart()==null ||
//				this.ArgumentNamespace.getLocalPart().trim().length()==0)) throw new ExecutionValidationException("Needed invocation argument namespace info not provided");
	}
	
	public ArgumentType GetArgumentType()
	{
		return ArgumentType.WSSOAP;
	}
	
	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<argument type=\""+this.GetArgumentType().toString()+"\" order=\"" + this.Order + "\" name=\"" + this.ArgumentName + "\">");
//		if(this.ArgumentNamespace!=null) buf.append("<arguments><nsURI>"+this.ArgumentNamespace.getNamespaceURI()+"</nsURI><local>"+this.ArgumentNamespace.getLocalPart()+"</local></arguments>");
		buf.append(this.Parameter.ToXML());
		buf.append("</argument>");
		return buf.toString();
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			if (!XMLUtils.AttributeExists((Element) XML, "order") || !XMLUtils.AttributeExists((Element) XML, "name")) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Order = Integer.parseInt(XMLUtils.GetAttribute((Element) XML, "order"));
			this.ArgumentName = XMLUtils.GetAttribute((Element) XML, "name");
			Element paramelem=XMLUtils.GetChildElementWithName(XML, "param");
			if(paramelem==null) throw new ExecutionSerializationException("Provided serialization is not valid");
			this.Parameter=(IInputParameter)ParameterUtils.GetParameter(paramelem);
//			Element envnsElem=XMLUtils.GetChildElementWithName((Element)XML, "arguments");
//			if(envnsElem==null) this.ArgumentNamespace=null;
//			else
//			{
//				Element envnsURIelem=XMLUtils.GetChildElementWithName(envnsElem, "nsURI");
//				if(envnsURIelem==null) throw new ExecutionSerializationException("Provided serialization is not valid");
//				Element envnsLocalelem=XMLUtils.GetChildElementWithName(envnsElem, "local");
//				if(envnsLocalelem==null) throw new ExecutionSerializationException("Provided serialization is not valid");
//				this.ArgumentNamespace=new QName(XMLUtils.GetChildText(envnsURIelem), XMLUtils.GetChildText(envnsLocalelem));
//			}
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided XML serialization", ex);
		}
	}

}
