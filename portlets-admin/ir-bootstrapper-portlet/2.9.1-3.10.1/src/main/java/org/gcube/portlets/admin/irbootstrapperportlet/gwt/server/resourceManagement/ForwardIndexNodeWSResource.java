/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class ForwardIndexNodeWSResource extends WSResource {
	
	public static final String TYPE_NAME = "ForwardIndexNodeResource";
	
	public static final String ATTR_COLLECTIONID = "/Document/Data/child::*[local-name()='CollectionID']/text()";
	public static final String ATTR_INDEXID = "/Document/Data/child::*[local-name()='IndexID']/text()";
	public static final String ATTR_KEYNAMES = "/Document/Data/child::*[local-name()='KeyDescription']/child::*[local-name()='KeyName']/text()";
	public static final String ATTR_KEYTYPES = "/Document/Data/child::*[local-name()='KeyDescription']/child::*[local-name()='IndexTypeID']/text()";
	
	/**
	 * Class constructor
	 * @param scope
	 * @param resourceTypeName
	 * @param baseISQuery
	 */
	public ForwardIndexNodeWSResource(String scope) {
		super(scope, TYPE_NAME, "ForwardIndexNode");
		
		this.addAttributeName(ATTR_COLLECTIONID);
		this.addAttributeName(ATTR_INDEXID);
		this.addAttributeName(ATTR_KEYNAMES);
		this.addAttributeName(ATTR_KEYTYPES);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.resourceHandling.Resource#fromXML(org.gcube.common.core.informationsystem.client.XMLResult)
	 */
	@Override
	void fromXML(String xmlResult) {
		super.fromXML(xmlResult);
		try {
			this.setAttributeValue(ATTR_COLLECTIONID, evaluateExpression(xmlResult,ATTR_COLLECTIONID));
			this.setAttributeValue(ATTR_INDEXID, evaluateExpression(xmlResult, ATTR_INDEXID));
			this.setAttributeValue(ATTR_KEYNAMES, evaluateExpression(xmlResult, ATTR_KEYNAMES));
			this.setAttributeValue(ATTR_KEYTYPES, evaluateExpression(xmlResult, ATTR_KEYTYPES));
		} catch (Exception e) { }
	}
}
