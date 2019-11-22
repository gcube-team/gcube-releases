package gr.uoa.di.madgik.commons.configuration.parameter.elements;

import gr.uoa.di.madgik.commons.configuration.parameter.ObjectParameter;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Element;

/**
 * This class is a member of the {@link ObjectParameter} description. Used to store the arguments
 * that should be used in either a {@link Method} or a {@link Constructor} call.
 *
 * @author gpapanikos
 */
public class Arguments
{

	private List<Argument> Arguments = null;

	/**
	 * Creates a new instnace
	 */
	public Arguments()
	{
		this.Arguments = new ArrayList<Argument>();
	}

	/**
	 * Adds a new argument to teh collection
	 * 
	 * @param arg the argument to add
	 */
	public void Add(Argument arg)
	{
		this.Arguments.add(arg);
	}

	/**
	 * The Contained arguments
	 *
	 * @return the arguments
	 */
	public List<Argument> GetArguments()
	{
		Collections.sort(this.Arguments);
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
		List<Element> args = XMLUtils.GetChildElementsWithName(element, "arg");
		for (Element arg : args)
		{
			Argument a = new Argument();
			a.FromXML(arg);
			this.Add(a);
		}
	}
}
