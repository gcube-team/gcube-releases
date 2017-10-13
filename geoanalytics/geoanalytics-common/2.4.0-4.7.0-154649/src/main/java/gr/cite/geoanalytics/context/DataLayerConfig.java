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
	private static final String shapeLayerColumnNameDefault = "\"SHP_LayerID\"";
	private static final String shapeImportIdentityColumnNameDefault = "\"SHPI_ShapeIdentity\"";
	private static final String shapeTableNameDefault = "\"Shape\"";
	private static final String geocodeShapeTableNameDefault = "\"GeocodeShape\"";
	private static final String geocodeShapeGeocodeColumnNameDefault = "\"GCSHP_Geocode\"";
	private static final String geocodeShapeShapeColumnNameDefault = "\"GCSHP_Shape\"";
//	private static final String shapeLayerTableNameDefault = "\"ShapeLayer\"";
//	private static final String shapeLayerLayerColumnNameDefault = "\"SHPT_LAYER_ID\"";
//	private static final String shapeLayerShapeColumnNameDefault = "\"SHPT_Shape\"";
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
	private String geocodeShapeTableName = null;
	private String geocodeShapeGeocodeColumnName = null;
	private String geocodeShapeShapeColumnName = null;
//	private String shapeLayerTableName = null;
	private String shapeLayerColumnName = null;
//	private String shapeLayerShapeColumnName = null;	
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
		log.trace("Setting database url: " + dbUrl);
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
		log.trace("Setting shape data column name: " + shapeDataColumnName);
		this.shapeDataColumnName = shapeDataColumnName;
	}
	
	
	public String getShapeGeographyColumnName() {
		return shapeGeographyColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeGeographyColumnName:" + shapeGeographyColumnNameDefault + "}")
	public void setShapeGeographyColumnName(String shapeGeographyColumnName) {
		log.trace("Setting shape geography column name: " + shapeGeographyColumnName);
		this.shapeGeographyColumnName = shapeGeographyColumnName;
	}
	
	public String getShapeIdColumnName() {
		return shapeIdColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeIdColumnName:" + shapeIdColumnNameDefault + "}")
	public void setShapeIdColumnName(String shapeIdColumnName) {
		log.trace("Setting shape id column name: " + shapeIdColumnName);
		this.shapeIdColumnName = shapeIdColumnName;
	}
	
	public String getShapeNameColumnName() {
		return shapeNameColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeNameColumnName:" + shapeNameColumnNameDefault + "}")
	public void setShapeNameColumnName(String shapeNameColumnName) {
		log.trace("Setting shape name column name: " + shapeNameColumnName);
		this.shapeNameColumnName = shapeNameColumnName;
	}
	
	public String getShapeCodeColumnName() {
		return shapeCodeColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeCodeColumnName:" + shapeCodeColumnNameDefault + "}")
	public void setShapeCodeColumnName(String shapeCodeColumnName) {
		log.trace("Setting code column name: " + shapeCodeColumnName);
		this.shapeCodeColumnName = shapeCodeColumnName;
	}
	
	public String getShapeImportColumnName() {
		return shapeImportColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeImportColumnName:" + shapeImportColumnNameDefault + "}")
	public void setShapeImportColumnName(String shapeImportColumnName) {
		log.trace("Setting import column name: " + shapeImportColumnName);
		this.shapeImportColumnName = shapeImportColumnName;
	}
	
	public String getShapeImportIdentityColumnName() {
		return shapeImportIdentityColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeImportIdentityColumnName:" + shapeImportIdentityColumnNameDefault + "}")
	public void setShapeImportIdentityColumnName(String shapeImportIdentityColumnName) {
		log.trace("Setting import identity column name: " + shapeImportIdentityColumnName);
		this.shapeImportIdentityColumnName = shapeImportIdentityColumnName;
	}
	
	public String getShapeTableName() {
		return shapeTableName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeTableName:" + shapeTableNameDefault + "}")
	public void setShapeTableName(String shapeTableName) {
		log.trace("Setting shape table name: " + shapeTableName);
		this.shapeTableName = shapeTableName;
	}
	
	public String getGeocodeShapeTableName() {
		return geocodeShapeTableName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.geocodeShapeTableName:" + geocodeShapeTableNameDefault + "}")
	public void setGeocodeShapeTableName(String geocodeShapeTableName) {
		log.trace("Setting geocode shape table name: " + geocodeShapeTableName);
		this.geocodeShapeTableName = geocodeShapeTableName;
	}
	
	public String getGeocodeShapeGeocodeColumnName() {
		return geocodeShapeGeocodeColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.geocodeShapeGeocodeColumnName:" + geocodeShapeGeocodeColumnNameDefault + "}")
	public void setGeocodeShapeGeocodeColumnName(String geocodeShapeGeocodeColumnName) {
		log.trace("Setting geocode shape geocode column name: " + geocodeShapeGeocodeColumnName);
		this.geocodeShapeGeocodeColumnName = geocodeShapeGeocodeColumnName;
	}
	
	public String getGeocodeShapeShapeColumnName() {
		return geocodeShapeShapeColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.geocodeShapeShapeColumnName:" + geocodeShapeShapeColumnNameDefault + "}")
	public void setGeocodeShapeShapeColumnName(String geocodeShapeShapeColumnName) {
		log.trace("Setting geocode shape shape column name: " + geocodeShapeShapeColumnName);
		this.geocodeShapeShapeColumnName = geocodeShapeShapeColumnName;
	}
	
//	public String getShapeLayerTableName() {
//		return shapeLayerTableName;
//	}
//	@Value("${gr.cite.geoanalytics.dataaccess.shapeLayerTableName:" + shapeLayerTableNameDefault + "}")
//	public void setShapeLayerTableName(String shapeLayerTableName) {
//		log.trace("Setting shape layer shape table name: " + shapeLayerTableName);
//		this.shapeLayerTableName = shapeLayerTableName;
//	}
//	
	public String getShapeLayerLayerColumnName() {
		return shapeLayerColumnName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.shapeLayerLayerColumnName:" + shapeLayerColumnNameDefault + "}")
	public void setShapeLayerLayerColumnName(String shapeLayerLayerColumnName) {
		log.trace("Setting shape layer column name: " + shapeTableName);
		this.shapeLayerColumnName = shapeLayerLayerColumnName;
	}
//	
//	public String getShapeLayerShapeColumnName() {
//		return shapeLayerShapeColumnName;
//	}
//	@Value("${gr.cite.geoanalytics.dataaccess.shapeLayerShapeColumnName:" + shapeLayerShapeColumnNameDefault + "}")
//	public void setShapeLayerShapeColumnName(String shapeLayerShapeColumnName) {
//		log.trace("Setting shape layer shape column name: " + shapeLayerShapeColumnName);
//		this.shapeLayerShapeColumnName = shapeLayerShapeColumnName;
//	}
	
	public String getViewBuilderClassName() {
		return viewBuilderClassName;
	}
	@Value("${gr.cite.geoanalytics.dataaccess.viewBuilderClassName:" + viewBuilderClassNameDefault + "}")
	public void setViewBuilderClassName(String viewBuilderClassName) {
		log.trace("Setting view builder class name: " + viewBuilderClassName);
		this.viewBuilderClassName = viewBuilderClassName;
	}
	
}
