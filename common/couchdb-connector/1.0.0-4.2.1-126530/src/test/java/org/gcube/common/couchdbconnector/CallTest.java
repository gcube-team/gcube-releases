package org.gcube.common.couchdbconnector;

import org.gcube.common.couchdb.connector.HttpCouchClient;
import org.junit.Test;

public class CallTest {

	@Test
	public void get() throws Exception{
		HttpCouchClient httpCouch =new HttpCouchClient("accounting-d4s.d4science.org:5984","authorization", "authz", "EeN3noo3");	
		System.out.println(httpCouch.getDoc("luciol", EntityExt.class));
		System.out.println(httpCouch.getDoc("luciol"));
	}
	
	@Test
	public void getAll() throws Exception{
		HttpCouchClient httpCouch =new HttpCouchClient("accounting-d4s.d4science.org:5984","accounting", "admin", "better_than_nothing");	
		System.out.println(httpCouch.getAllDocs());
	}
	
	@Test
	public void getFiltered() throws Exception{
		HttpCouchClient httpCouch =new HttpCouchClient("accounting-d4s.d4science.org:5984","authorization", "authz", "EeN3noo3");
		System.out.println(httpCouch.getFilteredDocs(EntityExt.class, "gcube", "_by_name","luciole"));
	}

	@Test
	public void put() throws Exception{
		HttpCouchClient httpCouch =new HttpCouchClient("accounting-d4s.d4science.org:5984","authorization", "authz", "EeN3noo3");	
		httpCouch.put(new EntityExt("test1", "luciotest", "stocasso"));
	}
}
