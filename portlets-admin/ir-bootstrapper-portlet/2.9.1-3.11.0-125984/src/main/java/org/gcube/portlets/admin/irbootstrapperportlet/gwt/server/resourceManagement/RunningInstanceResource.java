/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;



/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class RunningInstanceResource extends GCUBEResource {
	
	public static final String TYPE_NAME = "RunningInstanceType";
	private static final String BASE_IS_QUERY = BASE_NS + "for $result in collection(\"/db/Profiles/RunningInstance\")//Document/Data/is:Profile/Resource " + QUERY_CONDITION_PLACEHOLDER + " return $result";

	public static final String ATTR_SERVICENAME = "/Resource/Profile/ServiceName/text()";
	public static final String ATTR_SERVICECLASS = "/Resource/Profile/ServiceClass/text()";
	public static final String ATTR_ENDPOINT = "/Resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint/text()";
	public static final String ATTR_ENTRYNAME = "/Resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint/@EntryName";
	public static final String ATTR_STATUS = "/Resource/Profile/DeploymentData/Status/text()";
	
	/**
	 * Class constructor
	 * @param scope
	 * @param resourceTypeName
	 * @param baseISQuery
	 */
	public RunningInstanceResource(String scope) {
		super(scope, TYPE_NAME, BASE_IS_QUERY);
		
		this.addAttributeName(ATTR_SERVICENAME);
		this.addAttributeName(ATTR_SERVICECLASS);
		this.addAttributeName(ATTR_ENDPOINT);
		this.addAttributeName(ATTR_ENTRYNAME);
		this.addAttributeName(ATTR_STATUS);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.resourceManagement.GCUBEResource#fromXML(org.gcube.common.core.informationsystem.client.XMLResult)
	 */
	void fromXML(String xmlResult) {
		super.fromXML(xmlResult);
		
		try {
			this.setAttributeValue(ATTR_SERVICENAME, evaluateExpression(xmlResult, ATTR_SERVICENAME));
			this.setAttributeValue(ATTR_SERVICECLASS, evaluateExpression(xmlResult, ATTR_SERVICECLASS));
			this.setAttributeValue(ATTR_ENDPOINT, evaluateExpression(xmlResult, ATTR_ENDPOINT));
			this.setAttributeValue(ATTR_ENTRYNAME, evaluateExpression(xmlResult, ATTR_ENTRYNAME));
			this.setAttributeValue(ATTR_STATUS, evaluateExpression(xmlResult, ATTR_STATUS));
		} catch (Exception e) { }
	}

}
