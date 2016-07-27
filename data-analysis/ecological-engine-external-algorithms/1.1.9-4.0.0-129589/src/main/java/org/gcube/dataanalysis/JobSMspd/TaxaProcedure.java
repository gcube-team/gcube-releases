package org.gcube.dataanalysis.JobSMspd;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.dataanalysis.JobSMspd.TaxaProcedure.DataPenum;
import org.gcube.dataanalysis.JobSMspd.TaxaProcedure.DataProvidersType;
import org.gcube.dataanalysis.JobSMspd.TaxaProcedure.ExtentionDPEnum;
import org.gcube.dataanalysis.JobSMspd.TaxaProcedure.ExtentionDPType;
import org.gcube.dataanalysis.JobSMspd.TaxaProcedure.UnfoldDPEnum;
import org.gcube.dataanalysis.JobSMspd.TaxaProcedure.UnfoldDPEnumType;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.ecoengine.utils.DynamicEnum;
import org.hibernate.SessionFactory;

public class TaxaProcedure extends StandardLocalExternalAlgorithm {

	LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	static String databaseParameterName = "FishBase";
	static String userParameterName = "user";
	static String passwordParameterName = "password";
	static String urlParameterName = "FishBase";
	SessionFactory dbconnection = null;
	// public static boolean call=false;
	String tablename;
	String columnnames;
	List<Object> speciesList = null;
	// protected String fileName;
	// BufferedWriter out;
	String outputtablename;
	String outputErrortablename;
	String outputtable;
	HashMap<String, String> dpHash = new HashMap<String, String>();
	HashMap<String, String> dpUHash = new HashMap<String, String>();
	HashMap<String, String> dpEHash = new HashMap<String, String>();
	String tableError;
	private  static DataPenum dp = null;
	private  static ExtentionDPEnum dpE = null;
	private  static UnfoldDPEnum dpU = null;
	private String dataProvider = "Data Provider :";
	private String chosendataProvider = new String();
	private String dataProviderExtention = "Data Provider (Expand Option):";
	private String chosendataProviderExtention = new String();
	// private String chosendataProviderUnfold="Data Provider Unfold:";
	private String dataProviderUnfold = "Data Provider (Unfold Option):";
	private String chosendataProviderUnfold = new String();
	File file;
	 private Lock lock = new Lock();


