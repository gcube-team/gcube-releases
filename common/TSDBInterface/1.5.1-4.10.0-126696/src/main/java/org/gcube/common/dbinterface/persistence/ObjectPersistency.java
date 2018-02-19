package org.gcube.common.dbinterface.persistence;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.gcube.common.dbinterface.CastObject;
import org.gcube.common.dbinterface.ColumnDefinition;
import org.gcube.common.dbinterface.Condition;
import org.gcube.common.dbinterface.TableAlreadyExistsException;
import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.conditions.ANDCondition;
import org.gcube.common.dbinterface.conditions.OperatorCondition;
import org.gcube.common.dbinterface.persistence.annotations.AnnotationNotDefinedException;
import org.gcube.common.dbinterface.persistence.annotations.FieldDefinition;
import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.CreateTable;
import org.gcube.common.dbinterface.queries.Delete;
import org.gcube.common.dbinterface.queries.DropTable;
import org.gcube.common.dbinterface.queries.Insert;
import org.gcube.common.dbinterface.queries.Select;
import org.gcube.common.dbinterface.queries.Update;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.types.Type;
import org.gcube.common.dbinterface.types.Type.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * @author Lucio Lelii
 *
 * @param <T>
 */
public class ObjectPersistency<T> {

	@SuppressWarnings("rawtypes")
	public static HashMap<String, ObjectPersistency> persistencyMapping = new HashMap<String, ObjectPersistency>();
	
	private static final Logger logger = LoggerFactory.getLogger(ObjectPersistency.class);
	
	private static final String FIELD_PREFIX="ifield";
	//private static final String IDENTIFIERS_REF_FIELD_PREFIX="ref";
	private Class<T> _clazz;
	private SimpleTable table;
	
	private List<PersistencyCallback<T>> callbacks = new ArrayList<PersistencyCallback<T>>();
	
