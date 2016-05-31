package gr.uoa.di.madgik.environment.notifications;

import java.util.HashMap;

import org.w3c.dom.Element;

public class Message {
	
	HashMap<String, Boolean> booleanProperties;
	
	HashMap<String, Byte> byteProperties;
	
	HashMap<String, Double> doubleProperties;
	
	HashMap<String, Float> floatProperties;
	
	HashMap<String, Integer> integerProperties;
	
	HashMap<String, Long> longProperties;
	
	HashMap<String, Object> objectProperties;
	
	HashMap<String, String> stringProperties;
	
	Element message;
	
	int deliveryMode;
	
	long expiration;
	
	String messageId;
	
	int priority;
	
	boolean redelivered;
	
	long timestamp;
	
	String messageType;
	
	public HashMap<String, String> getStringProperties() {
		return stringProperties;
	}
	
	public Message() {
		booleanProperties = new HashMap<String, Boolean>();
		byteProperties = new HashMap<String, Byte>();
		integerProperties = new HashMap<String, Integer>();
		doubleProperties = new HashMap<String, Double>();
		floatProperties = new HashMap<String, Float>();
		longProperties = new HashMap<String, Long>();
		objectProperties = new HashMap<String, Object>();
		stringProperties = new HashMap<String, String>();
	}
	
	public void clearProperties() {
		booleanProperties.clear();
		byteProperties.clear();
		integerProperties.clear();
		doubleProperties.clear();
		floatProperties.clear();
		longProperties.clear();
		objectProperties.clear();
		stringProperties.clear();
	}
	
	
	public void addBooleanProperty(String name, Boolean value) {
		booleanProperties.put(name, value);
	}
	
	public boolean getBooleanProperty(String name) {
		return booleanProperties.get(name);
	}
	
	public void addByteProperty(String name, byte value) {
		byteProperties.put(name, value);
	}
	
	public byte getByteProperty(String name) {
		return byteProperties.get(name);
	}
	
	public void addDoubleProperty(String name, Double value) {
		doubleProperties.put(name, value);
	}
	
	public double getDoubleProperty(String name) {
		return doubleProperties.get(name);
	}
	
	public void addFloatProperty(String name, Float value) {
		floatProperties.put(name, value);
	}
	
	public double getFloatProperty(String name) {
		return floatProperties.get(name);
	}
	
	public void addIntProperty(String name, Integer value) {
		integerProperties.put(name, value);
	}
	
	public int getIntProperty(String name) {
		return integerProperties.get(name);
	}
	
	public void addStringProperty(String name, String value) {
		stringProperties.put(name, value);
	}
	
	public String getStringProperty(String name) {
		return stringProperties.get(name);
	}
	
	public void addObjectProperty(String name, Object value) {
		objectProperties.put(name, value);
	}

	public Object getObjectProperty(String name) {
		return objectProperties.get(name);
	}
	
	public void setMessage(Element message) {
		this.message = message;
	}
	
	public Element getMessage() {
		return message;
	}
	
	public void setDeliveryMode(int mode) {
		deliveryMode = mode;
	}
	
	public int getDeliveryMode(){
		return deliveryMode;
	}
	
	public void setExpiration(long exp) {
		expiration = exp;
	}
	
	public long getExpiration() {
		return expiration;
	}
	
	public void setMessageID(String messageId) {
		this.messageId = messageId;
	}
	
	public String getMessageID() {
		return messageId;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setRedelivered(boolean redelivered) {
		this.redelivered = redelivered;
	}
	
	public boolean getRedelivered() {
		return redelivered;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setMessageType(String msgType) {
		this.messageType = msgType;
	}
	
	public String getMessageType() {
		return messageType;
	}
	
}
