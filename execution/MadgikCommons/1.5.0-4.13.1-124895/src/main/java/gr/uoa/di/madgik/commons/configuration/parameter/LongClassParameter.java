package gr.uoa.di.madgik.commons.configuration.parameter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parameter of type {@link gr.uoa.di.madgik.commons.configuration.parameter.IParameter.ParameterType#LongClass}.
 * An example of this instnace configuratino type is the following
 * <p>
 * <pre>
 * {@code
 *
 * <param name="parameterKey" type="LongClass" generated="false" internal="false">4</param>
 *
 * }
 * </pre>
 * </p>
 *
 * @author gpapanikos
 */
public class LongClassParameter implements IParameter
{

	private String Name = null;
	private Long Value = 0l;
	private boolean generated = false;
	private boolean internal = false;
	private boolean checked = false;

	public Class<?> GetParameterClassType()
	{
		return Long.class;
	}

	public Boolean IsChecked()
	{
		return this.checked;
	}

	public void Check()
	{
		this.checked = true;
	}

	public ParameterType GetParameterType()
	{
		return ParameterType.LongClass;
	}

	public String GetName()
	{
		return Name;
	}

	public Boolean IsGenerated()
	{
		return this.generated;
	}

	public Boolean IsInternal()
	{
		return this.internal;
	}

	public Object GetValue()
	{
		return new Long(this.Value);
	}

	public void SetValue(Object Value) throws Exception
	{
		if (Value instanceof Long)
		{
			this.Value = new Long((Long) Value);
		} else
		{
			throw new Exception("Provided value not of expected type");
		}
	}

	public void FromXML(String xml) throws Exception
	{
		Document doc = XMLUtils.Deserialize(xml);
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Element element) throws Exception
	{
		if (!XMLUtils.AttributeExists(element, "name"))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		this.Name = XMLUtils.GetAttribute(element, "name");
		if (!XMLUtils.AttributeExists(element, "type"))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		if (!ParameterType.valueOf(XMLUtils.GetAttribute(element, "type")).equals(this.GetParameterType()))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		if (!XMLUtils.AttributeExists(element, "generated"))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		this.generated = Boolean.getBoolean(XMLUtils.GetAttribute(element, "generated"));
		if (!XMLUtils.AttributeExists(element, "internal"))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		this.internal = Boolean.getBoolean(XMLUtils.GetAttribute(element, "internal"));
		if (!this.generated)
		{
			this.Value = Long.parseLong(XMLUtils.GetChildText(element));
		}
	}
}
