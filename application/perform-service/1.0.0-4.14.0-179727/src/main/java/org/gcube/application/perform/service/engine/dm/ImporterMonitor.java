package org.gcube.application.perform.service.engine.dm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;

import org.gcube.application.perform.service.engine.DataBaseManager;
import org.gcube.application.perform.service.engine.PerformanceManager;
import org.gcube.application.perform.service.engine.impl.Queries;
import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.DBField.ImportRoutine;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.gcube.application.perform.service.engine.model.importer.ImportRoutineDescriptor;
import org.gcube.application.perform.service.engine.model.importer.ImportStatus;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitorListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ImporterMonitor implements DMMonitorListener {

	private static final Logger log= LoggerFactory.getLogger(ImporterMonitor.class);

	 
	private PerformanceManager performance;
	private ImportRoutineDescriptor routine;
	


	public ImporterMonitor(PerformanceManager performance, ImportRoutineDescriptor routine) {
		super();
		this.performance = performance;
		this.routine = routine;
	}

	@Override
	public void accepted() {
		updateStatus(ImportStatus.ACCEPTED,routine);		
	}

	@Override
	public void cancelled() {
		updateStatus(ImportStatus.CANCELLED,routine);
	}

	@Override
	public void complete(double percentage) {
		try{
			performance.loadOutputData(routine);		
			updateStatus(ImportStatus.COMPLETE,routine);
			log.debug("Completed monitoring of {} ",routine);
		}catch(Throwable t) {
			log.error("Unable to load output data for "+routine,t);
			updateStatus(ImportStatus.FAILED,routine);
		}
	}

	@Override
	public void failed(String message, Exception exception) {
		updateStatus(ImportStatus.FAILED,routine);
	}

	@Override
	public void running(double percentage) {
		updateStatus(ImportStatus.RUNNING,routine);
	}



	private static final void updateStatus(ImportStatus status,ImportRoutineDescriptor routine) {
		try{
			log.debug("Updateing status {} for {} ",status,routine);
			DataBaseManager db=DataBaseManager.get();
			Connection conn=db.getConnection();
			try {
				conn.setAutoCommit(true);

				Instant endTime=null;
				switch(status) {
				case CANCELLED:
				case COMPLETE : 
				case FAILED : endTime=Instant.now();
				}

				DBQueryDescriptor queryValues=new DBQueryDescriptor().
						add(DBField.ImportRoutine.fields.get(ImportRoutine.ID), routine.getId()).
						add(DBField.ImportRoutine.fields.get(ImportRoutine.STATUS), status.toString()).
						add(DBField.ImportRoutine.fields.get(ImportRoutine.END), endTime != null ? new Timestamp(endTime.toEpochMilli()) : null);

				PreparedStatement psUpdate=Queries.UPDATE_IMPORT_STATUS.get(conn, queryValues);		
				psUpdate.executeUpdate();
			}finally {
				conn.close();
			}
		}catch(Throwable t) {
			log.warn("Unable to update status on database");
			log.debug("Error : ",t);
		}
	}


	
}
