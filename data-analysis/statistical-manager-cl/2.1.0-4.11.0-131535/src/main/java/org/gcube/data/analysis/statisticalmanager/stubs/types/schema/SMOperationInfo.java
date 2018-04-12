package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = TYPES_NAMESPACE)
public class SMOperationInfo {

	@XmlElement(namespace = TYPES_NAMESPACE)
	private int status;

	@XmlElement(namespace = TYPES_NAMESPACE)
	private String percentage;

	public SMOperationInfo() {
    }

    public SMOperationInfo(
           String percentage,
           int status) {
           this.status = status;
           this.percentage = percentage;
    }

	
	
	public void status(int status)
	{
		this.status=status;
	}
	
	public int status()
	{
		return status;
		
	}
	
	public void percentage(String percentage)
	{
		this.percentage=percentage;
	}
	
	public String percentage()
	{
		return percentage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMOperationInfo [status=");
		builder.append(status);
		builder.append(", percentage=");
		builder.append(percentage);
		builder.append("]");
		return builder.toString();
	}

	
	
}
