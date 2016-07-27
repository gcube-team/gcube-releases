package org.gcube.informationsystem.notifier.util;

import java.io.*;

import javax.xml.namespace.QName;

import org.xml.sax.InputSource;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.SerializationException;



/**
 *  This class boxes a EnspointReferenceType in order to add equals method 
 * 
 * @author Andrea Manzi
 *
 */

public class EPR implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4502792483833265578L;

	private EndpointReferenceType epr;
	
	/**
	 * default constructor
	 *
	 */
	public EPR( ) {

	}

	/**
	 * the constructor 
	 * @param epr the epr
	 */
	public EPR(EndpointReferenceType epr) {

		this.epr = epr;

	}

	/**
	 * Default Setter for EPR
	 * 
	 * @param  epr the epr
	 */
	public void setEpr (EndpointReferenceType epr) {
		this.epr = epr;
	}


	/**
	 * Default getter for EPR
	 * 
	 * @return EndpointReferenceType epr
	 */
	public EndpointReferenceType getEpr () {
		return this.epr;
	}

	/**
	 * Used by the HashMap
	 * @return the hashcode
	 */
	public int hashCode() {

		return this.toString().hashCode();
	}

	/**
	 * tostring method
	 * @return the string representation of the objct
	 */
	public String toString() {

		return this.epr.toString();

	}

	/**
	 * equals method
	 * 
	 * @param obj the obj to compare
	 * @return true if the two objects are the same Key
	 */
	public boolean equals(Object obj) {

		if (!(obj instanceof EPR))
			return false;

		EPR epr = (EPR) obj;
		String keyObject = null;
		String keyInstance = null;
		MessageElement[] any = null;
		ReferencePropertiesType prop = null;
		try {
			prop = epr.getEpr().getProperties();       

			if (prop != null) {
				any = prop.get_any();
				if (any.length > 0) 
					keyObject= any[0].getValue();			
			}

			prop = null;

			prop = this.getEpr().getProperties();       

			if (prop != null) {
				any = prop.get_any();
				if (any.length > 0) 
					keyInstance= any[0].getValue();			
			}
		}catch (Exception e) {}

		if (keyInstance == null ||  keyObject== null) {

			if (epr.getEpr().getAddress().equals(this.getEpr().getAddress())) 
				return true;	
			else return false;

		}
		if (epr.getEpr().getAddress().equals(this.getEpr().getAddress())  && (keyInstance.compareTo(keyObject) == 0))
			return true;	
		else return false;

	} 

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc =
		new org.apache.axis.description.TypeDesc(EPR.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://diligentproject.org/namespaces/informationservice/disbroker/DISBrokerService", "EPR"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("epr");
		elemField.setXmlName(new javax.xml.namespace.QName("http://diligentproject.org/namespaces/informationservice/disbroker/DISBrokerService", "epr"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/ws/2004/03/addressing", "EndpointReferenceType"));
		typeDesc.addFieldDesc(elemField);
	}

	/**
	 * Return type metadata object
	 * @return the type desc
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/**
	 * Get Custom Serializer
	 * @param mechType the type
	 * @param _javaType the java type
	 * @param _xmlType  the xml type
	 * @return thr serializer
	 */
	@SuppressWarnings("unchecked")
	public static org.apache.axis.encoding.Serializer getSerializer(
			java.lang.String mechType,
			java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return
		new  org.apache.axis.encoding.ser.BeanSerializer(
				_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 * @param mechType the type
	 * @param _javaType the java type
	 * @param _xmlType  the xml type
	 * @return org.apache.axis.encoding.Deserializer
	 */
	@SuppressWarnings("unchecked")
	public static org.apache.axis.encoding.Deserializer getDeserializer(
			java.lang.String mechType,
			java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return
		new  org.apache.axis.encoding.ser.BeanDeserializer(
				_javaType, _xmlType, typeDesc);
	}

	/**
	 * 
	 * 
	 * 
	 * @param out
	 * @throws IOException
	 */
	
	private void writeObject( ObjectOutputStream out) throws IOException {
		try { 
			out.writeObject(ObjectSerializer.toString(this.getEpr(),new QName("Andrea","ResourceReference")));
		}catch (SerializationException e ) {
			e.printStackTrace();

		}

	}

	
	 /** 
	 * 
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject( ObjectInputStream in) throws IOException,ClassNotFoundException {
		try {

			this.setEpr((EndpointReferenceType) ObjectDeserializer.deserialize(new InputSource(new StringReader((String)in.readObject())),EndpointReferenceType.class));

		}catch (DeserializationException e ) {
			e.printStackTrace();

		}

	}



}
