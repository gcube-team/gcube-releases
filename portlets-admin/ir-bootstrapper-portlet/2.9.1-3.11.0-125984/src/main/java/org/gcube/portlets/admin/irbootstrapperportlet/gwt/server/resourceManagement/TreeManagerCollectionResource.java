/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class TreeManagerCollectionResource extends WSResource {
	
	//
	public static final String TYPE_NAME = "TreeManagerCollectionType";
	
	public static final String ATTR_COLNAME = "/Document/Data//child::*[local-name()='Name']/text()";
	public static final String ATTR_COLID = "/Document/Data//child::*[local-name()='SourceId']/text()";
	public static final String ATTR_TYPE = "/Document/Data//child::*[local-name()='Type']/text()";
	public static final String ATTR_NUMOFMEMBERS = "/Document/Data//child::*[local-name()='Cardinality']/text()";
	

	/**
	 * Class constructor
	 * @param scope
	 * @param resourceTypeName
	 * @param baseISQuery
	 */
	public TreeManagerCollectionResource(String scope) {
		super(scope, TYPE_NAME, "tree-manager-service");
		
		this.setAttributeValue(ATTR_RESOURCEKEY, "[ !binder, !manager ]");
		this.setBaseISQuery(ResourceManager.constructQueryExpressionForResource(this));
		
		this.addAttributeName(ATTR_COLID);
		this.addAttributeName(ATTR_COLNAME);
		this.addAttributeName(ATTR_TYPE);
		this.addAttributeName(ATTR_NUMOFMEMBERS);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.resourceHandling.Resource#fromXML(org.gcube.common.core.informationsystem.client.XMLResult)
	 */
	@Override
	void fromXML(String xmlResult) {
		super.fromXML(xmlResult);
		try {
			this.setAttributeValue(ATTR_COLNAME, evaluateExpression(xmlResult, ATTR_COLNAME));
			this.setAttributeValue(ATTR_COLID, evaluateExpression(xmlResult, ATTR_COLID));
			this.setAttributeValue(ATTR_TYPE, evaluateExpression(xmlResult, ATTR_TYPE));
			this.setAttributeValue(ATTR_NUMOFMEMBERS, evaluateExpression(xmlResult, ATTR_NUMOFMEMBERS));
		} catch (Exception e) { }
	}

}
