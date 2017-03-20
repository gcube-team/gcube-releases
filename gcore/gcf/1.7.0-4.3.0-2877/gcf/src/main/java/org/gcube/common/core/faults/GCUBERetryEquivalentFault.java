package org.gcube.common.core.faults;

import java.util.Calendar;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.oasis.wsrf.faults.BaseFaultType;
import org.oasis.wsrf.faults.BaseFaultTypeDescription;
import org.oasis.wsrf.faults.BaseFaultTypeErrorCode;

public class GCUBERetryEquivalentFault extends GCUBEFault {

    /**Serialisation version.*/
	private static final long serialVersionUID = 1L;
	
	/**Fault type.*/
	public static final String FAULT_TYPE = "RETRY_EQUIVALENT";

	public GCUBERetryEquivalentFault() {
    }
	
    public GCUBERetryEquivalentFault(Calendar timestamp,EndpointReferenceType originator,BaseFaultTypeErrorCode errorCode,
           BaseFaultTypeDescription[] description,BaseFaultType[] faultCause,String faultMessage, String faultType) {
        super(timestamp,originator,errorCode,description,faultCause,faultMessage,faultType);
    }

    public GCUBERetryEquivalentFault(Exception e, String ...msg) {
    	super(e,msg);
    }
    
    public GCUBERetryEquivalentFault(String ... msg) {
    	super(msg);
    }
    
    /** {@inheritDoc}*/
    public java.lang.String getFaultType() {return FAULT_TYPE;}
    
    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GCUBERetryEquivalentFault)) return false;
        GCUBERetryEquivalentFault other = (GCUBERetryEquivalentFault) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj);
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GCUBERetryEquivalentFault.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://gcube-system.org/namespaces/common/core/faults", "GCUBERetryEquivalentFault"));
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
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
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }
    
	/**{@inheritDoc}*/
    protected GCUBEException getException(String msg, Throwable t) {return new GCUBERetryEquivalentException(msg,t);}


}
