package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.elements;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;

@XmlRootElement
public class CalculateEnvelopeFromCellSelectionRequestType {

	@XmlElement
	private String speciesID;
	@XmlElement
	private StringArray cellIds;
	
	public CalculateEnvelopeFromCellSelectionRequestType() {
		// TODO Auto-generated constructor stub
	}

	public CalculateEnvelopeFromCellSelectionRequestType(String speciesID,
			StringArray cellIds) {
		super();
		this.speciesID = speciesID;
		this.cellIds = cellIds;
	}

	/**
	 * @return the speciesID
	 */
	public String speciesID() {
		return speciesID;
	}

	/**
	 * @param speciesID the speciesID to set
	 */
	public void speciesID(String speciesID) {
		this.speciesID = speciesID;
	}

	/**
	 * @return the cellIds
	 */
	public StringArray cellIds() {
		return cellIds;
	}

	/**
	 * @param cellIds the cellIds to set
	 */
	public void cellIds(StringArray cellIds) {
		this.cellIds = cellIds;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CalculateEnvelopeFromCellSelectionRequestType [speciesID=");
		builder.append(speciesID);
		builder.append(", cellIds=");
		builder.append(cellIds);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
