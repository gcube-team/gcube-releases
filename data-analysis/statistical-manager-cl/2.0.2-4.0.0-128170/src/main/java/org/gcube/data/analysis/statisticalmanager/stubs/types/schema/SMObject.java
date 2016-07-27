package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement()//,name="SMObject")
//@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="SMObject", namespace=TYPES_NAMESPACE)
public class SMObject extends SMResource implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7996882341565893426L;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String url;
	
	public SMObject() {
		super();
		
    }

    public SMObject(
           String url) {

           this.url = url;
    }
    
    public String url() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void url(String value) {
        this.url = value;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMObject [url=");
		builder.append(url);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	@Deprecated
	public boolean __hashCodeCalc = false;
    
    
}
