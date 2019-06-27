package org.gcube.data.analysis.tabulardata.model.table.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.table.TableType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericTableType extends TableType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 124599042863262853L;

	@Override
	public String getCode() {
		return "GENERIC";
	}

	@Override
	public String getName() {
		return "Generic";
	}

}
