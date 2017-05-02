/**
 * StringArrayArray.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Nov 14, 2006 (10:23:53 EST) WSDL2Java emitter.
 */

package org.gcube.common.core.types;

public class StringArrayArray  implements java.io.Serializable {
    private org.gcube.common.core.types.StringArray[] arrays;

    public StringArrayArray() {
    }

    public StringArrayArray(
    		org.gcube.common.core.types.StringArray[] arrays) {
           this.arrays = arrays;
    }


    /**
     * Gets the arrays value for this StringArrayArray.
     * 
     * @return arrays
     */
    public org.gcube.common.core.types.StringArray[] getArrays() {
        return arrays;
    }


    /**
     * Sets the arrays value for this StringArrayArray.
     * 
     * @param arrays
     */
    public void setArrays(org.gcube.common.core.types.StringArray[] arrays) {
        this.arrays = arrays;
    }

    public org.gcube.common.core.types.StringArray getArrays(int i) {
        return this.arrays[i];
    }

    public void setArrays(int i, org.gcube.common.core.types.StringArray _value) {
        this.arrays[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StringArrayArray)) return false;
        StringArrayArray other = (StringArrayArray) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.arrays==null && other.getArrays()==null) || 
             (this.arrays!=null &&
              java.util.Arrays.equals(this.arrays, other.getArrays())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getArrays() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getArrays());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getArrays(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StringArrayArray.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://gcube-system.org/namespaces/common/core/types", "stringArrayArray"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arrays");
        elemField.setXmlName(new javax.xml.namespace.QName("http://gcube-system.org/namespaces/common/core/types", "arrays"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://gcube-system.org/namespaces/common/core/types", "stringArray"));
        typeDesc.addFieldDesc(elemField);
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

}
