package org.gcube.common.dbinterface.tables;

import java.sql.ResultSet;
import java.util.LinkedHashMap;
import org.gcube.common.dbinterface.attributes.Attribute;
import org.gcube.common.dbinterface.attributes.AggregatedAttribute;
import org.gcube.common.dbinterface.attributes.AggregationFunctions;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.GetMetadata;
import org.gcube.common.dbinterface.queries.Select;
import org.gcube.common.dbinterface.types.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author lucio
 *
 */
public class SimpleTable extends Table{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1874819829226263856L;
	private LinkedHashMap<String, Type> fieldsMapping=null;
	private int count=-1;
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleTable.class);
	
	public SimpleTable(String tableName) {
		super(tableName);
	}
	 
	
	public SimpleTable(String tableName, String tableAlias){
		super(tableName, tableAlias);
	}

	
	public void initializeFieldMapping(DBSession session) throws Exception{
		GetMetadata metadata =DBSession.getImplementation(GetMetadata.class);
		metadata.setTable(this.getTableName());
		this.fieldsMapping= metadata.getResults(session);
	}
	
	public void initializeFieldMapping() throws Exception{
		DBSession session = DBSession.connect();
		try{
			initializeFieldMapping(session);
		}finally{
			if (session!=null) session.release();
		}
	}

	public void setCount(int count){
		this.count= count;
	}
	
	public int getCount() throws Exception{
		if (count==-1) this.initializeCount();
		//logger.debug("count for table "+this.getTable()+" is "+count);
		return count;
	}
	
	public void initializeCount(DBSession session) throws Exception{
		Select select= DBSession.getImplementation(Select.class);
		select.setTables(new Table(this.getTableName()));
		select.setAttributes(new Attribute[]{new AggregatedAttribute("*", AggregationFunctions.COUNT)});
		//logger.debug("select query for count is "+select.getExpression());
		ResultSet rs=select.getResults(session);
		rs.next();
		this.count=rs.getInt(1);
		logger.trace("initialized count for table "+this.getTable()+", it is "+this.count);
	}
	
	public void initializeCount() throws Exception{
		DBSession session = DBSession.connect();
		try{
			initializeCount(session);
		}finally{
			if (session!=null) session.release();
		}
	}
	
	public LinkedHashMap<String, Type> getFieldsMapping() throws Exception{
		//logger.debug("field mapping is null?"+(fieldsMapping==null));
		if (fieldsMapping!=null) return this.fieldsMapping;
		else{
			logger.debug("initializing table mapping");
			this.initializeFieldMapping();
			//logger.debug("fieldMapping is empty?"+fieldsMapping.isEmpty());
			return this.fieldsMapping;
		}
	}

	public void setFieldsMapping(LinkedHashMap<String, Type> fieldsMapping) {
		this.fieldsMapping = fieldsMapping;
	}
	
}
