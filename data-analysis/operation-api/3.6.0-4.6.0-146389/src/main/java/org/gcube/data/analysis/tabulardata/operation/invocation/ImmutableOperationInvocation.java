package org.gcube.data.analysis.tabulardata.operation.invocation;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.ImmutableOperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.invocation.adapter.MapAdapter;

@XmlRootElement(name="OperationInvocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImmutableOperationInvocation implements OperationInvocation {

	private TableId targetTableId;

	private ColumnLocalId targetColumnId;

	@XmlElementRef(type=ImmutableOperationDescriptor.class)
	private OperationDescriptor operationDescriptor;

	@XmlJavaTypeAdapter(MapAdapter.class)
	private Map<String, Object> parameterInstances;

	@SuppressWarnings("unused")
	private ImmutableOperationInvocation() {
	}

	public ImmutableOperationInvocation(TableId targetTableId, ColumnLocalId targetColumnId,
			OperationDescriptor operationDescriptor, Map<String, Object> parameterInstances) {
		this.targetTableId = targetTableId;
		this.targetColumnId = targetColumnId;
		this.operationDescriptor = operationDescriptor;
		this.parameterInstances = parameterInstances;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.operation.worker.OperationInvocation#getTargetTableId()
	 */
	public TableId getTargetTableId() {
		return targetTableId;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.operation.worker.OperationInvocation#getTargetColumnId()
	 */
	public ColumnLocalId getTargetColumnId() {
		return targetColumnId;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.operation.worker.OperationInvocation#getOperationDescriptor()
	 */
	public OperationDescriptor getOperationDescriptor() {
		return operationDescriptor;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.operation.worker.OperationInvocation#getParameterInstances()
	 */
	public Map<String, Object> getParameterInstances() {
		return parameterInstances;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImmutableOperationInvocation [\n\ttargetTableId=");
		builder.append(targetTableId);
		builder.append(",\n\ttargetColumnId=");
		builder.append(targetColumnId);
		builder.append(",\n\toperationDescriptor=");
		builder.append(operationDescriptor);
		builder.append(",\n\tparameterInstances=");
		builder.append(parameterInstances);
		builder.append("\n]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operationDescriptor == null) ? 0 : operationDescriptor.hashCode());
		result = prime * result + ((parameterInstances == null) ? 0 : parameterInstances.hashCode());
		result = prime * result + ((targetColumnId == null) ? 0 : targetColumnId.hashCode());
		result = prime * result + ((targetTableId == null) ? 0 : targetTableId.hashCode());
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
		ImmutableOperationInvocation other = (ImmutableOperationInvocation) obj;
		if (operationDescriptor == null) {
			if (other.operationDescriptor != null)
				return false;
		} else if (!operationDescriptor.equals(other.operationDescriptor))
			return false;
		if (parameterInstances == null) {
			if (other.parameterInstances != null)
				return false;
		} else if (!parameterInstances.equals(other.parameterInstances))
			return false;
		if (targetColumnId == null) {
			if (other.targetColumnId != null)
				return false;
		} else if (!targetColumnId.equals(other.targetColumnId))
			return false;
		if (targetTableId == null) {
			if (other.targetTableId != null)
				return false;
		} else if (!targetTableId.equals(other.targetTableId))
			return false;
		return true;
	}

	

}
