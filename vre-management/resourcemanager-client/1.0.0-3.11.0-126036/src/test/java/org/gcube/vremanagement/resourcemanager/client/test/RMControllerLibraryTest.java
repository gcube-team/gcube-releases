package org.gcube.vremanagement.resourcemanager.client.test;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.resourcemanager.client.RMControllerLibrary;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidOptionsException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesCreationException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesRemovalException;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.CreateScopeParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.OptionsParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ScopeOption;
import org.gcube.vremanagement.resourcemanager.client.proxies.Proxies;
import org.junit.Before;
import org.junit.Test;

public class RMControllerLibraryTest {

	public static RMControllerLibrary library=null;
	protected static String[] optionNames = new String[] {"creator", "designer", "endTime", "startTime", 
		"description", "displayName", "securityenabled"};
	public static String scope="/gcube/devsec/devTest";

	
//	@Before
	public void initialize(){
		ScopeProvider.instance.set("/gcube/devsec");
		library=Proxies.controllerService().at("node13.d.d4science.research-infrastructures.eu", 8080).withTimeout(1, TimeUnit.MINUTES).build();
	}

//	@Test
	public  void createScopeTest() throws ResourcesCreationException, InvalidScopeException, InvalidOptionsException{
		OptionsParameters options = new OptionsParameters();
		ArrayList<ScopeOption> scopeOptionList = new ArrayList<ScopeOption>();
		ScopeOption option=new ScopeOption();
		option.name="creator";
		option.value="roberto.cirillo";
		scopeOptionList.add(option);
		option=new ScopeOption();
		option.name="description";
		option.value="resource-manager-client component: junit test";
		scopeOptionList.add(option);
		option=new ScopeOption();
		option.name="displayName";
		option.value="/gcube/devsec/devTest";
		scopeOptionList.add(option);
		options.scopeOptionList=scopeOptionList;
		CreateScopeParameters create=new CreateScopeParameters();
		create.optionParameters=options;
		create.serviceMap="";
		create.targetScope=scope;
		library.createScope(create);
	}
	
//	@Test
	public void ChangeScopeOptionsTest() throws InvalidScopeException, InvalidOptionsException{
		ArrayList<ScopeOption> scopeOptionList = new ArrayList<ScopeOption>();
		ScopeOption option=new ScopeOption();
		option.name="creator";
		option.value="roberto.cirillo";
		scopeOptionList.add(option);
		option=new ScopeOption();
		option.name="description";
		option.value="resource-manager-client component: junit test: changeOptions test";
		scopeOptionList.add(option);
		scopeOptionList.add(option);
		OptionsParameters options = new OptionsParameters();
		options.scopeOptionList=scopeOptionList;
		options.targetScope=scope;
		library.changeScopeOptions(options);
	}
	
//	@Test
	public void disposeScopeTest() throws ResourcesRemovalException, InvalidScopeException {
		String report=library.disposeScope("/gcube/devsec/devTest");
		System.out.println("report for dispose scope "+scope+"\n "+report);
		assertNotNull(report);
	}


}
