/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class RelationalDBDataSource extends GCUBEResource {
	
	public static final String TYPE_NAME = "RelationalDBType";
	
	private static final String BASE_IS_QUERY = BASE_NS + "for $result in collection(\"/db/Profiles/GenericResource\")//Document/Data/is:Profile/Resource " + QUERY_CONDITION_PLACEHOLDER + " return $result";
	
	public static final String ATTR_DATASOURCE_NAME = "/Resource/Profile/Name/text()";
	public static final String ATTR_SOURCE_NAME = "/Resource/Profile/Body/DBProps/sourcename/text()";
	public static final String ATTR_PROPERTIES_NAME = "/Resource/Profile/Body/DBProps/propsname/text()";
	public static final String ATTR_TYPE = "/Resource/Profile/Body/DBProps/sourcetype/text()";
	public static final String ATTR_SECONDARYTYPE = "/Resource/Profile/SecondaryType/text()";
	

	
	/**
	 * Class constructor
	 * @param scope
	 * @param resourceTypeName
	 * @param baseISQuery
	 */
	public RelationalDBDataSource(String scope) {
		super(scope, TYPE_NAME, BASE_IS_QUERY);
			
		this.addAttributeName(ATTR_DATASOURCE_NAME);
		this.addAttributeName(ATTR_SOURCE_NAME);
		this.addAttributeName(ATTR_PROPERTIES_NAME);
		
		this.addAttributeName(ATTR_SECONDARYTYPE);
		this.addAttributeName(ATTR_TYPE);
		
		this.setAttributeValue(ATTR_TYPE, "RelationalDB");
		this.setAttributeValue(ATTR_SECONDARYTYPE, "HarvesterResource");
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.resourceManagement.GCUBEResource#fromXML(org.gcube.common.core.informationsystem.client.XMLResult)
	 */
	void fromXML(String xmlResult) {
		super.fromXML(xmlResult);
		
		try {
			this.setAttributeValue(ATTR_DATASOURCE_NAME, evaluateExpression(xmlResult, ATTR_DATASOURCE_NAME));
			this.setAttributeValue(ATTR_SOURCE_NAME, evaluateExpression(xmlResult, ATTR_SOURCE_NAME));
			this.setAttributeValue(ATTR_PROPERTIES_NAME, evaluateExpression(xmlResult, ATTR_PROPERTIES_NAME));
			this.setAttributeValue(ATTR_TYPE, evaluateExpression(xmlResult, ATTR_TYPE));
		} catch (Exception e) { }
	}

}
