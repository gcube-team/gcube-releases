/**
 * 
 */
package org.gcube.portlets.user.workspace.server.util.resource;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Mar 5, 2014
 *
 */
public class PropertySpecialFolderReader {
	
	
	protected static Logger logger = LoggerFactory.getLogger(PropertySpecialFolderReader.class);
	protected String specialFolderName = "";
	
	public PropertySpecialFolderReader(String absolutePath) throws PropertyFileReadingErrorException {
		
		try{
			logger.info("Instancing new PropertySpecialFolderReader with path: "+absolutePath);
//			URL resource = PropertySpecialFolderReader.class.getResource(ConstantsExplorer.SPECIALFOLDERNAMEPROPERTIESFILE);
//			File file = new File(resource.toURI());
//			FileInputStream input = new FileInputStream(file);
			
//			InputStream input = PropertySpecialFolderReader.class.getResourceAsStream(ConstantsExplorer.SPECIALFOLDERNAMEPROPERTIESFILE);
			File propsFile = new File(absolutePath);
			if(!propsFile.exists()){
				throw new Exception("File not found in path: "+absolutePath);
			}
			FileInputStream fis = new FileInputStream(propsFile);
			Properties properties = new Properties();
			properties.load(fis);
			specialFolderName = properties.getProperty(ConstantsExplorer.SPECIALFOLDERNAME);
			
			logger.info("PropertySpecialFolderReader read for key: "+ConstantsExplorer.SPECIALFOLDERNAME + " value: "+specialFolderName);
		}catch (Exception e) {
			logger.error("Error on loading property to read special folder name: ",e);
			throw new PropertyFileReadingErrorException("Error on reading property file: "+ConstantsExplorer.SPECIALFOLDERNAMEPROPERTIESFILE);
		}
	}
	
	public String getSpecialFolderName() {
		return specialFolderName;
	}

	public void setSpecialFolderName(String specialFolderName) {
		this.specialFolderName = specialFolderName;
	}
	
	
//	public static void main(String[] args) {
//		
//		try {
////			new PropertySpecialFolderReader();
//			
//		} catch (PropertyFileReadingErrorException e) {
//			e.printStackTrace();
//		}
//	}


}
