package org.gcube.resources.federation.fhnmanager.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.gcube.resources.federation.fhnmanager.is.ISProxyLocalYaml;
import org.gcube.vomanagement.occi.FHNConnector;
import org.gcube.vomanagement.occi.utils.X509CredentialManager;
import org.omg.CORBA.portable.InputStream;

public class Props {
public Props(){}
	
	public String getPath() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");
     
        //loading properites from properties file
        try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //reading proeprty
        String path = props.getProperty("STORAGE_DIR");
		return path; 

	}
	public String getProxy() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");
     
        //loading properites from properties file
        try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //reading proeprty
        String proxy = props.getProperty("PROXY");
		return proxy; 

	}
	public String getHost() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");
     
        //loading properites from properties file
        try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //reading proeprty
        String host = props.getProperty("HOST");
		return host; 

	}
	public String getPwd() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");
     
        //loading properites from properties file
        try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //reading proeprty
        String pwd = props.getProperty("PWD");
		return pwd; 

	}
}


