package gr.cite.geoanalytics.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DataLayerConfig {

	private static final String persistenceUnitDefault = "gr.cite.geoanalytics";
	private static final String shapeIdColumnNameDefault = "\"SHP_ID\"";
	private static final String shapeDataColumnNameDefault = "\"SHP_ExtraData\"";
	private static final String shapeGeographyColumnNameDefault = "\"SHP_Geography\"";
	private static final String shapeNameColumnNameDefault = "\"SHP_Name\"";
	private static final String shapeCodeColumnNameDefault = "\"SHP_Code\"";
	private static final String shapeImportColumnNameDefault = "\"SHP_ShapeImport\"";
	private static final String shapeImportIdentityColumnNameDefault = "\"SHPI_ShapeIdentity\"";
	private static final String shapeTableNameDefault = "\"Shape\"";
	private static final String taxonomyTermShapeTableNameDefault = "\"TaxonomyTermShape\"";
	private static final String taxonomyTermShapeTermColumnNameDefault = "\"TAXTS_Term\"";
	private static final String taxonomyTermShapeShapeColumnNameDefault = "\"TAXTS_Shape\"";
	private static final String shapeTermTableNameDefault = "\"ShapeTerm\"";
	private static final String shapeTermTermColumnNameDefault = "\"SHPT_Term\"";
	private static final String shapeTermShapeColumnNameDefault = "\"SHPT_Shape\"";
	private static final String viewBuilderClassNameDefault = "gr.cite.geoanalytics.util.PostGISViewBuilder";
	
	private static Logger log = LoggerFactory.getLogger(DataLayerConfig.class);
	//private String daoFactoryClassName = null;
	private String dbUrl = null;
	private String dbUser = null;
	private String dbPass = null;
	
	
	private String shapeDataColumnName = null;
	private String shapeGeographyColumnName = null;
	private String shapeIdColumnName = null;
	private String shapeNameColumnName = null;
	private String shapeCodeColumnName = null;
	private String shapeImportColumnName = null;
	private String shapeImportIdentityColumnName = null;
	private String shapeIdentityColumnName = null;
	private String shapeTableName = null;
	private String taxonomyTermShapeTableName = null;
	private String taxonomyTermShapeTermColumnName = null;
	private String taxonomyTermShapeShapeColumnName = null;
	private String shapeTermTableName = null;
	private String shapeTermTermColumnName = null;
	private String shapeTermShapeColumnName = null;	
	private String viewBuilderClassName = null;
	
//	public String getDaoFactoryClassName() {
//		return daoFactoryClassName;
//	}
//	public void setDaoFactoryClassName(String daoFactoryClassName) {
//		this.daoFactoryClassName = daoFactoryClassName;
//		
//		if(daoFactoryClassName != null) {
//			log.trace("Using non-managed dao factory: " + daoFactoryClassName);
//			try {
//				DataLayer.setDaoFactory((DaoFactory)Class.forName(daoFactoryClassName).newInstance());
//			}catch(Exception e) {
//				throw new ContextInitializationException("Error while creating dao factory", e);
//			}
//		}
//	}
	
	public String getDbUrl() {
		return dbUrl;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.dbUrl}")
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	
	public String getDbUser() {
		return dbUser;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.dbUser}")
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	
	public String getDbPass() {
		return dbPass;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.dbPass}")
	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}
	
	public String getShapeDataColumnName() {
		return shapeDataColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeDataColumnName:" + shapeDataColumnNameDefault + "}")
	public void setShapeDataColumnName(String shapeDataColumnName) {
		this.shapeDataColumnName = shapeDataColumnName;
	}
	
	
	public String getShapeGeographyColumnName() {
		return shapeGeographyColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeGeographyColumnName:" + shapeGeographyColumnNameDefault + "}")
	public void setShapeGeographyColumnName(String shapeGeographyColumnName) {
		this.shapeGeographyColumnName = shapeGeographyColumnName;
	}
	
	public String getShapeIdColumnName() {
		return shapeIdColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeIdColumnName:" + shapeIdColumnNameDefault + "}")
	public void setShapeIdColumnName(String shapeIdColumnName) {
		this.shapeIdColumnName = shapeIdColumnName;
	}
	
	public String getShapeNameColumnName() {
		return shapeNameColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeNameColumnName:" + shapeNameColumnNameDefault + "}")
	public void setShapeNameColumnName(String shapeNameColumnName) {
		this.shapeNameColumnName = shapeNameColumnName;
	}
	
	public String getShapeCodeColumnName() {
		return shapeCodeColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeCodeColumnName:" + shapeCodeColumnNameDefault + "}")
	public void setShapeCodeColumnName(String shapeCodeColumnName) {
		this.shapeCodeColumnName = shapeCodeColumnName;
	}
	
	public String getShapeImportColumnName() {
		return shapeImportColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeImportColumnName:" + shapeImportColumnNameDefault + "}")
	public void setShapeImportColumnName(String shapeImportColumnName) {
		this.shapeImportColumnName = shapeImportColumnName;
	}
	
	public String getShapeImportIdentityColumnName() {
		return shapeImportIdentityColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeImportIdentityColumnName:" + shapeImportIdentityColumnNameDefault + "}")
	public void setShapeImportIdentityColumnName(String shapeImportIdentityColumnName) {
		this.shapeImportIdentityColumnName = shapeImportIdentityColumnName;
	}
	
	public String getShapeTableName() {
		return shapeTableName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeTableName:" + shapeTableNameDefault + "}")
	public void setShapeTableName(String shapeTableName) {
		this.shapeTableName = shapeTableName;
	}
	
	public String getTaxonomyTermShapeTableName() {
		return taxonomyTermShapeTableName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.taxonomyTermShapeTableName:" + taxonomyTermShapeTableNameDefault + "}")
	public void setTaxonomyTermShapeTableName(String taxonomyTermShapeTableName) {
		this.taxonomyTermShapeTableName = taxonomyTermShapeTableName;
	}
	
	public String getTaxonomyTermShapeTermColumnName() {
		return taxonomyTermShapeTermColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.taxonomyTermShapeTermColumnName:" + taxonomyTermShapeTermColumnNameDefault + "}")
	public void setTaxonomyTermShapeTermColumnName(String taxonomyTermShapeTermColumnName) {
		this.taxonomyTermShapeTermColumnName = taxonomyTermShapeTermColumnName;
	}
	
	public String getTaxonomyTermShapeShapeColumnName() {
		return taxonomyTermShapeShapeColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.taxonomyTermShapeShapeColumnName:" + taxonomyTermShapeShapeColumnNameDefault + "}")
	public void setTaxonomyTermShapeShapeColumnName(String taxonomyTermShapeShapeColumnName) {
		this.taxonomyTermShapeShapeColumnName = taxonomyTermShapeShapeColumnName;
	}
	
	public String getShapeTermTableName() {
		return shapeTermTableName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeTermTableName:" + shapeTermTableNameDefault + "}")
	public void setShapeTermTableName(String shapeTermTableName) {
		this.shapeTermTableName = shapeTermTableName;
	}
	
	public String getShapeTermTermColumnName() {
		return shapeTermTermColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeTermTermColumnName:" + shapeTermTermColumnNameDefault + "}")
	public void setShapeTermTermColumnName(String shapeTermTermColumnName) {
		this.shapeTermTermColumnName = shapeTermTermColumnName;
	}
	
	public String getShapeTermShapeColumnName() {
		return shapeTermShapeColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeTermShapeColumnName:" + shapeTermShapeColumnNameDefault + "}")
	public void setShapeTermShapeColumnName(String shapeTermShapeColumnName) {
		this.shapeTermShapeColumnName = shapeTermShapeColumnName;
	}
	
	public String getViewBuilderClassName() {
		return viewBuilderClassName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.viewBuilderClassName:" + viewBuilderClassNameDefault + "}")
	public void setViewBuilderClassName(String viewBuilderClassName) {
		this.viewBuilderClassName = viewBuilderClassName;
	}
}
