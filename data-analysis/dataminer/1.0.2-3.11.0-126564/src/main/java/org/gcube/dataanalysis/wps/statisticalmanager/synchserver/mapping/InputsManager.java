package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.TablesList;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure.DatabaseInfo;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure.InfrastructureDialoguer;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils.GML2CSV;
import org.hibernate.SessionFactory;
import org.n52.wps.io.data.GenericFileData;

public class InputsManager {
	LinkedHashMap<String, Object> inputs;
	List<String> generatedTables;
	List<File> generatedFiles;
	HashMap<String, String> inputTableTemplates = new HashMap<String, String>();
	AlgorithmConfiguration config;
	public static String inputsSeparator = "\\|";

	public AlgorithmConfiguration getConfig() {
		return config;
	}

	public InputsManager(LinkedHashMap<String, Object> inputs, AlgorithmConfiguration config) {
		this.inputs = inputs;
		this.config = config;
		generatedTables = new ArrayList<String>();
		generatedFiles = new ArrayList<File>();
	}

	public List<String> getGeneratedTables() {
		return generatedTables;
	}

	public List<File> getGeneratedInputFiles() {
		return generatedFiles;
	}
	
	public void configSupportDatabaseParameters(DatabaseInfo supportDatabaseInfo) throws Exception {
		// retrieving database parameters
		config.setDatabaseDialect(supportDatabaseInfo.dialect);
		config.setDatabaseDriver(supportDatabaseInfo.driver);
		config.setDatabasePassword(supportDatabaseInfo.password);
		config.setDatabaseURL(supportDatabaseInfo.url);
		config.setDatabaseUserName(supportDatabaseInfo.username);
		// assigning database variables
		config.setParam("DatabaseDriver", supportDatabaseInfo.driver);
		config.setParam("DatabaseUserName", supportDatabaseInfo.username);
		config.setParam("DatabasePassword", supportDatabaseInfo.password);
		config.setParam("DatabaseURL", supportDatabaseInfo.url);
	}

	public void mergeWpsAndEcologicalInputs(DatabaseInfo supportDatabaseInfo) throws Exception {
		// browse input parameters from WPS
		for (String inputName : inputs.keySet()) {
			Object input = inputs.get(inputName);
			AnalysisLogger.getLogger().debug("Managing Input Parameter with Name "+ inputName);
			// case of simple input
			if (input instanceof String) {
				AnalysisLogger.getLogger().debug("Simple Input: "+ input);
				// manage lists
				String inputAlgo = ((String) input).trim().replaceAll(inputsSeparator, AlgorithmConfiguration.listSeparator);
				AnalysisLogger.getLogger().debug("Simple Input Transformed: " + inputAlgo);
				config.setParam(inputName, inputAlgo);
			}
			// case of Complex Input
			else if (input instanceof GenericFileData) {

				AnalysisLogger.getLogger().debug("Complex Input: " + input);
				// retrieve payload
				GenericFileData files = ((GenericFileData) input);
				List<File> localfiles = getLocalFiles(files);
				String inputtables = "";
				int nfiles = localfiles.size();
				for (int i = 0; i < nfiles; i++) {
					File tableFile = localfiles.get(i);
					generatedFiles.add(tableFile);

					String tableName = ("wps_" + ("" + UUID.randomUUID()).replace("_", "")).replace("-", "");
					// create the table
					
					if (inputTableTemplates.get(inputName) != null) {
						AnalysisLogger.getLogger().debug("Creating table: " + tableName);
						createTable(tableName, tableFile, config, supportDatabaseInfo, inputTableTemplates.get(inputName));
						generatedTables.add(tableName);
					}
					//case of non-table input file, e.g. FFANN
					else
						tableName = tableFile.getAbsolutePath();
					if (i > 0)
						inputtables = inputtables + AlgorithmConfiguration.getListSeparator();

					inputtables += tableName;
				}
				// the only possible complex input is a table - check the WPS
				// parsers
				config.setParam(inputName, inputtables);
			}
		}

	}

	public boolean isXML(String fileContent){
		
			if (fileContent.startsWith("&lt;"))
				return true;
			else 
				return false;
	}
	
