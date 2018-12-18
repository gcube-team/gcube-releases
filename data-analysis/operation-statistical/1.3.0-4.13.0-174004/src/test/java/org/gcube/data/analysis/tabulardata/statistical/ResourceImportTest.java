package org.gcube.data.analysis.tabulardata.statistical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)

public class ResourceImportTest {

	public String user = "fabio.sinibaldi";

	Map<String, String> toSerializeValues = new HashMap<String, String>();
	List<ResourceDescriptorResult> results = new ArrayList<ResourceDescriptorResult>();

	static {
		Handler.activateProtocol();
	}

	@Before
	public void init() throws Exception {
		TokenSetter.set("/gcube/devNext/NextNext");
	}

	// @Test
	// public void getMap() throws Exception{
	//
	//
	// String
	// mapUrl="smp://data.gcube.org/NFmD8IOnxT4yRTQ03/0NORx/zEOdVEFRGmbP5+HKCzc=";
	// for(Entry<String,SMResource> res:Common.asMap(new
	// SMObject(mapUrl)).entrySet()){
	// Common.handleSMResource(res.getValue(), results, toSerializeValues, null,
	// false, user, home);
	// }
	// }

	@Test
	public void getResource() throws WorkerException {
		// String resId="4f846e59-5465-4247-9d96-c12db60b224d";
		// for(SMResource res:smDs.getResources(user,null)){
		// if(res.resourceId().equals(resId))
		// Common.handleSMResource(res, results, toSerializeValues, null, false,
		// user, home);
		// }

	}

	@After
	public void print() {
		System.out.println("***** RESULTS ");
		System.out.println(results);
		System.out.println("***** TO SERIALIZE VALUES");
		System.out.println(toSerializeValues);
	}

}
