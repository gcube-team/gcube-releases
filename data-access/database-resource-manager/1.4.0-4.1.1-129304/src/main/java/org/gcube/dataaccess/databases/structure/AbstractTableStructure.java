package org.gcube.dataaccess.databases.structure;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.hibernate.SessionFactory;

/** Class that allows to create a table for a database */
public abstract class AbstractTableStructure {

	protected List<String> ColumnNames;
	protected List<String> TypesList;
	protected List<Integer> TypesLengths;
	protected List<String> DefaultValues;
	protected List<String> CompleteTypes;
	protected List<String> ColumnKeys;
	protected List<String> UniqueKeys;
	protected List<String> ForeignKeys;
	protected List<String> Indexes;
	protected List<Boolean> IsNullables;
	protected String databaseName;
	protected String charset;
	protected String tableName;

	
	// create table query
	protected static String createTableQueryElement = "\"%1$s\" %2$s %3$s %4$s";
	protected static String defaultTableQueryElement = "DEFAULT %1$s";
	protected static String createTableQuery = "CREATE TABLE %1$s ( %2$s );";

	protected static String primaryKeyStatement = "PRIMARY KEY";
	protected static String uniqueKeyStatement = "UNIQUE";
	protected static String foreignKeyStatement = "FOREIGN KEY";
	
	
    //Abstracts methods
	protected abstract void buildStructure(SessionFactory dbSession)
			throws Exception;

	protected abstract String getQueryForTableStructure(SessionFactory dbSession)
			throws Exception;

	protected abstract String getQueryForIndexes(SessionFactory dbSession)
			throws Exception;

	public AbstractTableStructure(String Databasename, String TableName,
			SessionFactory dbSession, boolean buildStructure) throws Exception {

		try {
			ColumnNames = new ArrayList<String>();
			TypesList = new ArrayList<String>();
			TypesLengths = new ArrayList<Integer>();
			DefaultValues = new ArrayList<String>();
			CompleteTypes = new ArrayList<String>();
			ColumnKeys = new ArrayList<String>();
			UniqueKeys = new ArrayList<String>();
			ForeignKeys = new ArrayList<String>();
			Indexes = new ArrayList<String>();
			IsNullables = new ArrayList<Boolean>();
			tableName= TableName;
			databaseName = Databasename;


			if (buildStructure)
				buildStructure(dbSession);

		} catch (Exception e) {
			
			throw e;

//			String error = e.getCause().toString();
//
//			if ((error.contains("Table")) && (error.contains("doesn't exist"))) {
//
//				System.out.println("Table " + TableName + " doesn't exist");
//
//			}
		}

	}

	public AbstractTableStructure(String Databasename, String TableName,
			SessionFactory dbSession) throws Exception {

		this(Databasename, TableName, dbSession, true);
	}

	// builds a table by merging information in data structure
	public String buildUpCreateTable() {
		int numOfElements = ColumnNames.size();
		StringBuffer elementsBuffer = new StringBuffer();

		// build up create statement elements
		for (int i = 0; i < numOfElements; i++) {
			String nullable = "";
			if (!IsNullables.get(i).booleanValue())
				nullable = "NOT NULL";

			String defaultvalue = "";
			
			if (DefaultValues.size()!=0){

			if ((DefaultValues.get(i) != null)
					&& (DefaultValues.get(i).trim().length() > 0)
					&& (nullable.equals("NOT NULL"))) {
				defaultvalue = DefaultValues.get(i);
				
				defaultvalue = String.format(defaultTableQueryElement,
						defaultvalue);
			}
			}
			

			String createStatementElement = String.format(
					createTableQueryElement, ColumnNames.get(i),
					TypesList.get(i), nullable, defaultvalue);

			elementsBuffer.append(createStatementElement);

			if (i < numOfElements - 1)
				elementsBuffer.append(",");
		}

		// build up primary keys statements
		elementsBuffer
				.append(buildUPConstraint(primaryKeyStatement, ColumnKeys));
		elementsBuffer
				.append(buildUPConstraint(uniqueKeyStatement, UniqueKeys));
		elementsBuffer.append(buildUPConstraint(foreignKeyStatement,
				ForeignKeys));

		// build up create statement
		String createStatement = String.format(createTableQuery, tableName,
				elementsBuffer.toString(), charset);

		AnalysisLogger.getLogger().debug(
				"AbstractTableStructure->Create Table Query: "
						+ createStatement);

		return createStatement;
	}

	private String buildUPConstraint(String statement, List<String> Keys) {

		// build up primary keys statements
		StringBuffer elementsBuffer = new StringBuffer();
		int numKeys = Keys.size();
		if (numKeys > 0) {
			elementsBuffer.append(", " + statement + "(");
			for (int i = 0; i < numKeys; i++) {
				String columnKey = Keys.get(i);
				if (columnKey != null) {
					elementsBuffer.append("\"" + columnKey + "\"");

					if (i < numKeys - 1)
						elementsBuffer.append(",");
				}

			}
			elementsBuffer.append(")");
		}

		return elementsBuffer.toString();

	}

}
