/**
 * 
 */
package org.gcube.data.tr.requests;

import javax.xml.bind.annotation.XmlAttribute;

import org.w3c.dom.Element;

/**
 * Partial {@link Request} implementation. 
 *  
 * @author Fabio Simeoni
 *
 */
public abstract class AbstractRequest implements Request {

	@XmlAttribute
	final Mode mode;
	
	AbstractRequest() {
		this(Mode.FULLACESSS);
	}
	
	AbstractRequest(Mode m) {
		if (m==null)
			throw new IllegalArgumentException("mode is null");
		mode=m;
	}
	
	/**
	 * Returns the access mode to the collection.
	 * @return the mode
	 */
	public Mode mode() {
		return mode;
	}
	
	public Element toElement() {
		return new RequestBinder().bind(this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractRequest))
			return false;
		AbstractRequest other = (AbstractRequest) obj;
		if (mode == null) {
			if (other.mode != null)
				return false;
		} else if (!mode.equals(other.mode))
			return false;
		return true;
	}
}
