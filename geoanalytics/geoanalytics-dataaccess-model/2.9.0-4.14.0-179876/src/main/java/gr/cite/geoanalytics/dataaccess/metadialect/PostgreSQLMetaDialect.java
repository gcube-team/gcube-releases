package gr.cite.geoanalytics.dataaccess.metadialect;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.cfg.reveng.dialect.JDBCMetaDataDialect;
import org.hibernate.cfg.reveng.dialect.MetaDataDialect;

/**
 * A implementation of the {@link MetaDataDialect} for the PostgreSQL Database.
 * This is basically a wrapper around {@link JDBCMetaDataDialect} to provide case sensitive
 * access to postgresql databases.
 * 
 */

public class PostgreSQLMetaDialect extends JDBCMetaDataDialect {

	private static final Logger log = Logger.getLogger(PostgreSQLMetaDialect.class.getName());
	
	/*
	 * (non-Javadoc)
	 * @see org.hibernate.cfg.reveng.dialect.AbstractMetaDataDialect#needQuote(java.lang.String)
	 */
	public boolean needQuote(String name) {		
		log.log(Level.FINE,"needQuote(" + name +")");
		if(name != null && name.compareTo(name.toUpperCase()) != 0) return true;
		return super.needQuote(name);
	}
}
