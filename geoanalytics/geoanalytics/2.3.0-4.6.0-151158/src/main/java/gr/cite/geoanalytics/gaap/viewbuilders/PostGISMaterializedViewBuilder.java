package gr.cite.geoanalytics.gaap.viewbuilders;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.gaap.utilities.StringUtils;
import gr.cite.geoanalytics.gaap.viewbuilders.PostGISMaterializedViewBuilder;
import gr.cite.geoanalytics.gaap.viewbuilders.PostGISViewBuilder;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class PostGISMaterializedViewBuilder extends PostGISViewBuilder {
	private static Logger log = LoggerFactory.getLogger(PostGISMaterializedViewBuilder.class);
	
	private Configuration configuration = null;
	
	@Inject
	public PostGISMaterializedViewBuilder(GeocodeManager taxonomyManager, ConfigurationManager configurationManager)
	{
		super(taxonomyManager, configurationManager);
	}
	
	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}	
	
	@Override
	public String removeViewIfExists() throws Exception {
		StringBuilder sql = new StringBuilder();

		try{
			String statement = "DROP MATERIALIZED VIEW IF EXISTS \"" + identity+"\" ;";
			sql.append(statement);
			log.debug("Drop materialized view if exists" + identity);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
		
		return sql.toString();
	}	
	
	@Override
	protected String generateViewStatement(String layerId, Map<String, ShapeAttributeDataType> attrs) throws Exception	{
		if(attrs == null || attrs.isEmpty()) throw new IllegalArgumentException("Missing data definitions");		
		
		StringBuilder sql = new StringBuilder();		
		sql.append("DROP MATERIALIZED VIEW IF EXISTS \"" + layerId + "\";");	
		sql.append("CREATE MATERIALIZED VIEW \"" + layerId  + "\""); 
		sql.append(" AS ");
		sql.append("SELECT ");
		sql.append(configuration.getDataLayerConfig().getShapeGeographyColumnName()+"::geometry AS " + configuration.getDataLayerConfig().getShapeGeographyColumnName()); /*TODO treat as geometry, replace when support for geography is improved*/
		sql.append(",");		
		sql.append(configuration.getDataLayerConfig().getShapeIdColumnName() + " AS " + configuration.getDataLayerConfig().getShapeIdColumnName());
		
		Iterator<Map.Entry<String, ShapeAttributeDataType>> entryIt = attrs.entrySet().iterator();

		while(entryIt.hasNext()) {
			Entry<String, ShapeAttributeDataType> attr = entryIt.next();
			String attrExpression = "(xpath('//extraData/" + attr.getKey() + "/text()', "+"s."+configuration.getDataLayerConfig().getShapeDataColumnName()+"))[1]"+"::text";
			
			List<AttributeMappingConfig> mcfgs = configurationManager.getMappingConfigsForLayer(attr.getKey(), identity);
			AttributeMappingConfig taxonomyMapping = null;
			boolean valueMapping = false;
			Boolean mapValue = false;
			boolean presentable = true;
			if(mcfgs != null && !mcfgs.isEmpty()) {
				for(AttributeMappingConfig mcfg : mcfgs)				{
					if(mcfg.getAttributeValue() != null && !mcfg.getAttributeValue().equals("")) {
						valueMapping = true;
						mapValue = mcfg.isMapValue(); //TODO null, why
					}else if(mcfg.getAttributeValue() == null || mcfg.getAttributeValue().equals("")) {
						if(mcfg.isPresentable() == false) {
							presentable = false;
							break;
						}
						if(mcfg.getTermId() != null) {
							taxonomyMapping = mcfg;
						}
					}
				}
			}
			
			if(!presentable){
				continue;
			}
			
			sql.append(", ");
			
			if(valueMapping && mapValue) {
				sql.append("\n");
				sql.append("CASE "); sql.append(attrExpression); sql.append(attr.getValue() != ShapeAttributeDataType.LONGSTRING ? "::" + getSQLDataType(attr.getValue()) : "");
				for(AttributeMappingConfig mcfg : mcfgs) {
					if(mcfg.getAttributeValue() != null && !mcfg.getAttributeValue().equals("")) {
						Geocode tt = taxonomyManager.findTermById(mcfg.getTermId(), false);
						if(tt == null) {
							log.error("Taxonomy term " + mcfg.getTermId() + " defined in mappings of attribute " + mcfg.getAttributeName() + " not found");
							throw new Exception("Taxonomy term " + mcfg.getTermId() + " defined in mappings of attribute " + mcfg.getAttributeName() + " not found");
						}
 						sql.append("\n");
						sql.append(" WHEN "); 
						sql.append(quoteTextual(mcfg.getAttributeValue(), attr.getValue()));
						sql.append(" THEN "); 
						sql.append(quoteTextual(tt.getName(), attr.getValue()));
					}
				}
				sql.append("\n");
				sql.append("ELSE "); sql.append(attrExpression); sql.append(attr.getValue() != ShapeAttributeDataType.LONGSTRING ? "::" + getSQLDataType(attr.getValue()) : "");
				sql.append(" END");
			}else {
				sql.append(attrExpression);
				sql.append(attr.getValue() != ShapeAttributeDataType.LONGSTRING ? "::" + getSQLDataType(attr.getValue()) : "");
			}
					
			sql.append(" AS " );
			if(taxonomyMapping != null)	{
				GeocodeSystem t = taxonomyManager.findGeocodeSystemById(taxonomyMapping.getTermId(), false);
				if(t == null) {
					log.error("Taxonomy " + taxonomyMapping.getTermId() + " defined in mappings of attribute " + taxonomyMapping.getAttributeName() + " not found");
					throw new Exception("Taxonomy " + taxonomyMapping.getTermId() + " defined in mappings of attribute " + taxonomyMapping.getAttributeName() + " not found");
				}
				sql.append(StringUtils.normalizeEntityName(t.getName()));
			}else{
				sql.append("\"" + attr.getKey() + "\""); 
			}
		}
		sql.append("\n");
		sql.append("FROM "+configuration.getDataLayerConfig().getShapeTableName()+" s\n");

		if(identity != null) {
			sql.append("WHERE s." + configuration.getDataLayerConfig().getShapeLayerLayerColumnName() + "='" + identity + "'");
		}else{
			sql.append("WHERE "+configuration.getDataLayerConfig().getShapeIdColumnName() +"='"+this.shape.getId()+"'"); //unused case
		}

		sql.append(" WITH DATA");
		sql.append(";");
		
		String id = configuration.getDataLayerConfig().getShapeIdColumnName();
		id = id.substring(1, id.length()-2);
		
		sql.append("CREATE UNIQUE INDEX pki_" + StringUtils.normalizeEntityNameForMV(layerId) + id  + " ");
		sql.append("ON \"" + layerId + "\" USING btree (" + configuration.getDataLayerConfig().getShapeIdColumnName() + ");");

		String geography = configuration.getDataLayerConfig().getShapeGeographyColumnName();
		geography = geography.substring(1, id.length()-2);
		
		sql.append("CREATE INDEX idx_" + StringUtils.normalizeEntityNameForMV(layerId) + geography);
		sql.append("  ON \"" + layerId + "\"");
		sql.append("  USING gist");
		sql.append(" (" + configuration.getDataLayerConfig().getShapeGeographyColumnName() + ");");		
		sql.append("VACUUM ANALYZE;");		
		
		log.debug("SQL Materialized View : " + sql.toString());

		return sql.toString();	
	}

}
