package org.gcube.application.perform.service.engine;

import java.sql.SQLException;
import java.util.List;

import org.gcube.application.perform.service.engine.dm.DMException;
import org.gcube.application.perform.service.engine.model.BeanNotFound;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.importer.ImportRequest;
import org.gcube.application.perform.service.engine.model.importer.ImportRoutineDescriptor;

public interface Importer {

	
	public ImportRoutineDescriptor importExcel(ImportRequest request) throws DMException, BeanNotFound, SQLException, InternalException;
	public List<ImportRoutineDescriptor> getDescriptors(DBQueryDescriptor query) throws SQLException, InternalException;
	public List<ImportRoutineDescriptor> getGroupedDescriptors(DBQueryDescriptor desc) throws SQLException, InternalException ;
}
