/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class PersistenceMongoDB extends PersistenceBackend {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistenceMongoDB.class);
	
	public static final String URL_PROPERTY_KEY = "URL";
	public static final String USERNAME_PROPERTY_KEY = "username";
	public static final String PASSWORD_PROPERTY_KEY = "password";
	public static final String DB_NAME = "dbName";
	public static final String COLLECTION_NAME = "collectionName";
	
	protected String collectionName;
	
	protected static final ReadPreference READ_PREFERENCE;
	protected static final MongoClientOptions MONGO_CLIENT_OPTIONS;
	
	static {
		READ_PREFERENCE = ReadPreference.secondaryPreferred();
		
		List<? extends Codec<?>> additionalCodecs = discoverAdditionalCodecs();
		CodecRegistry mongoDefaultCR = MongoClient.getDefaultCodecRegistry();
		@SuppressWarnings("rawtypes")
		Codec[] codecArray = new Codec[additionalCodecs.size()];
		codecArray = additionalCodecs.toArray(codecArray);
		CodecRegistry cr = addCoded(mongoDefaultCR, codecArray);
		MONGO_CLIENT_OPTIONS = createMongoClientOptions(cr);
		
	}
	
	protected PersistenceBackendConfiguration configuration;
	protected MongoClient mongoClient;
	protected MongoClientOptions mongoClientOptions;
	protected MongoDatabase mongoDatabase;
	
	public static CodecRegistry addCoded(CodecRegistry cr, Codec<?>[] codecs){
		CodecRegistry crFromCodes = CodecRegistries.fromCodecs(codecs);
		return CodecRegistries.fromRegistries(cr, crFromCodes);
	}
	
	protected static MongoClientOptions createMongoClientOptions(CodecRegistry cr){
		/*
		mongoClientOptions = MongoClientOptions.builder().
				codecRegistry(cr).connectionsPerHost(10).connectTimeout(30000).
				readPreference(READ_PREFERENCE).build();
		*/
		return MongoClientOptions.builder().codecRegistry(cr).build();
	}
	
	public PersistenceMongoDB() throws Exception {
		super();
		mongoClientOptions = MONGO_CLIENT_OPTIONS;
	}
	
	@Override
	public void openConnection() throws Exception {}
	
	@Override
	protected void closeConnection() throws Exception {}
	
	
	@Override
	public void close() throws Exception {
		mongoClient.close();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<? extends Codec<?>> discoverAdditionalCodecs(){
		List codecs = new ArrayList();
		
		Set<Class<? extends Enum>> enumClasses = new HashSet<>();
		
		Reflections recordClassesReflections = new Reflections();    
		Set<Class<? extends Record>> recordClasses = recordClassesReflections.getSubTypesOf(Record.class);
		for(Class<? extends Record> recordClass : recordClasses){
			Class<?> auxClass = recordClass;
			while(auxClass!=null){
				Class<?>[] classes = auxClass.getClasses();
				for(Class<?> clz : classes){
					if(clz.isEnum()){
						if(!enumClasses.contains((Class<? extends Enum>) clz)){
							logger.trace("Found Enum {}", clz);
							enumClasses.add((Class<? extends Enum>) clz);
						}
					}
				}
				auxClass = auxClass.getSuperclass();
			}
			
		}
		logger.trace("Found Enums : {}",enumClasses);
		
		for(Class<? extends Enum> enumClass : enumClasses){
			EnumCodec<? extends Enum> enumCodec = new EnumCodec<>(enumClass);
			codecs.add(enumCodec);
		}
		
		GenericCodec<URI> uriCodec = new GenericCodec<>(URI.class);
		codecs.add(uriCodec);
		return (List<? extends Codec<?>>) codecs;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception {
		logger.debug("Preparing Connection for {}", this.getClass().getSimpleName());
		this.configuration = configuration;
		
		String completeURL = configuration.getProperty(URL_PROPERTY_KEY);
		String username = configuration.getProperty(USERNAME_PROPERTY_KEY);
		String password = configuration.getProperty(PASSWORD_PROPERTY_KEY);
		String dbName = configuration.getProperty(DB_NAME);
		
		MongoCredential credential = MongoCredential.createScramSha1Credential(
				username, dbName, password.toCharArray());
		
		String[] urls = completeURL.split(";");
		List<ServerAddress> serverAddresses = new ArrayList<>();
		for(String url : urls){
			url = url.startsWith("http://") ? url.replace("http://", "") : url;
			url = url.startsWith("https://") ? url.replace("https://", "") : url;
			ServerAddress serverAddress = new ServerAddress(url);
			serverAddresses.add(serverAddress);
		}
		
		mongoClient = new MongoClient(serverAddresses, 
				Arrays.asList(credential), mongoClientOptions); //, MONGO_CLIENT_OPTIONS);
		
		mongoDatabase = mongoClient.getDatabase(dbName);
		
		collectionName = configuration.getProperty(COLLECTION_NAME);
		if(mongoDatabase.getCollection(collectionName)==null){
			mongoDatabase.createCollection(collectionName);
		}
	}
	
	protected void createItem(Document document) throws Exception {
		mongoDatabase.getCollection(collectionName).insertOne(document);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static List<Codec> findMissingCodecs(CodecRegistry cr, Record record){
		List<Codec> codecs = new ArrayList<>();
		Collection<? extends Serializable> properties = record.getResourceProperties().values();
		for(Serializable value : properties){
			try {
				try {
					cr.get(value.getClass());
					logger.trace("Codec found for {} : {}", value.getClass(), value);
				}catch(CodecConfigurationException cce){
					logger.trace("No Codec found for {} : {}", value.getClass(), value);
					if(value.getClass().isEnum()){
						EnumCodec<? extends Enum> enumCodec = 
								new EnumCodec<>((Class<? extends Enum>) value.getClass());
						codecs.add(enumCodec);
						logger.trace("Adding {} to manage {} : {}", enumCodec, value.getClass(), value);
					}else{
						GenericCodec genericCodec = new GenericCodec<>(value.getClass());
						try {
							Serializable recreatedValue = genericCodec.getFromString(value.toString());
							if(value instanceof Comparable && recreatedValue instanceof Comparable){
								Comparable valueComparable = (Comparable) value;
								Comparable recreatedValueComparable = (Comparable) recreatedValue;
								if(valueComparable.compareTo(recreatedValueComparable)==0){
									codecs.add(genericCodec);
									logger.trace("Adding {} to manage {} : {}", genericCodec, value.getClass(), value);
								}
							}else{
								if(value.hashCode()==recreatedValue.hashCode()){
									codecs.add(genericCodec);
									logger.trace("Adding {} to manage {} : {}", genericCodec, value.getClass(), value);
								}else{
									String message = String.format("%s != %s", value, recreatedValue);
									throw new Exception(message);
								}
							}
						}catch(Exception e){
							logger.error("{} cannot be used for {} : {}", GenericCodec.class.getSimpleName(), value.getClass(), value, e);
							continue;
						}
					}
				}
			}catch(Exception ex){
				logger.error("Error evaluating if {} can be serialized as bson Object", value, ex);
				continue;
			}
		}
		return codecs;
	}
	
	
	@SuppressWarnings("rawtypes")
	protected void checkSerializability(Record record) throws Exception{
		CodecRegistry cr = mongoClientOptions.getCodecRegistry();
		List<Codec> codecs = findMissingCodecs(cr, record);
		
		if(!codecs.isEmpty()){
			logger.debug("Recreating Mongo CLient to Add Codecs");
			Codec[] codecArray = new Codec[codecs.size()];
			codecArray = codecs.toArray(codecArray);
			CodecRegistry newCR = addCoded(cr, codecArray);
			mongoClientOptions = createMongoClientOptions(newCR);
			mongoClient.close();
			prepareConnection(configuration);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reallyAccount(Record record) throws Exception {
		checkSerializability(record);
		Document document = usageRecordToDocument(record);
		createItem(document);
	}
	
	public static Document usageRecordToDocument(Record record) throws Exception {
		Document document = new Document();
		document.putAll(record.getResourceProperties());
		return document;
	}
	
	protected static Record documentToUsageRecord(Document document) throws Exception {
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		Set<Entry<String, Object>> set = document.entrySet();
		for(Entry<String, Object> entry : set){
			Serializable value = (Serializable) entry.getValue();
			map.put(entry.getKey(), value);
		}
		Record record = RecordUtility.getRecord(map);
		return record;
	}

	@Override
	protected void clean() throws Exception {
		
	}

	@Override
	public boolean isConnectionActive() throws Exception {
		return true;
	}
	
	
	
	
}
