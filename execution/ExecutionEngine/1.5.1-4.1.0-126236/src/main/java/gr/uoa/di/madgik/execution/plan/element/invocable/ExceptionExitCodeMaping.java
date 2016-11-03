package gr.uoa.di.madgik.execution.plan.element.invocable;

import java.io.Serializable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ExceptionExitCodeMaping implements Serializable
{
	public enum MapType
	{
		Equal,
		NotEqual
	}
	
	public int ExitCode = 0;
	public String ErrorFullName = null;
	public String ErrorSimpleName = null;
	public String Message = "";
	public MapType TypeOfMapping=MapType.Equal;

	public ExceptionExitCodeMaping()
	{
	}

	public ExceptionExitCodeMaping(int ExitCode, String ErrorFullName, String ErrorSimpleName, String Message)
	{
		this.ExitCode = ExitCode;
		this.ErrorFullName = ErrorFullName;
		this.ErrorSimpleName = ErrorSimpleName;
		this.Message = Message;
		this.TypeOfMapping=MapType.Equal;
	}

	public ExceptionExitCodeMaping(int ExitCode, String ErrorFullName, String ErrorSimpleName, String Message,MapType TypeOfMapping)
	{
		this.ExitCode = ExitCode;
		this.ErrorFullName = ErrorFullName;
		this.ErrorSimpleName = ErrorSimpleName;
		this.Message = Message;
		this.TypeOfMapping=TypeOfMapping;
	}

	public void Validate() throws ExecutionValidationException
	{
		if (this.ErrorFullName == null || this.ErrorFullName.trim().length() == 0) throw new ExecutionValidationException("Needed value not provided");
		if (this.ErrorSimpleName == null || this.ErrorSimpleName.trim().length() == 0) throw new ExecutionValidationException("Needed value not provided");
		if (this.Message == null) throw new ExecutionValidationException("Needed value not provided");
	}

	public String ToXML() throws ExecutionSerializationException
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<exitCodeError>");
		buf.append("<exitCode value=\"" + this.ExitCode + "\" type=\""+this.TypeOfMapping.toString()+"\"/>");
		buf.append("<errorFullName value=\"" + this.ErrorFullName + "\"/>");
		buf.append("<errorSimpleName value=\"" + this.ErrorSimpleName + "\"/>");
		buf.append("<message value=\"" + this.Message + "\"/>");
		buf.append("</exitCodeError>");
		return buf.toString();
	}

	public void FromXML(String XML) throws ExecutionSerializationException
	{
		Document doc = null;
		try
		{
			doc = XMLUtils.Deserialize(XML);
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Node XML) throws ExecutionSerializationException
	{
		try
		{
			Element elem = XMLUtils.GetChildElementWithName(XML, "exitCode");
			if (elem == null) throw new ExecutionSerializationException("Not valid serialization of element");
			if (!XMLUtils.AttributeExists(elem, "value")) throw new ExecutionSerializationException("Not valid serialization of element");
			this.ExitCode = Integer.parseInt(XMLUtils.GetAttribute(elem, "value"));
			if (!XMLUtils.AttributeExists(elem, "type")) throw new ExecutionSerializationException("Not valid serialization of element");
			this.TypeOfMapping = MapType.valueOf(XMLUtils.GetAttribute(elem, "type"));
			elem = XMLUtils.GetChildElementWithName(XML, "errorFullName");
			if (elem == null) throw new ExecutionSerializationException("Not valid serialization of element");
			if (!XMLUtils.AttributeExists(elem, "value")) throw new ExecutionSerializationException("Not valid serialization of element");
			this.ErrorFullName = XMLUtils.GetAttribute(elem, "value");
			elem = XMLUtils.GetChildElementWithName(XML, "errorSimpleName");
			if (elem == null) throw new ExecutionSerializationException("Not valid serialization of element");
			if (!XMLUtils.AttributeExists(elem, "value")) throw new ExecutionSerializationException("Not valid serialization of element");
			this.ErrorSimpleName = XMLUtils.GetAttribute(elem, "value");
			elem = XMLUtils.GetChildElementWithName(XML, "message");
			if (elem == null) throw new ExecutionSerializationException("Not valid serialization of element");
			if (!XMLUtils.AttributeExists(elem, "value")) throw new ExecutionSerializationException("Not valid serialization of element");
			this.Message = XMLUtils.GetAttribute(elem, "value");
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not deserialize provided xml serialization", ex);
		}
	}
}
