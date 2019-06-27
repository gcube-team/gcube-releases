package org.gcube.dbinterface.h2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.gcube.common.dbinterface.Initializer;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.types.Type.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializerImpl implements Initializer {

	private String dbUrl="";
	private static final Logger logger = LoggerFactory.getLogger(InitializerImpl.class);
	
	public Properties getQueryMappingPropertiesStream() {
		logger.debug("postgres initializer called");
		Properties queriesProperties=new Properties();
		try {
			queriesProperties.load(InitializerImpl.class.getResourceAsStream("/org/gcube/dbinterface/h2/query_mapping.properties"));
		} catch (IOException e) {
			logger.debug("error loading DB properties",e);
		}
		return queriesProperties;
	}

	public void initialize(String username, String passwd, String dbPath) {
		this.dbUrl="jdbc:h2:"+dbPath;
		Properties properties=new Properties();
		try {
			properties.load(InitializerImpl.class.getResourceAsStream("/org/gcube/dbinterface/h2/type_functions.properties"));
		} catch (IOException e) {
			logger.error("error loading the properties");
		}
		for (Types type:Types.values()){
			//logger.debug("value "+ properties.getProperty(type.name()));
			String[] typesNameList=properties.getProperty(type.name()).split(";");
			//logger.debug("value for typeName"+type.name()+" is "+typesNameList[0]);
			type.setType(typesNameList[0]);
			type.setListSqlTypes(new ArrayList<String>());
			for (String singleTypeName: typesNameList )
				type.getListSqlTypes().add(singleTypeName.toLowerCase().trim());
			//type.setSpecificFunction("cast_wrapper_"+type.name().toLowerCase());
		}
	}

	public void postInitialization(DBSession session) {
		Properties properties=new Properties();
		try {
			properties.load(InitializerImpl.class.getResourceAsStream("/org/gcube/dbinterface/h2/type_functions.properties"));
		} catch (IOException e) {
			logger.error("error loading the properties");
		}
		int funcNum=Integer.parseInt(properties.getProperty("FUNCTIONS"));
		for (int i =0; i<funcNum; i++)
			try{
				logger.trace("reading function "+i+" "+properties.getProperty("Function."+i));
				session.executeUpdate(properties.getProperty("Function."+i));
			}catch (Exception e) {logger.warn("error updating function in the DB",e);}
	}

	public String getDriver() {
		return "org.h2.Driver";
	}

	public String getEntireUrl() {
		return this.dbUrl;
	}

	

}
