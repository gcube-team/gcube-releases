package org.gcube.application.perform.service.engine.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.application.perform.service.LocalConfiguration;
import org.gcube.application.perform.service.engine.DataBaseManager;
import org.gcube.application.perform.service.engine.Importer;
import org.gcube.application.perform.service.engine.PerformanceManager;
import org.gcube.application.perform.service.engine.dm.DMException;
import org.gcube.application.perform.service.engine.dm.DMUtils;
import org.gcube.application.perform.service.engine.dm.ImporterMonitor;
import org.gcube.application.perform.service.engine.model.BeanNotFound;
import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.DBField.ImportRoutine;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.InvalidRequestException;
import org.gcube.application.perform.service.engine.model.importer.ImportRequest;
import org.gcube.application.perform.service.engine.model.importer.ImportRoutineDescriptor;
import org.gcube.application.perform.service.engine.model.importer.ImportStatus;
import org.gcube.application.perform.service.engine.utils.CommonUtils;
import org.gcube.application.perform.service.engine.utils.ScopeUtils;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ImporterImpl implements Importer {

	private static final Logger log= LoggerFactory.getLogger(ImporterImpl.class);

	@Inject 
	private PerformanceManager performance;



	private static final String getHostname() {
		try{
			ApplicationContext context=ContextProvider.get();		
			ContainerConfiguration configuration=context.container().configuration();

			return configuration.hostname();
		}catch(Throwable t) {
			log.warn("UNABLE TO GET HOSTNAME. This should happen only in debug mode.");
			return "localhost";
		}
	}



	public void init() throws InternalException{
		try {
			log.info("Initializing IMPORTER");
			DataBaseManager db=DataBaseManager.get();
			Connection conn=db.getConnection();

			try {
				conn.setAutoCommit(true);
				String hostname=getHostname();

				DBField lockField=ImportRoutine.fields.get(ImportRoutine.LOCK);

				PreparedStatement psOrphans=Queries.ORPHAN_IMPORTS.get(conn,new DBQueryDescriptor(lockField, hostname));
				PreparedStatement psAcquire=Queries.ACQUIRE_IMPORT_ROUTINE.prepare(conn);
				// set ps



				ResultSet rsOrphans=psOrphans.executeQuery();
				long monitoredCount=0l;
				while(rsOrphans.next()) {
					Long id=rsOrphans.getLong(ImportRoutine.ID);
					try {
						ImportRoutineDescriptor desc=Queries.rowToDescriptor(rsOrphans);
						DBQueryDescriptor acquireDesc=new DBQueryDescriptor().
								add(lockField,hostname).
								add(ImportRoutine.fields.get(ImportRoutine.ID), id);

						Queries.ACQUIRE_IMPORT_ROUTINE.fill(psAcquire, acquireDesc);

						if(psAcquire.executeUpdate()>0) {
							log.debug("Acquired {} ",id);
							// Stored caller token
							log.debug("Setting stored token.. ");
							SecurityTokenProvider.instance.set(CommonUtils.decryptString(desc.getCaller()));

							monitor(desc);
							monitoredCount++;
						}
					}catch(Throwable t) {
						log.warn("Unable to monitor orphan with ID {} ",id,t);
					}
				}
				log.info("Acquired {} import executions for monitoring",monitoredCount);
			}finally {
				conn.close();
			}
		}catch(Throwable t) {
			log.warn("Unexpected Error while trying to check orphan import routines");
			throw new InternalException(t);
		}
	}


	@Override
	public ImportRoutineDescriptor importExcel(ImportRequest request) throws DMException, SQLException, InternalException {
		log.debug("Submitting {} ",request);
		ComputationId id=submit(request);
		log.debug("Registering {} computationID {} ",request,id);
		ImportRoutineDescriptor desc=register(id,request);
		log.debug("Monitoring {} computationID {} ",desc,id);
		monitor(desc);
		return getDescriptorById(desc.getId());
	}



	private void monitor(ImportRoutineDescriptor desc) throws DMException {
		log.debug("Monitoring {} ",desc);
		DMUtils.monitor(DMUtils.getComputation(desc), new ImporterMonitor(performance,desc));		
	}




	private ComputationId submit(ImportRequest request) throws DMException, InvalidRequestException {
		/**
		 * dataminer-prototypes.d4science.org/wps/WebProcessingService?
		 * request=Execute&service=WPS&Version=1.0.0&gcube-token=3a8e6a79-1ae0-413f-9121-0d59e5f2cea2-843339462&lang=en-US&
		 * Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.PERFORMFISH_DATA_EXTRACTOR
		 * &
		 * DataInputs=
		 * InputData=https%3A%2F%2Fdata.d4science.org%2Fshub%2F9689bbe2-148f-4406-ab69-6e0f6ab892ca;
		 * BatchType=GROW_OUT_AGGREGATED;
		 * FarmID=ID
		 */

		log.debug("Preparing DM Parameters from request : {} ",request);
		Map<String,String> parameters=new HashMap<>();
		try {
			parameters.put("InputData", request.getSource());
			parameters.put("BatchType", request.getBatchType());
			parameters.put("FarmID", request.getFarmId().toString());		
		}catch(Throwable t) {
			throw new InvalidRequestException("Invalid request : "+request,t); 
		}
		return DMUtils.submitJob(LocalConfiguration.getProperty(LocalConfiguration.IMPORTER_COMPUTATION_ID), parameters);		
	}

	private ImportRoutineDescriptor register(ComputationId computationId,ImportRequest request) throws SQLException, InternalException {

		DBQueryDescriptor insertionRow=new DBQueryDescriptor().
				add(ImportRoutine.fields.get(ImportRoutine.BATCH_TYPE), request.getBatchType()).
				add(ImportRoutine.fields.get(ImportRoutine.CALLER), CommonUtils.encryptString(ScopeUtils.getCaller())).
				add(ImportRoutine.fields.get(ImportRoutine.COMPUTATION_ID), computationId.getId()).
				add(ImportRoutine.fields.get(ImportRoutine.COMPUTATION_OPID), computationId.getOperatorId()).
				add(ImportRoutine.fields.get(ImportRoutine.COMPUTATION_OPNAME), computationId.getOperatorName()).
				add(ImportRoutine.fields.get(ImportRoutine.COMPUTATION_REQ), computationId.getEquivalentRequest()).
				add(ImportRoutine.fields.get(ImportRoutine.COMPUTATION_URL), computationId.getUrlId()).
				add(ImportRoutine.fields.get(ImportRoutine.FARM_ID), request.getFarmId()).
				add(ImportRoutine.fields.get(ImportRoutine.LOCK), getHostname()).
				add(ImportRoutine.fields.get(ImportRoutine.SOURCE_URL), request.getSource()).
				add(ImportRoutine.fields.get(ImportRoutine.SOURCE_VERSION), request.getVersion()).
				add(ImportRoutine.fields.get(ImportRoutine.START), java.sql.Timestamp.from(Instant.now())).
				add(ImportRoutine.fields.get(ImportRoutine.STATUS),ImportStatus.ACCEPTED.toString());


		DataBaseManager db=DataBaseManager.get();
		Connection conn=db.getConnection();
		conn.setAutoCommit(true);
		try {
			PreparedStatement ps=Queries.INSERT_ROUTINE.prepare(conn,Statement.RETURN_GENERATED_KEYS);
			Queries.INSERT_ROUTINE.fill(ps, insertionRow);
			ps.executeUpdate();

			ResultSet rs=ps.getGeneratedKeys();		
			rs.next();

			PreparedStatement psGet=Queries.GET_IMPORT_ROUTINE_BY_ID.get(conn, 
					new DBQueryDescriptor().add(ImportRoutine.fields.get(ImportRoutine.ID), rs.getLong(ImportRoutine.ID)));
			ResultSet rsGet=psGet.executeQuery();
			rsGet.next();
			return Queries.rowToDescriptor(rsGet);
		}finally {
			conn.close();
		}
	}	

	private ImportRoutineDescriptor getDescriptorById(Long id) throws SQLException, InternalException {
		DataBaseManager db=DataBaseManager.get();
		Connection conn=db.getConnection();		
		try {
			PreparedStatement ps=Queries.GET_IMPORT_ROUTINE_BY_ID.get(conn, 
					new DBQueryDescriptor().add(ImportRoutine.fields.get(ImportRoutine.ID), id));
			ps.setLong(1, id);
			ResultSet rs=ps.executeQuery();
			if(rs.next()) return Queries.rowToDescriptor(rs);
			else throw new BeanNotFound("Unable to find Routine with ID "+id);
		}finally {
			conn.close();
		}
	}


	@Override
	public List<ImportRoutineDescriptor> getDescriptors(DBQueryDescriptor desc) throws SQLException, InternalException {
		DataBaseManager db=DataBaseManager.get();
		Connection conn=db.getConnection();
		try {
			PreparedStatement ps=Queries.FILTER_IMPORTS.get(conn, desc);
			ResultSet rs=ps.executeQuery();
			ArrayList<ImportRoutineDescriptor> toReturn=new ArrayList<>();
			while (rs.next())
				toReturn.add(Queries.rowToDescriptor(rs));

			return toReturn;
		}finally {
			conn.close();
		}
	}


	/**
	 * Select * from imports where farmid=13625424 AND 
	 * (batch_type,end_time) IN 
	 * (Select batch_type,max(end_time) as end_time from imports 
	 * WHERE farmid=13625424 AND status = 'COMPLETE' group by batch_type order by batch_type)
	 * 
	 * @return
	 */
	@Override
	public List<ImportRoutineDescriptor> getGroupedDescriptors(DBQueryDescriptor desc)throws SQLException, InternalException {
		DataBaseManager db=DataBaseManager.get();
		Connection conn=db.getConnection();
		try {
			PreparedStatement ps=Queries.LAST_GROUPED_IMPORTS.get(conn, desc);
			ResultSet rs=ps.executeQuery();
			ArrayList<ImportRoutineDescriptor> toReturn=new ArrayList<>();
			while (rs.next())
				toReturn.add(Queries.rowToDescriptor(rs));
			return toReturn;
		}finally {
			conn.close();
		}
	}


}


