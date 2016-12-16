package org.gcube.informationsystem.registry.impl.resources;


import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEResource;

/**
 * A to-be-registered {@link GCUBEResource} passed to the ISRegistry
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GCUBERegisteredResource {

	
	protected GCUBEResource resource;
	
	Document dom = null;
	
	public GCUBERegisteredResource (GCUBEResource resource) throws Exception {
		this.resource = resource;
		this.parse();
	}
	
	public String getID() {
		return this.resource.getID();
	}
		
	/**
	 * States if the resource is a temporary resource, i.e. must be destroyed after its registration
	 * @return
	 */
	public boolean isTemporary() {
		if (resource.getType().compareTo(GCUBEHostingNode.TYPE) == 0)
		    return false;
		else
		    return true;
	}
	
	private void parse() throws Exception {		
		StringWriter writer = new StringWriter();
		try {
			resource.store(writer);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder builder = factory.newDocumentBuilder();

			StringReader reader = new StringReader(writer.toString().substring(writer.toString().indexOf("?>") + 2, writer.toString().length()));

			InputSource source = new InputSource(reader);

			this.dom = builder.parse(source);

		} catch (Exception e1) {
			throw new Exception("Unable to parse the resource");
		}

	}

	/**
	 * @return the XML Document representation of the resouce 
	 */
	public Document getAsDOM() {		
		return this.dom;
	}

	/**
	 * @return the source resource
	 */
	public GCUBEResource getSource() {
		return this.resource;
	}
}
