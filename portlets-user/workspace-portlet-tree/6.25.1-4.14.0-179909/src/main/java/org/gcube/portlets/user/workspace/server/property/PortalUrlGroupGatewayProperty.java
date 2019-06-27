package org.gcube.portlets.user.workspace.server.property;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Jun 26, 2013
 *
 */
public class PortalUrlGroupGatewayProperty {
	
	
	/**
	 * 
	 */
	protected static final String PORTALURL_PROPERTIES = "portalurl.properties";

	protected static Logger log = Logger.getLogger(PortalUrlGroupGatewayProperty.class);
	
	private String server = "";
	private String path = "";

	public PortalUrlGroupGatewayProperty(){
		
		Properties properties = new Properties();
		
		try {
			
			InputStream in = (InputStream) PortalUrlGroupGatewayProperty.class.getResourceAsStream(PORTALURL_PROPERTIES);

//			// load a properties file
			properties.load(in);
//			// get the properties value for Portal
			server = properties.getProperty("SERVER");
			path = properties.getProperty("PATH");		
			in.close();
			
		}catch (Exception e) {
			log.error("error on reading property file: "+PORTALURL_PROPERTIES, e);
		}

	}

	public String getServer() {
		return server;
	}

	public String getPath() {
		return path;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PortalUrlProperty [server=");
		builder.append(server);
		builder.append(", path=");
		builder.append(path);
		builder.append("]");
		return builder.toString();
	}
	
	public static void main(String[] args) {
		
		PortalUrlGroupGatewayProperty p = new PortalUrlGroupGatewayProperty();
		
		int lenght = p.getPath().length();
		String lastChar = p.getPath().substring(lenght-1, lenght-1);
			
		String path = lastChar.compareTo("/")!=0?p.getPath()+"/":p.getPath();
		

		System.out.println(p);
		System.out.println(path);
		
	}
	
}
