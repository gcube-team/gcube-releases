package org.gcube.dbinterface.h2.queries;
import org.gcube.common.dbinterface.attributes.Attribute;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.InsertFromSelect;
import org.gcube.common.dbinterface.queries.Select;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertFromSelectImpl extends AbstractUpdate implements InsertFromSelect {

	private static final Logger logger = LoggerFactory.getLogger(InsertFromSelectImpl.class);
	
	private String query=" INSERT INTO <%NAME%> <%ATTRIBUTESNAME%> <%QUERY%>";
	
	private Select subquery;
	private SimpleTable table;
	
	
	public SimpleTable execute(DBSession session) throws Exception {
		session.executeUpdate(this.getExpression());
		return this.table;
	}

	public void setSubQuery(Select query) {
		this.subquery= query;
	}

	public void setTable(SimpleTable table) {
		this.table= table;
	}

	@Override
	public String getExpression() {
		return query.replace("<%NAME%>", this.table.getTableName()).replace("<%QUERY%>", this.subquery.getExpression()).replace("<%ATTRIBUTESNAME%>", retrieveAttributesName());
	}

	private String retrieveAttributesName(){
		logger.trace("retrieving attributes name");
		if (this.subquery.getAttributes()==null) return "";
		StringBuilder toReturn= new StringBuilder("(");
		for (Attribute attrib : this.subquery.getAttributes())
			if (!attrib.getAttributeName().equals("*"))
				toReturn.append(attrib.getAttributeName()+",");
			else return "";
		logger.trace("to return is "+toReturn.toString());
		return toReturn.substring(0, toReturn.length()-1)+")";
	}
	
}
