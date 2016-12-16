package gr.uoa.di.madgik.rr.element;

import gr.uoa.di.madgik.rr.ResourceRegistryException;

import org.w3c.dom.Element;

public interface IDaoElement
{
	public void fromXML(Element element) throws ResourceRegistryException;
	public String toXML() throws ResourceRegistryException;
	public String getID() throws ResourceRegistryException;
	public Long getTimestamp() throws ResourceRegistryException;
	public void apply(IDaoElement other) throws ResourceRegistryException;
}
