/**
 * 
 */
package org.gcube.documentstore.records;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@SuppressWarnings({ "rawtypes" })
public class RecordUtility {
	
	private static Logger logger = LoggerFactory.getLogger(RecordUtility.class);
	
	private final static String LINE_FREFIX = "{";
	private final static String LINE_SUFFIX = "}";
	private final static String KEY_VALUE_PAIR_SEPARATOR = ",";
	private final static String KEY_VALUE_LINKER = "=";
	
	private RecordUtility(){}
	
	protected static Map<String, Class<? extends Record>> recordClassesFound;
	
	protected static Map<String, Class<? extends AggregatedRecord>> aggregatedRecordClassesFound;
	
	/**
	 * @return the recordClassesFound
	 */
	public static Map<String, Class<? extends Record>> getRecordClassesFound() {
		return recordClassesFound;
	}

	/**
	 * @return the aggregatedRecordClassesFound
	 */
	public static Map<String, Class<? extends AggregatedRecord>> getAggregatedRecordClassesFound() {
		return aggregatedRecordClassesFound;
	}
	
	static {
		recordClassesFound = new HashMap<>();
		aggregatedRecordClassesFound = new HashMap<>();
		
		Reflections recordClassesReflections = new Reflections();    
		Set<Class<? extends Record>> recordClasses = recordClassesReflections.getSubTypesOf(Record.class);
		for(Class<? extends Record> cls : recordClasses){
			if(Modifier.isAbstract(cls.getModifiers())){
				continue;
			}
			
			String discoveredRecordType;
			try {
				Record record = cls.newInstance();
				if(record instanceof AggregatedRecord){
					continue;
				}
				discoveredRecordType = record.getRecordType();
				
				if(!recordClassesFound.containsKey(discoveredRecordType)){
					recordClassesFound.put(discoveredRecordType, cls);
				}
				
			} catch (InstantiationException | IllegalAccessException e) {
				continue;
			}
		}
		
		aggregatedRecordClassesFound = new HashMap<>();
		Reflections aggregatedRecordReflections = new Reflections();
		Set<Class<? extends AggregatedRecord>> aggregatedRecordClasses = aggregatedRecordReflections.getSubTypesOf(AggregatedRecord.class);
		for(Class<? extends AggregatedRecord> cls : aggregatedRecordClasses){
			if(Modifier.isAbstract(cls.getModifiers())){
				continue;
			}
			
			String discoveredRecordType;
			try {
				discoveredRecordType = cls.newInstance().getRecordType();
				if(!aggregatedRecordClassesFound.containsKey(discoveredRecordType)){
					aggregatedRecordClassesFound.put(discoveredRecordType, cls);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("Unable to instantiate found {} class ({})", 
						AggregatedRecord.class.getSimpleName(), cls.getSimpleName(), e);
				continue;
			}
		}

	}

	public static Class<? extends AggregatedRecord> getAggregatedRecordClass(String recordType) throws ClassNotFoundException {
		if(getAggregatedRecordClassesFound().containsKey(recordType)){
			return getAggregatedRecordClassesFound().get(recordType);
		}
		logger.error("Unable to find {} class for {}.", 
				AggregatedRecord.class.getSimpleName(), recordType);
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
	
	/**
	 * Create a Record from a Map serialized using toString()
	 * @param serializedMap the String representation of Map
	 * @return the Record
	 * @throws Exception if deserialization fails
	 */
	public static Record getRecord(String serializedMap) throws Exception {
		Map<String,? extends Serializable> map = getMapFromString(serializedMap);
		return getRecord(map);
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
		logger.debug("Trying to instantiate {}", clz);
		
		Class[] usageRecordArgTypes = { Map.class };
		Constructor<? extends Record> usageRecordConstructor = clz.getDeclaredConstructor(usageRecordArgTypes);
		Object[] usageRecordArguments = {recordMap};
		
		Record record = usageRecordConstructor.newInstance(usageRecordArguments);
		
		logger.debug("Created {} : {}", Record.class.getSimpleName(), record);
		
		return record;
	}

	
}
