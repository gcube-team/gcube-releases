package org.gcube.resource.management.quota;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

import org.gcube.resource.management.quota.library.QuotaList;
import org.gcube.resource.management.quota.library.quotalist.AccessType;
import org.gcube.resource.management.quota.library.quotalist.CallerType;
import org.gcube.resource.management.quota.library.quotalist.Quota;
import org.gcube.resource.management.quota.library.quotalist.ServiceQuota;
import org.gcube.resource.management.quota.library.quotalist.StorageQuota;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class QuotaManagerTest extends JerseyTest{
/*
	@Override
	protected Application configure() {
		AbstractBinder binder = new AbstractBinder() {
			@Override
			protected void configure() {
			
				
			}
		};
		ResourceConfig config = new ResourceConfig(QuotaManager.class);
		config.register(binder);
		return config;
	}

*/
	List<Quota> quoteList;

	QuotaList retrievedQuota;

	@Before
	public void reset(){
		retrievedQuota = null;
		quoteList = null;
	}
	
	@Test
	public void add() throws Exception{
		quoteList = new ArrayList<Quota>();
		quoteList.add(new ServiceQuota("/gcube","alessandro.pieve",CallerType.USER,TimeInterval.DAILY,100.0,AccessType.ACCESS));
		quoteList.add(new StorageQuota("/gcube","lucio.lelii",CallerType.USER,TimeInterval.DAILY,100.0));
		Response response = target("quotaManager/insert").request()
				.post(Entity.xml(new QuotaList(quoteList)), Response.class);
		Assert.assertEquals(200, response.getStatus());
	}
		
	@Test
	public void deleteOne() throws Exception{
	}
	
}
