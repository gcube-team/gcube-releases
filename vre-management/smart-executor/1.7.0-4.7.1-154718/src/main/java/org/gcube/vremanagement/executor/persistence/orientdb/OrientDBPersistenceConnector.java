/**
 * 
 */
package org.gcube.vremanagement.executor.persistence.orientdb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.vremanagement.executor.SmartExecutorInitializator;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.exception.PluginStateNotRetrievedException;
import org.gcube.vremanagement.executor.exception.SchedulePersistenceException;
import org.gcube.vremanagement.executor.json.ObjectMapperManager;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConfiguration;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConnector;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.scheduledtask.ScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class OrientDBPersistenceConnector extends
		SmartExecutorPersistenceConnector {

	private static final Logger logger = LoggerFactory
			.getLogger(OrientDBPersistenceConnector.class);

	protected final static int LAST = -1;

	protected final String SCOPE = "scope";
	protected final String UUID = "uuid";
	protected final String ITERATION = "iteration";
	protected final String TIMESTAMP = "timestamp";
	
	protected final String RUN_ON = "runOn";

	protected OPartitionedDatabasePool oPartitionedDatabasePool;
	protected ObjectMapper mapper;

	public OrientDBPersistenceConnector(SmartExecutorPersistenceConfiguration configuration)
			throws Exception {
		super();
		prepareConnection(configuration);
		this.mapper = ObjectMapperManager.getObjectMapper();
	}

	protected void prepareConnection(
			SmartExecutorPersistenceConfiguration configuration)
			throws Exception {
		logger.debug("Preparing Connection for {}", this.getClass()
				.getSimpleName());
		String url = configuration.getURL();
		String username = configuration.getUsername();
		String password = configuration.getPassword();
		this.oPartitionedDatabasePool = new OPartitionedDatabasePool(url,
				username, password);
	}

	protected void prepareObjectMapper() {
		this.mapper = new ObjectMapper();

	}

	@Override
	public void close() throws Exception {
		oPartitionedDatabasePool.close();
	}

	protected PluginStateEvolution getPluginStateEvolution(UUID uuid,
			int iterationNumber) throws Exception {
		ODatabaseDocumentTx db = null;
		try {
			db = oPartitionedDatabasePool.acquire();
			String type = PluginStateEvolution.class.getSimpleName();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(UUID, uuid.toString());
			params.put(SCOPE, SmartExecutorInitializator.getCurrentScope());

			OSQLSynchQuery<ODocument> query = null;
			if (iterationNumber != LAST) {
				query = new OSQLSynchQuery<ODocument>(
						String.format(
								"SELECT FROM %s WHERE %s = :%s AND %s = :%s AND %s = :%s ORDER BY %s DESC LIMIT 1",
								type, SCOPE, SCOPE, UUID, UUID, ITERATION,
								ITERATION, TIMESTAMP));
				params.put(ITERATION, iterationNumber);
			} else {
				query = new OSQLSynchQuery<ODocument>(
						String.format(
								"SELECT FROM %s WHERE %s = :%s AND %s = :%s ORDER BY %s DESC",
								type, SCOPE, SCOPE, UUID, UUID, ITERATION));
			}

			List<ODocument> result = query.execute(params);

			ODocument resDoc = null;

			if (iterationNumber != LAST) {
				resDoc = result.get(0);
			} else {
				// TODO manage better
				long maxTimestamp = 0;
				for (ODocument oDoc : result) {
					long tm = (long) oDoc.field(TIMESTAMP);
					if (maxTimestamp <= tm) {
						maxTimestamp = tm;
						resDoc = oDoc;
					}
				}
			}

			String json = resDoc.toJSON("class");
			PluginStateEvolution pluginStateEvolution = mapper.readValue(json,
					PluginStateEvolution.class);

			return pluginStateEvolution;
		} catch (Exception e) {
			throw new PluginStateNotRetrievedException(e);
		} finally {
			db.close();
		}
	}

	@Override
	public PluginStateEvolution getPluginInstanceState(UUID uuid,
			int iterationNumber) throws Exception {
		return getPluginStateEvolution(uuid, iterationNumber);
	}

	@Override
	public PluginStateEvolution getLastPluginInstanceState(UUID uuid)
			throws Exception {
		return getPluginStateEvolution(uuid, LAST);
	}

	@Override
	public void pluginStateEvolution(PluginStateEvolution pluginStateEvolution,
			Exception exception) throws Exception {
		ODatabaseDocumentTx db = null;
		try {
			db = oPartitionedDatabasePool.acquire();

			ODocument doc = new ODocument(
					PluginStateEvolution.class.getSimpleName());
			String json = mapper.writeValueAsString(pluginStateEvolution);
			doc.fromJSON(json);
			doc.field(SCOPE, SmartExecutorInitializator.getCurrentScope());

			doc.save();
			db.commit();
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			throw e;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	@Override
	public void addScheduledTask(ScheduledTask scheduledTask)
			throws SchedulePersistenceException {
		ODatabaseDocumentTx db = null;
		try {
			db = oPartitionedDatabasePool.acquire();

			ODocument doc = new ODocument(ScheduledTask.class.getSimpleName());

			long timestamp = Calendar.getInstance().getTimeInMillis();
			doc.field(TIMESTAMP, timestamp);

			String json = mapper.writeValueAsString(scheduledTask);
			doc.fromJSON(json);
			doc.save();

			db.commit();
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			throw new SchedulePersistenceException(e);
		} finally {
			db.close();
		}

	}

	@Override
	public List<ScheduledTask> getOrphanScheduledTasks(
			Collection<? extends PluginDeclaration> pluginDeclarations)
			throws SchedulePersistenceException {
		ODatabaseDocumentTx db = null;
		try {
			db = oPartitionedDatabasePool.acquire();
			String type = ScheduledTask.class.getSimpleName();
			
			String queryString = String.format("SELECT * FROM %s WHERE %s = '%s'", type, "scope", SmartExecutorInitializator.getCurrentScope());
			if(pluginDeclarations!=null && pluginDeclarations.size()!=0){
				boolean first = true;
				for(PluginDeclaration pluginDeclaration : pluginDeclarations){
					if(first){
						first = false;
						queryString = String.format("%s AND ( (%s = '%s') ", 
								queryString, 
								ScheduledTask.LAUNCH_PARAMETER + "." + LaunchParameter.PLUGIN_NAME, 
								pluginDeclaration.getName());
					}else{
						queryString = String.format("%s OR (%s = '%s') ",
								queryString, 
								ScheduledTask.LAUNCH_PARAMETER + "." + LaunchParameter.PLUGIN_NAME, 
								pluginDeclaration.getName());
					}
				}
				queryString = queryString + ")";
			}
			
			
			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(
					queryString
			);

			List<ODocument> result = query.execute();

			List<ScheduledTask> scheduledTasks = new ArrayList<>();

			for (ODocument doc : result) {
				String json = doc.toJSON("class");

				ScheduledTask scheduledTask = mapper.readValue(json,
						ScheduledTask.class);
				try {
					if (isOrphan(scheduledTask)) {
						scheduledTasks.add(scheduledTask);
					}
				} catch (Exception e) {
					logger.error(
							"An Exception occurred while evaluating if {} is orphan",
							json, e);
				}

			}

			return scheduledTasks;
		} catch (Exception e) {
			throw new SchedulePersistenceException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	protected ODocument getScheduledTaskDocument(ODatabaseDocumentTx db,
			UUID uuid) throws SchedulePersistenceException {
		try {
			String type = ScheduledTask.class.getSimpleName();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(UUID, uuid.toString());

			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(
					String.format("SELECT FROM %s WHERE %s = :%s", type, UUID,
							UUID));

			List<ODocument> result = query.execute(params);
			if (result.size() > 1) {
				String error = String.format(
						"Found more than one %s with UUID=%s. %s. %s.", type,
						uuid.toString(),
						"This is really strange and should not occur",
						"Please contact the smart-executor administrator");
				logger.error(error);
				throw new SchedulePersistenceException(error);
			} else if (result.size() == 0) {
				String error = String.format("No %s with UUID=%s found.", type,
						uuid.toString());
				logger.error(error);
				throw new SchedulePersistenceException(error);
			}

			return result.get(0);
		} catch (Exception e) {
			throw new SchedulePersistenceException(e);
		}
	}

	@Override
	public ScheduledTask getScheduledTask(UUID uuid)
			throws SchedulePersistenceException {
		ODatabaseDocumentTx db = null;
		try {
			db = oPartitionedDatabasePool.acquire();
			ODocument doc = getScheduledTaskDocument(db, uuid);
			String json = doc.toJSON("class");
			return mapper.readValue(json, ScheduledTask.class);
		} catch (Exception e) {
			throw new SchedulePersistenceException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	@Override
	public void reserveScheduledTask(ScheduledTask scheduledTask)
			throws SchedulePersistenceException {
		releaseScheduledTask(scheduledTask);
	}

	@Override
	public void removeScheduledTask(UUID uuid)
			throws SchedulePersistenceException {
		ODatabaseDocumentTx db = null;
		try {
			db = oPartitionedDatabasePool.acquire();
			ODocument doc = getScheduledTaskDocument(db, uuid);
			doc.delete();
			db.commit();
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			throw new SchedulePersistenceException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	
	@Override
	public void removeScheduledTask(ScheduledTask scheduledTask)
			throws SchedulePersistenceException {
		removeScheduledTask(scheduledTask.getUUID());
	}

	@Override
	public void releaseScheduledTask(UUID uuid)
			throws SchedulePersistenceException {
		ODatabaseDocumentTx db = null;
		try {
			db = oPartitionedDatabasePool.acquire();
			ODocument doc = getScheduledTaskDocument(db, uuid);
			doc.removeField(RUN_ON);
			doc.save();
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			throw new SchedulePersistenceException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	@Override
	public void releaseScheduledTask(ScheduledTask scheduledTask)
			throws SchedulePersistenceException {
		releaseScheduledTask(scheduledTask.getUUID());
	}
}
