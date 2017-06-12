package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CustomQueryManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.CustomQueryDescriptorStubs;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryConstructurThread extends Thread {

	final static Logger logger= LoggerFactory.getLogger(QueryConstructurThread.class);

	public enum Operation {
		DELETE,CREATE
	}


	private String userId;
	private Operation op;
	private String table;
	private String queryString;




	public QueryConstructurThread(String userId, Operation op, String query, String tableName) {
		super(op+"_"+userId);
		this.userId=userId;
		this.op=op;
		this.table=tableName;
		this.queryString=query;
	}

	@Override
	public void run() {
		DBSession session=null;
		try{
			switch(op){
			case CREATE :
				CustomQueryDescriptorStubs desc=CustomQueryManager.getDescriptor(userId);
				desc.status(ExportStatus.ONGOING);
				CustomQueryManager.updateDescriptor(desc);
				try{
					session=DBSession.getInternalDBSession();
					
					logger.trace("Creating view [ "+table+" ]for "+userId+"'s query [ "+queryString+" ]");
					session.executeUpdate("CREATE TABLE "+table+" AS ( "+queryString+" )");
					logger.trace("Getting meta for custom table "+table);
					
					desc.rows(session.getTableCount(table));
					ResultSet rsColumns=session.executeQuery("SELECT * FROM "+table+" LIMIT 1 OFFSET 0");
					ResultSetMetaData meta=rsColumns.getMetaData();
					
					for(int i=1;i<=meta.getColumnCount();i++)
						desc.fields().theList().add(new Field(meta.getColumnName(i),"",FieldType.STRING));
					
					desc.status(ExportStatus.COMPLETED);
					CustomQueryManager.updateDescriptor(desc);
				}catch(Exception e){
					desc.status(ExportStatus.ERROR);
					desc.errorMessage(e.getMessage());
					CustomQueryManager.updateDescriptor(desc);
				}
				break;
			case DELETE : 
				logger.trace("Dropping "+userId+"'s custom query view "+table+", query was [ "+queryString+" ]");
				session=DBSession.getInternalDBSession();
				session.dropTable(table);
				break;
			default : throw new Exception ("Operation not defined");
			}
			logger.trace("DONE");
		}catch(Exception e){
			logger.error("Unable to "+op+" table",e);
		}
		finally{if(session!=null)
			try {
				session.close();
			} catch (Exception e) {
				logger.error("Unable to close session",e);
			}}
	}


}
