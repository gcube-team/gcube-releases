package org.gcube.application.perform.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

import org.gcube.application.perform.service.engine.DataBaseManager;
import org.gcube.application.perform.service.engine.PerformanceManager;
import org.gcube.application.perform.service.engine.dm.DMException;
import org.gcube.application.perform.service.engine.impl.PerformanceManagerImpl;
import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.InternalException;

public class LoadSchemaTest {

	public static void main(String[] args) throws MalformedURLException, IOException, SQLException, InternalException, DMException {
		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());
		
		PerformServiceLifecycleManager.initSchema("src/main/webapp/WEB-INF");
		
		TokenSetter.set("/gcube/preprod/preVRE");
		PerformanceManagerImpl.initDatabase();
		
		
		PerformanceManager mng =new PerformanceManagerImpl();
		
		/**
		 * [id=26, 
		 * farmId=12682101, 
		 * batch_type=HATCHERY_INDIVIDUAL, 
		 * sourceUrl=https://data1-d.d4science.org/shub/E_aUJUbDNzeUlLL29KL2xlZUloWFQ5TEdlZ0ZnZzlNNTVLNUEzeDVRNFVoVHlLMW5DVG5RbVVXVzlYeUUzZWFXRA==, sourceVersion=1.1, startTime=2019-01-25T14:52:31.442Z, endTime=null, status=ACCEPTED, lock=localhost, caller=MSgXVCkHb0SDoQLlCLQV9Kj8dPJlJ6gY+XicZJhenQkyuxA11kGUIdhxKs3jUdGK, computationId=7c6f5d7e-b778-42be-af20-b99229fa99ee, computationUrl=http://dataminer1-pre.d4science.org:80//wps/RetrieveResultServlet?id=7c6f5d7e-b778-42be-af20-b99229fa99ee, computationOperator=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PERFORMFISH_DATA_EXTRACTOR, computationOperatorName=Performfish Data Extractor, computationRequest=https://dataminer1-pre.d4science.org/wps/WebProcessingService?request=Execute&service=WPS&Version=1.0.0&gcube-token=97cfc53e-7f71-4676-b5e0-bdd149c8460f-98187548&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PERFORMFISH_DATA_EXTRACTOR&DataInputs=InputData=https%3A%2F%2Fdata1-d.d4science.org%2Fshub%2FE_aUJUbDNzeUlLL29KL2xlZUloWFQ5TEdlZ0ZnZzlNNTVLNUEzeDVRNFVoVHlLMW5DVG5RbVVXVzlYeUUzZWFXRA%3D%3D;BatchType=HATCHERY_INDIVIDUAL;FarmID=12682101;]
		 */

		// RELOAD DATA FROM COMPLETED ROUTINES
//		ResultSet rs=Queries.GET_IMPORT_ROUTINE_BY_ID.get(DataBaseManager.get().getConnection(), 
//				new DBQueryDescriptor().
//				add(DBField.ImportRoutine.fields.get(DBField.ImportRoutine.STATUS),"COMPLETE")).executeQuery();
//				
//		while(rs.next()) {
//		
//		ImportRoutineDescriptor desc=Queries.rowToDescriptor(rs);
//		// RELOAD DATA 
//			mng.loadOutputData(desc);
//		// RELAUNCH REQUEST
//			
//			
//		}
		

		
		
		
		
//		
//		CSVExportRequest req=new CSVExportRequest(new AnalysisType("GROW_OUT_AGGREGATED", "GROW_OUT_AGGREGATED"));
//		req.addAreas(Arrays.asList("A1","A2"));
//		req.addFarmId(12682549l);
//		req.addPeriods(Arrays.asList("p1"));
//		req.addSpecies(Arrays.asList("Gadidae"));
//		
//		System.out.println(mng.generateCSV(req));
	}

}
