package org.gcube.data.spd.caching;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.CreateTable;
import org.gcube.common.dbinterface.queries.Insert;
import org.gcube.common.dbinterface.queries.Select;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;
import org.gcube.common.dbinterface.types.Type;
import org.gcube.common.dbinterface.types.Type.Types;
import org.gcube.common.dbinterface.utils.Utility;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class QueryCache<T> implements Serializable{
		
	public static Lock lock = new ReentrantLock(true);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final UUIDGen uidfactory= UUIDGenFactory.getUUIDGen();
	
	private static Logger logger = LoggerFactory.getLogger(QueryCache.class);
	
	private String tableId;
	private boolean valid;
	private boolean tableCreated;
	private boolean empty;
	private boolean error;
		
	
	public QueryCache() {
		this.tableId = "C"+uidfactory.nextUUID().replace("-", "_");
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public boolean isTableCreated() {
		return tableCreated;
	}

	public void setTableCreated(boolean tableCreated) {
		this.tableCreated = tableCreated;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
		

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean store( T obj){
		SimpleTable table = null;
		DBSession session = null;
		try{
			session = DBSession.connect();
			if (!tableCreated){
				CreateTable tableQuery =DBSession.getImplementation(CreateTable.class);
				tableQuery.setColumnsDefinition(Utility.getColumnDefinition("obj", new Type(Types.TEXT)));
				tableQuery.setTableName(this.tableId);
				table = tableQuery.execute(session);
				this.tableCreated = true;
				//logger.trace("table created is "+this.tableId);
			}else table= new SimpleTable(this.tableId);

			if (table ==null) return false;
			Insert insert = DBSession.getImplementation(Insert.class);
			String serializedObject = Bindings.toXml(obj);
			insert.setTable(table);
			insert.setInsertValues(serializedObject);
			insert.execute(session);
			return true;
		}catch (Exception e) {
			logger.warn(" error storing cache ",e);
			return false;
		}finally{
			if (session!=null) session.release();
		}
	}

	@SuppressWarnings("unchecked")
	public void getAll(ObjectWriter<T> writer){
		if (!valid){
			logger.warn("the cache is not valid yet");
			return;
		}
		if (empty){
			logger.warn("the cache entry is empty");
			return;
		}
		DBSession session = null;
		try{
			session = DBSession.connect();
			Select select = DBSession.getImplementation(Select.class);
			select.setTables(new Table(this.tableId));
			ResultSet results = select.getResults();
			while (results.next()){
				if (!writer.isAlive())				
					break;
				writer.write((T)Bindings.fromXml(results.getString(1)));
			}
		}catch (Exception e) {
			logger.warn(" error getting cache ",e);
		}finally{
			logger.trace("closing cache and session is "+session);
			if (session != null) session.release();
		}
	}
	
	public void closeStore(){
		if (!tableCreated) empty =true;
		this.valid= true;
	}
	
	public boolean isValid(){
		return this.valid;
	}
	
		
	public boolean isEmpty() {
		return empty;
	}

	public void dispose(){
		try{
			this.valid=false;
			if(!empty)Utility.drop(this.tableId);
		}catch (Exception e) {
			logger.warn(" error disposing cache ",e);
		}
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException{
		out.writeBoolean(this.empty);
		out.writeBoolean(this.tableCreated);
		out.writeBoolean(this.valid);
		out.writeObject(this.tableId);
		out.writeBoolean(this.error);
		logger.trace("wrote with valid "+this.valid);
		out.flush();
		out.close();
	}
	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException{
		this.empty = in.readBoolean();
		this.tableCreated = in.readBoolean();
		this.valid = in.readBoolean();
		this.tableId = (String) in.readObject();
		this.error = in.readBoolean();
		logger.trace("read with valid "+this.valid);
		in.close();
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (empty ? 1231 : 1237);
		result = prime * result + (tableCreated ? 1231 : 1237);
		result = prime * result + ((tableId == null) ? 0 : tableId.hashCode());
		result = prime * result + (valid ? 1231 : 1237);
		return result;
	}

	@Override
	public String toString() {
		return "QueryCache [tableId=" + tableId + ", valid=" + valid
				+ ", empty=" + empty + ", error=" + error + "]";
	}
	
	
		
}
