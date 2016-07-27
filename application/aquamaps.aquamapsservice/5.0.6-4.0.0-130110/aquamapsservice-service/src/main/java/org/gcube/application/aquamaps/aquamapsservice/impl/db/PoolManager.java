package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.ConfigurationManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.DBDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoolManager {

	final static Logger logger= LoggerFactory.getLogger(PoolManager.class);

	private static GenericObjectPool internalDBconnectionPool; 
	private static ConnectionFactory internalDBconnectionFactory;
	private static PoolableConnectionFactory internalDBpoolableConnectionFactory;
	private static PoolingDriver internalDBdriver;

	private static final String internalDBPoolName="mySqlPool";
	private static final String postGISPoolName="postGISPool";
	//TODO load from properties 



	private static GenericObjectPool postGISconnectionPool; 
	private static ConnectionFactory postGISconnectionFactory;
	private static PoolableConnectionFactory postGISpoolableConnectionFactory;
	private static PoolingDriver postGISdriver;

	private static String validationQUERY="Select 1";

	private static String internalDBconnectionString=null; 


	static{
		//MYSQL

		try{			
			DBDescriptor internalDBDescriptor=ConfigurationManager.getVODescriptor().getInternalDB();
		try {
			switch(internalDBDescriptor.getType()){
			case mysql:	Class.forName("com.mysql.jdbc.Driver");
						internalDBconnectionString="jdbc:mysql:";
						break;
			case postgres:Class.forName("org.postgresql.Driver");
						internalDBconnectionString="jdbc:postgresql:";
			break;
			default : throw new ClassNotFoundException("Not Valid internal DB Type "+internalDBDescriptor.getType());
			}

			internalDBconnectionString+=internalDBDescriptor.getEntryPoint();
		} catch (ClassNotFoundException e) {
			logger.error("Unable to instantiate driver", e);
			throw e;
		}
		internalDBconnectionPool = new GenericObjectPool(null);
		internalDBconnectionPool.setMaxActive(internalDBDescriptor.getMaxConnection());
//		internalDBconnectionPool.setMaxIdle(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.INTERNAL_DB_MAX_IDLE));
		internalDBconnectionPool.setTestOnBorrow(true);
		internalDBconnectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		internalDBconnectionFactory = new DriverManagerConnectionFactory(internalDBconnectionString,internalDBDescriptor.getUser(), 
				internalDBDescriptor.getPassword());

		internalDBpoolableConnectionFactory = new PoolableConnectionFactory(internalDBconnectionFactory,internalDBconnectionPool,
				new StackKeyedObjectPoolFactory(),validationQUERY,false,true);
		internalDBdriver = new PoolingDriver();
		internalDBdriver.registerPool(internalDBPoolName,internalDBconnectionPool);

		//POSTGIS

		DBDescriptor postgisDBDescriptor=ConfigurationManager.getVODescriptor().getGeoDb();
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			logger.error("Unable to instantiate driver", e);
			throw e;
		}
		postGISconnectionPool = new GenericObjectPool(null);
		postGISconnectionPool.setMaxActive(postgisDBDescriptor.getMaxConnection());
//		postGISconnectionPool.setMaxIdle(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.GEOSERVER_DB_MAX_IDLE));
		postGISconnectionPool.setTestOnBorrow(true);
		postGISconnectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		postGISconnectionFactory = new DriverManagerConnectionFactory("jdbc:postgresql:"+postgisDBDescriptor.getEntryPoint(),
				postgisDBDescriptor.getUser(),
				postgisDBDescriptor.getPassword());

		postGISpoolableConnectionFactory = new PoolableConnectionFactory(postGISconnectionFactory,postGISconnectionPool,
				new StackKeyedObjectPoolFactory(),validationQUERY,false,true);
		postGISdriver = new PoolingDriver();
		postGISdriver.registerPool(postGISPoolName,postGISconnectionPool);
		
	}catch(Exception e){
		logger.error("",e);
	}
}


	public static Connection getInternalDBConnection()throws Exception{
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+internalDBPoolName);
	}
	public static Connection getPostGisDBConnection()throws Exception{
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+postGISPoolName);
	}


	public static String getInternalConnectionString(){return internalDBconnectionString;}

}
