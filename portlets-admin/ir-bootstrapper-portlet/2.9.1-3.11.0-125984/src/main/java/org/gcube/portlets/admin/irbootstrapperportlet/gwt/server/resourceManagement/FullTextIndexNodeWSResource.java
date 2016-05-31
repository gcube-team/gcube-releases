/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class FullTextIndexNodeWSResource extends GCUBEResource {
	
	public static final String TYPE_NAME = "FullTextIndexNodeResource";
	
	private static final String BASE_IS_QUERY = BASE_NS + "for $result in collection(\"/db/Profiles/GenericResource\")//Document/Data/is:Profile/Resource " + QUERY_CONDITION_PLACEHOLDER + " return $result";
		
	public static final String ATTR_COLLECTIONID = "/Resource/Profile/Body/indexResource/collections/text()";
	public static final String ATTR_INDEXID = "/Resource/Profile/Body/indexResource/indexID/text()";
	public static final String ATTR_SECONDARYTYPE = "/Resource/Profile/SecondaryType/text()";
	
	/**
	 * Class constructor
	 * @param scope
	 * @param resourceTypeName
	 * @param baseISQuery
	 */
	public FullTextIndexNodeWSResource(String scope) {
		super(scope, TYPE_NAME, BASE_IS_QUERY);
		
		this.addAttributeName(ATTR_COLLECTIONID);
		this.addAttributeName(ATTR_INDEXID);
		
		this.setAttributeValue(ATTR_SECONDARYTYPE, "IndexResources");
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
		} catch (Exception e) { }
	}
}
