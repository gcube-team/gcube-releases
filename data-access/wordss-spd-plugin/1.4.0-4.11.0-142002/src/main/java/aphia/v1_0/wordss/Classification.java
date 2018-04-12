/**
 * Classification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC2 Nov 16, 2004 (12:19:44 EST) WSDL2Java emitter.
 */

package aphia.v1_0.wordss;

public class Classification  implements java.io.Serializable {
    private int aphiaID;
    private java.lang.String rank;
    private java.lang.String scientificname;
    private aphia.v1_0.wordss.Classification child;

    public Classification() {
    }

    public Classification(
           int aphiaID,
           java.lang.String rank,
           java.lang.String scientificname,
           aphia.v1_0.wordss.Classification child) {
           this.aphiaID = aphiaID;
           this.rank = rank;
           this.scientificname = scientificname;
           this.child = child;
    }


    /**
     * Gets the aphiaID value for this Classification.
     * 
     * @return aphiaID
     */
    public int getAphiaID() {
        return aphiaID;
    }


    /**
     * Sets the aphiaID value for this Classification.
     * 
     * @param aphiaID
     */
    public void setAphiaID(int aphiaID) {
        this.aphiaID = aphiaID;
    }


    /**
     * Gets the rank value for this Classification.
     * 
     * @return rank
     */
    public java.lang.String getRank() {
        return rank;
    }


    /**
     * Sets the rank value for this Classification.
     * 
     * @param rank
     */
    public void setRank(java.lang.String rank) {
        this.rank = rank;
    }


    /**
     * Gets the scientificname value for this Classification.
     * 
     * @return scientificname
     */
    public java.lang.String getScientificname() {
        return scientificname;
    }


    /**
     * Sets the scientificname value for this Classification.
     * 
     * @param scientificname
     */
    public void setScientificname(java.lang.String scientificname) {
        this.scientificname = scientificname;
    }


    /**
     * Gets the child value for this Classification.
     * 
     * @return child
     */
    public aphia.v1_0.wordss.Classification getChild() {
        return child;
    }


    /**
     * Sets the child value for this Classification.
     * 
     * @param child
     */
    public void setChild(aphia.v1_0.wordss.Classification child) {
        this.child = child;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Classification)) return false;
        Classification other = (Classification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.aphiaID == other.getAphiaID() &&
            ((this.rank==null && other.getRank()==null) || 
             (this.rank!=null &&
              this.rank.equals(other.getRank()))) &&
            ((this.scientificname==null && other.getScientificname()==null) || 
             (this.scientificname!=null &&
              this.scientificname.equals(other.getScientificname()))) &&
            ((this.child==null && other.getChild()==null) || 
             (this.child!=null &&
              this.child.equals(other.getChild())));
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
        _hashCode += getAphiaID();
        if (getRank() != null) {
            _hashCode += getRank().hashCode();
        }
        if (getScientificname() != null) {
            _hashCode += getScientificname().hashCode();
        }
        if (getChild() != null) {
            _hashCode += getChild().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Classification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://aphia/v1.0", "Classification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aphiaID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "AphiaID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rank");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rank"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scientificname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "scientificname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("child");
        elemField.setXmlName(new javax.xml.namespace.QName("", "child"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://aphia/v1.0", "Classification"));
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
