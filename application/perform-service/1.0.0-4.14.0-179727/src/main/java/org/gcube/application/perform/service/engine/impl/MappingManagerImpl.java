package org.gcube.application.perform.service.engine.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.gcube.application.perform.service.engine.DataBaseManager;
import org.gcube.application.perform.service.engine.MappingManager;
import org.gcube.application.perform.service.engine.model.BeanNotFound;
import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.anagraphic.Batch;
import org.gcube.application.perform.service.engine.model.anagraphic.Farm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MappingManagerImpl implements MappingManager {

	private static final Logger log= LoggerFactory.getLogger(MappingManagerImpl.class);


	@Override
	public Batch getBatch(DBQueryDescriptor desc) throws SQLException, InternalException{		
		DataBaseManager db=DataBaseManager.get();
		Connection conn=db.getConnection();
		try{
			conn.setAutoCommit(true);
			Query getQuery=Queries.GET_BATCH_BY_DESCRIPTIVE_KEY;
			PreparedStatement psSearch=getQuery.get(conn, desc);	

			ResultSet rs=psSearch.executeQuery();

			if(rs.next())
				return Queries.rowToBatch(rs);	

			// ID NOT FOUND, TRY TO REGISTER IT

			log.trace("Registering new Batch from condition {}",desc);
			desc.add(DBField.Batch.fields.get(DBField.Batch.UUID), UUID.randomUUID());
			PreparedStatement psInsert=Queries.INSERT_BATCH.get(conn, desc);

			psInsert.executeUpdate();		
			rs=psSearch.executeQuery();

			if(rs.next())
				return Queries.rowToBatch(rs);
			else throw new BeanNotFound(String.format("Unable to find Bean with ",desc));
		}finally {
			conn.close();
		}

	}

	@Override
	public Farm getFarm(DBQueryDescriptor desc)  throws SQLException, InternalException{
		DataBaseManager db=DataBaseManager.get();


		Connection conn=db.getConnection();
		try{
			PreparedStatement psGet=null;
		DBField IDField=DBField.Farm.fields.get(DBField.Farm.FARM_ID);
		if(desc.getCondition().containsKey(IDField)) {			
			psGet=Queries.GET_FARM_BY_ID.get(conn, desc);
		}

		ResultSet rs=psGet.executeQuery();
		if(!rs.next())
			throw new BeanNotFound("Farm not found. Condition was "+desc);

		return Queries.rowToFarm(rs);
		}finally {
			conn.close();
		}
	}




}
