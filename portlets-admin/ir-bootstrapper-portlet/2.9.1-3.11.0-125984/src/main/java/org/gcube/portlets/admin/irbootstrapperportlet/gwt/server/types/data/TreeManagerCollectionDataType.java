/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.TreeManagerCollectionResource;
import org.w3c.dom.Document;


/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class TreeManagerCollectionDataType extends DataType<TreeManagerCollectionResource> {
	
	public static final String ATTR_COLNAME = "ColName";
	public static final String ATTR_COLID = "ColID";
	public static final String ATTR_TYPE = "Type";
	public static final String ATTR_NUMOFMEMBERS = "NumOfMembers";
	
	/** Logger */
	private static Logger logger = Logger.getLogger(TreeManagerCollectionDataType.class);
	
	/**
	 * Class constructor
	 * @throws Exception
	 */
	public TreeManagerCollectionDataType(String scope, TreeManagerCollectionResource associatedResource) throws Exception {
		super(scope, associatedResource, TreeManagerCollectionResource.class);
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getDataTypeAttrToResourceAttrMappings()
	 */
	@Override
	public Map<String, String> getDataTypeAttrToResourceAttrMappings() {
		Map<String,String> ret = new HashMap<String,String>();
		ret.put(ATTR_COLNAME, TreeManagerCollectionResource.ATTR_COLNAME);
		ret.put(ATTR_COLID, TreeManagerCollectionResource.ATTR_COLID);
		ret.put(ATTR_TYPE, TreeManagerCollectionResource.ATTR_TYPE);
		ret.put(ATTR_NUMOFMEMBERS, TreeManagerCollectionResource.ATTR_NUMOFMEMBERS);
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#doesIdentifyUniqueResource()
	 */
	@Override
	public boolean doesIdentifyUniqueResource() {
		/* This TreeManagerCollectionDataType object uniquely identifies a resource if one of the
		 * following is true:
		 * 1) the "ColID" attribute is set (the object references a specific collection)
		 */
		try {
			return ((this.getAttributeValue(ATTR_COLID) != null));
		} catch (Exception e) {
			logger.error("Error while trying to retrieve TreeManagerCollectionDataType attribute values.", e);
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getUID()
	 */
	@Override
	public String getUID() {
		try {
			return "TreeManagerCollection_" + this.getAttributeValue(ATTR_COLID);
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
			String uiName = this.getAttributeValue(ATTR_COLNAME) + " - " + 
			this.getAttributeValue(ATTR_COLID);
			
			return uiName;
		} catch (Exception e) {
			logger.error("Failed to construct the name of a TreeManagerCollectionDataType instance.", e);
			throw new Exception("Failed to construct the name of a TreeManagerCollectionDataType instance.");
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
					"<Type/>" +
					"<NumOfMembers/>" +
				"</type>"
			);
	}
	
	public String getCollectionName() { try { return getAttributeValue(ATTR_COLNAME); } catch (Exception e) { return null; } }
	public void setCollectionName(String s) throws Exception { setAttributeValue(ATTR_COLNAME, s); }
	
	public String getCollectionID() { try { return getAttributeValue(ATTR_COLID); } catch (Exception e) { return null; } }
	public void setCollectionID(String s) throws Exception { setAttributeValue(ATTR_COLID, s); }
	
	public String getCollectionType() { try { return getAttributeValue(ATTR_TYPE); } catch (Exception e) { return null; } }
	public void setCollectionType(String s) throws Exception { setAttributeValue(ATTR_TYPE, s); }
	
	public String isUser() { try { return getAttributeValue(ATTR_NUMOFMEMBERS); } catch (Exception e) { return null; } }
	public void setIsUser(String s) throws Exception { setAttributeValue(ATTR_NUMOFMEMBERS, s); }

}