	public String readOneLine(String filename){
		
			try {
					BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
					String line = null;
					String vud = "";

					while ((line = in.readLine()) != null) {
						if (line.trim().length()>0){
							vud = line.trim();
							break;
						}
					}
					in.close();
					return vud;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
	}
	public List<File> getLocalFiles(GenericFileData files) throws Exception {

		// download input
		List<File> filesList = new ArrayList<File>();
		File f = files.getBaseFile(false);
		AnalysisLogger.getLogger().debug("Retrieving file content as a URL link: " + f.getAbsolutePath());
		//TODO DO NOT READ FILE INTO MEMORY
		// read file content
		String fileLink = readOneLine(f.getAbsolutePath());
		AnalysisLogger.getLogger().debug("File link: " + fileLink.substring(0,Math.min(fileLink.length(),10)) + "...");
		String fileName = "";
		// case of a http link
		if (fileLink.toLowerCase().startsWith("http:") || fileLink.toLowerCase().startsWith("https:")) {
			// manage the case of multiple files
			String[] remotefiles = fileLink.split(inputsSeparator);
			for (String subfilelink : remotefiles) {
				subfilelink = subfilelink.trim();
				AnalysisLogger.getLogger().debug("Managing link: " + subfilelink);
				if (subfilelink.length() == 0)
					continue;
				InputStream is = null;
				HttpURLConnection urlConnection = null;
				URL url = new URL(subfilelink);
				urlConnection = (HttpURLConnection) url.openConnection();
				is = new BufferedInputStream(urlConnection.getInputStream());
				// retrieve payload: for test purpose only
				fileName = subfilelink.substring(subfilelink.lastIndexOf("/") + 1).trim();
				if (fileName.contains("."))
					fileName = fileName.substring(0, fileName.lastIndexOf(".")) + UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));
				else
					fileName = fileName + UUID.randomUUID();
				
				AnalysisLogger.getLogger().debug("Retrieving remote input in file: " + fileName);
				AnalysisLogger.getLogger().debug("Creating local temp file: " + fileName);
				File of = new File(config.getPersistencePath(), fileName);
				FileOutputStream fos = new FileOutputStream(of);
				IOUtils.copy(is, fos);
				is.close();
				fos.close();
				urlConnection.disconnect();
				filesList.add(of);
				AnalysisLogger.getLogger().debug("Created local file: " + of.getAbsolutePath());
			}
		} else {
			AnalysisLogger.getLogger().debug("Complex Input payload is the filelink");
			fileName = f.getName();
			AnalysisLogger.getLogger().debug("Retriving local input from file: " + fileName);
			
			//since this is a GenericFile we will suppose it is a csv file
			if (isXML(fileLink))
			{
				String xmlFile = f.getAbsolutePath();
				String csvFile = xmlFile+".csv";
				AnalysisLogger.getLogger().debug("Transforming XML file into a csv: " +  csvFile);
				GML2CSV.parseGML(xmlFile, csvFile);
				AnalysisLogger.getLogger().debug("GML Parsed: " + readOneLine(csvFile)+"[..]");
				f = new File(csvFile);
			}
			else
				AnalysisLogger.getLogger().debug("The file is a csv: " + f.getAbsolutePath());
			filesList.add(f);
			
		}

