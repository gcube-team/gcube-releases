package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

/**
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SRUResource extends GCUBEResource {
	
	/* A desired name */
	public static final String TYPE_NAME = "SRUResource";
	
	private static final String BASE_IS_QUERY = BASE_NS + "for $result in collection(\"/db/Profiles/GenericResource\")//Document/Data/is:Profile/Resource " + QUERY_CONDITION_PLACEHOLDER + " return $result";
	
	public static final String ATTR_COLLECTIONID = "/Resource/Profile/Body/sruConsumerResource/collectionID/text()";
	public static final String ATTR_SECONDARYTYPE = "/Resource/Profile/SecondaryType/text()";
	
	//public static final String ATTR_COLLECTIONID = "/Document/Data/child::*[local-name()='CollectionID']/text()";

	public SRUResource(String scope) {
		//super(scope, TYPE_NAME, "OpenSearchDataSource");
		//this.addAttributeName(ATTR_COLLECTIONID);
		
		super(scope, TYPE_NAME, BASE_IS_QUERY);
		this.addAttributeName(ATTR_COLLECTIONID);	
		this.setAttributeValue(ATTR_SECONDARYTYPE, "SruConsumerResources");
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.resourceHandling.Resource#fromXML(org.gcube.common.core.informationsystem.client.XMLResult)
	 */
	@Override
	void fromXML(String xmlResult) {
		super.fromXML(xmlResult);
		try {
			this.setAttributeValue(ATTR_COLLECTIONID, evaluateExpression(xmlResult, ATTR_COLLECTIONID));
		} catch (Exception e) { }
	}
}
