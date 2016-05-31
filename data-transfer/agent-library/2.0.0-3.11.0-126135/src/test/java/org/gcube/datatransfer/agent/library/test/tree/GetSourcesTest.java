package org.gcube.datatransfer.agent.library.test.tree;


import java.util.ArrayList;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.tml.proxies.BindRequest;
import org.gcube.data.tml.proxies.Binding;
import org.gcube.data.tml.proxies.TBinder;
import org.gcube.data.tml.proxies.TReader;
import org.gcube.data.tml.proxies.TServiceFactory;
import org.gcube.data.tml.proxies.TWriter;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.trees.patterns.ManyPattern;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.agent.library.proxies.Proxies;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetSourcesTest {


	static Pattern pattern = Patterns.any(); 
	
	static AgentLibrary library = null;
	static String agentAddress ="geoserver-dev.d4science-ii.research-infrastructures.eu";
	static int agentPort=8081;
	static String scope = "/gcube/devsec";
	
	@BeforeClass
	public static void setup() throws Exception {
		ScopeProvider.instance.set(scope);
		System.out.println(scope);		
		library = Proxies.transferAgent().at(agentAddress, agentPort).build();
	}

	@Test
	public void wholeProcess() throws Exception {
		getSources("writer");
	}

	public static void getSources(String type) {
		try {
			ScopeProvider.instance.set(scope);
			ArrayList<String> sources = library.getTreeSources(type);
			for(String tmp:sources)System.out.println(tmp);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

