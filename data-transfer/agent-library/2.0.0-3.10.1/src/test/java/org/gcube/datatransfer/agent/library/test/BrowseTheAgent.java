package org.gcube.datatransfer.agent.library.test;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.transferAgent;

import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.objs.LocalSource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowseTheAgent {
	Logger logger = LoggerFactory.getLogger(this.getClass().toString());
	static String path = null;
	AgentLibrary library = null;


	@Test
	public void browseTheAgent(){
		AgentLibrary library = null;
		ScopeProvider.instance.set("/gcube/devsec");
		try {
			library =  transferAgent().at("geoserver-dev.d4science-ii.research-infrastructures.eu", 8081).build();

		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			path="";
			print(library.getLocalSources(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void print(List<LocalSource> list){
		for(LocalSource path : list){
			if(path.isDirectory()){
				System.out.println("Dir: "+path.getPath());
			}
			else{
				System.out.println("File: "+path.getPath()+ " - size="+path.getSize());
			}		
		}
	}
}
