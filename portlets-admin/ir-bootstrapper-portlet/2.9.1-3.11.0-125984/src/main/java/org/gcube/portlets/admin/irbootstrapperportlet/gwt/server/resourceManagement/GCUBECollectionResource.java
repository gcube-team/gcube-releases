/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class GCUBECollectionResource extends GCUBEResource {
	
	public static final String TYPE_NAME = "GCUBECollectionType";
	
	private static final String BASE_IS_QUERY = BASE_NS + "for $result in collection(\"/db/Profiles/GenericResource\")//Document/Data/is:Profile/Resource " + QUERY_CONDITION_PLACEHOLDER + " return $result";
	
	public static final String ATTR_COLLECTIONNAME = "/Resource/Profile/Name/text()";
	public static final String ATTR_COLLECTIONDESC = "/Resource/Profile/Description/text()";
	public static final String ATTR_ISUSER = "/Resource/Profile/Body/SourceProperties/user/text()";
	public static final String ATTR_TYPE = "/Resource/Profile/Body/SourceProperties/type/text()";
	public static final String ATTR_SECONDARYTYPE = "/Resource/Profile/SecondaryType/text()";

	
	/**
	 * Class constructor
	 * @param scope
	 * @param resourceTypeName
	 * @param baseISQuery
	 */
	public GCUBECollectionResource(String scope) {
		super(scope, TYPE_NAME, BASE_IS_QUERY);
			
		this.addAttributeName(ATTR_COLLECTIONNAME);
		this.addAttributeName(ATTR_COLLECTIONDESC);
		this.addAttributeName(ATTR_TYPE);
		this.addAttributeName(ATTR_ISUSER);
		this.addAttributeName(ATTR_SECONDARYTYPE);
		
		this.setAttributeValue(ATTR_SECONDARYTYPE, "DataSource");
		this.setAttributeValue(ATTR_ISUSER, "true");
	//	this.setAttributeValue(ATTR_TYPE, "opensearch");
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.resourceManagement.GCUBEResource#fromXML(org.gcube.common.core.informationsystem.client.XMLResult)
	 */
	void fromXML(String xmlResult) {
		super.fromXML(xmlResult);
		
		try {
			this.setAttributeValue(ATTR_COLLECTIONNAME, evaluateExpression(xmlResult, ATTR_COLLECTIONNAME));
			this.setAttributeValue(ATTR_COLLECTIONDESC, evaluateExpression(xmlResult, ATTR_COLLECTIONDESC));
			this.setAttributeValue(ATTR_TYPE, evaluateExpression(xmlResult, ATTR_TYPE));
			this.setAttributeValue(ATTR_ISUSER, evaluateExpression(xmlResult, ATTR_ISUSER));
		} catch (Exception e) { }
	}

}
