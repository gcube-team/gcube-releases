package org.gcube.common.core.state;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.encoding.SerializationException;
import org.globus.wsrf.impl.SimpleResourceKey;

/**
 * An implementation of {@link ResourceKey} for gCube services.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBEWSResourceKey implements ResourceKey {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * An implementation {@link ResourceKey} wrapped by the key.
	 */
	ResourceKey innerKey;
	
	
	/**
	 * Creates an instance with a given qualified name and value;
	 * @param name the name.
	 * @param value the value.
	 */
	public GCUBEWSResourceKey(QName name, String value) {
		this.innerKey = new SimpleResourceKey(name,value);
	}
	
	/**
	 * Creates an instance with a given inner key.
	 * @param key the key.
	 */
	public GCUBEWSResourceKey(ResourceKey key) {
		this.innerKey = key;
	}
	
	

	/**
	 * Returns the implementation of {@link org.globus.wsrf.ResourceKey ResourceKey} wrapped by the key.
	 * @return the inner key.
	 */
	protected ResourceKey getInnerKey() {
		return this.innerKey;
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof GCUBEWSResourceKey) return innerKey.equals(((GCUBEWSResourceKey)obj).getInnerKey());
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public QName getName() {
		return innerKey.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValue() {
		return (String) innerKey.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return innerKey.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public SOAPElement toSOAPElement() throws SerializationException {
		return innerKey.toSOAPElement();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return innerKey.toString();
	}
}
