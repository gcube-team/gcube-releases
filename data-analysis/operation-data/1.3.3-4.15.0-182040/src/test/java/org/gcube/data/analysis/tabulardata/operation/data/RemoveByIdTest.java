package org.gcube.data.analysis.tabulardata.operation.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.dbutils.DbUtils;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.data.remove.RemoveRowsByIdFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class RemoveByIdTest extends OperationTester<RemoveRowsByIdFactory>{

	@Inject
	RemoveRowsByIdFactory factory;
	
	@Inject
	CodelistHelper codelistHelper;
	
	@Inject
	DatabaseConnectionProvider connProvider;
	
	
	private static Table testCodelist;
	
	@Before
	public void setupTestTables(){
		testCodelist = codelistHelper.createSpeciesCodelist();
	}
	
	@Override
	protected WorkerFactory getFactory() {
		return factory;
	}
	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String,Object> parameters = new HashMap<String, Object>();
		parameters.put(RemoveRowsByIdFactory.ID_PARAMETER.getIdentifier(), getRowIds());
		return parameters;
	}
	
	@Override
	protected TableId getTargetTableId() {
		return testCodelist.getId();
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private ArrayList<Integer> getRowIds(){
		ArrayList<Integer> toReturn=new ArrayList<Integer>();		
		Connection conn=null;
		Statement stmt=null;
		ResultSet rs=null;
		try{
			conn=connProvider.getConnection();
			stmt=conn.createStatement();
			rs=stmt.executeQuery(formIdQuery());
			while(rs.next()){
				toReturn.add(rs.getInt(1));
			}
			
		}catch(SQLException e){
			 Assert.fail("Test failed : " + e.getMessage());
		}finally{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}
		return toReturn;
	}
	
	
	private String formIdQuery(){
		Column idColumn=testCodelist.getColumnsByType(IdColumnType.class).get(0);
		return "SELECT " +idColumn.getName()+" FROM "+testCodelist.getName()+" LIMIT 20 OFFSET 0";
	}
}
