package org.gcube.contentmanager.storageserver.store;

import org.junit.BeforeClass;
import org.junit.Test;

public class MongoDBTest {

	private static String[] server={"mongo1-d-d4s.d4science.org","mongo2-d-d4s.d4science.org","mongo3-d-d4s.d4science.org"};
	private static MongoDB mongo;
	
	@BeforeClass
	public static void init(){
		mongo=new MongoDB(server, "devUser", "d3v_u534");
//		mongo=new MongoDB(server, "oplogger", "0pl0gg3r_d3v");
	}
	
//	@Test
	public void update(){
		StorageStatusObject ssr=new StorageStatusObject("test.consumer", 100, 1);
		mongo.updateUserVolume(ssr, "UPLOAD");
	}
	
	@Test
	public void put(){
		new StorageStatusOperationManager(mongo.getStorageStatusCollection()).putSSRecord( "test.consumer2", 100, 1);
	}

}
