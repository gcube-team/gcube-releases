package org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class PeriodDataType implements Serializable, Comparable<PeriodDataType> {

	private static final long serialVersionUID = -5172920999547673068L;

	private String name;
	private String label;
	private ArrayList<ValueDataFormat> timeDataFormats;

	public PeriodDataType() {

	}

	public PeriodDataType(String name, String label,
			ArrayList<ValueDataFormat> timeDataFormats) {
		super();
		this.name = name;
		this.label = label;
		this.timeDataFormats = timeDataFormats;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ArrayList<ValueDataFormat> getTimeDataFormats() {
		return timeDataFormats;
	}

	public void setTimeDataFormats(ArrayList<ValueDataFormat> timeDataFormats) {
		this.timeDataFormats = timeDataFormats;
	}

	@Override
	public int compareTo(PeriodDataType periodDataType) {
		return periodDataType.getName().compareTo(name);
	}
	
	@Override
	public String toString() {
		return "PeriodDataType [name=" + name + ", label=" + label
				+ ", timeDataFormats=" + timeDataFormats + "]";
	}
	

}
