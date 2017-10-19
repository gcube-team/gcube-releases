package org.gcube.data.analysis.tabulardata.operation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationId {

	private long value;

	@SuppressWarnings("unused")
	private OperationId() {
	}

	public OperationId(@SuppressWarnings("rawtypes") Class<? extends WorkerFactory> type) {
		value = type.getCanonicalName().hashCode();
	}

	public OperationId(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OperationId [value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (value ^ (value >>> 32));
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
		OperationId other = (OperationId) obj;
		if (value != other.value)
			return false;
		return true;
	}

}
