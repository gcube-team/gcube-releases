package org.gcube.dataanalysis.dataminer.poolmanager.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CheckMethod {
	
	private Logger logger;

	public CheckMethod() 
	{
		this.logger = LoggerFactory.getLogger(CheckMethod.class);
	}
	
	public boolean checkMethod(String machine, String token) throws Exception {
		try {
			this.logger.debug("Checking method for machine "+machine);
			this.logger.debug("By using tocken "+token);
			System.out.println("Machine: " + machine);
//			String getCapabilitesRequest = new String();
//			String getCapabilitesResponse = new String();
			System.out.println("   Token: " + token);
			String request = "http://" + machine
					+ "/wps/WebProcessingService?Request=GetCapabilities&Service=WPS&gcube-token=" + token;
			String response = machine + "___" + token + ".xml";
//			getCapabilitesRequest = request;
//			getCapabilitesResponse = response;
			String baseDescriptionRequest = "http://" + machine
					+ "/wps/WebProcessingService?Request=DescribeProcess&Service=WPS&Version=1.0.0" + "&gcube-token="
					+ token + "&Identifier=";
			URL requestURL = new URL(request);
			this.logger.debug("Request url "+requestURL.toString());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(requestURL.openStream()));
			FileWriter fileWriter = new FileWriter(response);
			String line;
			boolean flag = true;
			this.logger.debug("Writing file");
			while (flag && (line = bufferedReader.readLine()) != null) {
				this.logger.debug(line);
				fileWriter.write(line);
				fileWriter.write(System.lineSeparator());
				
				if (line.contains("ows:Identifier")) 
				{
					this.logger.debug("Identifier found");
					String operatorName = line.substring(line.indexOf(">") + 1);
					operatorName = operatorName.substring(0, operatorName.indexOf("<"));
					this.logger.debug("Operator "+operatorName);
					System.out.println("      " + operatorName);
					URL innerRequestURL = new URL(baseDescriptionRequest + operatorName);
					BufferedReader innerBufferedReader = new BufferedReader(
							new InputStreamReader(innerRequestURL.openStream()));
					String innerLine = innerBufferedReader.readLine();
					this.logger.debug("Inner line "+innerLine);
					boolean innerFlag = true;
					while (innerFlag && (innerLine = innerBufferedReader.readLine()) != null) 
					{
						if (innerLine.contains("ows:Abstract")) 
						{
							this.logger.debug("Abstract found");
							String operatorDescription = innerLine.substring(innerLine.indexOf(">") + 1);
							operatorDescription = operatorDescription.substring(0, operatorDescription.indexOf("<"));
							this.logger.debug("Operator descriptor "+operatorDescription);
							System.out.println("         " + operatorDescription);
							innerFlag = false;
						} else if (innerLine.contains("ows:ExceptionText")) 
						{
							this.logger.debug("Exception found");
							System.out.println("         " + "error retrieving operator description");
							innerFlag = false;
							flag = false;
						} else
						{
							innerLine = innerBufferedReader.readLine();
							this.logger.debug("Inner line completed "+innerLine);
						}
					}
				}
			}
			
			this.logger.debug("Operation successful");
			fileWriter.close();
			return true;

		} catch (Exception e) {
			e.getMessage();
			this.logger.error("Error "+e.getMessage(),e);
			return false;
		}
	}
	
	
	
	public abstract boolean algoExists(Algorithm algo/*, String env*/) throws Exception;

	
	public abstract void deleteFiles(Algorithm a/*,String env*/) throws Exception;

	
	public abstract boolean doesExist(String path/*, String env*/) throws Exception;
	

	
	public abstract void copyFromDmToSVN(File a/*,String env*/) throws Exception;
 
 

    
	public static List<String> getFiles(String a){

		String[] array = a.split(",");
		ArrayList<String> list = new ArrayList<>(Arrays.asList(array));
		List<String> ls = new LinkedList<String>();
		
        for (String s: list){
        	ls.add(s.trim());
        }
      
     	return ls;	
	} 
	
	

}
