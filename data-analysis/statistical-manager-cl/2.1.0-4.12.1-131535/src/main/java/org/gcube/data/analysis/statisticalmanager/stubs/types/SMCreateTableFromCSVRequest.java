package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace=TYPES_WSDL_NAMESPACE)

public class SMCreateTableFromCSVRequest extends SMCreateTableRequest{
	@XmlElement()
	private boolean hasHeader;
	@XmlElement()
	private String delimiter;
	@XmlElement()
	private String commentChar;
	
	 public SMCreateTableFromCSVRequest() {
		 super();
	    }

	    public SMCreateTableFromCSVRequest(
	           String commentChar,
	           String delimiter,
	           boolean hasHeader) {
	           this.hasHeader = hasHeader;
	           this.delimiter = delimiter;
	           this.commentChar = commentChar;
	    }

	
	public void hasHeader(boolean hasHeader)
	{
		this.hasHeader=hasHeader;
	}
	public boolean hasHeader()
	{
		return hasHeader;
	}
	
	
	public void delimiter(String delimiter)
	{
		this.delimiter=delimiter;
	}
	public String delimiter()
	{
		return delimiter;
	}

	
	public void commentChar(String commentChar)
	{
		this.commentChar=commentChar;
	}
	public String commentChar()
	{
		return commentChar;
	}
}