	@Override
	public String getDescription() {
		return " An Algorithm that retrieves the taxon from a data provided based on the given search options";
	}

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("SMFaoAlg");

	}

	public void fulfilParameters() throws IOException {
		config.setParam("DatabaseDriver", "org.postgresql.Driver");
		dbconnection = DatabaseUtils.initDBSession(config);
		tablename = getInputParameter("SpeciesTable");
		columnnames = getInputParameter("SpeciesColumns");

		outputtablename = getInputParameter("OutputTableName");
		outputtable = getInputParameter("OutputTable");
		tableError = getInputParameter("ErrorTable");
		chosendataProviderUnfold = getInputParameter(dataProviderUnfold);
		chosendataProviderExtention = getInputParameter(dataProviderExtention);

		chosendataProvider = getInputParameter(dataProvider);
		outputErrortablename = getInputParameter("ErrorTableName");

		String[] columnlist = columnnames.split(AlgorithmConfiguration
				.getListSeparator());
		speciesList = DatabaseFactory.executeSQLQuery("select " + columnlist[0]
				+ " from " + tablename, dbconnection);

	}

	@Override
	protected void process() throws Exception {

		try {
			fulfilParameters();
			createTables();

			int lenght = (int) (speciesList.size() / 3);
			ArrayList<String> chunk1 = new ArrayList<String>();
			ArrayList<String> chunk2 = new ArrayList<String>();
			ArrayList<String> chunk3 = new ArrayList<String>();
			for (int i = 0; i < speciesList.size(); i++) {
				if (i < lenght)
					chunk1.add((String) speciesList.get(i));
				if (i >= lenght && i <= 2 * lenght)
					chunk2.add((String) speciesList.get(i));

				if (i > 2 * lenght)

					chunk3.add((String) speciesList.get(i));
			}
			String scope = ScopeProvider.instance.get();
			ThreadExtractionTaxaFromSPD t1 = new ThreadExtractionTaxaFromSPD(
					chunk1, chosendataProvider, chosendataProviderExtention,
					chosendataProviderUnfold, scope);
			ThreadExtractionTaxaFromSPD t2 = new ThreadExtractionTaxaFromSPD(
					chunk2, chosendataProvider, chosendataProviderExtention,
					chosendataProviderUnfold, scope);
			ThreadExtractionTaxaFromSPD t3 = new ThreadExtractionTaxaFromSPD(
					chunk3, chosendataProvider, chosendataProviderExtention,
					chosendataProviderUnfold, scope);
			Thread th1 = new Thread(t1);
			th1.start();
			Thread th2 = new Thread(t2);
			th2.start();
			Thread th3 = new Thread(t3);
			th3.start();
			th1.join();
			th2.join();
			th3.join();
			AnalysisLogger.getLogger().debug("Thread finished");
			Vector<TaxonomyItem> taxaList = t1.getTaxaList();
			taxaList.addAll(t2.getTaxaList());
			taxaList.addAll(t3.getTaxaList());
			MapDwCA fileMaker = new MapDwCA(super.config.getPersistencePath());
			Iterator<TaxonomyItem> it = taxaList.iterator();
			file = fileMaker.createDwCA(it);
			AnalysisLogger.getLogger().debug("DWA Created");
			insertInTheTableErrors(t1.getErrors());
			insertInTheTableErrors(t2.getErrors());
			insertInTheTableErrors(t3.getErrors());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DatabaseUtils.closeDBConnection(dbconnection);
		}

	}

	private void createTables() throws Exception {

		DatabaseFactory.executeSQLUpdate("create table " + tableError
				+ " (error character varying)", dbconnection);
	}

	private void insertInTheTable(ArrayList<ArrayList<String>> arrays)
			throws IOException {

		for (ArrayList<String> array : arrays) {
			// String query = "insert into " + outputtable + st + " values (";
			String writeString = new String();
			int i = 0;

			for (String s : array) {
				if (i != 0) {
					writeString = writeString + "; ";
				}
				// query = query + ", ";}
				writeString = writeString + " '";
				// query = query + " '";
				if (s != null)
					s = s.replace("'", "");
				writeString = writeString + s;
				// query = query + s;
				// query = query + "'";
				writeString = writeString + "'";
				i++;

			}

		}

	}

	private void insertInTheTableErrors(ArrayList<String> arrays)
			throws Exception {
		if (arrays != null) {
			String st = " (error)";
			for (String er : arrays) {
				String query = "insert into " + tableError + st + " values ('"
						+ er + "')";
				AnalysisLogger.getLogger().debug("query error : " + query);
				DatabaseFactory.executeSQLUpdate(query, dbconnection);
			}
		}

	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shut down ");
	}

	private  void insertEnumValuesr() {
		AnalysisLogger.getLogger().debug(" insertEnumValuesr");

//		if (dp == null || dpU == null || dpE == null) {
//			dp = new DataPenum();
//			dpE = new ExtentionDPEnum();
//			dpU = new UnfoldDPEnum();
//			AnalysisLogger.getLogger().debug(" call setDynamicParameter");
//
//			setDynamicParameter();
//		
//		}
//		if (dp != null) {
			try {
				if (UnfoldDPEnumType.values().length == 0) {
					AnalysisLogger.getLogger().debug("Only one provider.");
					dp = new DataPenum();
					dpE = new ExtentionDPEnum();
					dpU = new UnfoldDPEnum();
					setDynamicParameter();
				}
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				AnalysisLogger.getLogger().debug(sw.toString());
			}
//		}

	}

	private  void setDynamicParameter() {
		AnalysisLogger.getLogger().debug("Inside setDynamicParameter");
		AnalysisLogger.getLogger().debug(
				"Procedure called in the scope"
						+ ScopeProvider.instance.get().toString());
		Manager manager = null;

		manager = manager().build();
		AnalysisLogger.getLogger().debug("build manager");
		AnalysisLogger.getLogger().debug("before plugin");
		List<PluginDescription> plugin = null;
		try {
			plugin = manager.getPluginsDescription();
		} catch (Exception e) {
			String eTracMes = e.getMessage();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			AnalysisLogger.getLogger().debug(eTracMes);
			AnalysisLogger.getLogger().debug(sw.toString());
		} finally {
			try {
				lock.lock();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dp.addEnum(DataProvidersType.class, "ALL");
			dpE.addEnum(ExtentionDPType.class, "ALL");
			dpU.addEnum(UnfoldDPEnumType.class, "NO OPTION");
			dpE.addEnum(ExtentionDPType.class, "NO OPTION");
		lock.unlock();
		}
		AnalysisLogger.getLogger().debug("get  plugin");

		if (plugin != null) {

			AnalysisLogger.getLogger().debug(
					"*****PluginDescription is NOT null - length: "
							+ plugin.size());

			for (int i = 0; i < plugin.size(); i++) {

				PluginDescription pluginDescription = plugin.get(i);

				AnalysisLogger.getLogger().debug(
						"For   plugin ***" + pluginDescription.getName());
				Map<Capabilities, List<Conditions>> pluginCapabilities = pluginDescription
						.getSupportedCapabilities();
				AnalysisLogger.getLogger().debug("created maps");
				AnalysisLogger.getLogger().debug(
						" map size" + pluginCapabilities.size());

				for (Entry<Capabilities, List<Conditions>> pluginCapability : pluginCapabilities
						.entrySet()) {

					Capabilities capability = pluginCapability.getKey();
					String capabilityName = capability.name().toString();
					AnalysisLogger.getLogger().debug(capabilityName);
					if (capabilityName.equals("Unfold")) {

						dpU.addEnum(UnfoldDPEnumType.class, pluginDescription
								.getName().toString());
					}

					if (capabilityName.equals("Expansion")) {

						dpE.addEnum(ExtentionDPType.class, pluginDescription
								.getName().toString());

					}
					if (capabilityName.equals("Classification")) {
						dp.addEnum(DataProvidersType.class, pluginDescription
								.getName().toString());
					}
				}
			}

		} else
			AnalysisLogger.getLogger().debug("*****PluginDescription is null");

	}

	@Override
	protected void setInputParameters() {
		try {
			AnalysisLogger.getLogger().debug("inside setInputParameters ");

			addRemoteDatabaseInput(databaseParameterName, urlParameterName,
					userParameterName, passwordParameterName, "driver",
					"dialect");

			List<TableTemplates> templates = new ArrayList<TableTemplates>();
			templates.add(TableTemplates.GENERIC);
			InputTable tinput = new InputTable(templates, "SpeciesTable",
					"The table containing the species information");
			ColumnTypesList columns = new ColumnTypesList("SpeciesTable",
					"SpeciesColumns", "Select the columns for  species name",
					false);
			addStringInput("OutputTableName", "The name of the output table",
					"occ_");
			addStringInput("ErrorTableName", "The name of the output table",
					"err_");
			ServiceType randomstring = new ServiceType(
					ServiceParameters.RANDOMSTRING, "OutputTable", "", "tax_");
			ServiceType randomstringErr = new ServiceType(
					ServiceParameters.RANDOMSTRING, "ErrorTable", "", "err");

			AnalysisLogger.getLogger().debug("before setDynamicParameter() ");
			// if(!call)
			insertEnumValuesr();

			addEnumerateInput(DataProvidersType.values(), dataProvider,
					"Choose Data Providere", "ALL");
			AnalysisLogger.getLogger().debug("After DataProvidersType");
			addEnumerateInput(ExtentionDPType.values(), dataProviderExtention,
					"Choose Expand Option Data Providere", "ALL");
			AnalysisLogger.getLogger().debug("After ExtentionDPType");
			addEnumerateInput(UnfoldDPEnumType.values(), dataProviderUnfold,
					"Choose Unfold Option Data Providere", "ALL");
			AnalysisLogger.getLogger().debug("After UnfoldDPEnumType");

			inputs.add(tinput);
			inputs.add(columns);
			inputs.add(randomstring);
			inputs.add(randomstringErr);
			DatabaseType.addDefaultDBPars(inputs);
		} catch (Throwable e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug(e.toString());
		}
		// call=true;

	}

	@Override
	public StatisticalType getOutput() {

		List<TableTemplates> outtemplate = new ArrayList<TableTemplates>();
		outtemplate.add(TableTemplates.GENERIC);
		List<TableTemplates> outtemplateErr = new ArrayList<TableTemplates>();
		outtemplateErr.add(TableTemplates.GENERIC);

		// OutputTable out = new OutputTable(outtemplate, outputtablename,
		// outputtable, "The output table containing all the matches");
		OutputTable outErr = new OutputTable(outtemplate, outputErrortablename,
				tableError, "The output table containing all the matches");
		PrimitiveType f = new PrimitiveType(File.class.getName(), file,
				PrimitiveTypes.FILE, "OccFile", "OccFile");
		map.put("Output", f);
		// map.put("Output", out);
		map.put("Errors", outErr);
		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), map,
				PrimitiveTypes.MAP, "ResultsMap", "Results Map");

		return output;
		// return out;

	}

	// public void write(String writeSt) {
	// try {
	// out.write(writeSt);
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	enum DataProvidersType {
	}

	class DataPenum extends DynamicEnum {
		public Field[] getFields() {
			Field[] fields = DataProvidersType.class.getDeclaredFields();
			return fields;
		}
	}

	enum ExtentionDPType {
	}

	class ExtentionDPEnum extends DynamicEnum {
		public Field[] getFields() {
			Field[] fields = ExtentionDPType.class.getDeclaredFields();
			return fields;
		}
	}

	enum UnfoldDPEnumType {
	}

	class UnfoldDPEnum extends DynamicEnum {
		public Field[] getFields() {
			Field[] fields = UnfoldDPEnumType.class.getDeclaredFields();
			return fields;
		}
	}
}
