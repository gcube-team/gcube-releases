package org.gcube.resources.federation.fhnmanager.utils;

import java.io.IOException;
import java.util.Properties;

import org.gcube.resources.federation.fhnmanager.is.ISProxyLocalYaml;

public class Props {
	public static String home = System.getProperty("user.home");

	public Props() {
	}

	public String getPath() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");

		// loading properites from properties file
		try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// reading proeprty
		String path = props.getProperty("STORAGE_DIR");
		return path;

	}

	public String getOccopusURL() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");

		// loading properites from properties file
		try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// reading proeprty
		String path = props.getProperty("REST_URL");
		return path;

	}

	public String getOccopusDIR() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");

		// loading properites from properties file
		try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// reading proeprty
		String path = home + props.getProperty("OCCOPUS_BIN");
		return path;

	}

	public String getPathOccopusNodes() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");

		// loading properites from properties file
		try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// reading proeprty
		String path = home + props.getProperty("STORAGE_DIR_OCCOPUS_NODES");
		return path;

	}

	public String getPathOccopusInfra() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");

		// loading properites from properties file
		try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// reading proeprty
		String path = props.getProperty("STORAGE_DIR_OCCOPUS_INFRA");
		return path;

	}

	public String getProxy() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");

		// loading properites from properties file
		try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// reading proeprty
		String proxy = props.getProperty("PROXY");
		return proxy;

	}

	public String getHost() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");

		// loading properites from properties file
		try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// reading proeprty
		String host = props.getProperty("HOST");
		return host;

	}

	public String getPwd() {
		Properties props = new Properties();
		java.io.InputStream input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("service.properties");

		// loading properites from properties file
		try {
			props.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// reading proeprty
		String pwd = props.getProperty("PWD");
		return pwd;

	}

	public static void main(String[] args) {
		Props a = new Props();
		System.out.println(a.getOccopusDIR());
		System.out.println(a.getPathOccopusNodes());
	}
}
