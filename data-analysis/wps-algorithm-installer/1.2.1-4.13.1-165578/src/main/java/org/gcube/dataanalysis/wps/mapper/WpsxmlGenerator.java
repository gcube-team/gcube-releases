package org.gcube.dataanalysis.wps.mapper;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


public class WpsxmlGenerator {
	
	public static void main (String[] args) throws Exception{
//		String transducerers = "C:/Users/coro/Desktop/WorkFolder/Workspace/EcologicalEngineConfiguration/cfg/transducerers.properties";
//		String transducerers = "C:/Users/coro/Desktop/WorkFolder/Workspace/EcologicalEngineConfiguration/cfg/models.properties";
		String transducerers = "C:/Users/coro/Desktop/WorkFolder/Workspace/EcologicalEngineConfiguration/cfg/nodealgorithms.properties";
		String userp = "C:/Users/coro/Desktop/WorkFolder/Workspace/EcologicalEngineConfiguration/cfg/userperspective.properties";
		String atype = "nodealgorithms"; 
		Properties props = new Properties();
		props.load(new FileReader(new File(transducerers)));
		
		Properties persp= new Properties();
		persp.load(new FileReader(new File(userp)));
		Collection<String> unsorted = (Collection)props.keySet();
		
		List<String> list = new ArrayList<String>(unsorted);
		java.util.Collections.sort(list);
		  
		for (Object algorithm:list)
		{
			String classname = (String) props.getProperty((String)algorithm);
			String found = "OTHER";
			for (Object category:persp.keySet()){
				String algorithms = persp.getProperty((String)category);
				if (algorithms.contains((String)algorithm)){
					found = (String)category;
					break;
				}
			}
			
			String addAlgorithm = "./addAlgorithm.sh  "+((String)algorithm).trim()+" " +found+" "+classname+" /gcube/devsec "+atype+" Y "+"a test algorithm for the alg publisher";
			System.out.println(addAlgorithm);
			//System.out.println("<Property name=\"Algorithm\" active=\"true\">org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers."+algorithm+"</Property>");
		}
	}
	
}
