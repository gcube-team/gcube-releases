package org.gcube.data.analysis.tabulardata.statistical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDataSpace;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMObject;
import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMResource;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)

public class ResourceImportTest {

	public String user="fabio.sinibaldi";
	
	public Home home;
	public StatisticalManagerDataSpace smDs;
	public StatisticalManagerFactory factory;
	
	
	Map<String,String> toSerializeValues=new HashMap<String,String>();		
	List<ResourceDescriptorResult> results=new ArrayList<ResourceDescriptorResult>();
	
	
	static{
		Handler.activateProtocol();
	}
	
	@Before
	public void init() throws InternalErrorException, HomeNotFoundException, UserNotFoundException{
		ScopeProvider.instance.set("/gcube/devsec");
		home=HomeLibrary.getHomeManagerFactory().getHomeManager().getHome(user);
		smDs=Common.getSMDataSpace();
		factory=Common.getSMFactory();
	}
		
//	@Test
//	public void getMap() throws Exception{
//
//		
//		String mapUrl="smp://data.gcube.org/NFmD8IOnxT4yRTQ03/0NORx/zEOdVEFRGmbP5+HKCzc=";
//		for(Entry<String,SMResource> res:Common.asMap(new SMObject(mapUrl)).entrySet()){
//			Common.handleSMResource(res.getValue(), results, toSerializeValues, null, false, user, home);
//		}
//	}
	
	
	@Test 
	public void getResource() throws WorkerException{
		String resId="4f846e59-5465-4247-9d96-c12db60b224d";
		for(SMResource res:smDs.getResources(user,null)){
			if(res.resourceId().equals(resId))
				Common.handleSMResource(res, results, toSerializeValues, null, false, user, home);
		}
		
	}
	
	@After
	public void print(){
		System.out.println("***** RESULTS ");
		System.out.println(results);
		System.out.println("***** TO SERIALIZE VALUES");
		System.out.println(toSerializeValues);
	}
	
}
