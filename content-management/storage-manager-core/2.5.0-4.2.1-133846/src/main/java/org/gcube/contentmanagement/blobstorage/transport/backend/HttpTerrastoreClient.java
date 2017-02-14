//package org.gcube.contentmanagement.blobstorage.transport.backend;
//
//
//import java.net.UnknownHostException;
//import java.util.Arrays;
//import java.util.Map;
//
//import org.gcube.contentmanagement.blobstorage.resource.MyFile;
//import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
//
//import com.mongodb.MongoException;
//
//import terrastore.client.TerrastoreClient;
//import terrastore.client.connection.OrderedHostManager;
//import terrastore.client.connection.resteasy.HTTPConnectionFactory;
//
///**
// * Terrastore Transport layer
// * @author Roberto Cirillo (ISTI - CNR)
// *
// */
//public class HttpTerrastoreClient extends TransportManager{
//	
//	private String[] server;
//	private TerrastoreClient client;
//	
//	public HttpTerrastoreClient(String[] server) {
//		client=new TerrastoreClient(new OrderedHostManager(Arrays.asList(server)), new HTTPConnectionFactory());
//	}
//
//	@Override
//	public Object get(MyFile resource, String key, Class<? extends Object> type) {
//		Object ret=null;
//		if((resource.getPathClient()!=null) && (!resource.getPathClient().isEmpty()))
//			ret=client.bucket(resource.getPathClient()).key(key).get(type);
//		else
//			throw new IllegalArgumentException("Local path not found");
//		return ret;
//	}
//
//	@Override
//	public String put(Object resource, String bucket, String key, boolean replaceOption) {
//	//replace option is ignored	
//		client.bucket(bucket).key(key).put(resource);
//		return null;
//	}
//
//	@Override
//	public Map getValues(String bucket, Class<? extends Object> type) {
//		return client.bucket(bucket).values().get(type);
//	}
//
//	@Override
//	public void clearBucket(String bucket) {
//		client.bucket(bucket).clear();
//		
//	}
//
//	@Override
//	public void removeKey(String bucket, String key) {
//		client.bucket(bucket).key(key).remove();
//		
//	}
//
//	@Override
//	public Map getValuesPredicate(String bucket, Class< ? extends Object> type, String predicate) {
//		return client.bucket(bucket).predicate(predicate).get(type);
//		
//	}
//
//	@Override
//	public  void removeDir(String remoteDir){}
//
//	@Override
//	public long getTTL(String pathServer) throws UnknownHostException,
//			MongoException {
//		throw new IllegalArgumentException("This operation is not compatible with this client");
//	}
//
//	@Override
//	public long renewTTL(MyFile resource) throws UnknownHostException,
//			MongoException {
//		throw new IllegalArgumentException("This operation is not compatible with this client");
//}
//	
//	
//}
