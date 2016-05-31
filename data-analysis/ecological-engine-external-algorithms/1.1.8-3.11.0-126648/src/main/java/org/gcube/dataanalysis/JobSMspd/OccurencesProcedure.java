package org.gcube.dataanalysis.JobSMspd;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.util.Capabilities;
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

public class OccurencesProcedure extends StandardLocalExternalAlgorithm {

	LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
	static String databaseParameterName = "FishBase";
	static String userParameterName = "user";
	static String passwordParameterName = "password";
	static String urlParameterName = "FishBase";
	SessionFactory dbconnection = null;
	// public static boolean call=false;
	String tablename;
	File outputResult;
	String columnnames;
	List<Object> speciesList = null;
	protected String fileName;
	String outputtablename;
	String outputErrortablename;
	String outputtable;
	HashMap<String, String> dpHash = new HashMap<String, String>();
	HashMap<String, String> dpUHash = new HashMap<String, String>();
	HashMap<String, String> dpEHash = new HashMap<String, String>();
	String tableError;
	private static DataPenum dp = null;
	private static ExtentionDPEnum dpE = null;
	private static UnfoldDPEnum dpU = null;
	private String dataProvider = "Data Provider :";
	private String chosendataProvider = new String();
	private String dataProviderExtention = "Data Provider (Expand Option):";
	private String chosendataProviderExtention = new String();
	// private String chosendataProviderUnfold="Data Provider Unfold:";
	private String dataProviderUnfold = "Data Provider (Unfold Option):";
	private String chosendataProviderUnfold = new String();
	private Lock lock = new Lock();

	@Override
	public String getDescription() {
		return "An Algorithm that retrieves the occurrences from a data provided based on the given search options";
	}

	@Override
	public void init() throws Exception {
		AnalysisLogger.getLogger().debug("SMFaoAlg");
		AnalysisLogger.getLogger().debug("Init scope :"+ScopeProvider.instance.get());
		

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
		fileName = super.config.getPersistencePath() + "results.csv";
		outputResult= new File(fileName);

	}

	@Override
	protected void process() throws Exception {

		try {
			String scope = ScopeProvider.instance.get();
			AnalysisLogger.getLogger().debug("process scope :"+scope);
			AnalysisLogger.getLogger().debug(
					"-------Procedure config scope"
							+ config.getGcubeScope());
			
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
			ThreadExtractionOccFromSPD t1 = new ThreadExtractionOccFromSPD(
					chunk1, chosendataProvider, chosendataProviderExtention,
					chosendataProviderUnfold, scope);
			ThreadExtractionOccFromSPD t2 = new ThreadExtractionOccFromSPD(
					chunk2, chosendataProvider, chosendataProviderExtention,
					chosendataProviderUnfold, scope);
			ThreadExtractionOccFromSPD t3 = new ThreadExtractionOccFromSPD(
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
			
			
			File []files= new File[3];
			if(t1.getInfo()!= null)
				files[0]=t1.getInfo();
			if(t1.getInfo()!= null)
				files[1]=t2.getInfo();
			if(t1.getInfo()!= null)
				files[2]=t3.getInfo();
			mergeFiles(files, outputResult);

			insertInTheTableErrors(t1.getErrors());
			insertInTheTableErrors(t2.getErrors());
			insertInTheTableErrors(t3.getErrors());

		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug(e.toString());

			throw e;
		} finally {

			DatabaseUtils.closeDBConnection(dbconnection);
			
		}

	}

	private void createTables() throws Exception {

		DatabaseFactory.executeSQLUpdate("create table " + tableError
				+ " (error character varying)", dbconnection);
	}

//	private void insertInTheTable(ArrayList<ArrayList<String>> arrays)
//			throws Exception {
//
//		for (ArrayList<String> array : arrays) {
//			// String query = "insert into " + outputtable + st + " values (";
//			String writeString = new String();
//			int i = 0;
//
//			for (String s : array) {
//				if (i != 0) {
//					writeString = writeString + "; ";
//				}
//				writeString = writeString + " '";
//				if (s != null)
//					s = s.replace("'", "");
//				writeString = writeString + s;
//
//				writeString = writeString + "'";
//				i++;
//
//			}
//			write(writeString);
//			out.newLine();
//
//		}
//
//	}

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

	private void insertEnumValuesr() {
		AnalysisLogger.getLogger().debug(" insertEnumValuesr");
		AnalysisLogger.getLogger().debug(" second version");
		if (dp == null || dpU == null || dpE == null) {
			dp = new DataPenum();
			dpE = new ExtentionDPEnum();
			dpU = new UnfoldDPEnum();
			try {
				setDynamicParameter();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AnalysisLogger.getLogger().debug(" call setDynamicParameter");

		}
		if (dp != null) {
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
		}

	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("Shut down ");
	}

	private void setDynamicParameter() throws InterruptedException {
		AnalysisLogger.getLogger().debug("Inside setDynamicParameter");
		// ScopeProvider.instance.set("/gcube/devsec");
		AnalysisLogger.getLogger().debug(
				"-------Procedure setParameter in the scope"
						+ ScopeProvider.instance.get().toString());
		
		Manager manager = null;

		manager = manager().build();
		AnalysisLogger.getLogger().debug("build Manager");
		AnalysisLogger.getLogger().debug("before dei plugin");
		List<PluginDescription> plugin = null;
		try {
			plugin = manager.getPluginsDescription();
		} catch (Exception e) {
			String eTracMes = e.getMessage();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			sw.toString();
			AnalysisLogger.getLogger().debug(eTracMes);
			AnalysisLogger.getLogger().debug(sw.toString());
			e.printStackTrace();
		} finally {
			lock.lock();
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
					if (capabilityName.equals("Unfold"))
						dpU.addEnum(UnfoldDPEnumType.class, pluginDescription
								.getName().toString());

					if (capabilityName.equals("Expansion"))

						dpE.addEnum(ExtentionDPType.class, pluginDescription
								.getName().toString());

					if (capabilityName.equals("Occurrence"))

						dp.addEnum(DataProvidersType.class, pluginDescription
								.getName().toString());

				}

			}

		} else
			AnalysisLogger.getLogger().debug("*****PluginDescription is null");

	}

