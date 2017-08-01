package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FieldArray;

@XmlRootElement
public class GetPhylogenyRequestType {

	private FieldArray fieldList;
	private Field toSelect;
	private PagedRequestSettings pagedRequestSettings;
	
	
	public GetPhylogenyRequestType() {
		// TODO Auto-generated constructor stub
	}


	public GetPhylogenyRequestType(FieldArray fieldList, Field toSelect,
			PagedRequestSettings pagedRequestSettings) {
		super();
		this.fieldList = fieldList;
		this.toSelect = toSelect;
		this.pagedRequestSettings = pagedRequestSettings;
	}


	/**
	 * @return the fieldList
	 */
	public FieldArray fieldList() {
		return fieldList;
	}


	/**
	 * @param fieldList the fieldList to set
	 */
	public void fieldList(FieldArray fieldList) {
		this.fieldList = fieldList;
	}


	/**
	 * @return the toSelect
	 */
	public Field toSelect() {
		return toSelect;
	}


	/**
	 * @param toSelect the toSelect to set
	 */
	public void toSelect(Field toSelect) {
		this.toSelect = toSelect;
	}


	/**
	 * @return the pagedRequestSettings
	 */
	public PagedRequestSettings pagedRequestSettings() {
		return pagedRequestSettings;
	}


	/**
	 * @param pagedRequestSettings the pagedRequestSettings to set
	 */
	public void pagedRequestSettings(PagedRequestSettings pagedRequestSettings) {
		this.pagedRequestSettings = pagedRequestSettings;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GetPhylogenyRequestType [fieldList=");
		builder.append(fieldList);
		builder.append(", toSelect=");
		builder.append(toSelect);
		builder.append(", pagedRequestSettings=");
		builder.append(pagedRequestSettings);
		builder.append("]");
		return builder.toString();
	}
	
	
}
