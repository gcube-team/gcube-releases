///**
// * 
// */
//package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.IRBootstrapperData;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.GeoIndexManagementWSResource;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.MetadataCollectionResource;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceCache;
//import org.w3c.dom.Document;
//
//
///**
// * @author Spyros Boutsis, NKUA
// *
// */
//public class GeoIndexDataType extends DataType<GeoIndexManagementWSResource> {
//	
//	public static final String ATTR_COLLECTIONID = "IndexedCollectionID";
//	public static final String ATTR_COLLECTIONNAME = "IndexedCollectionName";
//	public static final String ATTR_INDEXID = "IndexID";
//	
//	/** Logger */
//	private static Logger logger = Logger.getLogger(GeoIndexDataType.class);
//	
//	public GeoIndexDataType(GCUBEScope scope, GeoIndexManagementWSResource associatedResource) {
//		super(scope, associatedResource, GeoIndexManagementWSResource.class);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getDataTypeAttrToResourceAttrMappings()
//	 */
//	@Override
//	public Map<String, String> getDataTypeAttrToResourceAttrMappings() {
//		Map<String,String> ret = new HashMap<String,String>();
//		ret.put(ATTR_COLLECTIONID, GeoIndexManagementWSResource.ATTR_COLLECTIONID);
//		ret.put(ATTR_INDEXID, GeoIndexManagementWSResource.ATTR_INDEXID);
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
//		/* Construct a new MetadataCollectionResource based on the related MCID of this index. Then
//		 * fetch the corresponding resource from the ResourceCache and retrieve the MC's name from it.
//		 */
//		MetadataCollectionResource mc = new MetadataCollectionResource(getScope());
//		mc.setAttributeValue(MetadataCollectionResource.ATTR_COLID, this.getCollectionID());
//		ResourceCache cache = IRBootstrapperData.getInstance().getBootstrappingConfiguration(getScope()).getResourceCache();
//		List<MetadataCollectionResource> r = cache.getResourcesWithGivenAttributes(mc);
//		if (r.size() == 1) {
//			String colName = mc.getAttributeValue(MetadataCollectionResource.ATTR_COLNAME).get(0);
//			this.setCollectionName(colName);
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#doesIdentifyUniqueResource()
//	 */
//	@Override
//	public boolean doesIdentifyUniqueResource() {
//		/* This GeoIndexDataType object uniquely identifies a resource if one of the
//		 * following is true:
//		 * 1) the "IndexedCollectionID" attribute is set (the object references an index related to
//		 *    a specific metadata collection, there cannot be more than one indices for a collection)
//		 * 2) the "IndexID" attribute is set (the object references a specific index because index IDs are
//		 *    unique)
//		 */
//		try {
//			return (this.getAttributeValue(ATTR_COLLECTIONID)!=null || this.getAttributeValue(ATTR_INDEXID)!=null);
//		} catch (Exception e) {
//			logger.error("Error while trying to retrieve GeoIndexDataType attribute values.", e);
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
//			return "GeoIndex_" + this.getAttributeValue(ATTR_INDEXID);
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
//			logger.error("Failed to construct the name of a GeoIndexDataType instance.", e);
//			throw new Exception("Failed to construct the name of a GeoIndexDataType instance.");
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
//			GeoIndexDataType dt = (GeoIndexDataType) super.clone();
//			return dt;
//		} catch (Exception e) {
//			logger.error("Error while cloning GeoIndexDataType object", e);
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
//	
//}