	public void addCallback(PersistencyCallback<T> obj){
		this.callbacks.add(obj);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> ObjectPersistency<T> get(Class<T> clazz) throws Exception{
		//logger.debug("requesting table for class "+clazz.getName());
		if (!persistencyMapping.containsKey(clazz.getName()))
				persistencyMapping.put(clazz.getName(), new ObjectPersistency<T>(clazz));
		return persistencyMapping.get(clazz.getName());
	}
	
	private ObjectPersistency(Class<T> clazz) throws Exception {
		_clazz=clazz;
        if (!_clazz.isAnnotationPresent(TableRootDefinition.class)) throw new AnnotationNotDefinedException(); 
		String tableName = clazz.getSimpleName()+Math.abs(clazz.getName().hashCode());
		try{
			this.table =createTable(tableName);
		}catch (TableAlreadyExistsException e) {
			this.table= new SimpleTable(tableName);
			/*checkTableCompatibility(table);
			table.initializeFieldMapping();*/
		}
	}
	
	@SuppressWarnings("rawtypes")
	private FieldMappingPair retrieveColumnDefinition(Class _clazz, int fieldIndex) throws Exception{
		List<ColumnDefinition> cdList= new ArrayList<ColumnDefinition>();
		TreeMap<String, String> internalFieldMapping=new TreeMap<String, String>();
		
		logger.trace("retrieving column definition for --> table "+_clazz.getSimpleName());
		
		for (Field field:_clazz.getDeclaredFields()){
			if(field.isAnnotationPresent(FieldDefinition.class)){
				FieldDefinition fieldDefinition= field.getAnnotation(FieldDefinition.class);
				ColumnDefinition cDef= DBSession.getImplementation(ColumnDefinition.class);
				String fieldNameInTable= FIELD_PREFIX+fieldIndex;
				cDef.setLabel(fieldNameInTable);
				
				Type type = Type.getTypeByJavaClass(field.getType());
				if (type==null || (type.getType()==Types.STRING && fieldDefinition.precision().length==0))
					type = new Type(Types.TEXT);
				else type.setPrecision(fieldDefinition.precision());
				logger.trace(field.getName()+ " --> the type "+field.getType()+" is converted in  "+type.getType().name());
				cDef.setType(type);
				cDef.setSpecification(fieldDefinition.specifications());
				cdList.add(cDef);
				internalFieldMapping.put(field.getName(), fieldNameInTable);
				fieldIndex++;
			}
		}
		if (_clazz.getSuperclass()!=null){
			if (_clazz.getSuperclass().equals(ObjectStateControl.class)){
				ColumnDefinition cDef= DBSession.getImplementation(ColumnDefinition.class);
				cDef.setLabel("objectversion");
				Type type= new Type(Types.INTEGER);
				type.setPrecision(10);
				cDef.setType(type);
				//cDef.setSpecification(Specification.NOT_NULL);
				cdList.add(cDef);
				//internalFieldMapping.put("objectversion", "objectversion");
			} else {
				FieldMappingPair fmPair = retrieveColumnDefinition(_clazz.getSuperclass(), fieldIndex);
				cdList.addAll(fmPair.getColumnsDefinition());
				internalFieldMapping.putAll(fmPair.getFieldMapping());
			}
		}
		return new FieldMappingPair(internalFieldMapping, cdList);
	}
	
	private SimpleTable createTable(String tableName) throws Exception {
		CreateTable creator = DBSession.getImplementation(CreateTable.class);
		
		FieldMappingPair fmPair = retrieveColumnDefinition(this._clazz, 0);
		
		creator.setColumnsDefinition(fmPair.getColumnsDefinition().toArray(new ColumnDefinition[fmPair.getColumnsDefinition().size()]));
		creator.setTableName(tableName);
		DBSession session = null;
		try{
			session = DBSession.connect();
			logger.trace(creator.getExpression());
			table =creator.execute(session);
			SystemTableInfo.getSystemInfo().addInfo(fmPair.getFieldMapping(), table.getTableName());
		}finally{	
			if (session !=null)
				session.release();
		}
		return table;
	}
	
	public void deleteByKey(Object key) throws Exception{
		LinkedHashMap<String, Type> mapping=table.getFieldsMapping();
		String primaryKey = null;
		Type type= null;
		for (Entry<String, Type> entry: mapping.entrySet())
			if (entry.getValue().isPrimaryKey()){
				primaryKey = entry.getKey();
				type = entry.getValue(); 
				break;
			}
		
		if (callbacks.size()>0){
			T obj= getByKey(key);
			for (PersistencyCallback<T> callback :callbacks)
					callback.onObjectDeleted(obj);
		}
		
		Delete delete= DBSession.getImplementation(Delete.class);
		CastObject cast = DBSession.getImplementation(CastObject.class);
		cast.setStringValue(key.toString());
		cast.setType(type);
		delete.setFilter(new OperatorCondition<SimpleAttribute, CastObject>(new SimpleAttribute(primaryKey),cast,"="));
		delete.setTable(this.table);
		DBSession session = DBSession.connect();
		try{
			 delete.execute(session);
		}finally{
			session.release();
		}
		if (delete.getDeletedItems()==0) throw new ObjectNotFoundException();
	}
	
	public Map<String, String> getInfo() throws Exception{
		return SystemTableInfo.getSystemInfo().retrieveInfo(this.table.getTableName());
	}
	
	public T getByKey(Object key) throws ObjectNotFoundException, Exception{
		LinkedHashMap<String, Type> mapping=table.getFieldsMapping();
		String primaryKey = null;
		Type type= null;
		for (Entry<String, Type> entry: mapping.entrySet())
			if (entry.getValue().isPrimaryKey()){
				primaryKey = entry.getKey();
				type = entry.getValue(); 
				break;
			}
		if(type==null) throw new Exception("no primary key found in "+this.table.getTableName());
		Select select= DBSession.getImplementation(Select.class);
		CastObject cast = DBSession.getImplementation(CastObject.class);
		cast.setStringValue(key.toString());
		cast.setType(type);
		select.setFilter(new OperatorCondition<SimpleAttribute, CastObject>(new SimpleAttribute(primaryKey),cast,"="));
		select.setTables(this.table);
		T toReturn = null;
		DBSession session = null;
		try{
			session = DBSession.connect();
			ResultSet result = select.getResults(session);
			if (result.next()) toReturn = createObject(result);
			else throw new ObjectNotFoundException();
		}finally{
			if (session!=null) session.release();
		}
		
		return toReturn;
	}
	
	public void insert(T object) throws Exception{
		for (PersistencyCallback<T> callback: this.callbacks)
			callback.onBeforeStore(object);
		
		Insert insertor= DBSession.getImplementation(Insert.class);
		insertor.setTable(this.table);
		
		List<Object> oInsert = retrieveInsertField(object, _clazz);
		
		insertor.setInsertValues(oInsert.toArray(new Object[oInsert.size()]));
		DBSession session= DBSession.connect();
		try{
			insertor.execute(session);
		}finally{
			if (session!=null) session.release();
		}
		
		for (PersistencyCallback<T> callback: this.callbacks)
			callback.onObjectStored(object);
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Object> retrieveInsertField(T object, Class clazz) throws Exception{
		List<Object> oInsert= new ArrayList<Object>();

		logger.trace("storing class "+clazz.getSimpleName());
		
		for (Field field:clazz.getDeclaredFields())
			if(field.isAnnotationPresent(FieldDefinition.class)){
				if (Type.getTypeByJavaClass(field.getType())==null){
					logger.trace(field.getName()+" - "+field.getType());
					field.setAccessible(true);
					oInsert.add(new XStream().toXML(field.get(object)));
				}else {
					logger.trace(field.getName()+" - "+field.getType());
					field.setAccessible(true);
					oInsert.add(field.get(object));
				}
			}
		
		if (clazz.getSuperclass()!=null){
			if (clazz.getSuperclass().equals(ObjectStateControl.class))
				oInsert.add(0);
			else if (clazz.getSuperclass().isAnnotationPresent(TableRootDefinition.class)){
				oInsert.addAll(retrieveInsertField(object, clazz.getSuperclass()));
			}
		}
		return oInsert;
	}
	
	public void drop() throws Exception{
		DBSession session = DBSession.connect();
		DropTable drop = DBSession.getImplementation(DropTable.class);
		drop.setTableName(this.table.getTableName());
		try{
			drop.execute(session);
			SystemTableInfo.getSystemInfo().deleteInfo(this.table.getTableName());
		}finally{
			if (session!=null) session.release();
		}
	}
	
	public void deleteByValue(String fieldName,Object value) throws Exception{
		DBSession session= DBSession.connect();
		Delete delete= DBSession.getImplementation(Delete.class);
		delete.setTable(this.table);
		String internalFieldName= SystemTableInfo.getSystemInfo().retrieveInfo(this.table.getTableName()).get(fieldName);
		if (internalFieldName== null) throw new Exception("field "+fieldName+" non retrieved");
		
		if (callbacks.size()>0){
			Iterator<T> it= getObjectByField(fieldName, value).iterator();
			while (it.hasNext())
				for (PersistencyCallback<T> callback :callbacks)
					callback.onObjectDeleted(it.next());
		}
		
		delete.setFilter(new OperatorCondition<SimpleAttribute, Object>(new SimpleAttribute(internalFieldName),value,"="));
		try{
			delete.execute(session);
		}finally{
			if (session!=null) session.release();
		}
		
		
	}
	
	public List<T> getObjectByField(String fieldName, Object value) throws Exception{
		Select select= DBSession.getImplementation(Select.class);
		String internalFieldName= SystemTableInfo.getSystemInfo().retrieveInfo(this.table.getTableName()).get(fieldName);
		if (internalFieldName== null) throw new Exception("field "+fieldName+" non retrieved");
		select.setFilter(new OperatorCondition<SimpleAttribute, Object>(new SimpleAttribute(internalFieldName),value,"="));
		select.setTables(this.table);
		DBSession session= DBSession.connect();
		return toList(select.getResults(session));
	}
	
	/*public Iterator<T> getObjectByMultipleFieldValues(String fieldName, Object[] values) throws Exception{
		Select select= DBSession.getImplementation(Select.class);
		String internalFieldName= SystemTableInfo.getSystemInfo().retrieveInfo(this.table.getTableName()).get(fieldName);
		if (internalFieldName== null) throw new Exception("field "+fieldName+" non retrieved");
		ArrayList<OperatorCondition<SimpleAttribute, Object>> conditions = new ArrayList<OperatorCondition<SimpleAttribute,Object>>();  
		for (Object value: values)
			conditions.add(new OperatorCondition<SimpleAttribute, Object>(new SimpleAttribute(internalFieldName),value,"="));
		select.setUseDistinct(true);
		select.setFilter(new ORCondition(conditions.toArray(new OperatorCondition[conditions.size()])));
		select.setTables(this.table);
		DBSession session= DBSession.connect();
		return new InternalIterator(select.getResults(session), session);
	}*/
	
	public List<T> getObjectByFields(HashMap<String, Object> fieldValueMapping) throws Exception{
		if (fieldValueMapping.isEmpty()) throw new Exception("the filedValueMapping is empty");
		Select select= DBSession.getImplementation(Select.class);
		ArrayList<OperatorCondition<SimpleAttribute, Object>> conditions = new ArrayList<OperatorCondition<SimpleAttribute,Object>>();  
		for (Entry<String, Object> entry: fieldValueMapping.entrySet()){
			String internalFieldName= SystemTableInfo.getSystemInfo().retrieveInfo(this.table.getTableName()).get(entry.getKey());
			if (internalFieldName== null) throw new Exception("field "+entry.getKey()+" non retrieved");
			conditions.add(new OperatorCondition<SimpleAttribute, Object>(new SimpleAttribute(internalFieldName),entry.getValue(),"="));
		}
		select.setFilter(new ANDCondition(conditions.toArray(new OperatorCondition[conditions.size()])));
		select.setTables(this.table);
		DBSession session= DBSession.connect();
		return toList(select.getResults(session));
	}
	
	public List<T> getAll() throws Exception{
		Select select= DBSession.getImplementation(Select.class);
		select.setTables(this.table);
		DBSession session= DBSession.connect();
		return toList(select.getResults(session));
	}
		
	public SimpleTable getTable() {
		return table;
	}

	public boolean existsKey(Object key) throws Exception{
		DBSession session = null;
		try{
			LinkedHashMap<String, Type> mapping=table.getFieldsMapping();
			String primaryKey = null;
			Type type= null;
			for (Entry<String, Type> entry: mapping.entrySet())
				if (entry.getValue().isPrimaryKey()){
					primaryKey = entry.getKey();
					type = entry.getValue(); 
					break;
				}
			if(type==null) throw new ObjectNotFoundException("no primary key found in "+this.table.getTableName());
			Select select= DBSession.getImplementation(Select.class);
			CastObject cast = DBSession.getImplementation(CastObject.class);
			cast.setStringValue(key.toString());
			cast.setType(type);
			select.setFilter(new OperatorCondition<SimpleAttribute, CastObject>(new SimpleAttribute(primaryKey),cast,"="));
			select.setTables(this.table);
			session = DBSession.connect();
			ResultSet result = select.getResults(session);
			if (result.next()) return true;
			else return false;
		}catch (Exception e) {
			logger.error("errror retrieving key",e);
			throw e;
		}finally{
			if (session !=null) session.release();
		}
	}
		
	public boolean existEntryByFields(HashMap<String, Object> fieldValueMapping) throws Exception{
		if (fieldValueMapping.isEmpty()) throw new Exception("the filedValueMapping is empty");
		Select select= DBSession.getImplementation(Select.class);
		ArrayList<OperatorCondition<SimpleAttribute, Object>> conditions = new ArrayList<OperatorCondition<SimpleAttribute,Object>>();  
		for (Entry<String, Object> entry: fieldValueMapping.entrySet()){
			String internalFieldName= SystemTableInfo.getSystemInfo().retrieveInfo(this.table.getTableName()).get(entry.getKey());
			if (internalFieldName== null) throw new Exception("field "+entry.getKey()+" non retrieved");
			conditions.add(new OperatorCondition<SimpleAttribute, Object>(new SimpleAttribute(internalFieldName),entry.getValue(),"="));
		}
		select.setFilter(new ANDCondition(conditions.toArray(new OperatorCondition[conditions.size()])));
		select.setTables(this.table);
		return select.getResults(false).next();
	}
	
	/**
	 * 
	 * @param obj
	 * @throws ObjectStateChangedException
	 * @throws ObjectNotFoundException
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void update(T obj) throws ObjectStateChangedException,ObjectNotFoundException, Exception{
		LinkedHashMap<String, Type> mapping=table.getFieldsMapping();
		OperatorCondition primaryKeyOperator=null;		
		String primaryKey = null;
		Type type= null;
		for (Entry<String, Type> entry: mapping.entrySet())
			if (entry.getValue().isPrimaryKey()){
				primaryKey = entry.getKey();
				type = entry.getValue(); 
				break;
			}
		if(type==null) throw new ObjectNotFoundException("no primary key found in "+this.table.getTableName());
		
		Object keyValue = getFieldValue(obj, SystemTableInfo.getSystemInfo().retrieveFieldName(this.table.getTableName(), primaryKey) , type);
		CastObject cast= DBSession.getImplementation(CastObject.class);
		cast.setStringValue(keyValue!=null?keyValue.toString():null);
		cast.setType(type);
		primaryKeyOperator=new OperatorCondition<SimpleAttribute, CastObject>(new SimpleAttribute(primaryKey), cast , "=");
				
		executeUpdate(obj, primaryKeyOperator);
		
	}	

	
	
	/**
	 * 
	 * @param obj
	 * @param fieldValueMapping
	 * @throws ObjectStateChangedException
	 * @throws ObjectNotFoundException
	 * @throws Exception
	 */
	public void updateByFields(T obj, HashMap<String, Object> fieldValueMapping) throws ObjectStateChangedException,ObjectNotFoundException, Exception{
		if (fieldValueMapping.isEmpty()) throw new Exception("the filedValueMapping is empty");
		ArrayList<OperatorCondition<SimpleAttribute, Object>> conditions = new ArrayList<OperatorCondition<SimpleAttribute,Object>>();  
		Update update= DBSession.getImplementation(Update.class);
		update.setTable(table);
		for (Entry<String, Object> entry: fieldValueMapping.entrySet()){
			String internalFieldName= SystemTableInfo.getSystemInfo().retrieveInfo(this.table.getTableName()).get(entry.getKey());
			if (internalFieldName== null) throw new Exception("field "+entry.getKey()+" non retrieved");
			conditions.add(new OperatorCondition<SimpleAttribute, Object>(new SimpleAttribute(internalFieldName),entry.getValue(),"="));
		}
		executeUpdate(obj, new ANDCondition(conditions.toArray(new OperatorCondition[conditions.size()])));
	}
	
	private Object getFieldValue(T obj, String fieldName, Type internalType) throws Exception{
		Field field = retrieveField(fieldName, _clazz);
		field.setAccessible(true);
		Object fieldValue= null;
		if (internalType.getType().getJavaClass().isPrimitive()){
			Method fieldMethod= Field.class.getDeclaredMethod(internalType.getType().getReflectionMethodGet(), Object.class);
			fieldValue = fieldMethod.invoke(field, obj);
		}else if (field.getType().isEnum()){
			fieldValue = field.get(obj).toString();
		}
		else if(Type.getTypeByJavaClass(field.getType())==null) fieldValue = new XStream().toXML(field.get(obj));
		else fieldValue = field.get(obj);
		return fieldValue;
	}
	
	@SuppressWarnings("rawtypes")
	private void executeUpdate(T obj, Condition filter) throws ObjectStateChangedException,ObjectNotFoundException,Exception{
		LinkedHashMap<String, Type> mapping=table.getFieldsMapping();
		List<OperatorCondition> setters= new ArrayList<OperatorCondition>();
		for (Entry<String, String> entry:SystemTableInfo.getSystemInfo().retrieveInfo(this.table.getTableName()).entrySet()){
			Type internaltype=mapping.get(entry.getValue());
			if (!internaltype.isPrimaryKey()){
				CastObject cast= DBSession.getImplementation(CastObject.class);
				Object fieldValue = getFieldValue(obj, entry.getKey(), internaltype);
				cast.setStringValue(fieldValue!=null?fieldValue.toString():null);
				cast.setType(internaltype);
				setters.add(new OperatorCondition<SimpleAttribute, CastObject>(new SimpleAttribute(entry.getValue()), cast , "="));
			}
		}
		
		DBSession session= DBSession.connect();
		session.disableAutoCommit();
		
		//preparing the query
		Update update= DBSession.getImplementation(Update.class);
		update.setTable(table);
		update.setFilter(filter);
				
		
		//checking if the object is already stored, and adding control for version consistency
		try{
			Select select = DBSession.getImplementation(Select.class);
			select.setTables(table);
			select.setFilter(filter);
			if (!select.getResults().next()) throw new ObjectNotFoundException();
			
			if (isUnderVersionControl()){
				Field field=ObjectStateControl.class.getDeclaredField("objectversion");
				field.setAccessible(true);
				int versionValue = field.getInt(obj);
				Condition versionControl = new OperatorCondition<SimpleAttribute, Integer>(new SimpleAttribute("objectversion"), versionValue,"=");
				update.setFilter(new ANDCondition(filter, versionControl));
				setters.add(new OperatorCondition<SimpleAttribute, Integer>(new SimpleAttribute("objectversion"), versionValue+1,"="));
				field.setInt(obj, versionValue+1);
			}
			update.setOperators(setters.toArray(new OperatorCondition[setters.size()]));
			//executing the update
			update.execute(session);
			session.commit();
			if (update.getAffectedLines()==0) throw new ObjectStateChangedException();
		}catch (Exception e) {
			throw e;
		}finally{	
			session.release();
		}
		if (obj!=null){
			for (PersistencyCallback<T> callback: this.callbacks)
				callback.onObjectUpdated(obj);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Field retrieveField(String fieldName, Class clazz) {
		Field field = null;
		try{	
			field = clazz.getDeclaredField(fieldName);
		}catch (Exception e) {
			if (clazz.getSuperclass()!=null && clazz.getSuperclass().isAnnotationPresent(TableRootDefinition.class))
				return retrieveField(fieldName, clazz.getSuperclass());
		}
		return field;
		
			
		
	}
	
	@SuppressWarnings("rawtypes")
	private boolean isUnderVersionControl(){
		Class clazz = _clazz;
		while ((clazz=clazz.getSuperclass())!=null)
			if (clazz.equals(ObjectStateControl.class)) return true;
		return false;
	}
	
	
	private List<T> toList(ResultSet resultSet) throws Exception{
		ArrayList<T> returnList= new ArrayList<T>();
		while (resultSet.next())
			returnList.add(createObject(resultSet));
		resultSet.close();
		return returnList;
	}
	
	
		
	
	private T createObject(ResultSet result) throws Exception{
		Constructor<T> constr= _clazz.getDeclaredConstructor();
		logger.debug("createObjectInternal with class "+_clazz.getName());
		constr.setAccessible(true);
		T returnObj=constr.newInstance();
		table.initializeFieldMapping();
		LinkedHashMap<String, Type> mapping=table.getFieldsMapping();
		Map<String, String> classTableMap= SystemTableInfo.getSystemInfo().retrieveInfo(this.table.getTableName());
		createObjectInternal(_clazz, mapping, returnObj, classTableMap, result);
		
		if (returnObj!=null){
			for (PersistencyCallback<T> callback: this.callbacks)
				callback.onObjectLoaded(returnObj);
		}
		return returnObj;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createObjectInternal(Class clazz, LinkedHashMap<String, Type> mapping, T returnObj, Map<String, String> classTableMap,  ResultSet result) throws Exception{
		for (Field field: clazz.getDeclaredFields()){
			String internalFieldName= classTableMap.get(field.getName());
			if (internalFieldName!=null){
				field.setAccessible(true);
				logger.trace("fieldName is "+field.getName());
				if (mapping.get(internalFieldName).getType().getJavaClass().isPrimitive()){
					Method rsMethod= ResultSet.class.getDeclaredMethod(mapping.get(internalFieldName).getType().getReflectionMethodGet(), String.class);
					Method fieldMethod= Field.class.getDeclaredMethod(mapping.get(internalFieldName).getType().getReflectionMethodSet(), Object.class, mapping.get(internalFieldName).getType().getJavaClass());
					fieldMethod.invoke(field, returnObj, rsMethod.invoke(result, internalFieldName));
				}else if (field.getType().isEnum()){
					String value = result.getString(internalFieldName);
					field.set(returnObj, Enum.valueOf(((Class<? extends Enum>)field.getType()), value));
				} else if (Type.getTypeByJavaClass(field.getType())==null)//not identified object
					field.set(returnObj,new XStream().fromXML(result.getString(internalFieldName)));
				else if (Type.getTypeByJavaClass(field.getType()).getType()==Types.STRING)
					field.set(returnObj,result.getString(internalFieldName));
				else{
					logger.trace("method is "+mapping.get(internalFieldName).getType().getReflectionMethodGet());
					Method rsMethod= ResultSet.class.getDeclaredMethod(mapping.get(internalFieldName).getType().getReflectionMethodGet(), String.class);
					field.set(returnObj, rsMethod.invoke(result, internalFieldName));
				}
			}
		}
		if (clazz.getSuperclass()!=null){
			if (clazz.getSuperclass()!=null){
				if (clazz.getSuperclass().equals(ObjectStateControl.class)){
					Field superClassField=clazz.getSuperclass().getDeclaredField("objectversion");
					superClassField.setAccessible(true);
					superClassField.setInt(returnObj, result.getInt("objectversion"));
				}
				else
					createObjectInternal(clazz.getSuperclass(), mapping, returnObj, classTableMap, result);
			}
		}
	}
	
	
	private  class FieldMappingPair{
				
		private TreeMap<String, String> fieldMapping;
		private List<ColumnDefinition> columnsDefinition;
		
		public FieldMappingPair(TreeMap<String, String> fieldMapping,
				List<ColumnDefinition> columnsDefinition) {
			super();
			this.fieldMapping = fieldMapping;
			this.columnsDefinition = columnsDefinition;
		}
		/**
		 * @return the fieldMapping
		 */
		public TreeMap<String, String> getFieldMapping() {
			return fieldMapping;
		}
		/**
		 * @return the columnsDefinition
		 */
		public List<ColumnDefinition> getColumnsDefinition() {
			return columnsDefinition;
		}
		
		
		
	}
	
}
