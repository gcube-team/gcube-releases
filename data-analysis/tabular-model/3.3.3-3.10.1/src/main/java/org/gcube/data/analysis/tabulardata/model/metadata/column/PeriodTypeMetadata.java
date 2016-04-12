package org.gcube.data.analysis.tabulardata.model.metadata.column;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.time.PeriodType;


@XmlRootElement(name="PeriodTypeMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class PeriodTypeMetadata implements ColumnMetadata{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7913053123943691090L;
	private PeriodType type;
	
	@Override
	public boolean isInheritable() {
		return true;
	}

	@SuppressWarnings("unused")
	private PeriodTypeMetadata() {
	}

	public PeriodTypeMetadata(PeriodType type) {
		super();
		this.type = type;
	}
	
	public PeriodType getType() {
		return type;
	}
	public void setType(PeriodType type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeriodTypeMetadata other = (PeriodTypeMetadata) obj;
		if (type != other.type)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PeriodTypeMetadata [type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
	
}
