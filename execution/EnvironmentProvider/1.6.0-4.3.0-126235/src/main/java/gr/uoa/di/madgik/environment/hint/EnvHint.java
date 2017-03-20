package gr.uoa.di.madgik.environment.hint;

import java.io.Serializable;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentSerializationException;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;


public class EnvHint implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String Payload=null;
	
	public EnvHint(){}
	
	public EnvHint(String Payload)
	{
		this.Payload=Payload;
	}

	public void Validate() throws EnvironmentValidationException
	{
		if(this.Payload==null) throw new EnvironmentValidationException("No hint specified");
	}
	
	public void FromXML(String xml) throws EnvironmentSerializationException
	{
		this.Payload=XMLUtils.UndoReplaceSpecialCharachters(xml);
	}

	public String ToXML() throws EnvironmentSerializationException
	{
		return XMLUtils.DoReplaceSpecialCharachters(this.Payload);
	}
}
