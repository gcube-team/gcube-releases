package org.gcube.data.analysis.tabulardata.model.metadata.column;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationReferencesMetadata implements ColumnMetadata {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9022171393208058355L;

	private List<ColumnLocalId> validationReferenceColumn = new ArrayList<ColumnLocalId>();
	
	
	public ValidationReferencesMetadata(ColumnLocalId... columnId){
		for(ColumnLocalId id:columnId){
			this.validationReferenceColumn.add(id);
		}
	}
	
	public ValidationReferencesMetadata(Column... column){
		for(Column col:column){
			this.validationReferenceColumn.add(col.getLocalId());
		}
	}
	
	
	@SuppressWarnings("unused")
	private ValidationReferencesMetadata(){}	
	
	/**
	 * @return the validationReferenceColumn
	 */
	public List<ColumnLocalId> getValidationReferenceColumn() {
		return validationReferenceColumn;
	}

	@Override
	public boolean isInheritable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValidationReferencesMetadata [validationReferenceColumn="
				+ validationReferenceColumn + "]";
	}

	
	
}
