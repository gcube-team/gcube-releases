package gr.uoa.di.madgik.environment.is.elements;

import java.io.Serializable;
import java.util.UUID;
import org.w3c.dom.Element;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import gr.uoa.di.madgik.environment.is.elements.invocable.context.InvocableContext;

public abstract class InvocableProfileInfo implements Serializable
{
	public static final String ExecutionProfileNS="http://profile.execution.madgik.di.uoa.gr";
	
	private static final long serialVersionUID = 3729186827521535051L;
	public String ID=UUID.randomUUID().toString();
	public InvocableContext ExecutionContext=new InvocableContext();

	public abstract String ToXML() throws EnvironmentInformationSystemSerializationException;
	public abstract void FromXML(String XML) throws EnvironmentInformationSystemSerializationException;
	public abstract void FromXML(Element XML) throws EnvironmentInformationSystemSerializationException;
}
