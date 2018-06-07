package org.gcube.dbinterface.h2;

import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.conditions.OperatorCondition;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.CreateTable;
import org.gcube.common.dbinterface.queries.Delete;
import org.gcube.common.dbinterface.queries.DropTable;
import org.gcube.common.dbinterface.queries.GetMetadata;
import org.gcube.common.dbinterface.queries.Insert;
import org.gcube.common.dbinterface.queries.Select;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;
import org.gcube.common.dbinterface.types.Type;
import org.gcube.common.dbinterface.types.Type.Types;
import org.gcube.common.dbinterface.utils.Utility;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

public class CreateTableTest {

	private static final Logger logger = LoggerFactory.getLogger(CreateTableTest.class);
	
	private static String TABLE_NAME ="testTable";
	private static String FIELD_NAME ="test1";
	private static String FIELD_VALUE ="testValue";
	
	
	
	static {
		try {
			DBSession.initialize("org.gcube.dbinterface.h2", "sa", "", "mem:h2Test",1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void tableLifeCycletest() throws Exception{
		createTable();
		insertInto();
		getMetadata();
		select();
		deleteEntry();
		drop();
		getMetadata();
	}
	
	public void createTable() throws Exception {
		CreateTable tableQuery =DBSession.getImplementation(CreateTable.class);
		tableQuery.setColumnsDefinition(Utility.getColumnDefinition(FIELD_NAME, new Type(Types.STRING, 150)));
		tableQuery.setTableName(TABLE_NAME);
		DBSession session = DBSession.connect();
		Table table = tableQuery.execute(session);
		session.release();
		assertNotNull(table);
	}
	
	public void getMetadata() throws Exception {
		logger.trace("getMetadataTest");
		GetMetadata metadata =DBSession.getImplementation(GetMetadata.class);
		metadata.setTable(TABLE_NAME);
		DBSession session = DBSession.connect();
		LinkedHashMap<String, Type> metadataMap = metadata.getResults(session);
		session.release();
		for (Entry<String, Type> entry : metadataMap.entrySet())
			logger.debug("[TEST]"+entry.getKey()+" of type "+entry.getValue().getType().getValue());
	}
	

	public void insertInto() throws Exception {
		logger.trace("insertIntoTest");
		Insert insertValues =DBSession.getImplementation(Insert.class);
		insertValues.setInsertValues(FIELD_VALUE);
		SimpleTable st = new SimpleTable(TABLE_NAME);
		//logger.debug("[TEST] the columns of the table "+TABLE_NAME+" are "+st.getFieldsMapping().values().size());
		insertValues.setTable(st);
		DBSession session = DBSession.connect();
		insertValues.execute(session);
		session.release();
		assertTrue(st.getCount()>0);
	}
	
	public void select() throws Exception{
		Select select = DBSession.getImplementation(Select.class);
		select.setTables(new Table(TABLE_NAME));
		DBSession session = DBSession.connect();
		ResultSet result = select.getResults(session, true);
		assertTrue(result.next());
		assertTrue(result.getString(FIELD_NAME).equals(FIELD_VALUE));
	}
	
	public void deleteEntry() throws Exception{
		Delete delete = DBSession.getImplementation(Delete.class);
		SimpleTable t = new SimpleTable(TABLE_NAME);
		delete.setTable(t);
		delete.setFilter(new OperatorCondition<SimpleAttribute, String>(new SimpleAttribute(FIELD_NAME), FIELD_VALUE, " LIKE "));
		DBSession session = DBSession.connect();
		delete.execute(session);
		assertTrue(delete.getDeletedItems()>0);
		assertTrue(t.getCount()==0);
	}
	
	public void drop() throws Exception{
		DropTable drop = DBSession.getImplementation(DropTable.class);
		drop.setTableName(TABLE_NAME);
		DBSession session = DBSession.connect();
		drop.execute(session);
		session.release();		
	}
	
}
