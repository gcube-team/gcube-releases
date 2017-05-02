package gr.cite.gaap.viewbuilders;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.GeocodeManager;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * DEPWARN SQL statements tied to PostGIS dialect are defined and used in this class
 *
 */
public abstract class PostGISViewBuilder extends AbstractViewBuilder
{
	private static Logger log = LoggerFactory.getLogger(PostGISViewBuilder.class);
	
	public PostGISViewBuilder(GeocodeManager taxonomyManager, ConfigurationManager configurationManager)
	{
		super(taxonomyManager, configurationManager);
	}
	
	protected String getSQLDataType(ShapeAttributeDataType dt) throws Exception
	{
		switch(dt)
		{
		case SHORT:
			return "smallint";
		case INTEGER:
			return "integer";
		case LONG:
			return "bigint";
		case FLOAT:
		case DOUBLE:
			return "numeric";
		case DATE:
			return "timestamp";
		case STRING:
			return "character varying(250)";
		case LONGSTRING:
			return "text";
		}
		throw new Exception("Unrecognized data type " + dt);
	}

	protected String quoteTextual(String attrValue, ShapeAttributeDataType dt)
	{
		switch(dt)
		{
		case DATE:
		case STRING:
		case LONGSTRING:
			return "'" + attrValue + "'";
		case DOUBLE:
		case FLOAT:
		case SHORT:
		case INTEGER:
		case LONG:
			return attrValue;
		default:
			return attrValue;
		}
	}
	
	protected String createDropFunction(String funcName)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE OR REPLACE FUNCTION " + funcName + "(objectName varchar) RETURNS integer AS $$\n");
		sql.append("DECLARE ");
		sql.append("isTable integer;");
		sql.append("isView integer;");
		sql.append("isMatView integer;");
		sql.append("BEGIN ");
		sql.append("SELECT INTO isTable count(*) FROM pg_tables where tablename=objectName;");
		sql.append("SELECT INTO isView count(*) FROM pg_views where viewname=objectName;");
		sql.append("SELECT INTO isMatView count(*) FROM pg_matviews where matviewname=objectName;");
		sql.append("IF isTable = 1 THEN ");
    	sql.append(" execute 'DROP TABLE \"' || objectName || '\"';");
    	sql.append("RETURN 1;");
    	sql.append("END IF;");
    	sql.append("IF isView = 1 THEN ");
    	sql.append("execute 'DROP VIEW \"' || objectName || '\"';");
    	sql.append("RETURN 2;");
    	sql.append("END IF;");
    	sql.append("IF isMatView =1 THEN ");
    	sql.append("execute 'DROP MATERIALIZED VIEW \"' || objectName || '\"';");
    	sql.append("RETURN 3;");
    	sql.append("END IF;");
    	sql.append("RETURN 0;");
    	sql.append("END;");
    	sql.append("$$ LANGUAGE plpgsql;");	
    	return sql.toString();
	}
}
