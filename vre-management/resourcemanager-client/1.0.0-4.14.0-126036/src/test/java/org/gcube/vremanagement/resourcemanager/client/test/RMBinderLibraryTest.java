package org.gcube.vremanagement.resourcemanager.client.test;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.resourcemanager.client.RMBinderLibrary;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesCreationException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesRemovalException;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.AddResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.PackageItem;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.RemoveResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ResourceItem;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.ResourceList;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.SoftwareList;
import org.gcube.vremanagement.resourcemanager.client.proxies.Proxies;
import org.junit.Before;
import org.junit.Test;

public class RMBinderLibraryTest {
	
	public static RMBinderLibrary library=null;
	public static String rmHost="node13.d.d4science.research-infrastructures.eu";
	public static String ghnHost="node12.d.d4science.research-infrastructures.eu:8080";
	public static String scope="/gcube/devsec";
	public static ArrayList<PackageItem> packList;
	public static ResourceItem resource;
	public static ArrayList<String> ghnList;
	
//	@Before
	public void initialize(){
		ScopeProvider.instance.set("/gcube/devsec");
		library=Proxies.binderService()/*.at("node13.d.d4science.research-infrastructures.eu", 8080).withTimeout(1, TimeUnit.MINUTES)*/.build();
		resource=new ResourceItem();
		resource.id="de099260-9f43-11e1-ae32-9a6b727ba3fb";
		resource.type="Service";
		packList=new ArrayList<PackageItem>();
		PackageItem  pack= new PackageItem();
		pack.serviceClass="DataTransfer";
		pack.serviceName="agent-service";
		pack.serviceVersion="1.0.0";
		pack.packageName="agent-service";
		pack.packageVersion="2.0.0-SNAPSHOT";
		pack.targetGHNName=ghnHost;
		packList.add(pack);

	}

//	@Test
	public void addResourceTest() throws ResourcesCreationException, InvalidScopeException{
		AddResourcesParameters params=null;
		params=new AddResourcesParameters();
		ResourceList resourceList=new ResourceList();
		ArrayList<ResourceItem> resources=new ArrayList<ResourceItem>();
		resources.add(resource);
		resourceList.resource=resources;
		params.resources=resourceList;
		SoftwareList sl=new SoftwareList();
		sl.software=packList;
		ghnList=new ArrayList<String>();
		ghnList.add(ghnHost);	
		sl.suggestedTargetGHNNames=ghnList;
		params.softwareList=sl;
		params.targetScope=scope;
		String result=library.addResources(params);
		assertNotNull(result);
		
	}
	
//	@Test
	public void removeResourceTest() throws ResourcesRemovalException, InvalidScopeException {
		RemoveResourcesParameters params= new RemoveResourcesParameters();
		ResourceList resourceList=new ResourceList();
		ArrayList<ResourceItem> resources=new ArrayList<ResourceItem>();
		resources.add(resource);
		resourceList.resource=resources;
		params.resources=resourceList;
		ghnList=new ArrayList<String>();
		ghnList.add(ghnHost);
		SoftwareList sl=new SoftwareList();
		sl.software=packList;
		sl.suggestedTargetGHNNames=ghnList;
		params.softwareList=sl;
		params.targetScope=scope;
		String result=library.removeResources(params);
		assertNotNull(result);
	}

}
