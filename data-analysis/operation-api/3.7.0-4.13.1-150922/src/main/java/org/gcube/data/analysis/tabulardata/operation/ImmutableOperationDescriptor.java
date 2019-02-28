package org.gcube.data.analysis.tabulardata.operation;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;

@XmlRootElement(name="OperationDescriptor")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImmutableOperationDescriptor implements OperationDescriptor {

	private OperationId operationId;

	private String name;

	private String description;

	private OperationScope scope;

	private OperationType type;

	private List<Parameter> parameters;

	@SuppressWarnings("unused")
	private ImmutableOperationDescriptor() {
	}

	public ImmutableOperationDescriptor(OperationId operationId, String name, String description, OperationScope scope,
			OperationType type, List<Parameter> parameters) {
		this.operationId = operationId;
		this.name = name;
		this.description = description;
		this.scope = scope;
		this.type = type;
		this.parameters = parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.data.analysis.tabulardata.operation.worker.OperationDescriptor
	 * #getOperationId()
	 */
	public OperationId getOperationId() {
		return operationId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.data.analysis.tabulardata.operation.worker.OperationDescriptor
	 * #getName()
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.data.analysis.tabulardata.operation.worker.OperationDescriptor
	 * #getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.data.analysis.tabulardata.operation.worker.OperationDescriptor
	 * #getScope()
	 */
	public OperationScope getScope() {
		return scope;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.data.analysis.tabulardata.operation.worker.OperationDescriptor
	 * #getType()
	 */
	public OperationType getType() {
		return type;
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImmutableOperationDescriptor [operationId=");
		builder.append(operationId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", type=");
		builder.append(type);
		builder.append(", parameters=");
		builder.append(parameters);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((operationId == null) ? 0 : operationId.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ImmutableOperationDescriptor other = (ImmutableOperationDescriptor) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (operationId == null) {
			if (other.operationId != null)
				return false;
		} else if (!operationId.equals(other.operationId))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (scope != other.scope)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
