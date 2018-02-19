package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.gcube.contentmanagement.graphtools.data.conversions.ImageTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.OutputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalTypeList;
import org.gcube.dataanalysis.ecoengine.datatypes.TablesList;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils.FileManager;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StatisticalTypeToWPSType {
	
	private static Logger logger = LoggerFactory.getLogger(StatisticalTypeToWPSType.class);
	
	public static Properties templates;
	static String ABSTRACT = "#ABSTRACT#";
	static String TITLE = "#TITLE#";
	static String CLASSNAME = "#CLASSNAME#";
	static String ALLOWED = "#ALLOWED#";
	static String DEFAULT = "#DEFAULT#";
	static String ID = "#ID#";
	static String IDMETHOD = "#IDMETHOD#";
	static String METHOD_ORDER = "#ORDER_VALUE#";
		
	private int orderValue = 0;
	
	public LinkedHashMap<String, IOWPSInformation> inputSet = new LinkedHashMap<String, IOWPSInformation>();
	public LinkedHashMap<String, IOWPSInformation> outputSet = new LinkedHashMap<String, IOWPSInformation>();
	public List<File> generatedFiles = new ArrayList<File>();
	public List<String> generatedTables = new ArrayList<String>();

	public List<File> getGeneratedFiles() {
		return generatedFiles;
	}

	public List<String> getGeneratedTables() {
		return generatedTables;
	}

	public synchronized void getTemplates() throws Exception {
		if (templates != null)
			return;

		templates = new Properties();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("templates/classtemplate.properties");
		templates.load(is);
		is.close();
	}

	public StatisticalTypeToWPSType() throws Exception {
		getTemplates();
	}

	public String cleanID(String name) {
		return name.replaceAll("[ \\]\\[!\"#$%&'()*+,\\./:;<=>?@\\^`{|}~-]", "_");
		// return name;
	}

	public String convert2WPSType(StatisticalType stype, boolean isinput, AlgorithmConfiguration config) throws Exception {
		if (stype == null)
			return "";

		String wpstype = null;
		String outputType = "";
		TableTemplatesMapper mapper = new TableTemplatesMapper();

		String webpersistence = config.getParam(ConfigurationManager.webPersistencePathVariable);
		logger.debug("Using the foll. web persistence: " + webpersistence);

		String name = stype.getName();
		String classForname = stype.getClass().getSimpleName();
		if (name == null || name.length() == 0)
			name = classForname.replace(".", "");
		String id = (name);
		String abstractStr = stype.getDescription() != null ? stype.getDescription() : "";
		String allowed = "";
		String defaultVal = stype.getDefaultValue() != null ? stype.getDefaultValue() : "";

		String content = null;
		String localcontent = null;
		String mimeType = "";
		if (stype instanceof PrimitiveType) {
			PrimitiveType ptype = (PrimitiveType) stype;
			PrimitiveTypes subtype = ptype.getType();

			switch (subtype) {
			case STRING:
				content = (String) ptype.getContent();
				outputType = "string";
				mimeType = "text/plain";
				break;
			case NUMBER: {
				content = "" + ptype.getContent();
				String classname = ptype.getClassName();
				outputType = "integer";
				mimeType = "text/plain";
				if (!classname.equals(Integer.class.getName())) {
					outputType = "double";
				}
				break;
			}

			case ENUMERATED: {
				Object contentObj = ptype.getContent();
				content = Arrays.toString((Object[]) ptype.getContent());
				outputType = "enumerated";
				mimeType = "text/plain";
				Object[] allowedObjs = (Object[]) contentObj;
				allowed = "";

				// generation of allowed values with check of the default value
				String candidatedefaultvalue = "";
				boolean defaultfound = false;
				for (int i = 0; i < allowedObjs.length; i++) {
					String allowedS = ("" + allowedObjs[i]).trim();
					allowed += "\"" + allowedS + "\"";
					if (i == 0)
						candidatedefaultvalue = allowedS;
					if (allowedS.equals(defaultVal))
						defaultfound = true;
					if (i < allowedObjs.length - 1)
						allowed += ",";
				}

				if (!defaultfound)
					defaultVal = candidatedefaultvalue;

				break;
			}
			case FILE:
				String filename = "";
				if (ptype.getContent() != null) {
					String originalfile = ((File) ptype.getContent()).getAbsolutePath();
					((File) ptype.getContent()).getAbsolutePath();
					// search for the object in various locations
					logger.debug("Searching for file in: " + originalfile);
					if (!new File(originalfile).exists()) {
						originalfile = new File(config.getPersistencePath(), ((File) ptype.getContent()).getName()).getAbsolutePath();
						logger.debug("Searching for file in persistence path: " + originalfile);
						if (!new File(originalfile).exists()) {
							originalfile = new File(config.getConfigPath(), ((File) ptype.getContent()).getName()).getAbsolutePath();
							logger.debug("Searching for file in config path: " + originalfile);
						}
					}
					if (!new File(originalfile).exists()) {
						logger.debug("The file does not exist! " + originalfile);
					} else {
						logger.debug("The file exists! " + originalfile);
						filename = ((File) ptype.getContent()).getName();
						String filenameDest = System.currentTimeMillis() + "_" + filename;
						logger.debug("file destination for output is "+filenameDest);
						String destinationfile = new File(webpersistence, filenameDest).getAbsolutePath();
						logger.debug("Copying file into a temporary file: " + destinationfile);
						
						FileManager.fileCopy(originalfile, destinationfile);
						content = config.getParam(ConfigurationManager.webpathVariable) + filenameDest;
						localcontent = destinationfile;
						logger.debug("Web content associated to the file is: " + content);
						generatedFiles.add(new File(originalfile));
					}
				}
				if (filename.toLowerCase().endsWith(".csv") || filename.toLowerCase().endsWith(".txt")) {
					outputType = "csvFile";
					mimeType = "text/csv";
				} else {
					outputType = "d4scienceFile";
					mimeType = "application/d4science";
				}
				logger.debug("File managed correctly: Type: " + outputType + " mimetype: " + mimeType);
				break;
			case MAP: {
				@SuppressWarnings("unchecked")
				Map<String, StatisticalType> subelements = (Map<String, StatisticalType>) ptype.getContent();
				wpstype = "";
				int counter = 1;
				for (String subel : subelements.keySet()) {
					StatisticalType stsub = subelements.get(subel);
					String sclassForname = stsub.getClass().getSimpleName();
					if (stsub.getName() == null || stsub.getName().length() == 0)
						stsub.setName(sclassForname.replace(".", "") + counter);

					wpstype = wpstype + "\n" + convert2WPSType(stsub, isinput, config);
					counter++;
				}
				break;
			}
			case BOOLEAN:
				outputType = "boolean";
				mimeType = "text/plain";
				defaultVal=defaultVal.toLowerCase();
				break;
			case IMAGES: {
				// content = ptype.getContent();
				@SuppressWarnings("unchecked")
				Map<String, Image> subelements = (Map<String, Image>) ptype.getContent();
				wpstype = "";
				if (subelements != null && subelements.size() > 0) {
					for (String subel : subelements.keySet()) {
						// Image stsub = subelements.get(subel);
						outputType = "pngFile";
						outputType += isinput ? "Input" : "Output";

						wpstype = wpstype + "\n" + ((String) templates.get(outputType)).replace(ABSTRACT, subel).replace(TITLE, subel).replace(ID, subel).replace(METHOD_ORDER, Integer.toString(orderValue++)).replace(IDMETHOD, cleanID(id)).replace(DEFAULT, defaultVal);

						String imagefilename = new File(webpersistence, subel + "_" + UUID.randomUUID() + ".png").getAbsolutePath();
						BufferedImage bi = ImageTools.toBufferedImage(subelements.get(subel));
						File f = new File(imagefilename);

						ImageIO.write(bi, "png", f);

						// upload on WS and get URL - TOO SLOW!
						// String url =
						// AbstractEcologicalEngineMapper.uploadOnWorkspaceAndGetURL(config.getGcubeScope(),
						// imagefilename);
						String url = config.getParam(ConfigurationManager.webpathVariable) + f.getName();
						logger.debug("Got URL for the file " + url);

						IOWPSInformation info = new IOWPSInformation();
						info.setName(subel);
						info.setAbstractStr(subel);
						info.setAllowed(allowed);
						info.setContent(url);
						info.setLocalMachineContent(imagefilename);
						info.setDefaultVal(defaultVal);
						info.setMimetype("image/png");
						info.setClassname(classForname);
						generatedFiles.add(f);
						if (isinput)
							inputSet.put(subel, info);
						else
							outputSet.put(subel, info);

					}
				}
				break;
			}
			default:
				return null;
			}
		} else if (stype instanceof PrimitiveTypesList) {
			String format = ((PrimitiveTypesList) stype).getClassName();
			format = format.substring(format.lastIndexOf(".") + 1);
			PrimitiveType pptype = new PrimitiveType(((PrimitiveTypesList) stype).getClassName(), null, ((PrimitiveTypesList) stype).getType(), name, abstractStr + " [a sequence of values separated by | ] (format: " + format + ")", defaultVal);
			pptype.setType(PrimitiveTypes.STRING);
			pptype.setClassName(String.class.getName());
			wpstype = convert2WPSType(pptype, isinput, config);
		} else if (stype instanceof ColumnType) {
			outputType = "string";
			abstractStr += " [the name of a column from " + ((ColumnType) stype).getTableName() + "]";
			mimeType = "text/plain";
		} else if (stype instanceof ColumnTypesList) {
			outputType = "string";
			abstractStr += " [a sequence of names of columns from " + ((ColumnTypesList) stype).getTabelName() + " separated by | ]";
			mimeType = "text/plain";
		} else if (stype instanceof DatabaseType) {
			return null;
		} else if (stype instanceof OutputTable) {
			// content = ((OutputTable) stype).getTableName();
			String tablename = (String) ((OutputTable) stype).getTableName();

			outputType = "csvFile";
			mimeType = "text/csv";
			String template = ((OutputTable) stype).getTemplateNames().get(0).name();
			abstractStr += " [a http link to a table in UTF-8 ecoding following this template: " + mapper.linksMap.get(template) + "]";
			if (tablename != null && tablename.length() > 0) {
				generatedTables.add(tablename);
				String localfile = new File(webpersistence, tablename + UUID.randomUUID() + ".csv").getAbsolutePath();
				logger.debug("Creating file " + localfile + " from table " + content);
				dumpTable(localfile, tablename, ",", config.databaseUserName, config.databasePassword, config.databaseURL);
				logger.debug("File " + localfile + " has been created");
				// upload on WS and get URL
				// String url =
				// AbstractEcologicalEngineMapper.uploadOnWorkspaceAndGetURL(config.getGcubeScope(),
				// localfile);
				String url = config.getParam(ConfigurationManager.webpathVariable) + new File(localfile).getName();
				logger.debug("Got URL for file " + url);
				content = url;
				localcontent = localfile;
			}
			// upload on storage and get URL

		} else if (stype instanceof InputTable) {
			outputType = "csvFile";
			mimeType = "text/csv";
			String template = ((InputTable) stype).getTemplateNames().get(0).name();
			abstractStr += " [a http link to a table in UTF-8 encoding following this template: " + mapper.linksMap.get(template) + "]";
		} else if (stype instanceof ServiceType) {
			return null;
		} else if (stype instanceof StatisticalTypeList) {
			return null;
		} else if (stype instanceof TablesList) {
			outputType = "csvFile";
//			outputType = "string";
			String template = ((TablesList) stype).getTemplates().get(0).name();
			abstractStr += " [a sequence of http links separated by | , each indicating a table in UTF-8 encoding following this template: " + mapper.linksMap.get(template) + "]";
			mimeType = "text/csv";
//			mimeType = "text/plain";
		}

		outputType += isinput ? "Input" : "Output";

		if (name == null)
			name = outputType;

		if (wpstype == null) {
			// wpstype = ((String) templates.get(outputType)).replace(ABSTRACT,
			// abstractStr).replace(TITLE, name).replace(ID,
			// id).replace(DEFAULT, defaultVal).replace(ALLOWED, allowed);
			wpstype = ((String) templates.get(outputType)).replace(ABSTRACT, "Name of the parameter: " + name + ". " + abstractStr).replace(TITLE, abstractStr).replace(ID, id).replace(METHOD_ORDER, Integer.toString(orderValue++)).replace(IDMETHOD, cleanID(id)).replace(DEFAULT, defaultVal).replace(ALLOWED, allowed);

			IOWPSInformation info = new IOWPSInformation();
			info.setName(name);
			info.setAllowed(allowed);
			info.setContent(content);
			info.setDefaultVal(defaultVal);
			info.setLocalMachineContent(localcontent);
			info.setMimetype(mimeType);
			info.setClassname(classForname);
			info.setAbstractStr(abstractStr);

			if (localcontent != null)
				generatedFiles.add(new File(localcontent));
			if (isinput)
				inputSet.put(name, info);
			else
				outputSet.put(name, info);

		}
		return wpstype.trim();
	}

	public void dumpTable(String filePath, String tablename, String delimiter, String username, String password, String databaseurl) throws Exception {

		Connection conn = DatabaseFactory.getDBConnection("org.postgresql.Driver", username, password, databaseurl);
		CopyManager copyManager = new CopyManager((BaseConnection) conn);
		FileWriter fw = new FileWriter(filePath);
		copyManager.copyOut(String.format("COPY %s TO STDOUT WITH DELIMITER '%s' NULL AS '' CSV HEADER QUOTE AS '\"'", tablename, delimiter), fw);
		conn.close();
		fw.close();
	}

	
	
	
	/*
	 * enum a { A, B };
	 */
	public static void main(String[] args) throws Exception {
		// Object[] elements = a.values();
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setGcubeScope("/gcube/devsec");
		// config.setParam(AbstractEcologicalEngineMapper.processingSession,
		// ""+UUID.randomUUID());
		// config.setParam(AbstractEcologicalEngineMapper.serviceUserNameParameter,
		// "wps.synch");

		// AbstractEcologicalEngineMapper.uploadOnWorkspaceAndGetURL(config,new
		// File( "./datasets/hcaf_d_mini.csv"), "test gp for WPS", "text/csv");

	}
}
