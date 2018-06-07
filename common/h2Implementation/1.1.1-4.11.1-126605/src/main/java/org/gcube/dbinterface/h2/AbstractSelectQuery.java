package org.gcube.dbinterface.h2;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.gcube.common.dbinterface.attributes.*;
import org.gcube.common.dbinterface.Condition;
import org.gcube.common.dbinterface.Limit;
import org.gcube.common.dbinterface.Order;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractSelectQuery {

		
	private static final Logger logger = LoggerFactory.getLogger(AbstractSelectQuery.class);
	
	private String expression="SELECT <%DISTINCT%> <%ATTRIBUTES%> <%TABLE%> <%WHERE%> <%GROUPBY%> <%ORDER%> <%LIMIT%>  ";
	
	public String getExpression(){
		String tmpExpression= new String(expression);
		
		tmpExpression= tmpExpression.replaceAll("<%DISTINCT%>", this._getDistinct());
		
		tmpExpression= tmpExpression.replaceAll("<%WHERE%>", this._getFilters());
		
		tmpExpression= tmpExpression.replaceAll("<%GROUPBY%>", this._getGroups());
		
		tmpExpression= tmpExpression.replaceAll("<%ATTRIBUTES%>", this._getAttributes());
		
		tmpExpression= tmpExpression.replaceAll("<%ORDER%>", this._getOrders());
		
		tmpExpression= tmpExpression.replaceAll("<%TABLE%>", this._getTables());
		
		tmpExpression= tmpExpression.replaceAll("<%LIMIT%>", this._getLimits());
						
		return tmpExpression;
	}
	
	protected String _getAttributes(){
		//resolving the attributes
		if ((this.getAttributes()==null) || (this.getAttributes().length==0)) return "*";
		else {
			String tmpAttributes="";
			for (Attribute attribute: this.getAttributes())
				tmpAttributes+=attribute.getAttribute()+",";
			return tmpAttributes.substring(0, tmpAttributes.length()-1);
		}
	}
	
	protected String _getFilters(){
		//resolving the filters (where clause)
		if (this.getFilter()==null) return ""; 
		else 
			return "WHERE "+getFilter().getCondition();
	}
	
	protected String _getOrders(){
		//resolving the Orders
		String tmpOrder=" ";
		if (this.getOrders()==null || this.getOrders().length==0) return "";
		else{
			tmpOrder="ORDER BY ";
			for (Order order: this.getOrders()){
				tmpOrder+=order.getOrder()+",";
			}
			return tmpOrder.substring(0, tmpOrder.length()-1);
		}
		
	}
	
	protected String _getTables(){
		//resolving the Tables
		if (this.getTables()==null || this.getTables().length==0) return "";
		else{
			String tmpTables=" FROM ";
			for (Table table: this.getTables())
				tmpTables+=table.getTable()+",";
			return tmpTables.substring(0, tmpTables.length()-1);
		}
	}
	
	protected String _getLimits(){
		//resolving the Limits		
		if (this.getLimit()==null) return "";
		else return this.getLimit().getLimits();
	}
	
	protected String _getGroups(){
		//resolving the Tables
		if (this.getGroups()==null || this.getGroups().length==0) return "";
		else{
			String tmpGroups="GROUP BY ";
			for (Attribute attrib: this.getGroups())
				tmpGroups+=attrib.getAttribute()+",";
			return tmpGroups.substring(0, tmpGroups.length()-1);
		}
	}
	
	protected String _getDistinct(){
		return this.isUseDistinct()?"DISTINCT":""; 
	}
	
	public abstract Order[] getOrders();
	
	public abstract Limit getLimit();
	
	public abstract Condition getFilter();
	
	public abstract Attribute[] getAttributes();
	
	public abstract Table[] getTables();
	
	public abstract Attribute[] getGroups();
	
	public abstract boolean isUseDistinct();
	
	/**
	 * 
	 */
	public String toString(){
		return this.getExpression();
	}
	//public abstract void setTables(Table[] tables);
	
	/**
	 * 
	 */
	public String getResultAsJSon(boolean useTableCount,boolean ... resultSetReuse) throws Exception{
		DBSession session= DBSession.connect();
		String tempJson;
		try{
			tempJson= getResultAsJSon(session, useTableCount, resultSetReuse);
		}finally{
			session.release();
		}
		return tempJson;
	}
	
	/**
	 * 
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public String getResultAsJSon(DBSession session,boolean useTableCount, boolean ... resultSetReuse) throws Exception{
		return  this.toJSon(session.execute(this.getExpression(), (resultSetReuse==null || resultSetReuse.length==0)?false:resultSetReuse[0]), useTableCount);
	} 
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public ResultSet getResults(boolean ... resultSetReuse) throws Exception{
		DBSession session = DBSession.connect();
		ResultSet tempRes;
		try{
			tempRes=this.getResults(session, resultSetReuse);
		}finally{
			session.release();
		}
		return tempRes;		
	}
	
	/**
	 * 
	 * @param session
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getResults(DBSession session, boolean ... resultSetReuse) throws Exception{
		return session.execute(this.getExpression(),(resultSetReuse==null || resultSetReuse.length==0)?false:resultSetReuse[0]);
	}
	
	private String toJSon(ResultSet resultSet,boolean useTableCount) throws SQLException
	{
		StringBuilder json = new StringBuilder();

		json.append("{\"response\":{\"value\":{\"items\":[");

		ResultSetMetaData metaData = resultSet.getMetaData();
		int numberOfColumns = metaData.getColumnCount();
		
		//logger.debug("number of columns "+numberOfColumns);
			
		int count = 0;

		while(resultSet.next()){

			if (count>0) json.append(",{");
			else json.append('{');
						
			
			for (int column = 1; column <=numberOfColumns; column++){
				if (column>1) json.append(',');
				json.append(quote(metaData.getColumnName(column)));
				json.append(':');
				json.append(quote(resultSet.getString(column)));
			}

			json.append('}');
			count++;
		}
		int totalCount=0;
		if (useTableCount){
			if (SimpleTable.class.getName().compareTo(this.getTables()[0].getClass().getName())==0)
				try {
					long start = System.currentTimeMillis();
					totalCount=((SimpleTable) this.getTables()[0]).getCount();
					logger.trace("totalcount is "+totalCount+" and took  "+(System.currentTimeMillis()-start));
				} catch (Exception e) {
					logger.warn("error getting count in ResultToJson");
				}
			else
				try {
					long start = System.currentTimeMillis();
					totalCount=new SimpleTable(this.getTables()[0].getTableName()).getCount();
					logger.trace("totalcount is "+totalCount+" and took  "+(System.currentTimeMillis()-start));
				} catch (Exception e) {
					logger.warn("error getting count in ResultToJson");
				}
			
		} else totalCount = count;
		json.append("],\"total_count\":"+totalCount);
		json.append(",\"version\":1}}}");
		logger.trace("the json count is: "+totalCount);
		return json.toString();

	}
	
	/**
     * Produce a string in double quotes with backslash sequences in all the
     * right places. A backslash will be inserted within </, allowing JSON
     * text to be delivered in HTML. In JSON text, a string cannot contain a
     * control character or an unescaped quote or backslash.
     * @param string A String
     * @return  A String correctly formatted for insertion in a JSON text.
     */
    private static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         b;
        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);
        String       t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }
	
}
