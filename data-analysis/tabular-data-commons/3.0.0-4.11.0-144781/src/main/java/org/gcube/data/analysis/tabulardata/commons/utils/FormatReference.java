package org.gcube.data.analysis.tabulardata.commons.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.ReferenceObject;
import org.gcube.data.analysis.tabulardata.model.DataTypeFormats;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FormatReference extends ReferenceObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4168658426393768681L;

	
	private String formatIdentifier;
	
	@SuppressWarnings("unused")
	private FormatReference() {}
	
	public FormatReference(String formatIdentifier) {
		super();
		this.formatIdentifier = formatIdentifier;
	}

	public String getFormatIdentifier() {
		return formatIdentifier;
	}

	@Override
	public boolean check(Class<? extends DataType> datatype) {
		return DataTypeFormats.getFormatPerId(datatype, this.formatIdentifier)!=null;	
	}

	
}
