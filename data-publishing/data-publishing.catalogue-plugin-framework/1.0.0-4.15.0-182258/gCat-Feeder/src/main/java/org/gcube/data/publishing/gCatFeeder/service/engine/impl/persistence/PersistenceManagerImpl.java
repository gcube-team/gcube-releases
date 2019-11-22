package org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence;

import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.ID;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.fields;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.gcube.data.publishing.gCatFeeder.service.engine.ConnectionManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.PersistenceManager;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.ElementNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InvalidRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.PersistenceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistenceManagerImpl implements PersistenceManager {


	private static final Logger log= LoggerFactory.getLogger(PersistenceManagerImpl.class);

	@Inject 
	ConnectionManager connections;


	@Override
	public ExecutionDescriptor create(ExecutionRequest request) throws PersistenceError, InvalidRequest {
		Connection conn=null;
		try {
			log.debug("Looking for execution similar to request {} ",request);
			conn=connections.getConnection();
			DBQueryDescriptor queryDescriptor=Queries.translateObject(request);
			PreparedStatement ps=Queries.GET_SIMILAR.get(conn, queryDescriptor);
			ResultSet rs=ps.executeQuery();
			if(rs.next()) {
				log.debug("Found similar, returning it..");
				// FOUND SIMILAR OPTION
				return Queries.translateRow(rs);
			}else {
				log.debug("Inserting request ..");
				// PREPARE REQUEST
				PreparedStatement psInsert=Queries.INSERT_NEW.prepare(conn, Statement.RETURN_GENERATED_KEYS);
				psInsert=Queries.INSERT_NEW.fill(psInsert, queryDescriptor);
				psInsert.executeUpdate();
				ResultSet rsId=psInsert.getGeneratedKeys();
				rsId.next();
				Long generatedId=rsId.getLong(ID);

				DBQueryDescriptor getQuery=new DBQueryDescriptor(fields.get(ID), generatedId);
				PreparedStatement psGet=Queries.GET_BY_ID.get(conn, getQuery);
				rs=psGet.executeQuery();
				rs.next();
				ExecutionDescriptor toReturn= Queries.translateRow(rs);				
				conn.commit();
				return toReturn;
			}
		}catch(InvalidRequest e) {
			throw e;
		}catch(Throwable t) {
			throw new PersistenceError(t);
		}finally {
			try {
				if(conn!=null)conn.close();
			} catch (SQLException e) {
				throw new PersistenceError(e);
			}
		}		
	}

	@Override
	public ExecutionDescriptor getById(Long id) throws PersistenceError, ElementNotFound, InvalidRequest {
		Connection conn=null;
		try {
			log.debug("Querying by ID {} ",id);
			conn=connections.getConnection();
			DBQueryDescriptor getQuery=new DBQueryDescriptor(fields.get(ID), id);
			PreparedStatement psGet=Queries.GET_BY_ID.get(conn, getQuery);
			ResultSet rs=psGet.executeQuery();
			if(rs.next())
				return Queries.translateRow(rs);
			else throw new ElementNotFound("Unable to locate Element with ID "+id);
		}catch(InvalidRequest e) {
			throw e;
		}catch(Throwable t) {
			throw new PersistenceError(t);
		}finally {
			try {
				if(conn!=null)conn.close();
			} catch (SQLException e) {
				throw new PersistenceError(e);
			}
		}
	}

	@Override
	public Collection<ExecutionDescriptor> get(DBQueryDescriptor filter)
			throws PersistenceError, InvalidRequest {
		Connection conn=null;
		try {
			log.debug("Looking for execution according to filter {}",filter);
			conn=connections.getConnection();
			ArrayList<ExecutionDescriptor> toReturn=new ArrayList<>();			
			PreparedStatement psGet=Queries.GET_ALL.get(conn, filter);
			ResultSet rs=psGet.executeQuery();
			while(rs.next())
				toReturn.add(Queries.translateRow(rs));
			return toReturn;
		}catch(InvalidRequest e) {
			throw e;
		}catch(Throwable t) {
			throw new PersistenceError(t);
		}finally {
			try {
				if(conn!=null)conn.close();
			} catch (SQLException e) {
				throw new PersistenceError(e);
			}
		}
	}

	@Override
	public boolean update(ExecutionDescriptor toUpdate) throws PersistenceError, ElementNotFound,InvalidRequest {
		Connection conn=null;
		try {
			log.debug("Updateing {} ",toUpdate);
			conn=connections.getConnection();
			PreparedStatement ps=Queries.UPDATE.get(conn, Queries.translateObject(toUpdate));
			int result=ps.executeUpdate();
			conn.commit();
			return result>0;
		}catch(InvalidRequest e) {
			throw e;
		}catch(Throwable t) {
			throw new PersistenceError(t);
		}finally {
			try {
				if(conn!=null)conn.close();
			} catch (SQLException e) {
				throw new PersistenceError(e);
			}
		}
	}

	@Override
	public boolean acquire(Long id) throws PersistenceError, ElementNotFound,InvalidRequest {		
		Connection conn=null;
		try {
			log.debug("Acquiring {} ",id);
			conn=connections.getConnection();
			PreparedStatement ps=Queries.ACQUIRE.get(conn, new DBQueryDescriptor(fields.get(ID), id));
			int result=ps.executeUpdate();
			conn.commit();
			return result>0;
		}catch(InvalidRequest e) {
			throw e;
		}catch(Throwable t) {
			throw new PersistenceError(t);
		}finally {
			try {
				if(conn!=null)conn.close();
			} catch (SQLException e) {
				throw new PersistenceError(e);
			}
		}
		
	}



}
