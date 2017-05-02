/**
 * 
 */
package org.gcube.documentstore.records;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.documentstore.exception.InvalidValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class RecordUtility {
	
	private static Logger logger = LoggerFactory.getLogger(RecordUtility.class);
	
	private final static String LINE_FREFIX = "{";
	private final static String LINE_SUFFIX = "}";
	private final static String KEY_VALUE_PAIR_SEPARATOR = ",";
	private final static String KEY_VALUE_LINKER = "=";
	
	protected static Set<Package> recordPackages;
	protected static Map<String, Class<? extends Record>> recordClassesFound;
	protected static Map<String, Class<? extends AggregatedRecord<?,?>>> aggregatedRecordClassesFound;
	protected static Map<Class<? extends Record>, Class<? extends AggregatedRecord<?,?>>> recordAggregationMapping;
	
	private RecordUtility(){}
		
	@SuppressWarnings("unchecked")
	public static void addRecordPackage(Package packageObject) {
		if(recordPackages.contains(packageObject)){
			logger.trace("Package ({}) already scanned", packageObject.getName());
			return;
		}
		
		recordPackages.add(packageObject);
		
		try {
			List<Class<?>> classes = ReflectionUtility.getClassesForPackage(packageObject);
			for(Class<?> clz : classes){
				logger.trace("found a class:{}",clz.getSimpleName());
				if(Record.class.isAssignableFrom(clz)){
					addRecordClass((Class<? extends Record>) clz);
				}
				if(AggregatedRecord.class.isAssignableFrom(clz)){
					addAggregatedRecordClass((Class<? extends AggregatedRecord<?,?>>) clz);
				}
			}
		} catch (ClassNotFoundException e) {
			logger.error("Error discovering classes inside package {}", packageObject.getName(), e);
		}
	}
	
	protected static void addRecordClass(Class<? extends Record> cls){
		if(Modifier.isAbstract(cls.getModifiers())){
			return;
		}
		
		String discoveredRecordType;
		try {
			Record record = cls.newInstance();
			if(record instanceof AggregatedRecord){
				return;
			}
			discoveredRecordType = record.getRecordType();
			
			if(!recordClassesFound.containsKey(discoveredRecordType)){
				logger.trace("Not containsKey discoveredRecordType:{}, cls:{}",discoveredRecordType.toString(),cls.toString());
				recordClassesFound.put(discoveredRecordType, cls);
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Unable to instantiate found {} class ({})", 
					Record.class.getSimpleName(), cls.getSimpleName(), e);
			return;
		}
	}
	
	protected static void addAggregatedRecordClass(Class<? extends AggregatedRecord<?,?>> cls){
		if(Modifier.isAbstract(cls.getModifiers())){
			logger.trace("is abstract cls:{}",cls);
			return;
		}
		
		String discoveredRecordType;
		try {
			AggregatedRecord<?,?> instance = cls.newInstance();
			discoveredRecordType = instance.getRecordType();
			if(!aggregatedRecordClassesFound.containsKey(discoveredRecordType)){
				logger.trace("discoveredRecordType not found"+discoveredRecordType+" with cls:"+cls.getName());
				aggregatedRecordClassesFound.put(discoveredRecordType, cls);
				
				Class<? extends Record> recordClass = instance.getAggregable();
				recordAggregationMapping.put(recordClass, cls);
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("Unable to instantiate found {} class ({})", 
					AggregatedRecord.class.getSimpleName(), cls.getSimpleName(), e);
			return;
		}
	}
	
	/**
	 * @return the recordClassesFound
	 */
	public static Map<String, Class<? extends Record>> getRecordClassesFound() {
		return recordClassesFound;
	}

	/**
	 * @return the aggregatedRecordClassesFound
	 */
	public static Map<String, Class<? extends AggregatedRecord<?,?>>> getAggregatedRecordClassesFound() {
		return aggregatedRecordClassesFound;
	}
	
	static {
		recordPackages = new HashSet<>();
		
		recordClassesFound = new HashMap<>();
		aggregatedRecordClassesFound = new HashMap<>();
		
		recordAggregationMapping = new HashMap<>();
		
		/* Old code using Reflections
		Reflections recordClassesReflections = new Reflections();    
		Set<Class<? extends Record>> recordClasses = recordClassesReflections.getSubTypesOf(Record.class);
		for(Class<? extends Record> cls : recordClasses){
			addRecordClass(cls)
		}
		
		aggregatedRecordClassesFound = new HashMap<>();
		Reflections aggregatedRecordReflections = new Reflections();
		Set<Class<? extends AggregatedRecord>> aggregatedRecordClasses = aggregatedRecordReflections.getSubTypesOf(AggregatedRecord.class);
		for(Class<? extends AggregatedRecord> cls : aggregatedRecordClasses){
			addAggregatedRecordClass(cls);
		}
		*/
		
	}

	public static Class<? extends AggregatedRecord<?,?>> getAggregatedRecordClass(String recordType) throws ClassNotFoundException {
	
		
		if(getAggregatedRecordClassesFound().containsKey(recordType)){
			logger.trace("record type {},getAggregatedRecordClassesFound {}",recordType,getAggregatedRecordClassesFound(),getAggregatedRecordClassesFound().get(recordType));
			return getAggregatedRecordClassesFound().get(recordType);
		}
		logger.error("Unable to find {} class for {}.", 
				AggregatedRecord.class.getSimpleName(), recordType);
		
		
		
		//logger.trace("getAggregatedRecordClass getAggregatedRecordClassesFound:"+getAggregatedRecordClassesFound());
		
		throw new ClassNotFoundException();
	}
	
	public static Class<? extends Record> getRecordClass(String recordType) throws ClassNotFoundException {
		if(recordClassesFound.containsKey(recordType)){
			return recordClassesFound.get(recordType);
		}
		logger.error("Unable to find {} class for {}.", 
				Record.class.getSimpleName(), recordType);
		throw new ClassNotFoundException();
	}
	
	protected static Class<? extends Record> getClass(String recordType, boolean aggregated) throws ClassNotFoundException {
		if(aggregated){
			return RecordUtility.getAggregatedRecordClass(recordType);
		}
		return getRecordClass(recordType);
	}
	
	/* 
	 * IT DOES NOT WORK
	 * @SuppressWarnings("unchecked")
	 * public static Map<String,Serializable> getMapFromString(String serializedMap) throws IOException, ClassNotFoundException {
	 * 	ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedMap.getBytes());
	 * 	ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
	 * 	return ((Map<String,Serializable>) objectInputStream.readObject());
	 * }
	 */
	
	protected static Map<String, ? extends Serializable> getMapFromString(String serializedMap){
		/* Checking line sanity */
    	if(!serializedMap.startsWith(LINE_FREFIX) && !serializedMap.endsWith(LINE_SUFFIX)){
    		return null;
    	}
    	
    	/* Cleaning prefix and suffix to parse line */
    	serializedMap = serializedMap.replace(LINE_FREFIX, "");
    	serializedMap = serializedMap.replace(LINE_SUFFIX, "");
    	
    	Map<String, Serializable> map = new HashMap<String,Serializable>();
    	
        String[] pairs = serializedMap.split(KEY_VALUE_PAIR_SEPARATOR);
        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            pair.trim();
            
            String[] keyValue = pair.split(KEY_VALUE_LINKER);
            String key = keyValue[0].trim();
            Serializable value = keyValue[1].trim();
            map.put(key, value);
            
        }
        
        return map;
	}
	
	protected static final String INVALID = "invalid";
	
	/**
	 * Create a Record from a Map serialized using toString()
	 * @param serializedMap the String representation of Map
	 * @return the Record
	 * @throws Exception if deserialization fails
	 */
	public static Record getRecord(String serializedMap) throws Exception {
		Map<String,? extends Serializable> map = getMapFromString(serializedMap);
		Record record = getRecord(map);
		try {
			record.validate();
		}catch(InvalidValueException e){
			record.setResourceProperty(INVALID, true);
			logger.error("Recovered record is not valid. Anyway, it will be persisted", e);
		}
		return record;
	}

	/**
	 * Create a Record from a Map
	 * @param recordMap the Map
	 * @return the Record
	 * @throws Exception if deserialization fails
	 */
	@SuppressWarnings("unchecked")
	public static Record getRecord(Map<String, ? extends Serializable> recordMap) throws Exception {
		
		String className = (String) recordMap.get(Record.RECORD_TYPE);
		
		/*  
		 * Patch to support accounting records accounted on fallback
		 * with usageRecordType instead recordType property.
		 * 
		 * TODO Remove when all old fallback files has been elaborated
		 */ 
		if(className == null){
			className = (String) recordMap.get("usageRecordType");
			((Map<String, Serializable>) recordMap).put(Record.RECORD_TYPE, className);
		}
		/* END of Patch */
		
		boolean aggregated = false; 
		try {		
			aggregated = (Boolean) recordMap.get(AggregatedRecord.AGGREGATED);
		}catch(Exception e){
			try{
				aggregated = Boolean.parseBoolean((String)recordMap.get(AggregatedRecord.AGGREGATED)); 
			} catch(Exception e1){}
		}
		
		Class<? extends Record> clz = getClass(className, aggregated);
		//logger.debug("Trying to instantiate {}", clz);
		
		Class<?>[] usageRecordArgTypes = { Map.class };
		Constructor<? extends Record> usageRecordConstructor = clz.getDeclaredConstructor(usageRecordArgTypes);
		Object[] usageRecordArguments = {recordMap};
		
		Record record = usageRecordConstructor.newInstance(usageRecordArguments);
		
		//logger.debug("Created {} : {}", Record.class.getSimpleName(), record);
		
		return record;
	}

	
}
