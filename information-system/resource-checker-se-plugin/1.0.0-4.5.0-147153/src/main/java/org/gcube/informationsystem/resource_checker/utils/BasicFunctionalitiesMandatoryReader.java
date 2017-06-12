package org.gcube.informationsystem.resource_checker.utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Parse the generic resource containing the basic functionalities and retrieve them.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class BasicFunctionalitiesMandatoryReader {

	private static Logger logger = LoggerFactory.getLogger(BasicFunctionalitiesMandatoryReader.class);

	private List<BasicFunctionalityBean> mandatoryFunctionalities = null;

	private static final String FILE_PROPRETIES_LOCATION_PATH = "/META-INF/plugin_resources/resources_to_fetch.properties";
	private static final String SERVICE_NAME_KEY = "ServiceNames";
	private static final String CATEGORY_NAME_KEY = "CategoryNames";
	private static final String TYPES = "Types";
	private static final String SEPARATOR = ",";

	public BasicFunctionalitiesMandatoryReader() throws Exception{
		readFromLocalFile();
	}

	/**
	 * Read it from the fallback file
	 * @return 
	 * @throws FileNotFoundException 
	 */
	private void readFromLocalFile() throws Exception {

		Properties prop = new Properties();
		prop.load(getClass().getResourceAsStream(FILE_PROPRETIES_LOCATION_PATH));

		String serviceNames = prop.getProperty(SERVICE_NAME_KEY);
		String serviceCategories = prop.getProperty(CATEGORY_NAME_KEY);
		String types = prop.getProperty(TYPES);

		if(serviceNames == null || serviceCategories == null)
			throw new Exception("Service names or categories are missing in file " + FILE_PROPRETIES_LOCATION_PATH);

		String[] serviceNamesSplitted = serviceNames.split(SEPARATOR);
		String[] serviceCategoriesSplitted = serviceCategories.split(SEPARATOR);

		String[] typesSplitted = null;
		if(types != null)
			typesSplitted = types.split(SEPARATOR);

		// build the java objects
		if(serviceNamesSplitted.length != serviceCategoriesSplitted.length)
			throw new Exception("The file at " + FILE_PROPRETIES_LOCATION_PATH + " seems to be malformed (service names and categories number do not match)!");

		if(typesSplitted != null && serviceNamesSplitted.length != typesSplitted.length)
			throw new Exception("The file at " + FILE_PROPRETIES_LOCATION_PATH + " seems to be malformed (types lenght doesn't match the other properties)!");

		// Build the objects
		mandatoryFunctionalities = new ArrayList<BasicFunctionalityBean>();
		for (int i = 0; i < serviceCategoriesSplitted.length; i++) {
			mandatoryFunctionalities.add(new BasicFunctionalityBean(serviceCategoriesSplitted[i], serviceNamesSplitted[i], typesSplitted != null ? typesSplitted[i] : null));
		}

		logger.info("Built list is " + mandatoryFunctionalities);

	}


	/**
	 * Retrieve the list of mandatory service endpoints
	 * @return a list of mandatory service endpoints
	 */
	public List<BasicFunctionalityBean> getMandatoryFunctionalities() {
		return mandatoryFunctionalities;
	}	

}
