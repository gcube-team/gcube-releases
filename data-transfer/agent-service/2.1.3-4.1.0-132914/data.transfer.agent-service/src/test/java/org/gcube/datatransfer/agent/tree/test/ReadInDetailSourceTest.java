package org.gcube.datatransfer.agent.tree.test;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERuntimeResourceQuery;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.informationsystem.client.eximpl.queries.WSResourceQueryImpl;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class ReadInDetailSourceTest {
	public static String scope="/gcube/devsec";
	@BeforeClass
	public static void setup() throws Exception {
		ScopeProvider.instance.set(scope);
	}

	@Test
	public void process() {
		List<String> gresources= getDetailTreeSource("reader");		

		System.out.println(gresources.size()+"\n\n");
		for(String tmp:gresources)System.out.println(tmp);
	}

	public List<String> getDetailTreeSource(String type){
		List<String> gresources=new ArrayList<String>();
		try{			
			ISClient client = GHNContext.getImplementation(ISClient.class);
			if(client==null){System.out.println("client=null");return null;}
			WSResourceQuery WSquery = client.getQuery(WSResourceQuery.class);

			List<RPDocument> docList = client.execute(WSquery,GCUBEScope.getScope(scope));
			for (RPDocument resource : docList){				
				if(resource.getServiceClass()==null || 
						resource.getServiceClass().compareTo("DataAccess")!=0 ||
						resource.getServiceName()==null || 
						resource.getServiceName().compareTo("tree-manager-service")!=0){
					continue;
				}
				if(!resource.getEndpoint().getAddress().toString().endsWith("reader") &&
						!resource.getEndpoint().getAddress().toString().endsWith("writer")){
					continue;
				}
				if(type!=null){
					if(!resource.getEndpoint().getAddress().toString().endsWith(type))
						continue;
				}
				//we omit the empty read sources
				if(resource.getEndpoint().getAddress().toString().endsWith("reader")){
					String cardinality=getParameter(resource,"Cardinality");
					if(cardinality.startsWith("no_")) ;//we keep it - no cardinality info 
					else{
						int num = Integer.valueOf(cardinality);
						if(num<1)continue;
					}
				}

				String id = resource.getKey().getValue();
				String name=getParameter(resource,"Name");
				//there is no 'Description' in wsResources
				//String description=getParameter(resource,"Description");
				//String treeSource=id+"--"+name+"--"+description;
				String treeSource=id+"--"+name;
				System.out.println("treeSource="+treeSource);

				//treeSource structure: 
				// id--name 
				gresources.add(treeSource);					
			}
		}catch(Exception e){				
			e.printStackTrace();
			return null;
		}
		return gresources;
	}

	public String getParameter(RPDocument resource,String field){
		try{
			List<String> list = resource.evaluate("//"+field);
			if(list==null)return "no_"+field;
			else if(list.size()==0)return "no_"+field;
			else{				
				String wholeName=list.get(0);
				//System.out.println(field+"="+wholeName);
				String name=wholeName.substring(0,wholeName.lastIndexOf("</"));
				name = name.substring(name.lastIndexOf(">")+1);
				return name;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return "no_"+field;
		}
	}
}
