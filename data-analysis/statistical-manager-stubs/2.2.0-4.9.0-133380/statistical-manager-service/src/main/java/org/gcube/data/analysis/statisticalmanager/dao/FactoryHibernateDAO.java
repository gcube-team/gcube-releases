package org.gcube.data.analysis.statisticalmanager.dao;

import org.gcube.data.analysis.statisticalmanager.dao.impl.SMOperationDAOImpl;

public class FactoryHibernateDAO extends FactoryDAO {
	
//	private static final String DRIVER_CLASS = "hibernate.connection.driver_class";
//	private static final String URL = "hibernate.connection.url";
//	private static final String USERNAME = "hibernate.connection.username";
//	private static final String PASSWORD = "hibernate.connection.password";

	@Override
	public SMOperationDAO getOperationDAO() {
		return new SMOperationDAOImpl();
	}

}
