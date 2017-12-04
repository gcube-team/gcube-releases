package org.gcube.contentmanager.storageserver.store;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.types.BSONTimestamp;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

public class OplogTest {

	private static String[] server={"mongo1-d-d4s.d4science.org","mongo2-d-d4s.d4science.org","mongo3-d-d4s.d4science.org","mongo4-d-d4s.d4science.org"};
//	private static MongoDB mongo;
	private List<String> srvs;
	protected static ReadPreference READ_PREFERENCE=ReadPreference.primary();
	protected static final String DEFAULT_DB_NAME="local";
	private MongoClient mongoClient;
	DB local;
	private DBCollection oplog;
	private String user="oplogger";
	private String password="0pl0gg3r_d3v";

	
//	@BeforeClass
//	public static void init(){
//		initBackend();
//	}
	

	
	@Test
	public void initTest(){
		initBackend();
		DBCursor lastCursor = oplog.find().sort(new BasicDBObject("$natural", -1)).limit(1);
        if (!lastCursor.hasNext()) {
            System.out.println("no oplog!");
            return;
        }
        DBObject last = lastCursor.next();
        BSONTimestamp ts = (BSONTimestamp) last.get("ts");
        DBCursor cursor = oplog.find(new BasicDBObject("ts", new BasicDBObject("$gt", ts)));
        cursor.addOption(Bytes.QUERYOPTION_TAILABLE);
        cursor.addOption(Bytes.QUERYOPTION_AWAITDATA);
        if (cursor.hasNext()) {
            DBObject x = cursor.next();
            System.out.println("oplog current object: "+x);
        }
	}
	
	private void initBackend() {
		List<ServerAddress> srvList=new ArrayList<ServerAddress>();
		for(String srv : server){
			srvList.add(new ServerAddress(srv));
		}
		MongoClientOptions options=MongoClientOptions.builder().readPreference(READ_PREFERENCE).build();
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean auth =false;
			MongoCredential credential = MongoCredential.createMongoCRCredential(user, "admin", password.toCharArray());
			mongoClient = new MongoClient(srvList, Arrays.asList(credential), options);//"146.48.123.71"
        local = mongoClient.getDB("local");
        oplog = local.getCollection("oplog.rs");
	}
}
