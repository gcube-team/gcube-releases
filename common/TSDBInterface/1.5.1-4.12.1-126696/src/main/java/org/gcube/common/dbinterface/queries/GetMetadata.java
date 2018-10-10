package org.gcube.common.dbinterface.queries;

import java.util.LinkedHashMap;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.types.Type;


public interface GetMetadata {

	
	public void setTable(String tableName);
	
	public LinkedHashMap<String, Type> getResults() throws Exception;
	
	/**
	 * the result of getMetadata query should contains in order {column_name, data_type, character_length, numeric_precision, numeric_scale}
	 * is created for internal use only and its use is at your own risk :)  
	 * 
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public LinkedHashMap<String, Type> getResults(DBSession session) throws Exception;
	

}
