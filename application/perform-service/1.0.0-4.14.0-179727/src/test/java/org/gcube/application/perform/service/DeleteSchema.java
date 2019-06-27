package org.gcube.application.perform.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.application.perform.service.engine.DataBaseManager;
import org.gcube.application.perform.service.engine.impl.PerformanceManagerImpl;
import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.importer.AnalysisType;
import org.gcube.application.perform.service.engine.model.importer.ImportedTable;

public class DeleteSchema {

	public static void main(String[] args) throws SQLException, IOException, InternalException {
		
		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());
		
		PerformServiceLifecycleManager.initSchema("src/main/webapp/WEB-INF");
		
		TokenSetter.set("/gcube/preprod/preVRE");
		
		Connection conn=DataBaseManager.get().getConnection();
		Statement stmt=conn.createStatement();
		
		
		
		
		
		for(Entry<AnalysisType,Set<ImportedTable>> entry:PerformanceManagerImpl.getAnalysisConfiguration().entrySet()) {
			for(ImportedTable t:entry.getValue()) {
				stmt.execute("DROP TABLE "+t.getTableName());
			}
		}
		// CLEAN IMPORTS 
				stmt.executeUpdate("DELETE FROM "+DBField.ImportRoutine.TABLE);
		
		conn.commit();
//		throw new RuntimeException("Uncomment commit to really perform cleanup");
	}
	
}
