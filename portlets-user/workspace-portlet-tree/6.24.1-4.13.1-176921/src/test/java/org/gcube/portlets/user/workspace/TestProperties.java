package org.gcube.portlets.user.workspace;

import java.io.IOException;
import java.util.Properties;

import org.gcube.portlets.user.workspace.server.GWTWorkspaceServiceImpl;

public class TestProperties {

	public static void main(String[] args) {

		Properties prop = new Properties();

		try {
			// load a properties file from class path, inside static method
			prop.load(GWTWorkspaceServiceImpl.class.getClassLoader().getResourceAsStream("portletClassName.properties"));

			// get the property value and print it out
			System.out.println(prop.getProperty("idreport"));
			System.out.println(prop.getProperty("idtemplate"));


		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}
