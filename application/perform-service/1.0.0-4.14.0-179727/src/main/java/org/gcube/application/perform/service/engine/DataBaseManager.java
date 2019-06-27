package org.gcube.application.perform.service.engine;

import java.sql.Connection;
import java.sql.SQLException;

import org.gcube.application.perform.service.engine.impl.DataBaseManagerImpl;
import org.gcube.application.perform.service.engine.model.InternalException;

public interface DataBaseManager {
	
	
	public static DataBaseManager get() {
		return new DataBaseManagerImpl();
	}
	
	
	public Connection getConnection() throws SQLException, InternalException;
	
	
}
