/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.GCUBECollectionResource;
import org.w3c.dom.Document;


/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class GCUBECollectionDataType extends DataType<GCUBECollectionResource> {
	
	public static final String ATTR_COLLECTIONNAME = "ColName";
	public static final String ATTR_COLID = "ColID";
	public static final String ATTR_COLDESC = "ColDesc";
	public static final String ATTR_COLTYPE = "ColType";
	public static final String ATTR_COLISUSER = "ColIsUser";
	
	/** Logger */
	private static Logger logger = Logger.getLogger(GCUBECollectionDataType.class);
	
	/**
	 * Class constructor
	 * @throws Exception
	 */
	public GCUBECollectionDataType(String scope, GCUBECollectionResource associatedResource) throws Exception {
		super(scope, associatedResource, GCUBECollectionResource.class);
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getDataTypeAttrToResourceAttrMappings()
	 */
	@Override
	public Map<String, String> getDataTypeAttrToResourceAttrMappings() {
		Map<String,String> ret = new HashMap<String,String>();
		ret.put(ATTR_COLLECTIONNAME, GCUBECollectionResource.ATTR_COLLECTIONNAME);
		ret.put(ATTR_COLID, GCUBECollectionResource.ATTR_ID);
		ret.put(ATTR_COLDESC, GCUBECollectionResource.ATTR_COLLECTIONDESC);
		ret.put(ATTR_COLTYPE, GCUBECollectionResource.ATTR_TYPE);
		ret.put(ATTR_COLISUSER, GCUBECollectionResource.ATTR_ISUSER);
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#doesIdentifyUniqueResource()
	 */
	@Override
	public boolean doesIdentifyUniqueResource() {
		/* This MetadataCollectionDataType object uniquely identifies a resource if one of the
		 * following is true:
		 * 1) the "ColID" attribute is set (the object references a specific metadata collection)
		 * 2) the "ColName", "SchemaName" AND "Language" attributes are set (the object references
		 *    a metadata collection with a given name, schema and language => a specific collection) 
		 */
		try {
			return ((this.getAttributeValue(ATTR_COLID) != null));
		} catch (Exception e) {
			logger.error("Error while trying to retrieve GCUBECollectionDataType attribute values.", e);
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getUID()
	 */
	@Override
	public String getUID() {
		try {
			return "GCUBECollection_" + this.getAttributeValue(ATTR_COLID);
		} catch (Exception e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getUIDescription()
	 */
	@Override
	public String getUIDescription() throws Exception {
		return getUIName();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getUIName()
	 */
	@Override
	public String getUIName() throws Exception {
		try {
			String uiName = this.getAttributeValue(ATTR_COLLECTIONNAME) + " - " + 
			this.getAttributeValue(ATTR_COLID);
			
			return uiName;
		} catch (Exception e) {
			logger.error("Failed to construct the name of a GCUBECollectionDataType instance.", e);
			throw new Exception("Failed to construct the name of a GCUBECollectionDataType instance.");
		}
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getXMLTypeDefinitionDocument()
	 */
	@Override
	public Document getXMLTypeDefinitionDocument() throws Exception {
		return Util.parseXMLString(
				"<type> " +
					"<ColName/> " +
					"<ColID/>" +
					"<ColDesc/>" +
					"<ColType/>" +
					"<ColIsUser/>" +
				"</type>"
			);
	}
	
	public String getCollectionName() { try { return getAttributeValue(ATTR_COLLECTIONNAME); } catch (Exception e) { return null; } }
	public void setCollectionName(String s) throws Exception { setAttributeValue(ATTR_COLLECTIONNAME, s); }
	
	public String getCollectionID() { try { return getAttributeValue(ATTR_COLID); } catch (Exception e) { return null; } }
	public void setCollectionID(String s) throws Exception { setAttributeValue(ATTR_COLID, s); }
	
	public String isUser() { try { return getAttributeValue(ATTR_COLISUSER); } catch (Exception e) { return null; } }
	public void setIsUser(String s) throws Exception { setAttributeValue(ATTR_COLISUSER, s); }

	public String hasType() { try { return getAttributeValue(ATTR_COLTYPE); } catch (Exception e) { return null; } }
	public void setHasType(String s) throws Exception { setAttributeValue(ATTR_COLTYPE, s); }
	
	public String getCollectionDesc() { try { return getAttributeValue(ATTR_COLDESC); } catch (Exception e) { return null; } }
	public void setCollectionDesc(String s) throws Exception { setAttributeValue(ATTR_COLDESC, s); }
}
