 /**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.IRBootstrapperData;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.FullTextIndexNodeWSResource;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceCache;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.TreeManagerCollectionResource;
import org.w3c.dom.Document;


/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class FullTextIndexNodeDataType extends DataType<FullTextIndexNodeWSResource> {
	
	public static final String ATTR_COLLECTIONID = "IndexedCollectionID";
	public static final String ATTR_COLLECTIONNAME = "IndexedCollectionName";
	public static final String ATTR_INDEXID = "IndexID";
	
	/** Logger */
	private static Logger logger = Logger.getLogger(FullTextIndexNodeDataType.class);
	
	public FullTextIndexNodeDataType(String scope, FullTextIndexNodeWSResource associatedResource) {
		super(scope, associatedResource, FullTextIndexNodeWSResource.class);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getDataTypeAttrToResourceAttrMappings()
	 */
	@Override
	public Map<String, String> getDataTypeAttrToResourceAttrMappings() {
		Map<String,String> ret = new HashMap<String,String>();
		ret.put(ATTR_COLLECTIONID, FullTextIndexNodeWSResource.ATTR_COLLECTIONID);
		ret.put(ATTR_INDEXID, FullTextIndexNodeWSResource.ATTR_INDEXID);
		return ret;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#copyDataFromResource()
	 */
	public void copyDataFromResource() throws Exception {
		super.copyDataFromResource();
		
		/* Construct a new TreeCollectionResource based on the related ID of this index. Then
		 * fetch the corresponding resource from the ResourceCache and retrieve the collection's name from it.
		 */
		TreeManagerCollectionResource c = new TreeManagerCollectionResource(getScope());
		c.setAttributeValue(TreeManagerCollectionResource.ATTR_ID, this.getCollectionID());
		ResourceCache cache = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope()).getResourceCache();
		List<TreeManagerCollectionResource> r = cache.getResourcesWithGivenAttributes(c);
		if (r.size() == 1) {
			String colName = c.getAttributeValue(TreeManagerCollectionResource.ATTR_COLNAME).get(0);
			this.setCollectionName(colName);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#doesIdentifyUniqueResource()
	 */
	@Override
	public boolean doesIdentifyUniqueResource() {
		/* This FullTextIndexDataType object uniquely identifies a resource if one of the
		 * following is true:
		 * 1) the "IndexedCollectionID" attribute is set (the object references an index related to
		 *    a specific collection, there cannot be more than one indices for a collection)
		 * 2) the "IndexID" attribute is set (the object references a specific index because index IDs are
		 *    unique)
		 */
		try {
			//return (this.getAttributeValue(ATTR_INDEXID)!=null);// || this.getAttributeValue(ATTR_CLUSTERID)!=null);
			return false;
		} catch (Exception e) {
			logger.error("Error while trying to retrieve FullTextIndexNodeDataType attribute values.", e);
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getUID()
	 */
	@Override
	public String getUID() {
		try {
			return "FullTextIndexNode_" + this.getAttributeValue(ATTR_INDEXID);
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
			//return this.getAttributeValue(ATTR_CLUSTERID);
			return this.getAttributeValue(ATTR_INDEXID);
		} catch (Exception e) {
			logger.error("Failed to construct the name of a FullTextIndexNodeDataType instance.", e);
			throw new Exception("Failed to construct the name of a FullTextIndexNodeDataType instance.");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getXMLTypeDefinitionDocument()
	 */
	@Override
	public Document getXMLTypeDefinitionDocument() throws Exception {
		return Util.parseXMLString(
				"<type> " +
					"<IndexedCollectionID/> " +
					"<IndexedCollectionName/>" +
					"<IndexID/> " +
				"</type>"
			);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
		try {
			FullTextIndexNodeDataType dt = (FullTextIndexNodeDataType) super.clone();
			return dt;
		} catch (Exception e) {
			logger.error("Error while cloning DataType object", e);
			return null;
		}
	}
	
	public String getIndexID() { try { return getAttributeValue(ATTR_INDEXID); } catch (Exception e) { return null; } }
	public void setIndexID(String s) throws Exception { setAttributeValue(ATTR_INDEXID, s); }
	
	public String getCollectionID() { try { return getAttributeValue(ATTR_COLLECTIONID); } catch (Exception e) { return null; } }
	public void setCollectionID(String s) throws Exception { setAttributeValue(ATTR_COLLECTIONID, s); }
	
	public String getCollectionName() { try { return getAttributeValue(ATTR_COLLECTIONNAME); } catch (Exception e) { return null; } }
	public void setCollectionName(String s) throws Exception { setAttributeValue(ATTR_COLLECTIONNAME, s); }

	
}
