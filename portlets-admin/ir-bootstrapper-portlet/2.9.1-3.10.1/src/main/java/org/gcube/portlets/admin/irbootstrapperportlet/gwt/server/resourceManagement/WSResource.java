/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public abstract class WSResource extends Resource {
	
	public static final String BASE_IS_QUERY = BASE_NS + "for $result in collection(\"/db/Properties\")//Document " + QUERY_CONDITION_PLACEHOLDER + " return $result";
	
	public static final String ATTR_ID = "/Document/ID/text()";
	public static final String ATTR_SERVICENAME = "/Document/Data/child::*[local-name()='ServiceName']/text()";
	public static final String ATTR_SERVICECLASS = "/Document/Data/child::*[local-name()='ServiceClass']/text()";
	public static final String ATTR_ADDRESS = "/Document/Source/text()";
	public static final String ATTR_RESOURCEKEY = "/Document/SourceKey/text()";
	
	/**
	 * Class constructor
	 * @param scope
	 * @param resourceTypeName
	 * @param baseISQuery
	 */
	WSResource(String scope, String typeName, String serviceName) {
		super(scope, typeName, BASE_IS_QUERY);
		
		this.addAttributeName(ATTR_ID);
		this.addAttributeName(ATTR_SERVICENAME);
		this.addAttributeName(ATTR_SERVICECLASS);
		this.addAttributeName(ATTR_ADDRESS);
		this.addAttributeName(ATTR_RESOURCEKEY);
		
		this.setAttributeValue(ATTR_SERVICENAME, serviceName);
		this.setBaseISQuery(ResourceManager.constructQueryExpressionForResource(this));
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.resourceManagement.Resource#fromXML(org.gcube.common.core.informationsystem.client.XMLResult)
	 */
	@Override
	void fromXML(String xmlResult) {
		try{
			this.setAttributeValue(ATTR_ID, evaluateExpression(xmlResult, ATTR_ID));
			this.setAttributeValue(ATTR_SERVICENAME, evaluateExpression(xmlResult, ATTR_SERVICENAME));
			this.setAttributeValue(ATTR_SERVICECLASS,evaluateExpression(xmlResult, ATTR_SERVICECLASS));
			this.setAttributeValue(ATTR_ADDRESS, evaluateExpression(xmlResult, ATTR_ADDRESS));
			this.setAttributeValue(ATTR_RESOURCEKEY, evaluateExpression(xmlResult, ATTR_RESOURCEKEY));
		} catch (Exception e) { }
	}
}
