package gr.uoa.di.madgik.commons.configuration.parameter.elements;

import gr.uoa.di.madgik.commons.configuration.parameter.ObjectParameter;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import org.w3c.dom.Element;

/**
 * This class is a member of the {@link ObjectParameter} description. Used to store the constructor
 * that should be used to instnatiate the {@link ObjectParameter}
 *
 * @author gpapanikos
 */
public class Constructor
{

	private Arguments Arguments = null;

	/**
	 * Creates a new instnace
	 */
	public Constructor()
	{
		this.Arguments = new Arguments();
	}

	/**
	 * Retrieves the arguments that should be provided to contatructor
	 *
	 * @return the arguments
	 */
	public Arguments GetArguments()
	{
		return this.Arguments;
	}

	/**
	 * Parses the XML subtree provided to populate the instance
	 *
	 * @param element the XML subtree
	 * @throws java.lang.Exception the parsing could not be performed
	 */
	public void FromXML(Element element) throws Exception
	{
		Element elem = XMLUtils.GetChildElementWithName(element, "arguments");
		if (elem == null)
		{
			return;
		}
		this.Arguments.FromXML(elem);
	}
}
