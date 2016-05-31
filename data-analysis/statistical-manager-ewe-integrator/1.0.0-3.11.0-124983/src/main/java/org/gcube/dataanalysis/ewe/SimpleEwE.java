package org.gcube.dataanalysis.ewe;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ewe.util.ExecUtils;
import org.gcube.dataanalysis.ewe.util.FileSystemUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A simple implementation of a wrapper around EwE
 * 
 * @author Paolo Fabriani (Engineering Ingegneria Informatica S.p.A.)
 * 
 */
public class SimpleEwE extends AbstractEwE {


	private static String MODEL_FILE = "Model File";
	private static String CONFIG_FILE = "Config File";

	/**
	 * The name of the tag in 'run_config' referencing the model file
	 */
	private static final String MODEL_FILE_TAG_NAME = "model_file";

	@Override
	public void init() throws Exception {
		super.init();
		AnalysisLogger.getLogger().info("EwE Initialisation");
		AnalysisLogger.getLogger().info(this.getDescription());
	}

	@Override
	public String getDescription() {
		return super.getDescription();
	}

	@Override
	protected void setInputParameters() {

		// ask for the model input file
		this.inputs.add(new PrimitiveType(File.class.getName(), null,
				PrimitiveTypes.FILE, MODEL_FILE,
				"A file containing the model (e.g. Georgia_Strait.eiixml)"));

		// ask for the model input file
		this.inputs.add(new PrimitiveType(File.class.getName(), null,
				PrimitiveTypes.FILE, CONFIG_FILE,
				"A file containing execution parameters (e.g. run_config.xml)"));

	}

	protected void prepareInput() throws Exception {
		FileSystemUtils fsu = new FileSystemUtils(this.getExecutionId());
		// copy input files to working directory
		AnalysisLogger.getLogger().debug("Copying input files...");
		// copy and rename config file
		fsu.copyInputFileAs(config.getParam(CONFIG_FILE), CONFIG_FILE_NAME);
		// extract the name of the model file
		String modelFileName = this.extractModelFileNameFromConfigFile(new File(fsu.getInputLocation(), CONFIG_FILE_NAME));
		// copy and rename model file
		fsu.copyInputFileAs(config.getParam(MODEL_FILE), modelFileName);
	}
	
	private String extractModelFileNameFromConfigFile(File config_file) throws Exception {
		
		AnalysisLogger.getLogger().debug("Extracting model file name from " + config_file);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		}
		catch(ParserConfigurationException e) {
			AnalysisLogger.getLogger().error(e);
			throw new Exception(e);
		}
		
		Document doc = null;
		try {
			doc = builder.parse(config_file);
		}
		catch(SAXException e) {
			AnalysisLogger.getLogger().error(e);
			throw new Exception("Unable to parse the configuration file. Is it an xml file?");
		}
		catch(IOException e) {
			AnalysisLogger.getLogger().error(e);
			throw new Exception("I/O problem in accessing the configuration file");
		}
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName(MODEL_FILE_TAG_NAME);
		
		String out = null;
		
		if(nList.getLength()==0) {
			AnalysisLogger.getLogger().error("Can't find a tag named '"+MODEL_FILE_TAG_NAME+"' in the configuration file");
			throw new Exception("Unable to extract model name from the configuration file");
		}
		else {
			out = nList.item(0).getTextContent();
			if(nList.getLength()>1) {
				AnalysisLogger.getLogger().warn("More than one model name found. Returning the first: " + out);
			}
		}
		AnalysisLogger.getLogger().debug("Model file name is " + out);
		System.out.println(out);
		return out;
	}
	
}

