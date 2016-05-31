package org.gcube.datatransfer.agent.library.test.tree;

import java.util.ArrayList;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.agent.library.proxies.Proxies;
import org.junit.Test;


public class TreeBasedTest2 {

	static AgentLibrary library = null;
	static String scope = "/gcube/devsec";
//	static String scope = "/d4science.research-infrastructures.eu/Ecosystem";
	static String transferId = "";

	static String agentAddress ="geoserver-dev.d4science-ii.research-infrastructures.eu";
	static int agentPort=8081;

	static String SOURCE_ID="from_portlet";
	static String remoteAddress = "node6.d.d4science.research-infrastructures.eu";
	//static String remoteAddress="node20.p.d4science.research-infrastructures.eu";
	static int remotePort = 8080;

	@Test
	public void process(){
		try{
			setUp();
			createNewSource(SOURCE_ID,remoteAddress,remotePort);
		//	removeSource("test2_22_5_2013");
		//	getSources();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void setUp() throws Exception{		
		ScopeProvider.instance.set(scope);
		//library = Proxies.transferAgent().at("localhost", 9999).build();
		library = Proxies.transferAgent().at(agentAddress, agentPort).build();
	}

	public static void createNewSource(String sourceId,String address,int port){
		String result = library.createTreeSource(sourceId, address, port);
		System.out.println("createTreeSource result: "+result);
	}

	public static void getSources(){
		try{
			ArrayList<String> result = library.getTreeSources("reader");
			if(result==null){System.out.println("return is null");return;}
			System.out.println("result.size()="+result.size());
			for(String tmp:result)System.out.println(tmp);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void removeSource(String id){
		try{
			String result = library.removeGenericResource(id);
			System.out.println("return is "+result);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