	@Override
	protected void setInputParameters() {
		try {
			AnalysisLogger.getLogger().debug("inside setInputParameters2 ");

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
					ServiceParameters.RANDOMSTRING, "OutputTable", "", "occ");
			ServiceType randomstringErr = new ServiceType(
					ServiceParameters.RANDOMSTRING, "ErrorTable", "", "err");

			insertEnumValuesr();

			addEnumerateInput(DataProvidersType.values(), dataProvider,
					"Choose Data Providere", "ALL");
			AnalysisLogger.getLogger().debug("After DataProvidersType");
			addEnumerateInput(ExtentionDPType.values(), dataProviderExtention,
					"Choose Expand Option Data Providere", "ALL");
			AnalysisLogger.getLogger().debug("After ExtentionDPType");
			addEnumerateInput(UnfoldDPEnumType.values(), dataProviderUnfold,
					"Choose UnfoldRR Option Data Providere", "ALL");
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

		OutputTable outErr = new OutputTable(outtemplate, outputErrortablename,
				tableError, "The output table containing all the matches");
		PrimitiveType f = new PrimitiveType(File.class.getName(), outputResult, PrimitiveTypes.FILE, "OccFile", "OccFile");
		map.put("Output", f);
		map.put("Errors", outErr);
		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), map,
				PrimitiveTypes.MAP, "ResultsMap", "Results Map");

		return output;

	}

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

	public static void mergeFiles(File[] files, File mergedFile) {
		AnalysisLogger.getLogger().debug("Inside mergeFiles");
		 if (mergedFile.exists()){
			 mergedFile.delete();
          }   
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(mergedFile, true);
			out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String title = "institutionCode, " + "collectionCode, "
				+ "catalogueNumber, " + "dataSet, " + "dataProvider, "
				+ "dataSource, " + "scientificNameAuthorship,"
				+ "identifiedBy," + "credits," + "recordedBy, " + "eventDate, "
				+ "modified, " + "scientificName, " + "kingdom, " + "family, "
				+ "locality, " + "country, " + "citation, "
				+ "decimalLatitude, " + "decimalLongitude, "
				+ "coordinateUncertaintyInMeters, " + "maxDepth, "
				+ "minDepth, " + "basisOfRecord";
		try {
			out.write(title);
			out.newLine();
			for (File f : files) {
				System.out.println("merging: " + f.getName());
				FileInputStream fis;

				fis = new FileInputStream(f);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						fis));

				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine);
					out.newLine();
				}

				in.close();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
