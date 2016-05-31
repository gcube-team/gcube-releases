// /**
// * 
// */
//package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.IRBootstrapperData;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ForwardIndexNodeWSResource;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceCache;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.TreeManagerCollectionResource;
//import org.w3c.dom.Document;
//
//
///**
// * @author Spyros Boutsis, NKUA
// *
// */
//public class ForwardIndexNodeDataType extends DataType<ForwardIndexNodeWSResource> {
//	
//	public static final String ATTR_COLLECTIONID = "IndexedCollectionID";
//	public static final String ATTR_COLLECTIONNAME = "IndexedCollectionName";
//	public static final String ATTR_INDEXID = "IndexID";
//	public static final String ATTR_KEYNAMES = "IndexedKeyNames";
//	public static final String ATTR_KEYTYPES = "IndexedKeyTypes";
//	
//	/** Logger */
//	private static Logger logger = Logger.getLogger(ForwardIndexNodeDataType.class);
//	
//	public ForwardIndexNodeDataType(String scope, ForwardIndexNodeWSResource associatedResource) {
//		super(scope, associatedResource, ForwardIndexNodeWSResource.class);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getDataTypeAttrToResourceAttrMappings()
//	 */
//	@Override
//	public Map<String, String> getDataTypeAttrToResourceAttrMappings() {
//		Map<String,String> ret = new HashMap<String,String>();
//		ret.put(ATTR_COLLECTIONID, ForwardIndexNodeWSResource.ATTR_COLLECTIONID);
//		ret.put(ATTR_INDEXID, ForwardIndexNodeWSResource.ATTR_INDEXID);
//		ret.put(ATTR_KEYNAMES, ForwardIndexNodeWSResource.ATTR_KEYNAMES);
//		ret.put(ATTR_KEYTYPES, ForwardIndexNodeWSResource.ATTR_KEYTYPES);
//		return ret;
//	}
//	
//	/*
//	 * (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#copyDataFromResource()
//	 */
//	public void copyDataFromResource() throws Exception {
//		super.copyDataFromResource();
//		
//		/* Construct a new GCUBECollectionResource based on the related ID of this index. Then
//		 * fetch the corresponding resource from the ResourceCache and retrieve the collection's name from it.
//		 */
//		TreeManagerCollectionResource c = new TreeManagerCollectionResource(getScope());
//		c.setAttributeValue(TreeManagerCollectionResource.ATTR_ID, this.getCollectionID());
//		ResourceCache cache = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope()).getResourceCache();
//		List<TreeManagerCollectionResource> r = cache.getResourcesWithGivenAttributes(c);
//		if (r.size() == 1) {
//			String colName = c.getAttributeValue(TreeManagerCollectionResource.ATTR_COLNAME).get(0);
//			this.setCollectionName(colName);
//		}
//		
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#doesIdentifyUniqueResource()
//	 */
//	@Override
//	public boolean doesIdentifyUniqueResource() {
//		/* This FullTextIndexDataType object uniquely identifies a resource if one of the
//		 * following is true:
//		 * 1) the "IndexedCollectionID" attribute is set (the object references an index related to
//		 *    a specific collection, there cannot be more than one indices for a collection)
//		 * 2) the "IndexID" attribute is set (the object references a specific index because index IDs are
//		 *    unique)
//		 */
//		try {
//			//return (this.getAttributeValue(ATTR_COLLECTIONID)!=null || this.getAttributeValue(ATTR_INDEXID)!=null);
//			return false;
//		} catch (Exception e) {
//			logger.error("Error while trying to retrieve ForwardIndexNodeDataType attribute values.", e);
//			return false;
//		}
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getUID()
//	 */
//	@Override
//	public String getUID() {
//		try {
//			return "ForwardIndexNode_" + this.getAttributeValue(ATTR_INDEXID);
//		} catch (Exception e) {
//			return null;
//		}
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getUIDescription()
//	 */
//	@Override
//	public String getUIDescription() throws Exception {
//		return getUIName();
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getUIName()
//	 */
//	@Override
//	public String getUIName() throws Exception {
//		try {
//			return this.getAttributeValue(ATTR_COLLECTIONNAME);
//		} catch (Exception e) {
//			logger.error("Failed to construct the name of a ForwardIndexNodeDataType instance.", e);
//			throw new Exception("Failed to construct the name of a ForwardIndexNodeDataType instance.");
//		}
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getXMLTypeDefinitionDocument()
//	 */
//	@Override
//	public Document getXMLTypeDefinitionDocument() throws Exception {
//		return Util.parseXMLString(
//				"<type> " +
//					"<IndexedCollectionID/> " +
//					"<IndexedCollectionName/>" +
//					"<IndexID/> " +
//					"<IndexedKeyNames/>" +
//					"<IndexedKeyTypes/>" +
//				"</type>"
//			);
//	}
//	
//	/*
//	 * (non-Javadoc)
//	 * @see java.lang.Object#clone()
//	 */
//	public Object clone() throws CloneNotSupportedException {
//		try {
//			ForwardIndexNodeDataType dt = (ForwardIndexNodeDataType) super.clone();
//			return dt;
//		} catch (Exception e) {
//			logger.error("Error while cloning DataType object", e);
//			return null;
//		}
//	}
//	
//	public String getIndexID() { try { return getAttributeValue(ATTR_INDEXID); } catch (Exception e) { return null; } }
//	public void setIndexID(String s) throws Exception { setAttributeValue(ATTR_INDEXID, s); }
//	
//	public String getCollectionID() { try { return getAttributeValue(ATTR_COLLECTIONID); } catch (Exception e) { return null; } }
//	public void setCollectionID(String s) throws Exception { setAttributeValue(ATTR_COLLECTIONID, s); }
//	
//	public String getCollectionName() { try { return getAttributeValue(ATTR_COLLECTIONNAME); } catch (Exception e) { return null; } }
//	public void setCollectionName(String s) throws Exception { setAttributeValue(ATTR_COLLECTIONNAME, s); }
//	
//	public String getIndexedKeyNames() { try { return getAttributeValue(ATTR_KEYNAMES); } catch (Exception e) { return null; } }
//	public void setIndexedKeyNames(String s) throws Exception { setAttributeValue(ATTR_KEYNAMES, s); }
//
//	public String getIndexedKeyTypes() { try { return getAttributeValue(ATTR_KEYTYPES); } catch (Exception e) { return null; } }
//	public void setIndexedKeyTypes(String s) throws Exception { setAttributeValue(ATTR_KEYTYPES, s); }
//
//	
//}
