package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace.DataProvenance;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace.StoredData;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils.GML2CSV;
import org.hibernate.SessionFactory;
import org.n52.wps.io.data.GenericFileData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputsManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(InputsManager.class);

	LinkedHashMap<String, Object> inputs;
	List<String> generatedTables;
	List<File> generatedFiles;
	HashMap<String, String> inputTableTemplates = new HashMap<String, String>();
	AlgorithmConfiguration config;
	String computationId;

	List<StoredData> provenanceData = new ArrayList<StoredData>();

	public List<StoredData> getProvenanceData() {
		return provenanceData;
	}

	public static String inputsSeparator = "\\|";

	public AlgorithmConfiguration getConfig() {
		return config;
	}

	public InputsManager(LinkedHashMap<String, Object> inputs, AlgorithmConfiguration config, String computationId) {
		this.inputs = inputs;
		this.config = config;
		this.computationId = computationId;
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

	public void mergeWpsAndEcologicalInputs(DatabaseInfo supportDatabaseInfo,
			List<StatisticalType> dataminerInputParameters) throws Exception {
		LOGGER.debug("Merge WPS And Ecological Inputs");
		// browse input parameters from WPS
		for (String inputName : inputs.keySet()) {
			Object input = inputs.get(inputName);
			LOGGER.debug("Managing Input Parameter with Name " + inputName);
			// case of simple input
			if (input instanceof String) {
				LOGGER.debug("Simple Input: " + input);
				// manage lists
				String inputAlgoOrig = ((String) input).trim();
				String inputAlgo = ((String) input).trim().replaceAll(inputsSeparator,
						AlgorithmConfiguration.listSeparator);
				LOGGER.debug("Simple Input Transformed: " + inputAlgo);
				config.setParam(inputName, inputAlgo);

				saveInputData(inputName, inputName, inputAlgoOrig);
			}
			// case of Complex Input
			else if (input instanceof GenericFileData) {

				LOGGER.debug("Complex Input");
				// retrieve payload
				GenericFileData files = ((GenericFileData) input);
				LOGGER.debug("GenericFileData: [fileExtension=" + files.getFileExtension() + ", mimeType="
						+ files.getMimeType() + "]");

				List<File> localfiles = getLocalFiles(files, inputName, dataminerInputParameters);
				String inputtables = "";
				int nfiles = localfiles.size();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < nfiles; i++) {
					File tableFile = localfiles.get(i);
					generatedFiles.add(tableFile);

					String tableName = ("wps_" + ("" + UUID.randomUUID()).replace("_", "")).replace("-", "");
					// create the table

					if (inputTableTemplates.get(inputName) != null) {
						LOGGER.debug("Creating table: " + tableName);
						createTable(tableName, tableFile, config, supportDatabaseInfo,
								inputTableTemplates.get(inputName));
						generatedTables.add(tableName);
					}
					// case of non-table input file, e.g. FFANN
					else
						tableName = tableFile.getAbsolutePath();
					if (i > 0)
						inputtables = inputtables + AlgorithmConfiguration.getListSeparator();

					inputtables += tableName;

					saveInputData(tableFile.getName(), inputName, tableFile.getAbsolutePath());
					if (i > 0)
						sb.append("|");

					sb.append(tableFile.getName());
				}
				sb.append("|");
				if (nfiles > 0)
					saveInputData(inputName, inputName, sb.toString());

				// the only possible complex input is a table - check the WPS
				// parsers
				config.setParam(inputName, inputtables);
			}
		}

	}

	public boolean isXML(String fileContent) {

		if (fileContent.startsWith("&lt;"))
			return true;
		else
			return false;
	}

	public String readOneLine(String filename) {

		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
			String line = null;
			String vud = "";

			while ((line = in.readLine()) != null) {
				if (line.trim().length() > 0) {
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

	public String inputNameFromHttpHeader(String url) throws Exception {
		LOGGER.debug("Search filename in http header from: " + url);
		URL obj = new URL(url);
		URLConnection conn = obj.openConnection();
		String filename = null;
		// get all headers
		Map<String, List<String>> map = conn.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			String value = entry.getValue().toString();
			LOGGER.debug("Header value: " + value);
			if (value.toLowerCase().contains("filename")) {
				LOGGER.debug("Searching in http header: found file name in header value {}", value);
				filename = value.substring(value.indexOf("=") + 1);
				filename = filename.replace("\"", "").replace("]", "");
				LOGGER.debug("Searching in http header: retrieved file name {}", filename);
				break;
			}
		}
		LOGGER.debug("Filename retrieved from http header: " + filename);
		return filename;
	}

	public List<File> getLocalFiles(GenericFileData files, String inputName,
			List<StatisticalType> dataminerInputParameters) throws Exception {
		LOGGER.debug("GetLocalFiles: [files: " + files + ", inputName: " + inputName + "]");
		// download input
		List<File> filesList = new ArrayList<File>();
		File f = files.getBaseFile(false);
		LOGGER.debug("Retrieving local files: " + f.getAbsolutePath());
		// TODO DO NOT READ FILE INTO MEMORY
		// read file content
		String fileLink = readOneLine(f.getAbsolutePath());
		LOGGER.debug("Check File is link: {} ...", fileLink.substring(0, Math.min(fileLink.length(), 10)));
		String fileName = "";
		// case of a http link
		if (fileLink != null
				&& (fileLink.toLowerCase().startsWith("http:") || fileLink.toLowerCase().startsWith("https:"))) {
			// manage the case of multiple files
			LOGGER.debug("Complex Input payload is link");

			LOGGER.debug("Retrieving files from url: " + fileLink);
			String[] remotefiles = fileLink.split(inputsSeparator);
			for (String subfilelink : remotefiles) {
				subfilelink = subfilelink.trim();
				LOGGER.debug("Managing link: {}", subfilelink);
				if (subfilelink.length() == 0)
					continue;
				InputStream is = null;
				HttpURLConnection urlConnection = null;
				URL url = new URL(subfilelink);
				urlConnection = (HttpURLConnection) url.openConnection();
				is = new BufferedInputStream(urlConnection.getInputStream());
				// retrieve payload: for test purpose only
				String fileNameTemp = inputNameFromHttpHeader(subfilelink);

				LOGGER.debug("the fileNameTemp is {}", fileNameTemp);

				if (fileNameTemp != null && !fileNameTemp.isEmpty()) {
					fileName = String.format("%s_(%s).%s", inputName, computationId,
							FilenameUtils.getExtension(fileNameTemp));
				} else {
					fileName = String.format("%s_(%s).%s", inputName, computationId,
							FilenameUtils.getExtension(inputName));

				}
				LOGGER.debug("the name of the generated file is {}", fileName);

				File of = new File(config.getPersistencePath(), fileName);
				FileOutputStream fos = new FileOutputStream(of);
				IOUtils.copy(is, fos);
				is.close();
				fos.flush();
				fos.close();
				urlConnection.disconnect();
				filesList.add(of);
				LOGGER.debug("Created local file: {}", of.getAbsolutePath());
			}
		} else {
			LOGGER.debug("Complex Input payload is file");
			fileName = f.getName();

			LOGGER.debug("Retrieving local input from file: {}", fileName);

			String fileExt = null;

			if (isXML(fileLink)) {
				String xmlFile = f.getAbsolutePath();
				String csvFile = xmlFile + ".csv";
				LOGGER.debug("Transforming XML file into a csv: {} ", csvFile);
				GML2CSV.parseGML(xmlFile, csvFile);
				LOGGER.debug("GML Parsed: {} [..]", readOneLine(csvFile));
				f = new File(csvFile);
				fileExt = "csv";
			} else {
				LOGGER.debug("The file is a csv: {}", f.getAbsolutePath());
				fileExt = FilenameUtils.getExtension(fileName);
			}

			LOGGER.debug("Retrieve default extension");
			String fileDefaultValue = null;
			for (StatisticalType defaultInputParameter : dataminerInputParameters) {
				if (defaultInputParameter.getName().compareTo(inputName) == 0) {
					fileDefaultValue = defaultInputParameter.getDefaultValue();
					break;
				}
			}
			LOGGER.debug("Parameter default value retrieved: " + fileDefaultValue);

			if (fileDefaultValue != null && !fileDefaultValue.isEmpty()) {
				int lastPointIndex = fileDefaultValue.lastIndexOf(".");
				if (lastPointIndex > -1 && lastPointIndex < (fileDefaultValue.length() - 1)) {
					fileExt = fileDefaultValue.substring(lastPointIndex + 1);
					LOGGER.debug("Default Extension retrieved: " + fileExt);
				}
			}

			LOGGER.debug("Use extension: " + fileExt);

			String absFile = new File(f.getParent(), String.format("%s_(%s).%s", inputName, computationId, fileExt))
					.getAbsolutePath();
			LOGGER.debug("Renaming to: " + absFile);
			System.gc();
			boolean renamed = f.renameTo(new File(absFile));
			if (renamed)
				f = new File(absFile);
			LOGGER.debug("The file has been renamed as : {} - {}", f.getAbsolutePath(), renamed);
			filesList.add(f);

		}

		return filesList;
	}

	public void createTable(String tableName, File tableFile, AlgorithmConfiguration config,
			DatabaseInfo supportDatabaseInfo, String inputTableTemplate) throws Exception {

		// creating table
		LOGGER.debug("Complex Input size after download: " + tableFile.length());
		if (tableFile.length() == 0)
			throw new Exception("Error: the Input file is empty");

		LOGGER.debug("Creating table from file: " + tableFile.getAbsolutePath());

		SessionFactory dbConnection = null;
		try {
			dbConnection = DatabaseUtils.initDBSession(config);
			BufferedReader br = new BufferedReader(new FileReader(tableFile));
			String header = br.readLine();
			br.close();

			LOGGER.debug("File header: " + header);
			String templatename = inputTableTemplate;
			LOGGER.debug("Suggested Template: " + templatename);
			String tableStructure = suggestTableStructure(header, templatename);
			LOGGER.debug("Extracted table structure: " + tableStructure);
			if (tableStructure == null)
				throw new Exception("Input table is not compliant to the required structure");
			TableTemplatesMapper mapper = new TableTemplatesMapper();
			String createstatement = mapper.generateCreateStatement(tableName, templatename, tableStructure);
			LOGGER.debug("Creating table: " + tableName);
			DatabaseUtils.createBigTable(true, tableName, supportDatabaseInfo.driver, supportDatabaseInfo.username,
					supportDatabaseInfo.password, supportDatabaseInfo.url, createstatement, dbConnection);
			DatabaseUtils.createRemoteTableFromFile(tableFile.getAbsolutePath(), tableName, ",", true,
					supportDatabaseInfo.username, supportDatabaseInfo.password, supportDatabaseInfo.url);

		} catch (Exception e) {
			LOGGER.error("Error in database transaction ", e);
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

	public void addInputServiceParameters(List<StatisticalType> agentInputs,
			InfrastructureDialoguer infrastructureDialoguer) throws Exception {

		// check and fullfil additional parameters
		DatabaseInfo dbinfo = null;
		inputTableTemplates = new HashMap<String, String>();

		for (StatisticalType type : agentInputs) {
			if (type instanceof PrimitiveType) {
				if (((PrimitiveType) type).getType() == PrimitiveTypes.CONSTANT) {
					String constant = "" + ((PrimitiveType) type).getDefaultValue();
					config.setParam(type.getName(), constant);
					LOGGER.debug("Constant parameter: " + constant);
				}
			}
			if (type instanceof ServiceType) {
				ServiceType stype = (ServiceType) type;
				LOGGER.debug("Found ServiceType Input: " + stype);
				String name = stype.getName();
				LOGGER.debug("ServiceType Input Name: " + name);
				ServiceParameters sp = stype.getServiceParameter();
				LOGGER.debug("ServiceType Parameter: " + sp);
				String value = "";
				if (sp == ServiceParameters.RANDOMSTRING)
					value = "stat" + UUID.randomUUID().toString().replace("-", "");
				else if (sp == ServiceParameters.USERNAME) {
					value = (String) inputs.get(ConfigurationManager.usernameParameter);

					LOGGER.debug("User name used by the client: " + value);
				}
				LOGGER.debug("ServiceType Adding: (" + name + "," + value + ")");
				config.setParam(name, value);
			} else if (type instanceof DatabaseType) {
				DatabaseType dtype = (DatabaseType) type;
				String name = dtype.getName();
				LOGGER.debug("Found DatabaseType Input: " + dtype + " with name " + name);
				DatabaseParameters parameter = dtype.getDatabaseParameter();
				LOGGER.debug("DatabaseType Input Parameter: " + parameter);
				if (parameter == DatabaseParameters.REMOTEDATABASERRNAME) {
					dbinfo = infrastructureDialoguer.getDatabaseInfo(name);
					LOGGER.debug("Requesting remote database name: " + name);
				} else if (parameter == DatabaseParameters.REMOTEDATABASEDIALECT) {
					config.setParam(name, dbinfo.dialect);
					LOGGER.debug("Extracted db dialect: " + dbinfo.dialect);
				} else if (parameter == DatabaseParameters.REMOTEDATABASEDRIVER) {
					config.setParam(name, dbinfo.driver);
					LOGGER.debug("Extracted db driver: " + dbinfo.driver);
				} else if (parameter == DatabaseParameters.REMOTEDATABASEPASSWORD) {
					config.setParam(name, dbinfo.password);
					LOGGER.debug("Extracted db password: " + dbinfo.password);
				} else if (parameter == DatabaseParameters.REMOTEDATABASEUSERNAME) {
					config.setParam(name, dbinfo.username);
					LOGGER.debug("Extracted db username: " + dbinfo.username);
				} else if (parameter == DatabaseParameters.REMOTEDATABASEURL) {
					config.setParam(name, dbinfo.url);
					LOGGER.debug("Extracted db url: " + dbinfo.url);
				}
				LOGGER.debug("DatabaseType Input Parameter Managed");
			} else if (type instanceof InputTable) {
				String name = type.getName();
				inputTableTemplates.put(name, ((InputTable) type).getTemplateNames().get(0).name());
			} else if (type instanceof TablesList) {
				String name = type.getName();
				inputTableTemplates.put(name, ((TablesList) type).getTemplates().get(0).name());
			}
		}

	}

	private void saveInputData(String name, String description, String payload) {
		LOGGER.debug("SaveInputData [name="+name+", description="+description+", payload="+payload+"]");
		String id = name;
		DataProvenance provenance = DataProvenance.IMPORTED;
		String creationDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(System.currentTimeMillis());
		String operator = config.getAgent();

		String type = "text/plain";

		if (payload != null && (new File(payload).exists())) {
			if (payload.toLowerCase().endsWith(".csv") || payload.toLowerCase().endsWith(".txt")) {
				type = "text/csv";
			} else
				type = "application/d4science";
		}

		StoredData data = new StoredData(name, description, id, provenance, creationDate, operator, computationId, type,
				payload, config.getGcubeScope());

		provenanceData.add(data);
	}

}