		return filesList;
	}

	public void createTable(String tableName, File tableFile, AlgorithmConfiguration config, DatabaseInfo supportDatabaseInfo, String inputTableTemplate) throws Exception {

		// creating table
		AnalysisLogger.getLogger().debug("Complex Input size after download: " + tableFile.length());
		if (tableFile.length() == 0)
			throw new Exception("Error: the Input file is empty");

		AnalysisLogger.getLogger().debug("Creating table from file: " + tableFile.getAbsolutePath());

		SessionFactory dbConnection = null;
		try {
			dbConnection = DatabaseUtils.initDBSession(config);
			BufferedReader br = new BufferedReader(new FileReader(tableFile));
			String header = br.readLine();
			br.close();

			AnalysisLogger.getLogger().debug("File header: " + header);
			String templatename = inputTableTemplate;
			AnalysisLogger.getLogger().debug("Suggested Template: " + templatename);
			String tableStructure = suggestTableStructure(header, templatename);
			AnalysisLogger.getLogger().debug("Extracted table structure: " + tableStructure);
			if (tableStructure == null)
				throw new Exception("Input table is not compliant to the required structure");
			TableTemplatesMapper mapper = new TableTemplatesMapper();
			String createstatement = mapper.generateCreateStatement(tableName, templatename, tableStructure);
			AnalysisLogger.getLogger().debug("Creating table: " + tableName);
			DatabaseUtils.createBigTable(true, tableName, supportDatabaseInfo.driver, supportDatabaseInfo.username, supportDatabaseInfo.password, supportDatabaseInfo.url, createstatement, dbConnection);
			DatabaseUtils.createRemoteTableFromFile(tableFile.getAbsolutePath(), tableName, ",", true, supportDatabaseInfo.username, supportDatabaseInfo.password, supportDatabaseInfo.url);

		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("Error in database transaction " + e.getLocalizedMessage());
			throw new Exception("Error in creating the table for " + tableName + ": " + e.getLocalizedMessage());
		} finally {
			DatabaseUtils.closeDBConnection(dbConnection);
		}
	}

	public String suggestTableStructure(String header, String templatename) {
		TableTemplatesMapper mapper = new TableTemplatesMapper();
		String variablesString = mapper.varsMap.get(templatename);
		String[] headersVector = header.split(",");
		String[] variables = variablesString.split(",");
		boolean check = true;
		HashMap<String, String> definitionsMap = new HashMap<String, String>();
		for (String var : variables) {
			var = var.trim();
			if (var.contains("<")) {
				check = false;
			}

			if (check) {
				String varname = var.substring(0, var.indexOf(" "));
				boolean found = false;
				for (String headvar : headersVector) {
					if (headvar.trim().equalsIgnoreCase(varname)) {
						definitionsMap.put(headvar.trim(), var);
						found = true;
						break;
					}
				}

				if (!found)
					return null;
			}
			if (var.contains(">")) {
				check = true;
			}
		}

		StringBuffer structure = new StringBuffer();
		int counter = 0;
		for (String headvar : headersVector) {
			String def = definitionsMap.get(headvar);
			if (def == null)
				structure.append(headvar + " character varying");
			else
				structure.append(def);
			if (counter < headersVector.length - 1)
				structure.append(", ");

			counter++;
		}

		return structure.toString();
	}

	public void addInputServiceParameters(List<StatisticalType> agentInputs, InfrastructureDialoguer infrastructureDialoguer) throws Exception {

		// check and fullfil additional parameters
		DatabaseInfo dbinfo = null;
		inputTableTemplates = new HashMap<String, String>();

		for (StatisticalType type : agentInputs) {
			if (type instanceof PrimitiveType) {
				if (((PrimitiveType) type).getType()==PrimitiveTypes.CONSTANT){
					String constant = ""+((PrimitiveType) type).getDefaultValue();
					config.setParam(type.getName(), constant);
					AnalysisLogger.getLogger().debug("Constant parameter: "+constant);
				}
			}
			if (type instanceof ServiceType) {
				ServiceType stype = (ServiceType) type;
				AnalysisLogger.getLogger().debug("Found ServiceType Input: " + stype);
				String name = stype.getName();
				AnalysisLogger.getLogger().debug("ServiceType Input Name: " + name);
				ServiceParameters sp = stype.getServiceParameter();
				AnalysisLogger.getLogger().debug("ServiceType Parameter: " + sp);
				String value = "";
				if (sp == ServiceParameters.RANDOMSTRING)
					value = "stat" + UUID.randomUUID().toString().replace("-", "");
				else if (sp == ServiceParameters.USERNAME){
					value = (String) inputs.get(ConfigurationManager.usernameParameter);
					
					AnalysisLogger.getLogger().debug("User name used by the client: "+value);
				}
				AnalysisLogger.getLogger().debug("ServiceType Adding: (" + name + "," + value + ")");
				config.setParam(name, value);
			} else if (type instanceof DatabaseType) {
				DatabaseType dtype = (DatabaseType) type;
				String name = dtype.getName();
				AnalysisLogger.getLogger().debug("Found DatabaseType Input: " + dtype + " with name " + name);
				DatabaseParameters parameter = dtype.getDatabaseParameter();
				AnalysisLogger.getLogger().debug("DatabaseType Input Parameter: " + parameter);
				if (parameter == DatabaseParameters.REMOTEDATABASERRNAME) {
					dbinfo = infrastructureDialoguer.getDatabaseInfo(name);
					AnalysisLogger.getLogger().debug("Requesting remote database name: " + name);
				} else if (parameter == DatabaseParameters.REMOTEDATABASEDIALECT) {
					config.setParam(name, dbinfo.dialect);
					AnalysisLogger.getLogger().debug("Extracted db dialect: " + dbinfo.dialect);
				} else if (parameter == DatabaseParameters.REMOTEDATABASEDRIVER) {
					config.setParam(name, dbinfo.driver);
					AnalysisLogger.getLogger().debug("Extracted db driver: " + dbinfo.driver);
				} else if (parameter == DatabaseParameters.REMOTEDATABASEPASSWORD) {
					config.setParam(name, dbinfo.password);
					AnalysisLogger.getLogger().debug("Extracted db password: " + dbinfo.password);
				} else if (parameter == DatabaseParameters.REMOTEDATABASEUSERNAME) {
					config.setParam(name, dbinfo.username);
					AnalysisLogger.getLogger().debug("Extracted db username: " + dbinfo.username);
				} else if (parameter == DatabaseParameters.REMOTEDATABASEURL) {
					config.setParam(name, dbinfo.url);
					AnalysisLogger.getLogger().debug("Extracted db url: " + dbinfo.url);
				}
				AnalysisLogger.getLogger().debug("DatabaseType Input Parameter Managed");
			} else if (type instanceof InputTable) {
				String name = type.getName();
				inputTableTemplates.put(name, ((InputTable) type).getTemplateNames().get(0).name());
			} else if (type instanceof TablesList) {
				String name = type.getName();
				inputTableTemplates.put(name, ((TablesList) type).getTemplates().get(0).name());
			}
		}

	}
}
