package org.gcube.rest.commons.resourceawareservice.resources;

import java.io.StringWriter;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.rest.commons.helpers.XMLConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.google.common.collect.Lists;

@XmlRootElement(name = "MadgikResource")
public class Resource extends GeneralResource {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String type;
	private List<String> scopes = Lists.newArrayList();
	private String description;
	private Node body;

	public Resource() {
		setResourceID(UUID.randomUUID().toString());
		this.newBody();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the body
	 */
	@XmlAnyElement
	public Node getBody() {
		return body;
	}
	
	/**
	 * @return the scopes
	 */
	public List<String> getScopes() {
		return scopes;
	}

	/**
	 * @param scopes
	 *            the scopes to set
	 */
	public void setScopes(List<String> scopes) {
		this.scopes = scopes;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return this resource with new body
	 */
	public Resource newBody() {
		Document document = null;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			document.appendChild(document.createElement("doc"));
			body = document.getDocumentElement();
		} catch (ParserConfigurationException e) {
		}
		
		return this;
	}

	/**
	 * @return the body as String
	 * @throws JAXBException 
	 */
	public String getBodyAsString() throws JAXBException {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "no");
			t.transform(new DOMSource(body), new StreamResult(sw));
		} catch (TransformerException te) {
			return null;
		}
		return sw.toString();
	}
	
	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(Node body) {
		this.body = body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Resource [id=" + getResourceID() + ", name=" + name + ", scopes=" + scopes + ", description=" + description + ", body=" + body + "]";
	}
}
