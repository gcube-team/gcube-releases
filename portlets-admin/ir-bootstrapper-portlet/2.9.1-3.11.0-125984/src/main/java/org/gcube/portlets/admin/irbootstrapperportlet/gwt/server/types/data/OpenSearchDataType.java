package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.OpenSearchWSResource;
import org.w3c.dom.Document;


public class OpenSearchDataType extends DataType<OpenSearchWSResource> {
	
	public static final String ATTR_COLLECTIONID = "OpenSearchCollectionID";
	
	/** Logger */
	private static Logger logger = Logger.getLogger(OpenSearchDataType.class);

	public OpenSearchDataType(String scope, OpenSearchWSResource associatedResource) {
		super(scope, associatedResource, OpenSearchWSResource.class);
	}

	@Override
	public Map<String, String> getDataTypeAttrToResourceAttrMappings() {
		Map<String,String> ret = new HashMap<String,String>();
		ret.put(ATTR_COLLECTIONID, OpenSearchWSResource.ATTR_COLLECTIONID);
		return ret;
	}
	
	@Override
	public boolean doesIdentifyUniqueResource() {
		/* This OpenSearchDataType object uniquely identifies a resource if:
		 * the "OpenSearchMetadataCollectionID" attribute is set. A open search ws resource is mapped to only one metadata collection
		 * based on its ID
		 */
		try {
			return (this.getAttributeValue(ATTR_COLLECTIONID)!=null);
		} catch (Exception e) {
			logger.error("Error while trying to retrieve OpenSearchDataType attribute values.", e);
			return false;
		}
	}

	@Override
	public String getUID() {
		try {
			return "OpenSearchResource_" + this.getAttributeValue(ATTR_COLLECTIONID);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getUIDescription() throws Exception {
		return getUIName();
	}

	@Override
	public String getUIName() throws Exception {
		try {
			return this.getAttributeValue(ATTR_COLLECTIONID);
		} catch (Exception e) {
			logger.error("Failed to construct the name of a OpenSearchDataType instance.", e);
			throw new Exception("Failed to construct the name of a OpenSearchDataType instance.");
		}
	}

	@Override
	public Document getXMLTypeDefinitionDocument() throws Exception {
		return Util.parseXMLString(
				"<type> " +
					"<OpenSearchCollectionID/> " +
				"</type>"
			);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		try {
			OpenSearchDataType ot = (OpenSearchDataType) super.clone();
			return ot;
		} catch (Exception e) {
			logger.error("Error while cloning DataType object", e);
			return null;
		}
	}
	
	public String getCollectionID() { try { return getAttributeValue(ATTR_COLLECTIONID); } catch (Exception e) { return null; } }
	public void setCollectionID(String s) throws Exception { setAttributeValue(ATTR_COLLECTIONID, s); }

}
