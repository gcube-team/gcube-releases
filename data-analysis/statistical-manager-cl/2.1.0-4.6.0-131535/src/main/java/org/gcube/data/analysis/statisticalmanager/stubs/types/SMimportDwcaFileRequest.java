package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = TYPES_WSDL_NAMESPACE)
public class SMimportDwcaFileRequest extends SMimportFileRequest {
	@XmlElement()
	private String baseFileName;
	@XmlElement()
	private String vernacularLocator;
	@XmlElement()
	private String taxaLocator;

	public SMimportDwcaFileRequest() {
		super();
	}

	public SMimportDwcaFileRequest(String baseFileName, String taxaLocator,
			String vernacularLocator) {
		this.baseFileName = baseFileName;
		this.vernacularLocator = vernacularLocator;
		this.taxaLocator = taxaLocator;
	}

	public void baseFileName(String baseFileName) {
		this.baseFileName = baseFileName;
	}

	public String baseFileName() {
		return baseFileName;
	}

	public void vernacularLocator(String vernacularLocator) {
		this.vernacularLocator = vernacularLocator;
	}

	public String vernacularLocator() {
		return vernacularLocator;
	}

	public void taxaLocator(String taxaLocator) {
		this.taxaLocator = taxaLocator;
	}

	public String taxaLocator() {
		return taxaLocator;
	}
}
