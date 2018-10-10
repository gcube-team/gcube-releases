package org.gcube.accounting.accounting.summary.access.impl;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionManager {

	
	public Connection getConnection() throws SQLException;
	
}
