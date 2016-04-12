/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public abstract class GCUBEResource extends Resource {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(GCUBEResource.class);
		
	public static final String ATTR_ID = "/Resource/ID/text()";
	
	/**
	 * Class constructor
	 * @param scope
	 * @param resourceTypeName
	 * @param baseISQuery
	 */
	GCUBEResource(String scope, String typeName, String baseISQuery) {
		super(scope, typeName, baseISQuery);
		
		this.addAttributeName(ATTR_ID);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.resourceManagement.Resource#fromXML(org.gcube.common.core.informationsystem.client.XMLResult)
	 */
	@Override
	void fromXML(String xmlResult) {
		try{
			//logger.debug("FROMXML ID evaluation on -> " + xmlResult);
			List<String> result = evaluateExpression(xmlResult, ATTR_ID);
			this.setAttributeValue(ATTR_ID, result);
		} catch (Exception e) { }
	}
	
}
