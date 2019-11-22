package org.gcube.mongodb.access;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import java.util.LinkedList;
import java.util.List;
/**
 * 
 * @author Roberto Cirillo (ISTI-CNR) 2017
 *
 */
@Deprecated
public class MongoConnector {
	
	    private final MongoClient mongoClient;
	    private MongoDatabase db;

	    public MongoConnector(String host, int port)
	    {
	        this.mongoClient = new MongoClient(host, port);
	    }

	    public MongoConnector(String host, int port, String username, String password)
	    {
	        String textUri = "mongodb://"+username+":"+password+"@"+host+":"+port;
	        MongoClientURI uri = new MongoClientURI(textUri);
	        this.mongoClient = new MongoClient(uri);
	    }

	    public boolean connectToDB(String dbName)
	    {
	        this.db = mongoClient.getDatabase(dbName);
	        return this.db != null;    
	    }

	    public List<String> listAllDB()
	    {
	        List<String> ret = new LinkedList<>();
	        MongoIterable<String> x = mongoClient.listDatabaseNames();
	        for(String t : x)
	        {
	            ret.add(t.toString());
	        }
	        return ret;
	    }
}

