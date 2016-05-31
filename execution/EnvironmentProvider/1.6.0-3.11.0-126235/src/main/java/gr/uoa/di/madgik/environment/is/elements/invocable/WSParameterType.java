package gr.uoa.di.madgik.environment.is.elements.invocable;

import org.w3c.dom.Element;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.InvocableProfileInfo;
import gr.uoa.di.madgik.environment.is.elements.invocable.WSMethod.ExtractType;

public class WSParameterType extends ParameterType
{
	private static final long serialVersionUID = -310453490441142473L;
	public String ExtractExpression;
	public ExtractType ExpressionExtractType=ExtractType.String;

	public void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException
	{
		try
		{
			Element tmpTypeElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "type", InvocableProfileInfo.ExecutionProfileNS);
			if(tmpTypeElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
			if(!XMLUtils.AttributeExists(tmpTypeElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.Type=XMLUtils.GetAttribute(tmpTypeElement, "value");
			Element tmpConverterElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "converter", InvocableProfileInfo.ExecutionProfileNS);
			if(tmpConverterElement!=null)
			{
				if(!XMLUtils.AttributeExists(tmpConverterElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
				this.Converter=XMLUtils.GetAttribute(tmpConverterElement, "value");
			}
			Element tmpArgTemplateElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "argumentTemplate", InvocableProfileInfo.ExecutionProfileNS);
			if(tmpArgTemplateElement!=null) this.ArgumentTemplate=XMLUtils.GetChildCDataText(tmpArgTemplateElement);
			else this.ArgumentTemplate=null;
			Element tmpEngineTypeElement=XMLUtils.GetChildElementWithNameAndNamespace(XML, "engineType", InvocableProfileInfo.ExecutionProfileNS);
			if(tmpEngineTypeElement==null)throw new EnvironmentInformationSystemException("Not Valid serialization");
			if(!XMLUtils.AttributeExists(tmpEngineTypeElement, "value")) throw new EnvironmentInformationSystemException("Not Valid serialization");
			this.EngineType=XMLUtils.GetAttribute(tmpEngineTypeElement, "value");
			Element returnExtractElem=XMLUtils.GetChildElementWithNameAndNamespace(XML, "extract", InvocableProfileInfo.ExecutionProfileNS);
			this.ExtractExpression=null;
			if(returnExtractElem!=null)
			{
				if(!XMLUtils.AttributeExists(returnExtractElem, "type")) throw new EnvironmentInformationSystemException("Not Valid serialization");
				this.ExpressionExtractType=ExtractType.valueOf(XMLUtils.GetAttribute(returnExtractElem, "type"));
				this.ExtractExpression=XMLUtils.GetChildText(returnExtractElem);
				if(this.ExtractExpression==null || this.ExtractExpression.trim().length()==0) throw new EnvironmentInformationSystemException("Not Valid serialization");
			}
		}catch(Exception ex)
		{
			throw new EnvironmentInformationSystemSerializationException("Could not deserialize element", ex);
		}
	}
	
	public String ToXML() throws EnvironmentInformationSystemSerializationException
	{
		StringBuilder buf=new StringBuilder();
		buf.append("<execprf:type value=\""+this.Type+"\"/>");
		buf.append("<execprf:engineType value=\""+this.EngineType+"\"/>");
		if(this.ExtractExpression!=null)
		{
			buf.append("<execprf:extract type=\""+this.ExpressionExtractType.toString()+"\">");
			buf.append(this.ExtractExpression);
			buf.append("</execprf:extract>");
		}
		if(this.ArgumentTemplate!=null)
		{
			buf.append("<execprf:argumentTemplate><![CDATA[");
			buf.append(this.ArgumentTemplate);
			buf.append("]]></execprf:argumentTemplate>");
		}
		return buf.toString();
	}
}
