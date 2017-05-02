package org.gcube.data.tml.proxies;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.tml.Constants;

/**
 * A path of identifiers to a tree node.
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="path",namespace=Constants.namespace)
public class Path {

	@XmlElement(name="id")
	private String[] ids;
	
	Path() {}
	
	/**
	 * Creates an instance with given node identifiers
	 * @param ids the identifiers.
	 */
	public Path(String ... ids) {
		this.ids=ids;
	}
	
	/**
	 * Returns the node identifiers of the path.
	 * @return the identifiers
	 */
	public String[] ids() {
		return ids;
	}
}
