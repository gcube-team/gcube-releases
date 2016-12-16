package org.gcube.dbinterface.h2.queries;

import java.util.LinkedHashMap;

import org.gcube.common.dbinterface.TableAlreadyExistsException;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.CreateTableLike;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.types.Type;

public class CreateTableLikeImpl implements CreateTableLike {

	private String query= "CREATE TABLE <%NAME%> AS  SELECT * FROM <%TABLELIKE%> LIMIT 0"; 
	
	private SimpleTable tableLike;
	private String tableName;
	
	@SuppressWarnings("unchecked")
	public SimpleTable execute(DBSession session)
			throws TableAlreadyExistsException, Exception {
		GetMetadataImpl metadata= new GetMetadataImpl();
		metadata.setTable(this.tableName);
		if (metadata.getResults(session).size()>0) throw new TableAlreadyExistsException("the table "+this.tableName+" already exists");
		session.executeUpdate(this.getExpression());
		SimpleTable table=new SimpleTable(this.tableName);
		table.setFieldsMapping((LinkedHashMap<String, Type>) this.tableLike.getFieldsMapping().clone());
		return table;
	}

	public String getExpression() {
		return query.replace("<%NAME%>", this.tableName).replace("<%TABLELIKE%>", this.tableLike.getTableName());
	}

	public void setTableLike(SimpleTable tableLike) {
		this.tableLike= tableLike;
	}

	public void setTableName(String tableName) {
		this.tableName= tableName;
	}

}
