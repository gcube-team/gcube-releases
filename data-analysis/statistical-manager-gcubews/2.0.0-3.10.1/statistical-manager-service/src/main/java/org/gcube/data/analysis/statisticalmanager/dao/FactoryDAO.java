package org.gcube.data.analysis.statisticalmanager.dao;

public abstract class FactoryDAO {
	
	public abstract SMOperationDAO getOperationDAO();
	
	
	public static FactoryDAO getFactory() {
		return new FactoryHibernateDAO();
	}

}
