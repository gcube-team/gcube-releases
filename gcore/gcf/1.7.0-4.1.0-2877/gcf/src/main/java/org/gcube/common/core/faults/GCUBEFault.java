/**
 * GCUBEFault.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis 1.2RC2 Nov 14, 2006 (10:23:53 EST) WSDL2Java emitter.
 */

package org.gcube.common.core.faults;

import javax.wsdl.Fault;


/**
 * Root of all remote faults with GCUBE semantics.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 * 
 */
public class GCUBEFault extends org.oasis.wsrf.faults.BaseFaultType implements java.io.Serializable {

	/** Serialisation version. */
	private static final long serialVersionUID = 1L;
	/** The fault message. */
	private java.lang.String faultMessage;
	/** The fault type */
	private java.lang.String faultType;

	/** Creates a new instance. */
	public GCUBEFault() {
		this.faultType = this.getFaultType();
	}

	/**
	 * Creates a new fault from various parameters.
	 * 
	 * @param timestamp the timestamp.
	 * @param originator the originat.
	 * @param errorCode the error code.
	 * @param description the description.
	 * @param faultCause the cause.
	 * @param faultMessage the message.
	 * @param faultType the type.
	 */
	public GCUBEFault(java.util.Calendar timestamp,
			org.apache.axis.message.addressing.EndpointReferenceType originator,
			org.oasis.wsrf.faults.BaseFaultTypeErrorCode errorCode,
			org.oasis.wsrf.faults.BaseFaultTypeDescription[] description,
			org.oasis.wsrf.faults.BaseFaultType[] faultCause, java.lang.String faultMessage, java.lang.String faultType) {
		super(timestamp, originator, errorCode, description, faultCause);
		this.faultMessage = faultMessage;
		this.faultType = faultType;
	}

	/**
	 * Creates an instance from a given cause and, optionally, a message.
	 * 
	 * @param e the cause
	 * @param msg (optional) message.
	 */
	public GCUBEFault(Throwable e, String... msg) {
		this(msg);
	}

	/**
	 * Creates an instance with an optional message.
	 * 
	 * @param msg (optional) the message.
	 */
	public GCUBEFault(String... msg) {
		this.setFaultMessage((msg == null || msg.length == 0) ? "" : msg[0]);
	}

	/**
	 * Returns the fault message.
	 * 
	 * @return faultMessage the message.
	 */
	public java.lang.String getFaultMessage() {
		return faultMessage;
	}

	/**
	 * Sets the fault message.
	 * 
	 * @param faultMessage the message.
	 */
	public void setFaultMessage(java.lang.String faultMessage) {
		this.faultMessage = faultMessage;
	}

	/**
	 * Returns the fault type.
	 * 
	 * @return faultType the type.
	 */
	public java.lang.String getFaultType() {
		return faultType;
	}

	/**
	 * Sets the fault type.
	 * 
	 * @param faultType the type.
	 */
	public void setFaultType(java.lang.String faultType) {
		this.faultType = faultType;
	}

	private java.lang.Object __equalsCalc = null;

	/** {@inheritDoc} */
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof GCUBEFault))
			return false;
		GCUBEFault other = (GCUBEFault) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = super.equals(obj)
				&& ((this.faultMessage == null && other.getFaultMessage() == null) || (this.faultMessage != null && this.faultMessage
						.equals(other.getFaultMessage())))
				&& ((this.faultType == null && other.getFaultType() == null) || (this.faultType != null && this.faultType
						.equals(other.getFaultType())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	/** {@inheritDoc} */
	public synchronized int hashCode() {
		if (__hashCodeCalc)
			return 0;
		__hashCodeCalc = true;
		int _hashCode = super.hashCode();
		if (getFaultMessage() != null)
			_hashCode += getFaultMessage().hashCode();
		if (getFaultType() != null)
			_hashCode += getFaultType().hashCode();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			GCUBEFault.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://gcube-system.org/namespaces/common/core/faults",
				"GCUBEFault"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("faultMessage");
		elemField.setXmlName(new javax.xml.namespace.QName("http://gcube-system.org/namespaces/common/core/faults",
				"FaultMessage"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("faultType");
		elemField.setXmlName(new javax.xml.namespace.QName("http://gcube-system.org/namespaces/common/core/faults",
				"FaultType"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		typeDesc.addFieldDesc(elemField);
	}

	/**
	 * Returns the type description
	 * 
	 * @return the description
	 **/
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/** Get a sustom Serializer **/
	public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
	}

	/** Get Custom Deserializer */
	public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType,
			java.lang.Class _javaType, javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
	}

	/** {@inheritDoc} **/
	public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context)
			throws java.io.IOException {
		context.serialize(qname, null, this);
	}

	/**
	 * Returns the remote cause of the fault as a {@link Throwable}
	 * @return the remote cause as a {@link Throwable}
	 * @see FaultUtils#remoteCause(GCUBEFault)
	 */
	public Throwable remoteCause() {
		return FaultUtils.remoteCause(this);
	}
	/**
	 * Converts the fault into a corresponding {@link GCUBEException}.
	 * 
	 * @return the exception.
	 */
	public GCUBEException toException() {

		return getException(getFaultMessage(), remoteCause()); // default is unrecoverable
	}

	/**
	 * Returns an exception corresponding to this fault type.
	 * 
	 * @param msg the message of the exception.
	 * @return the exception.
	 */
	protected GCUBEException getException(String msg, Throwable cause) {
		return new GCUBEUnrecoverableException(msg, cause);
	}

}
