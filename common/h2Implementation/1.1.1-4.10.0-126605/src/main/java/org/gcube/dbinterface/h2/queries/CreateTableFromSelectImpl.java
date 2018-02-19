package org.gcube.dbinterface.h2.queries;

import org.gcube.common.dbinterface.Limit;
import org.gcube.common.dbinterface.TableAlreadyExistsException;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.CreateTableFromSelect;
import org.gcube.common.dbinterface.queries.Select;
import org.gcube.common.dbinterface.tables.SimpleTable;


public class CreateTableFromSelectImpl extends AbstractUpdate implements CreateTableFromSelect {

	
	private String query= "CREATE TABLE <%NAME%> AS <%QUERY%>";
	
	private String tableName;
	private boolean withData= true;
	private Select subQuery;
	
	
	@Override
	public SimpleTable execute(DBSession session) throws TableAlreadyExistsException, Exception {
		GetMetadataImpl metadata= new GetMetadataImpl();
		metadata.setTable(this.tableName);
		if (metadata.getResults(session).size()>0) throw new TableAlreadyExistsException("the table "+this.tableName+" already exists");
		session.executeUpdate(this.getExpression());
		SimpleTable table=new SimpleTable(this.tableName);
		return table;
	}
	
	@Override
	public String getExpression(){
		Limit tmpLimit= this.subQuery.getLimit();
		if(!this.withData) this.subQuery.setLimit(new Limit(0));
		String expression= query.replace("<%NAME%>",this.tableName ).replace("<%QUERY%>", this.subQuery.getExpression());
		this.subQuery.setLimit(tmpLimit);
		return expression;
	}

	public boolean isWithData() {
		return this.withData;
	}

	public void setSelect(Select query) {
		this.subQuery= query;
	}

	public void setTableName(String tableName) {
		this.tableName= tableName;
	}

	public void setWithData() {
		this.withData= true;
	}

	public void setWithoutData() {
		this.withData= false;
	}

}
