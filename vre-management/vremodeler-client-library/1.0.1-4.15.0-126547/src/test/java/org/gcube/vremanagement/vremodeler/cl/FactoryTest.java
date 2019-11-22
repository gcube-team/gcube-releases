package org.gcube.vremanagement.vremodeler.cl;

import static org.gcube.vremanagement.vremodel.cl.plugin.AbstractPlugin.factory;
import java.net.URL;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodel.cl.proxy.Factory;
import org.gcube.vremanagement.vremodel.cl.stubs.types.Report;
import org.junit.Test;

public class FactoryTest {

	@Test
	public void create() throws Exception{
		try{
			ScopeProvider.instance.set("/gcube/devsec");
			Factory factory = factory().build();
			System.out.println(factory.createResource());
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void getAllVREs() throws Exception{
		try{
			ScopeProvider.instance.set("/gcube/devsec");
			Factory factory = factory().build();
			List<Report> reports = factory.getAllVREs();
			for (Report report : reports)
				System.out.println("report "+report);
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void getExistingNames() throws Exception{
		try{
			ScopeProvider.instance.set("/gcube/devsec");
			Factory factory = factory().at(new URL("http://localhost:8080")).build();
			for (String name : factory.getExistingNamesVREs())
				System.out.println("name: "+name);
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void removeVRE() throws Exception{
		try{
			ScopeProvider.instance.set("/gcube/devsec");
			Factory factory = factory().at(new URL("http://localhost:8080")).build();
			factory.removeVRE("9894d270-24fe-11e3-ae8b-e4e9fffaadd4");
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
