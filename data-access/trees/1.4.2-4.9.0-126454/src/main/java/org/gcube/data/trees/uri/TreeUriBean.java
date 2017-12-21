package org.gcube.data.trees.uri;

import java.net.URI;
import java.util.List;

import org.gcube.common.uri.ScopedURIBean;

/**
 * Models the information in a tree URI.
 * 
 * @author Fabio Simeoni
 *
 */
public class TreeUriBean {

	private final ScopedURIBean bean;
	
	private final String sourceId;
	private final String treeId;
	private final String nodeId;
	private final List<String> nodeIds;
	
	public TreeUriBean(URI uri) throws IllegalArgumentException {

		this.bean=new ScopedURIBean(uri);
		List<String> elements = bean.elements();
		try {
			sourceId = elements.get(1);
			treeId = elements.get(2);
			nodeId = elements.get(elements.size()-1);
			nodeIds = elements.subList(2,elements.size());
		}
		catch(Exception e) {
			throw new IllegalArgumentException(bean+" is not a valid tree URI",e);
		}
	}

	/**
	 * Returns the source identifier.
	 * @return the identifier
	 */
	public String sourceId() {
		return sourceId;
	}
	
	/**
	 * Returns the tree identifier.
	 * @return the identifier
	 */
	public String treeId() {
		return treeId;
	}
	
	/**
	 * Returns the identifier of the node.
	 * @return the identifier
	 */
	public String nodeId()  {
		return nodeId;
	}
	
	/**
	 * Returns the identifiers of the nodes.
	 * @return the identifiers
	 */
	public List<String> nodeIDs() {
		return nodeIds;
	}
	
	/**
	 * Returns the underlying URI.
	 * @return the URI
	 */
	public URI url() {
		return bean.uri();
	}
	
	@Override
	public String toString() {
		return bean.toString();
	}
	
	
	/**
	 * Returns the scope of the URL.
	 * @return the scope.
	 */
	public String scope() {
		return bean.scope();
	}
}
