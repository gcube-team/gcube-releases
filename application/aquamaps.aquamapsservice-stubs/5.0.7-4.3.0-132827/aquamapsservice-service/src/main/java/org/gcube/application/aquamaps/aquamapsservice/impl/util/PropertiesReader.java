package org.gcube.application.aquamaps.aquamapsservice.impl.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesReader {
	final static Logger logger= LoggerFactory.getLogger(PropertiesReader.class);
	
	private long lastAccessed=0;
	
	private Properties props=null;
	
	private static long refreshTime=3*60*1000;
	
	private String path; 
	
	
	private static PropertiesReader instance;
	
	private PropertiesReader(String path) throws Exception {
		props=new Properties();
		this.path=path;		
	}

	public static PropertiesReader get(String path) throws Exception{
		if (instance==null) instance= new PropertiesReader(path);
		return instance;
	}
	
	public String getParam(String paramName) throws Exception{
		long secondsFromLastRead=(System.currentTimeMillis()-lastAccessed)/1000;
		if(secondsFromLastRead>=refreshTime){
			load();
			lastAccessed=System.currentTimeMillis();
		}
		return props.getProperty(paramName).trim();
	}
	
	
	private void load()throws Exception{
		InputStream is=null;
		try{
//		logger.debug("******************* LOADING PROPERTIES FROM "+path);
		String propertiesFilePath = path;
		logger.debug("******************* LOADING propertiesFilePath "+propertiesFilePath);
		is= new FileInputStream (propertiesFilePath);
		
		props.load(is);			
		is.close ();
		logger.debug("loaded properties file :" +props.toString());
		}catch(Exception e){
			throw e;
		}finally{is.close();}		
	}
}
