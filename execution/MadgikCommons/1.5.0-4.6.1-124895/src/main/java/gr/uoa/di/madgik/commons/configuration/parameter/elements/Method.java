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
public class Method implements Comparable<Method>
{

	private String Name = null;
	private Arguments Arguments = null;
	private Output Output = null;
	private int Order = 0;

	/**
	 * Creates a new instnace
	 */
	public Method()
	{
		this.Arguments = new Arguments();
		this.Output = new Output();
	}

	/**
	 * Retrieves the name of the method to call
	 *
	 * @return the name
	 */
	public String GetName()
	{
		return this.Name;
	}

	/**
	 * Retrieves the arguments that should be provided to the method invocation
	 *
	 * @return the arguments
	 */
	public Arguments GetArguments()
	{
		return this.Arguments;
	}

	/**
	 * Retrieves the output parameter to hold the return value of the method
	 *
	 * @return the output description
	 */
	public Output GetOutput()
	{
		return this.Output;
	}

	/**
	 * Retrieves the order this method should be invoced compared to the rest of the methods
	 * declared in the containing {@link ObjectParameter}
	 *
	 * @return the order
	 */
	public int GetOrder()
	{
		return this.Order;
	}

	public int compareTo(Method o)
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
		if (!XMLUtils.AttributeExists(element, "order") || !XMLUtils.AttributeExists(element, "name"))
		{
			throw new Exception("Not valid serialization of method");
		}
		this.Order = Integer.parseInt(XMLUtils.GetAttribute(element, "order"));
		this.Name = XMLUtils.GetAttribute(element, "name");
		Element elem = XMLUtils.GetChildElementWithName(element, "arguments");
		if (elem != null)
		{
			this.Arguments.FromXML(elem);
		}
		elem = XMLUtils.GetChildElementWithName(element, "output");
		if (elem != null)
		{
			this.Output.FromXML(elem);
		}
	}
}
