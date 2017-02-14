package org.gcube.datatransfer.agent.tree.test;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryTest {
	
	@BeforeClass
	public static void setup() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
	}
	
	@Test
	public void readerQuerys() {
		List<String> sources = new ArrayList<String>();
		
		StatefulQuery q = TServiceFactory.readSource().build();		
		if(q==null){System.out.println("getTreeSources - query=null");return;}

		List<javax.xml.ws.EndpointReference> refs = q.fire();

		if(refs==null){System.out.println("getTreeSources - refs=null");return;}
				
		for(javax.xml.ws.EndpointReference ref:refs){
			System.out.println("ref="+ref.toString());
			//keep only the tree source id
			String refer = ref.toString();
			String[] parts1=refer.split("<ns1:ResourceKey");
			if(parts1.length<2)continue;
			String[] parts2=(parts1[1]).split("</ns1:ResourceKey><");
			if(parts2.length<2)continue;
			
			String sourceId=parts2[0].substring(parts2[0].indexOf("\">")+2);
			sources.add(sourceId);
		}		
		
		System.out.println("getTreeSources(read) - refs.size()="+refs.size()+" - sources.size()="+sources.size());
		int i=0;
		for(String tmp:sources){
			if(i==0){System.out.println("Tree source ids(read):");i=1;}
			System.out.println(tmp);
		}	
	}
	@Test
	public void writerQuery() {
		List<String> sources = new ArrayList<String>();
		
		StatefulQuery q = TServiceFactory.writeSource().build();		
		if(q==null){System.out.println("getTreeSources - query=null");return;}

		List<javax.xml.ws.EndpointReference> refs = q.fire();

		if(refs==null){System.out.println("getTreeSources - refs=null");return;}
				
		for(javax.xml.ws.EndpointReference ref:refs){
			System.out.println("ref="+ref.toString());
			//keep only the tree source id
			String refer = ref.toString();
			String[] parts1=refer.split("<ns1:ResourceKey");
			if(parts1.length<2)continue;
			String[] parts2=(parts1[1]).split("</ns1:ResourceKey><");
			if(parts2.length<2)continue;
			
			String sourceId=parts2[0].substring(parts2[0].indexOf("\">")+2);
			sources.add(sourceId);
		}		
		
		System.out.println("getTreeSources(write) - refs.size()="+refs.size()+" - sources.size()="+sources.size());
		int i=0;
		for(String tmp:sources){
			if(i==0){System.out.println("Tree source ids(write):");i=1;}
			System.out.println(tmp);
		}	
	}
}
