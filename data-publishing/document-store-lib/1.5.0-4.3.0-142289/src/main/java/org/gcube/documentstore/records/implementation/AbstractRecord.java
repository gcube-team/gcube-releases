/**
 * 
 */
package org.gcube.documentstore.records.implementation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.implementation.validations.annotations.NotEmpty;
import org.gcube.documentstore.records.implementation.validations.annotations.ValidLong;
import org.gcube.documentstore.records.implementation.validations.validators.ValidLongValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public abstract class AbstractRecord implements Record {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -2060728578456796388L;
	
	private static Logger logger = LoggerFactory.getLogger(AbstractRecord.class);
	
	@NotEmpty
	protected static final String ID = Record.ID;

	@ValidLong
	protected static final String CREATION_TIME = Record.CREATION_TIME;
	
	@NotEmpty
	protected static final String RECORD_TYPE = Record.RECORD_TYPE;
	
	/** resource-specific properties */
	protected Map<String, Serializable> resourceProperties;
	
	protected Map<String, List<FieldAction>> validation;
	protected Map<String, List<FieldAction>> computation;
	
	protected Set<String> requiredFields;
	protected Set<String> computedFields;
	protected Set<String> aggregatedFields;
	
	protected static Set<Field> getAllFields(Class<?> type) {
		Set<Field> fields = new HashSet<Field>();
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
			fields.addAll(Arrays.asList(c.getFields()));
		}
		return fields;
	}
	
	
	protected void initializeValidation() {
		Set<Field> fields = getAllFields(this.getClass());
		for(Field field : fields){
			boolean defaultAccessibility = field.isAccessible();
			field.setAccessible(true);
			String keyString;
			try {
				keyString = (String) field.get(null);
			} catch (Exception e) {
				continue;
			}
			
			if(field.getAnnotations().length>0){
				
				List<FieldAction> fieldValidators =  validation.get(keyString);
				if(fieldValidators==null){
					fieldValidators = new ArrayList<FieldAction>();
					validation.put(keyString, fieldValidators);
				}
				
				List<FieldAction> fieldComputations = computation.get(keyString);
				if(fieldComputations==null){
					fieldComputations = new ArrayList<FieldAction>();
					computation.put(keyString, fieldComputations);
				}
				
				for (Annotation annotation : field.getAnnotations()){
					Class<? extends Annotation> annotationType = annotation.annotationType();
					if (annotationType.isAnnotationPresent(FieldDecorator.class)){
						Class<? extends FieldAction> managedClass = ((FieldDecorator)annotationType.getAnnotation(FieldDecorator.class)).action();
						FieldAction validator;
						try {
							validator = managedClass.newInstance();
						} catch (InstantiationException | IllegalAccessException e) {
							logger.error("{} {}", keyString, annotation, e);
							continue;
						}
						fieldValidators.add(validator);
					}
					if(annotationType.isAssignableFrom(RequiredField.class)){
						requiredFields.add(keyString);
					}
					if(annotationType.isAssignableFrom(AggregatedField.class)){
						aggregatedFields.add(keyString);
					}
					if(annotationType.isAssignableFrom(ComputedField.class)){
						computedFields.add(keyString);
						Class<? extends FieldAction> managedClass = ((ComputedField) annotation).action();
						FieldAction computeAction;
						try {
							computeAction = managedClass.newInstance();
						} catch (InstantiationException | IllegalAccessException e) {
							logger.error("{} {}", keyString, annotation, e);
							continue;
						}
						fieldComputations.add(computeAction);
					}
				}
			}
			field.setAccessible(defaultAccessibility);
		}
		
	}
	
	@Override
	public SortedSet<String> getQuerableKeys()
					throws Exception {
		SortedSet<String> properties = new TreeSet<>(
				this.getRequiredFields());

		properties.removeAll(this.getAggregatedFields());
		properties.removeAll(this.getComputedFields());
		properties.remove(Record.ID);
		properties.remove(Record.CREATION_TIME);
		properties.remove(Record.RECORD_TYPE);

		return properties;
	}

	
	protected void cleanExtraFields(){
		Set<String> neededFields = this.requiredFields;
		neededFields.addAll(this.aggregatedFields);
		
		Set<String> keysToRemove = new HashSet<String>();
		Set<String> propertyKeys = this.resourceProperties.keySet();
		for(String propertyName : propertyKeys){
			if(!neededFields.contains(propertyName)){
				keysToRemove.add(propertyName);
			}
		}
		
		for(String keyToRemove : keysToRemove){
			this.resourceProperties.remove(keyToRemove);
		}
	}
	
	/**
	 * Initialize variable
	 */
	protected void init() {
		this.validation = new HashMap<String, List<FieldAction>>();
		this.computation = new HashMap<String, List<FieldAction>>();
		this.requiredFields = new HashSet<String>();
		this.aggregatedFields = new HashSet<String>();
		this.computedFields = new HashSet<String>();
		this.resourceProperties = new HashMap<String, Serializable>();
		initializeValidation();
	}
	
	public AbstractRecord(){
		init();
		this.resourceProperties.put(ID, UUID.randomUUID().toString());
		this.setRecordType();
		Calendar calendar = Calendar.getInstance();
		this.resourceProperties.put(CREATION_TIME, calendar.getTimeInMillis());
	}

	public AbstractRecord(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		init();
		setResourceProperties(properties);
		if(this instanceof AggregatedRecord){
			this.resourceProperties.put(AggregatedRecord.AGGREGATED, true);
			cleanExtraFields();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getRequiredFields() {
		return new HashSet<String>(requiredFields);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getComputedFields() {
		return new HashSet<String>(computedFields);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<String> getAggregatedFields() {
		return new HashSet<String>(aggregatedFields);
	}
	
	@Override
	public String getRecordType() {
		return (String) this.resourceProperties.get(RECORD_TYPE);
	}

	protected abstract String giveMeRecordType();
	
	protected void setRecordType(){
		this.resourceProperties.put(RECORD_TYPE, this.giveMeRecordType());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return (String) this.resourceProperties.get(ID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setId(String id) throws InvalidValueException {
		setResourceProperty(ID, id);
	}
	
	public static Calendar timestampToCalendar(long millis){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Calendar getCreationTime() {
		Long millis = null;
		try {
			millis = (Long) new ValidLongValidator().validate(CREATION_TIME, this.resourceProperties.get(CREATION_TIME), null);
			return timestampToCalendar(millis);
		} catch (InvalidValueException e) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCreationTime(Calendar creationTime) throws InvalidValueException {
		setResourceProperty(CREATION_TIME, creationTime.getTimeInMillis());
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Serializable> getResourceProperties() {
		return new HashMap<String, Serializable>(this.resourceProperties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResourceProperties(Map<String, ? extends Serializable> properties) throws InvalidValueException {
		Map<String, ? extends Serializable> validated = validateProperties(properties);
		this.resourceProperties = new HashMap<String, Serializable>(validated);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable getResourceProperty(String key) {
		return this.resourceProperties.get(key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResourceProperty(String key, Serializable value) throws InvalidValueException {
		Serializable checkedValue = validateField(key, value);
		if(checkedValue == null){
			this.resourceProperties.remove(key);
		}else{
			this.resourceProperties.put(key, checkedValue);
		}
	}
	
	// AGGREGATION
	/* --------------------------------------- */
	
	/**
	 * Set the right end of the time interval covered by this Record
	 * @param endTime End Time
	 * @throws InvalidValueException
	 */
	protected void setEndTime(Calendar endTime) throws InvalidValueException {
		setResourceProperty(AggregatedRecord.END_TIME, endTime.getTimeInMillis());
	}
	
	protected int getOperationCount() {
		return (Integer) this.resourceProperties.get(AggregatedRecord.OPERATION_COUNT);
	}

	protected void setOperationCount(int operationCount) throws InvalidValueException {
		setResourceProperty(AggregatedRecord.OPERATION_COUNT, operationCount);
	}
	
	/**
	 * Return the left end of the time interval covered by this Record
	 * @return Start Time
	 */
	protected long getStartTimeInMillis() {
		return (Long) this.resourceProperties.get(AggregatedRecord.START_TIME);
	}
	
	/**
	 * Return the left end of the time interval covered by this Record
	 * @return Start Time
	 */
	protected Calendar getStartTimeAsCalendar() {
		long millis = getStartTimeInMillis();
		return timestampToCalendar(millis);
	}
	
	/**
	 * Set the left end of the time interval covered by this Record
	 * @param startTime Start Time
	 * @throws InvalidValueException
	 */
	protected void setStartTime(Calendar startTime) throws InvalidValueException {
		setResourceProperty(AggregatedRecord.START_TIME, startTime.getTimeInMillis());
	}
	
	/**
	 * Return the right end of the time interval covered by this Record
	 * @return End Time
	 */
	protected long getEndTimeInMillis() {
		return (Long) this.resourceProperties.get(AggregatedRecord.END_TIME);
	}
	
	/**
	 * Return the right end of the time interval covered by this Record
	 * @return End Time
	 */
	protected Calendar getEndTimeAsCalendar() {
		long millis = getEndTimeInMillis();
		return timestampToCalendar(millis);
	}

	protected Serializable validateField(String key, Serializable value) throws InvalidValueException {
		if(key == null){
			throw new InvalidValueException("The key of property to set cannot be null");
		}
		Serializable checkedValue = value;
		List<FieldAction> fieldValidators = validation.get(key);
		if(fieldValidators!=null){
			for(FieldAction fieldValidator : fieldValidators){
				if(aggregatedFields.contains(key)){
					// TODO
				}
				if(computedFields.contains(key)){
					logger.debug("{} is a computed field. To be calculated all the required fields to calcutalate it MUST be set. In any case the provided value will be ignored.", key);
				}
				try {
					
					checkedValue = fieldValidator.validate(key, checkedValue, this);
					if(checkedValue==null){
						return null;
					}
				} catch (InvalidValueException e) {
					logger.error(String.format("The provided value %s is NOT valid for field with key %s.", checkedValue.toString(), key));
					throw e;
				}
			}
		}
		return checkedValue;
	}
	
	protected void computeField(String key) throws InvalidValueException {
		if(key == null){
			throw new InvalidValueException("The key of property to set cannot be null");
		}
		Serializable computedValue = null;
		List<FieldAction> fieldComputations = computation.get(key);
		if(fieldComputations!=null){
			for(FieldAction fieldValidator : fieldComputations){
				try {
					computedValue = fieldValidator.validate(key, null, this);
					this.resourceProperties.put(key, computedValue);
				} catch (InvalidValueException e) {
					logger.error(String.format("Unable to calculate the field with key %s", key));
					throw e;
				}
			}
		}
	}
	
	
	protected Map<String, ? extends Serializable> validateProperties(Map<String, ? extends Serializable> properties) throws InvalidValueException{
		Map<String, Serializable> validated = new HashMap<String, Serializable>();
		for(String key : properties.keySet()){
			Serializable value = properties.get(key);
			
			/* TODO Test Patch */
			Serializable checkedValue = validateField(key, value);
			if(checkedValue == null){
				validated.remove(key);
			}else{
				validated.put(key, checkedValue);
			}
			/* Restore if test patch is not good
			validated.put(key, validateField(key, value));
			*/
		}
		return validated;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validate() throws InvalidValueException {
		for(String key : this.computedFields){
			
			computeField(key);
		}
		
		validateProperties(this.resourceProperties);
		Set<String> notPresentProperties = new HashSet<String>();
		for(String key : this.requiredFields){
			if(!this.resourceProperties.containsKey(key)){
				notPresentProperties.add(key);
			}
		}
		if(!notPresentProperties.isEmpty()){
			String pluralManagement = notPresentProperties.size() == 1 ? "y" : "ies";
			logger.debug("ID doc:{}",this.getId());
			throw new InvalidValueException(String.format("The Record does not contain the following required propert%s %s", pluralManagement, notPresentProperties.toString()));
		}
	}
	
	@Override
	public String toString(){
		return resourceProperties.toString();
	}

	/**
	 * Compare this Record instance with the one provided as argument
	 * @param record the Record to compare
	 * @return 0 is and only if the Record provided as parameter
	 * contains all and ONLY the parameters contained in this instance.
	 * If the number of parameters differs, the methods return the difference 
	 * between the number of parameter in this instance and the ones in the
	 * Record provided as parameter.
	 * If the size is the same but the Record provided as parameter does
	 * not contains all parameters in this instance, -1 is returned. 
	 */
	@Override
	public int compareTo(Record record) {
		Set<Entry<String, Serializable>> thisSet = this.resourceProperties.entrySet();
		Set<Entry<String, Serializable>> recordSet = record.getResourceProperties().entrySet();
		if(thisSet.size() != recordSet.size()){
			return thisSet.size() - recordSet.size();
		}
		if(recordSet.containsAll(thisSet)){
			return 0;
		}
		return 1;
	}

}
