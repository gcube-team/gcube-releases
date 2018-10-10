package gr.uoa.di.madgik.commons.configuration.parameter.elements;

import gr.uoa.di.madgik.commons.configuration.parameter.ObjectParameter;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * This class is a member of the {@link ObjectParameter} description. Used to store a single argument
 * in an {@link Arguments} instnace
 *
 * @author gpapanikos
 */
public class Argument implements Comparable<Argument>
{

	private String Name = null;
	private String ParameterName = null;
	private int Order = 0;

	/**
	 * Creates a new Argument
	 */
	public Argument()
	{
	}

	/**
	 * Retrieves teh order of the parameter in the parameter list
	 *
	 * @return the order
	 */
	public int GetOrder()
	{
		return this.Order;
	}

	/**
	 * Retrieves the name of the argument
	 *
	 * @return the name
	 */
	public String GetName()
	{
		return this.Name;
	}

	/**
	 * Retrieves the name of the parameter that stores the value to be used for this argument
	 * 
	 * @return the parameter name
	 */
	public String GetParameterName()
	{
		return this.ParameterName;
	}

	public int compareTo(Argument o)
	{
		return new Integer(this.Order).compareTo(o.Order);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	/**
	 * Parses the XML subtree provided to populate the instance
	 *
	 * @param element the XML subtree
	 * @throws java.lang.Exception the parsing could not be performed
	 */
	public void FromXML(Element element) throws Exception
	{
		if (!XMLUtils.AttributeExists(element, "order") || !XMLUtils.AttributeExists(element, "name") || !XMLUtils.AttributeExists(element, "param"))
		{
			throw new Exception("Not valid serialization of argument");
		}
		this.Order = Integer.parseInt(XMLUtils.GetAttribute(element, "order"));
		this.Name = XMLUtils.GetAttribute(element, "name");
		this.ParameterName = XMLUtils.GetAttribute(element, "param");
	}
}
