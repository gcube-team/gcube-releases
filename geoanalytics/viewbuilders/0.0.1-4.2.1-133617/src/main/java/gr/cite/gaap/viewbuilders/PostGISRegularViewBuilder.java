package gr.cite.gaap.viewbuilders;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.ShapeImportManager;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
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
public class PostGISRegularViewBuilder extends PostGISViewBuilder
{
	private static Logger log = LoggerFactory.getLogger(PostGISRegularViewBuilder.class);
	
	private Configuration configuration = null;
	
	@Inject
	public PostGISRegularViewBuilder(ShapeManager shapeManager, ShapeImportManager shapeImportManager, 
			TaxonomyManager taxonomyManager, ConfigurationManager configurationManager)
	{
		super(shapeManager, shapeImportManager, taxonomyManager, configurationManager);
	}
	
	@Inject
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	protected String generateViewStatement(String identity, String name, Map<String, ShapeAttributeDataType> attrs) throws Exception
	{
//		Set<UUID> importIds = new HashSet<UUID>();
//		if(identity != null)
//		{
//			List<ShapeImport> sis = shapeImportManager.findByImportIdentity(identity);
//			for(ShapeImport si : sis) importIds.add(si.getId());
//		}
		
		StringBuilder sql = new StringBuilder();
		String funcName = "delete_table_or_view";
		sql.append(createDropFunction(funcName));
		sql.append("SELECT " + funcName + "('" + name + "');");
		sql.append("CREATE OR REPLACE VIEW \"" + name + "\" (");
		sql.append(configuration.getDataLayerConfig().getShapeGeographyColumnName());
		sql.append(",");
		sql.append(configuration.getDataLayerConfig().getShapeIdColumnName());
		Iterator<Entry<String,ShapeAttributeDataType>> entryIt = attrs.entrySet().iterator();
		//if(entryIt.hasNext()) sql.append(" ,");
		while(entryIt.hasNext())
		{
			Entry<String, ShapeAttributeDataType> attr = entryIt.next();
			
			List<AttributeMappingConfig> mcfgs = configurationManager.getMappingConfigsForLayer(attr.getKey(), identity);
			AttributeMappingConfig taxonomyMapping = null;
			boolean presentable = true;
			if(mcfgs != null)
			for(AttributeMappingConfig mcfg : mcfgs)
			{
				if(mcfg.getAttributeValue() == null || mcfg.getAttributeValue().equals(""))
				{
					if(mcfg.isPresentable() == false)
					{
						presentable = false;
						break;
					}
					if(mcfg.getTermId() != null)
					{
						taxonomyMapping = mcfg;
						break;
					}
				}
			}
			
			if(!presentable)
				continue;
			
			sql.append(", ");
			if(taxonomyMapping != null)
			{
				Taxonomy t = taxonomyManager.findTaxonomyById(taxonomyMapping.getTermId(), false);
				if(t == null)
				{
					log.error("Taxonomy " + taxonomyMapping.getTermId() + " defined in mappings of attribute " + taxonomyMapping.getAttributeName() + " not found");
					throw new Exception("Taxonomy " + taxonomyMapping.getTermId() + " defined in mappings of attribute " + taxonomyMapping.getAttributeName() + " not found");
				}
				sql.append(t.getName());
			}else
				sql.append(attr.getKey()); //note that (name,value)->taxonomyterm mappings can exist even if no name->taxonomy mapping exists. In that case, the attribute itself will not be mapped, only its values will
			//sql.append(" ");
			//sql.append(getSQLDataType(attr.getValue()));
			//if(entryIt.hasNext()) sql.append(", ");
		}
		
		sql.append(")\n");
		sql.append("AS ");
		sql.append("SELECT ");
		sql.append(configuration.getDataLayerConfig().getShapeGeographyColumnName()+"::geometry"); /*TODO treat as geometry, replace when support for geography is improved*/
		sql.append(",");
		sql.append(configuration.getDataLayerConfig().getShapeIdColumnName());
		entryIt = attrs.entrySet().iterator();
		//if(entryIt.hasNext()) sql.append(", ");
		while(entryIt.hasNext())
		{
			Entry<String, ShapeAttributeDataType> attr = entryIt.next();
			String attrExpression = "(xpath('//extraData/" + attr.getKey() + "/text()', "+"s."+configuration.getDataLayerConfig().getShapeDataColumnName()+"))[1]"+"::text";
			
			List<AttributeMappingConfig> mcfgs = configurationManager.getMappingConfigsForLayer(attr.getKey(), identity);
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
					
			
			//if(entryIt.hasNext()) sql.append(",");
		}
		sql.append("\n");
		sql.append("FROM "+configuration.getDataLayerConfig().getShapeTableName()+" s, " + configuration.getDataLayerConfig().getShapeTermTableName() +" st\n");
	//	sql.append("WHERE "+configuration.getDataLayerConfig().getShapeCodeColumnName() + "=" + "\""+identity+"\");");
		if(identity != null)
		{
			sql.append("WHERE st." + configuration.getDataLayerConfig().getShapeTermShapeColumnName() + "=" + "s." + configuration.getDataLayerConfig().getShapeIdColumnName() + 
					" and st." + configuration.getDataLayerConfig().getShapeTermTermColumnName() + "='" + identity + "';");
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
		sql.append(";");
		
		System.out.println("SQL = " + sql.toString());

		
		return sql.toString();	
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
	}

}
