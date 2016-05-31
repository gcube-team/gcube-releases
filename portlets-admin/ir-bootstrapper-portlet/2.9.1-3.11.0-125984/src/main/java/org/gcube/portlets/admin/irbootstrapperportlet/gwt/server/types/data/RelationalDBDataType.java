/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.RelationalDBDataSource;
import org.w3c.dom.Document;


/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class RelationalDBDataType extends DataType<RelationalDBDataSource> {
	
	public static final String ATTR_DATASOURCE_NAME = "DataSourceName";
	public static final String ATTR_DATASOURCE_ID = "DataSourceID";
	public static final String ATTR_SOURCE_NAME = "DataSourceSourceName";
//	public static final String ATTR_TYPE = "DBDataSourceType";
	public static final String ATTR_PROPERTIES_NAME = "DataSourcePropertiesName";
	
	
	/** Logger */
	private static Logger logger = Logger.getLogger(RelationalDBDataType.class);
	
	/**
	 * Class constructor
	 * @throws Exception
	 */
	public RelationalDBDataType(String scope, RelationalDBDataSource associatedResource) throws Exception {
		super(scope, associatedResource, RelationalDBDataSource.class);
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getDataTypeAttrToResourceAttrMappings()
	 */
	@Override
	public Map<String, String> getDataTypeAttrToResourceAttrMappings() {
		Map<String,String> ret = new HashMap<String,String>();
		ret.put(ATTR_DATASOURCE_NAME, RelationalDBDataSource.ATTR_DATASOURCE_NAME);
		ret.put(ATTR_DATASOURCE_ID, RelationalDBDataSource.ATTR_ID);
		ret.put(ATTR_SOURCE_NAME, RelationalDBDataSource.ATTR_SOURCE_NAME);
		ret.put(ATTR_PROPERTIES_NAME, RelationalDBDataSource.ATTR_PROPERTIES_NAME);
//		ret.put(ATTR_TYPE, RelationalDBDataSource.ATTR_TYPE);
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
			return ((this.getAttributeValue(ATTR_DATASOURCE_ID) != null));
		} catch (Exception e) {
			logger.error("Error while trying to retrieve RelationalDBDataType attribute values.", e);
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getUID()
	 */
	@Override
	public String getUID() {
		try {
			return "RelationalDB_DataSource_" + this.getAttributeValue(ATTR_DATASOURCE_ID);
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
			String uiName = this.getAttributeValue(ATTR_DATASOURCE_NAME) + " - " + 
			this.getAttributeValue(ATTR_DATASOURCE_ID);
			
			return uiName;
		} catch (Exception e) {
			logger.error("Failed to construct the name of a RelationalDBDataType instance.", e);
			throw new Exception("Failed to construct the name of a RelationalDBDataType instance.");
		}
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.data.DataType#getXMLTypeDefinitionDocument()
	 */
	@Override
	public Document getXMLTypeDefinitionDocument() throws Exception {
		return Util.parseXMLString(
				"<type> " +
					"<DataSourceName/> " +
					"<DataSourceID/>" +
					"<DataSourceSourceName/>" +
					"<DataSourcePropertiesName/>" +
				"</type>"
			);
	}
	
	public String getDataSourceName() { try { return getAttributeValue(ATTR_DATASOURCE_NAME); } catch (Exception e) { return null; } }
	public void setDataSourceName(String s) throws Exception { setAttributeValue(ATTR_DATASOURCE_NAME, s); }
	
	public String getDataSourceID() { try { return getAttributeValue(ATTR_DATASOURCE_ID); } catch (Exception e) { return null; } }
	public void setDataSourceID(String s) throws Exception { setAttributeValue(ATTR_DATASOURCE_ID, s); }
	
	public String getDataSourceSourceName() { try { return getAttributeValue(ATTR_SOURCE_NAME); } catch (Exception e) { return null; } }
	public void setDataSourceSourceName(String s) throws Exception { setAttributeValue(ATTR_SOURCE_NAME, s); }

	//public String getDataSourceType() { try { return getAttributeValue(ATTR_TYPE); } catch (Exception e) { return null; } }
	//public void setDataSourceType(String s) throws Exception { setAttributeValue(ATTR_TYPE, s); }
	
	public String getDataSourcePropertiesName() { try { return getAttributeValue(ATTR_PROPERTIES_NAME); } catch (Exception e) { return null; } }
	public void setDataSourcePropertiesName(String s) throws Exception { setAttributeValue(ATTR_PROPERTIES_NAME, s); }
}
