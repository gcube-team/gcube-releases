package org.gcube.data.analysis.tabulardata.model.table.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.table.TableType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DatasetViewTableType extends TableType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8496761087349245412L;

	@Override
	public String getCode() {
		return "DATASETVIEW";
	}

	@Override
	public String getName() {
		return "Dataset View";
	}

}
