package org.acme;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.util.Map;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.gcube.resources.discovery.client.queries.impl.QueryTemplate;

public class QueryClient {

	  public static void main(String[] args) {
		
		  //a custom query: client entirely exposed to query language
		  Query q1 = new QueryBox("my query");
		  System.out.println(q1);
		  
		  //a custom template query: client provides template and parameters previously gathered
		  Map<String,String> params = params().add("param","query").build();
		  Query q2 = new QueryTemplate("my <param/>",params);
		  System.out.println(q2);
		  
		  //another custom template query: client provides template and parameters in a phased manner
		  QueryTemplate q2bis = new QueryTemplate("my <param/>",params);
		  q2bis.addParameter("param","newquery");
		  System.out.println(q2bis);
		  
		  //a predefined simple query: client adds conditions
		  XQuery q3 = queryFor(GenericResource.class);
		  System.out.println(q3);
		  q3.addCondition("$resource/a/simple/condition/string() eq value");
		  System.out.println(q3);
		  
		  //a predefined simple query: clients customises results
		  XQuery q4 = queryFor(GenericResource.class);
		  q4.setResult("$resource/ID");
		  q4.setResult("$resource/ID");
		  System.out.println(q4);
		  
		  XQuery q5 = queryFor(ServiceInstance.class);
		  q5.addVariable("$other", "$resource/some/prop");
		  System.out.println(q5);
	}
}
