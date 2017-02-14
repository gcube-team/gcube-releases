package gr.cite.gaap.viewbuilders;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.ShapeImportManager;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.gaap.utilities.StringUtils;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * DEPWARN SQL statements tied to PostGIS dialect are defined and used in this class
 *
 */
@Service
public class PostGISMaterializedViewBuilder extends PostGISViewBuilder
{
	private static Logger log = LoggerFactory.getLogger(PostGISMaterializedViewBuilder.class);
	
	private Configuration configuration = null;
	
	@Inject
	public PostGISMaterializedViewBuilder(ShapeManager shapeManager, ShapeImportManager shapeImportManager, 
			TaxonomyManager taxonomyManager, ConfigurationManager configurationManager)
	{
		super(shapeManager, shapeImportManager, taxonomyManager, configurationManager);
	}
	
	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	
	
	@Override
	public void removerViewIfExists() throws Exception {
		try{
			String statement = "DROP VIEW IF EXIST" + identityName;
			entityManager.createNativeQuery(statement);
			log.debug("Drop view if exists" + identityName);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
		try{
			String statement = "DROP MATERIALIZED VIEW IF EXIST" + identityName;
			entityManager.createNativeQuery(statement);
			log.debug("Drop materialized view if exists" + identityName);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	protected String generateViewStatement(String identity, String name, Map<String, ShapeAttributeDataType> attrs) throws Exception
	{
		//if(shape == null) throw new IllegalArgumentException("Missing shape");
		if(attrs == null || attrs.isEmpty()) throw new IllegalArgumentException("Missing data definitions");

		String funcName = "delete_table_or_view";
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("DROP VIEW IF EXISTS \"" + name + "\";");	//TODO remove when materialized views are supported
		sql.append(createDropFunction(funcName));
    	
		sql.append("SELECT " + funcName + "('" + name + "MV');"); //TODO remove MV suffix when materialized views are supported  
		//sql.append("CREATE MATERIALIZED VIEW \"" + name  + ""\"");
		sql.append("CREATE MATERIALIZED VIEW \"" + name  + "MV\""); //TODO remove MV suffix when materialized views are supported  
		sql.append(" AS ");
		sql.append("SELECT ");
		sql.append(configuration.getDataLayerConfig().getShapeGeographyColumnName()+"::geometry AS " + configuration.getDataLayerConfig().getShapeGeographyColumnName()); /*TODO treat as geometry, replace when support for geography is improved*/
		sql.append(",");		
		sql.append(configuration.getDataLayerConfig().getShapeIdColumnName() + " AS " + configuration.getDataLayerConfig().getShapeIdColumnName());
		
		Iterator<Map.Entry<String, ShapeAttributeDataType>> entryIt = attrs.entrySet().iterator();
		//if(entryIt.hasNext()) sql.append(", ");
		while(entryIt.hasNext())
		{
			Entry<String, ShapeAttributeDataType> attr = entryIt.next();
			String attrExpression = "(xpath('//extraData/" + attr.getKey() + "/text()', "+"s."+configuration.getDataLayerConfig().getShapeDataColumnName()+"))[1]"+"::text";
			
			List<AttributeMappingConfig> mcfgs = configurationManager.getMappingConfigsForLayer(attr.getKey(), identity);
			AttributeMappingConfig taxonomyMapping = null;
			boolean valueMapping = false;
			Boolean mapValue = false;
			boolean presentable = true;
			if(mcfgs != null && !mcfgs.isEmpty())
			{
				for(AttributeMappingConfig mcfg : mcfgs)
				{
					if(mcfg.getAttributeValue() != null && !mcfg.getAttributeValue().equals(""))
					{
						valueMapping = true;
						mapValue = mcfg.isMapValue(); //TODO null, why
					}else if(mcfg.getAttributeValue() == null || mcfg.getAttributeValue().equals(""))
					{
						if(mcfg.isPresentable() == false)
						{
							presentable = false;
							break;
						}
						if(mcfg.getTermId() != null)
							taxonomyMapping = mcfg;
					}
				}
			}
			
			if(!presentable)
				continue;
			
			sql.append(", ");
			if(valueMapping && mapValue)
			{
				sql.append("\n");
				sql.append("CASE "); sql.append(attrExpression); sql.append(attr.getValue() != ShapeAttributeDataType.LONGSTRING ? "::" + getSQLDataType(attr.getValue()) : "");
				for(AttributeMappingConfig mcfg : mcfgs)
				{
					if(mcfg.getAttributeValue() != null && !mcfg.getAttributeValue().equals(""))
					{
						TaxonomyTerm tt = taxonomyManager.findTermById(mcfg.getTermId(), false);
						if(tt == null)
						{
							log.error("Taxonomy term " + mcfg.getTermId() + " defined in mappings of attribute " + mcfg.getAttributeName() + " not found");
							throw new Exception("Taxonomy term " + mcfg.getTermId() + " defined in mappings of attribute " + mcfg.getAttributeName() + " not found");
						}
						String dt = getSQLDataType(attr.getValue());
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
			}else
			{
				sql.append(attrExpression);
				sql.append(attr.getValue() != ShapeAttributeDataType.LONGSTRING ? "::" + getSQLDataType(attr.getValue()) : "");
			}
					
			sql.append(" AS " );
			if(taxonomyMapping != null)	{
				Taxonomy t = taxonomyManager.findTaxonomyById(taxonomyMapping.getTermId(), false);
				if(t == null)
				{
					log.error("Taxonomy " + taxonomyMapping.getTermId() + " defined in mappings of attribute " + taxonomyMapping.getAttributeName() + " not found");
					throw new Exception("Taxonomy " + taxonomyMapping.getTermId() + " defined in mappings of attribute " + taxonomyMapping.getAttributeName() + " not found");
				}
				sql.append(StringUtils.normalizeEntityName(t.getName()));
			}else{
				sql.append("\"" + attr.getKey() + "\""); 
				//note that (name,value)->taxonomyterm mappings can exist even if no name->taxonomy mapping exists. In that case, the attribute itself will not be mapped, only its values will
				//if(entryIt.hasNext()) sql.append(",");
			}
		}
		sql.append("\n");
		sql.append("FROM "+configuration.getDataLayerConfig().getShapeTableName()+" s, " + configuration.getDataLayerConfig().getShapeTermTableName() +" st\n");
	//	sql.append("WHERE "+configuration.getDataLayerConfig().getShapeCodeColumnName() + "=" + "\""+identity+"\");");
		if(identity != null)
		{
			sql.append("WHERE st." + configuration.getDataLayerConfig().getShapeTermShapeColumnName() + "=" + "s." + configuration.getDataLayerConfig().getShapeIdColumnName() + 
					" and st." + configuration.getDataLayerConfig().getShapeTermTermColumnName() + "='" + identity + "'");
//			sql.append("WHERE "+configuration.getDataLayerConfig().getShapeImportColumnName() +" IN (");
//			Iterator<UUID> it = importIds.iterator();
//			while(it.hasNext())
//			{
//				sql.append("'"+it.next()+"'");
//				if(it.hasNext()) sql.append(",");
//			}
		}else
			sql.append("WHERE "+configuration.getDataLayerConfig().getShapeIdColumnName() +"='"+this.shape.getId()+"'"); //unused case
		//sql.append(");");
		sql.append(" WITH DATA");
		sql.append(";");
		
		String id = configuration.getDataLayerConfig().getShapeIdColumnName();
		id = id.substring(1, id.length()-2);
		
		sql.append("CREATE UNIQUE INDEX pki_" + StringUtils.normalizeEntityName(name) + "MV_" + id  + " ");
		sql.append("ON \"" + name + "MV\" USING btree (" + configuration.getDataLayerConfig().getShapeIdColumnName() + ");");

		String geography = configuration.getDataLayerConfig().getShapeGeographyColumnName();
		geography = geography.substring(1, id.length()-2);
		
		sql.append("CREATE INDEX idx_" + StringUtils.normalizeEntityName(name) + "MV_" + geography);
		sql.append("  ON \"" + name + "MV\"");
		sql.append("  USING gist");
		sql.append(" (" + configuration.getDataLayerConfig().getShapeGeographyColumnName() + ");");
		
		sql.append("CREATE VIEW \"" + name + "\" AS SELECT * FROM \"" + name + "MV\";"); //TODO remove when materialized views are supported
		
		sql.append("VACUUM ANALYZE;");		
		
		return sql.toString();	
	}
}
