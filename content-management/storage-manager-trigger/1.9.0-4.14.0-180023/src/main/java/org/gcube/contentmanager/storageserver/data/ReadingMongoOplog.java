package org.gcube.contentmanager.storageserver.data;


import java.util.Arrays;
import java.util.List;
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
import org.bson.types.BSONTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadingMongoOplog extends Thread{
	
	public static final String DBNAME="remotefs";
	public static final String LOCAL_COLLECTION="oplog.rs";
	public static final String LOCAL_DB="local";
	protected static ReadPreference READ_PREFERENCE=ReadPreference.secondaryPreferred();
	final static Logger logger=LoggerFactory.getLogger(ReadingMongoOplog.class);
	private ServerAddress[] server;
	private MongoClient mongoClient;
	private DB local;
	private DBCollection oplog;
	private CubbyHole c1;
	private CubbyHole c2;
	private String user;
	private String password;
	private int number;
	private List<String> srvs;
	
	public ReadingMongoOplog(List<String> srvs, CubbyHole c1,  CubbyHole c2, int numberT){
		this.c1=c1;
		this.c2=c2;
		this.number=numberT;
		this.srvs=srvs;
		setupServerAddress(srvs);
        initBackend();
	}


    public ReadingMongoOplog(List<String> srvs, String user,
			String password, CubbyHole c1, CubbyHole c2, int numberT) {
    	this.c1=c1;
    	this.c2=c2;
		this.number=numberT;
		this.user=user;
		this.password=password;
		setupServerAddress(srvs);
        initBackend();
	}

	public void run() {
		// check oplog collection
        DBCursor lastCursor = oplog.find().sort(new BasicDBObject("$natural", -1)).limit(1);
        if (!lastCursor.hasNext()) {
            logger.error("no oplog!");
            return;
        }
        DBObject last = lastCursor.next();
        BSONTimestamp ts = (BSONTimestamp) last.get("ts");
        while (true) {
            logger.debug("Sniffing is starting at ts: " + ts);
            DBCursor cursor = oplog.find(new BasicDBObject("ts", new BasicDBObject("$gt", ts)));
            cursor.addOption(Bytes.QUERYOPTION_TAILABLE);
            cursor.addOption(Bytes.QUERYOPTION_AWAITDATA);
            cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
            while (cursor.hasNext()) {
            	try{
		                DBObject x = cursor.next();
		                logger.debug("oplog current object: "+x);
		                ts = (BSONTimestamp) x.get("ts");
		                String ns=(String)x.get("ns");
		                // check if discard or process the current DB record       
		                if((x.get("o2") != null) || (ns.equalsIgnoreCase(DBNAME+".fs.files"))){
		        		    if(x.containsField("o")){ // add check on operation type
		        		    	// c1 buffer for suer accounting 		    	
		        		    	c1.put(x);
		        		    	// c2 buffer for folder accounting (TODO)	
		        		    	if(c2 !=null)
		        		    		c2.put(x);
		        		    	//  	parser.runWithoutThread(x);
		        		    	
		        		    }else{
		   		        	 	logger.debug("operation is not accounted");
			   		        }
			   		    }else{
			   		    	logger.debug("object discarded ");
			   		    }
            	}catch(Exception e){
            		logger.error("Exception throws: "+e.getMessage());
            		e.printStackTrace();
            	}
                logger.debug("...waiting new object... ");
            }
            logger.warn("out of the cycle ");
        }
    }

//	@SuppressWarnings("deprecation")
	private void initBackend() {
		MongoClientOptions options=MongoClientOptions.builder().readPreference(READ_PREFERENCE).build();
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean auth =false;
		logger.info("try to auth ");
		if(user!=null && password !=null){
			MongoCredential credential = MongoCredential.createMongoCRCredential(user, "admin", password.toCharArray());
			logger.debug("try to connect to mongo with authentication... ");
			mongoClient = new MongoClient(Arrays.asList(server), Arrays.asList(credential), options);//"146.48.123.71"
			
		}else{
			logger.debug("try to connect to mongo... ");
			mongoClient = new MongoClient(Arrays.asList(server));
		}
		logger.debug("authenticated. ");
		logger.debug("try to connect to local db...");
        local = mongoClient.getDB(LOCAL_DB);
        logger.debug("db connected ");
        oplog = local.getCollection(LOCAL_COLLECTION);
	}

	private void setupServerAddress(List<String> srvs) {
			if(srvs.size() > 0){
				server=new ServerAddress[srvs.size()];
				int i=0;
				for(String s : srvs){
					server[i]=new ServerAddress(s);
					i++;
				}
			}else{
				logger.error("MongoDB server not set. Please set one or more servers");
				throw new RuntimeException("MongoDB server not set. Please set one or more servers");
			}
	}

}
 
