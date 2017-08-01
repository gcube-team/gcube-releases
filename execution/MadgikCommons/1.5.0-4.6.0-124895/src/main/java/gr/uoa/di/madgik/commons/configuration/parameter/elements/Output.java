package gr.uoa.di.madgik.commons.configuration.parameter.elements;

import gr.uoa.di.madgik.commons.configuration.parameter.ObjectParameter;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * This class is a member of the {@link ObjectParameter} description. Used to store the output of a method
 * invokation and the parameter it should be stored to
 *
 * @author gpapanikos
 */
public class Output
{

	private String ParameterName = null;

	/**
	 * Creates a new instance
	 */
	public Output()
	{
	}

	/**
	 * Retireves the parameter name that shoulc hold the output of a method invokation
	 * 
	 * @return the name
	 */
	public String GetParameterName()
	{
		return this.ParameterName;
	}

	/**
	 * Parses the XML subtree provided to populate the instance
	 *
	 * @param element the XML subtree
	 * @throws java.lang.Exception the parsing could not be performed
	 */
	public void FromXML(Element element) throws Exception
	{
		if (!XMLUtils.AttributeExists(element, "param"))
		{
			throw new Exception("Not valid serialization of output");
		}
		this.ParameterName = XMLUtils.GetAttribute(element, "param");
	}
}
