package org.gcube.dataanalysis.averageannual;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.hibernate.SessionFactory;

public class AverageAnnualPrecipitation extends StandardLocalExternalAlgorithm {

	// Class Attributes
	String outputtablename;
	String outputtable;

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "This is a simple algorithm that returns the average annual of precipitation. The input is a general tabular resource with two columns (date and precipitation).";
	}

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("Initialization");
	}

	@Override
	protected void process() throws Exception {
		// Recovering data
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		SessionFactory dbconnection = DatabaseUtils.initDBSession(config);
		try {
			String tablename = getInputParameter("PrecTable");
			String columnnames = getInputParameter("PrecColumns");
			outputtablename = getInputParameter("OutputTableName");
			outputtable = getInputParameter("OutputTable");
			String[] columnlist = columnnames.split(AlgorithmConfiguration.getListSeparator());
			List<Object> dataList = DatabaseFactory.executeSQLQuery("select " + columnlist[0] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			List<Object> precList = DatabaseFactory.executeSQLQuery("select " + columnlist[1] + " from " + tablename + " order by " + columnlist[0] + " asc", dbconnection);
			// Business Logic
			AnalysisLogger.getLogger().info("Creating output table [" + "create table " + outputtable + " (year integer, value real)]");
			DatabaseFactory.executeSQLUpdate("create table " + outputtable + " (year integer, value real)", dbconnection);
			List<Float> avaregePrecList = new ArrayList<Float>();
			Float averageValue = 0F;
			for (int i = 0; i < dataList.size(); i++) { // for each line
				if (i == 0) { // first iteration
					averageValue += Float.parseFloat( ""+precList.get(i));
					if (dataList.size() == 1) { // if first iteration is also
												// the last
						Date currentDate = (Date) dataList.get(i);
						Calendar currentDateCal = Calendar.getInstance();
						currentDateCal.setTime(currentDate);
						int yearCurrentDate = currentDateCal.get(Calendar.YEAR);
						AnalysisLogger.getLogger().info("Inserting into table " + "insert into " + outputtable + " (year,value) values (" + yearCurrentDate + "," + averageValue + ")");
						DatabaseFactory.executeSQLUpdate("insert into " + outputtable + " (year,value) values (" + yearCurrentDate + "," + averageValue + ")", dbconnection);
						averageValue = 0F;
					}
				}
				if (i > 0) { // other iterations
					Date currentDate = (Date) dataList.get(i);
					Date lastDate = (Date) dataList.get(i - 1);
					Calendar currentDateCal = Calendar.getInstance();
					currentDateCal.setTime(currentDate);
					int yearCurrentDate = currentDateCal.get(Calendar.YEAR);
					Calendar lastDateCal = Calendar.getInstance();
					lastDateCal.setTime(lastDate);
					int yearLastDate = lastDateCal.get(Calendar.YEAR);
					if (yearCurrentDate > yearLastDate) {
						// inserting average annual
						AnalysisLogger.getLogger().info("Inserting into table " + "insert into " + outputtable + " (year,value) values (" + yearLastDate + "," + averageValue + ")");
						DatabaseFactory.executeSQLUpdate("insert into " + outputtable + " (year,value) values (" + yearLastDate + "," + averageValue + ")", dbconnection);
						averageValue = 0F;
						averageValue = Float.parseFloat( ""+precList.get(i));
					} else {
						averageValue += Float.parseFloat( ""+precList.get(i));
					}
				}
				if (i != 0 && i == dataList.size() - 1) { // last iteration
					Date currentDate = (Date) dataList.get(i);
					Calendar currentDateCal = Calendar.getInstance();
					currentDateCal.setTime(currentDate);
					int yearCurrentDate = currentDateCal.get(Calendar.YEAR);
					AnalysisLogger.getLogger().info("Inserting into table " + "insert into " + outputtable + " (year,value) values (" + yearCurrentDate + "," + averageValue + ")");
					DatabaseFactory.executeSQLUpdate("insert into " + outputtable + " (year,value) values (" + yearCurrentDate + "," + averageValue + ")", dbconnection);
					averageValue = 0F;
				}
			}
		} catch (Exception e) {
			AnalysisLogger.getLogger().error(e.getMessage());
			throw e;
		} finally {
			DatabaseUtils.closeDBConnection(dbconnection);
		}
	}

	@Override
	protected void setInputParameters() {
		// First parameter: Internal tabular resource
		List<TableTemplates> templates = new ArrayList<TableTemplates>();
		templates.add(TableTemplates.GENERIC);
		InputTable tinput = new InputTable(templates, "PrecTable", "Precipitation tabular resource");
		ColumnTypesList columns = new ColumnTypesList("PrecTable", "PrecColumns", "Selected columns for date and precipitation", false);
		inputs.add(tinput);
		inputs.add(columns);
		// Second parameter: Output table
		ServiceType randomstring = new ServiceType(ServiceParameters.RANDOMSTRING, "OutputTable", "", "prec");
		inputs.add(randomstring);
		DatabaseType.addDefaultDBPars(inputs);
		// Third parameter: Output table name
		addStringInput("OutputTableName", "The name of the output table", "prec_");
	}

	@Override
	public StatisticalType getOutput() {
		List<TableTemplates> outtemplate = new ArrayList<TableTemplates>();
		outtemplate.add(TableTemplates.GENERIC);
		OutputTable out = new OutputTable(outtemplate, outputtablename, outputtable, "The output table containing all the matches");
		return out;
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shutdown");
	}

}
