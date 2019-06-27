package org.gcube.application.perform.service.engine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import org.gcube.application.perform.service.engine.dm.DMException;
import org.gcube.application.perform.service.engine.model.CSVExportRequest;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.InvalidRequestException;
import org.gcube.application.perform.service.engine.model.importer.AnalysisType;
import org.gcube.application.perform.service.engine.model.importer.ImportRoutineDescriptor;

public interface PerformanceManager {

	public Map<String,String> generateCSV(CSVExportRequest request)throws SQLException, InvalidRequestException, InternalException, IOException;
	
	public void loadOutputData(ImportRoutineDescriptor desc)throws SQLException, InvalidRequestException, InternalException, IOException, DMException;
	
	public Map<String,String> getStatistics(AnalysisType type)throws SQLException, InvalidRequestException, InternalException, IOException;
}
