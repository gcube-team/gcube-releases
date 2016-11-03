package org.gcube.dataanalysis.ecoengine.datatypes;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;

public class DatabaseType extends StatisticalType{

	public DatabaseType(DatabaseParameters databaseParameter, String name, String description, String defaultValue, boolean optional) {
		super(name, description, defaultValue, optional);
		this.databaseParameter=databaseParameter;
	}

	public DatabaseType(DatabaseParameters databaseParameter, String name, String description, String defaultValue) {
		super(name, description, defaultValue);
		this.databaseParameter=databaseParameter;
	}

	public DatabaseType(DatabaseParameters databaseParameter, String name, String description) {
		super(name, description);
		this.databaseParameter=databaseParameter;
	}

	protected DatabaseParameters databaseParameter;

	public DatabaseParameters getDatabaseParameter() {
		return databaseParameter;
	}

	public void setDatabaseParameter(DatabaseParameters databaseParameters) {
		this.databaseParameter = databaseParameters;
	}
	
	public static void addDefaultDBPars(List<StatisticalType> parameters){
		DatabaseType p1 = new DatabaseType(DatabaseParameters.DATABASEUSERNAME, "DatabaseUserName", "db user name");
		DatabaseType p2 = new DatabaseType(DatabaseParameters.DATABASEPASSWORD, "DatabasePassword", "db password");
		DatabaseType p3 = new DatabaseType(DatabaseParameters.DATABASEDRIVER, "DatabaseDriver", "db driver");
		DatabaseType p4 = new DatabaseType(DatabaseParameters.DATABASEURL, "DatabaseURL", "db url");
		DatabaseType p5 = new DatabaseType(DatabaseParameters.DATABASEDIALECT, "DatabaseDialect", "db dialect");
		DatabaseType p6 = new DatabaseType(DatabaseParameters.DATABASETABLESPACE, "DatabaseTableSpace", "db tablespace");

		parameters.add(p1);
		parameters.add(p2);
		parameters.add(p3);
		parameters.add(p4);
		parameters.add(p5);
		parameters.add(p6);
	}
	
}
