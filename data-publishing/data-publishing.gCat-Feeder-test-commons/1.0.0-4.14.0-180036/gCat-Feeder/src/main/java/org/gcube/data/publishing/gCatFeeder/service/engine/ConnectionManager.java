package org.gcube.data.publishing.gCatFeeder.service.engine;

import java.sql.Connection;
import java.sql.SQLException;

import org.gcube.data.publishing.gCatFeeder.service.model.fault.InternalError;

public interface ConnectionManager {

	
	public Connection getConnection() throws SQLException, InternalError;
	
}
