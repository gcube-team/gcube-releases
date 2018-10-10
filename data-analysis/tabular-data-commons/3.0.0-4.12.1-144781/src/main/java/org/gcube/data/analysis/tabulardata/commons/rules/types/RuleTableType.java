package org.gcube.data.analysis.tabulardata.commons.rules.types;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RuleTableType extends RuleType{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Class<? extends DataType>> mappingPlaceholderIdDataType;
		
	public RuleTableType(
			Map<String, Class<? extends DataType>> mappingPlaceholderIdDataType) {
		super();
		this.mappingPlaceholderIdDataType = mappingPlaceholderIdDataType;
	}

	protected RuleTableType() {
		super();
	}




	@Override
	public Map<String, Class<? extends DataType>> getInternalType() {
		return mappingPlaceholderIdDataType;
	}

	
	
}
