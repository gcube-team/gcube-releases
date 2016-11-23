package org.gcube.data.analysis.tabulardata.model.metadata.table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

@XmlRootElement(name = "TimePeriodTypeMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimePeriodTypeMetadata implements TableMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3861276394910633807L;

	private PeriodType periodType;

	public TimePeriodTypeMetadata() {
		// TODO Auto-generated constructor stub
	}
	
	public TimePeriodTypeMetadata(PeriodType periodType) {
		this.periodType = periodType;
	}

	public PeriodType getPeriodType() {
		return periodType;
	}

	public boolean isInheritable() {
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimePeriodTypeMetadata [\n\tperiodType=");
		builder.append(periodType);
		builder.append("\n]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((periodType == null) ? 0 : periodType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimePeriodTypeMetadata other = (TimePeriodTypeMetadata) obj;
		if (periodType != other.periodType)
			return false;
		return true;
	}

}
