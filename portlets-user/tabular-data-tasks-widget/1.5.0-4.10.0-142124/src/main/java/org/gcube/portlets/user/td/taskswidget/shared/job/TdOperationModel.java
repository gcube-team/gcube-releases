/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.shared.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 18, 2013
 * 
 */
public class TdOperationModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 865922279808567237L;

	private String operationId;

	private String name;

	private String description;
	
	private List<String> parameters = new ArrayList<String>();

//	public static enum OperationScopeModel {
//		VOID, TABLE, COLUMN
//	}
//
//	public static enum OperationTypeModel {
//		TRANSFORMATION, VALIDATION, IMPORT, EXPORT
//	}


	public TdOperationModel(String id, String name, String description) {
		this.operationId = id;
		this.name = name;
		this.description = description;
	}

	/**
	 * 
	 */
	public TdOperationModel() {
	}

	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TdOperationModel other = (TdOperationModel) obj;
		if (operationId == null) {
			if (other.operationId != null)
				return false;
		} else if (!operationId.equals(other.operationId))
			return false;
		return true;
	}


	/**
	 * @param string
	 */
	public void addParameter(String parameter) {
		parameters.add(parameter);
		
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdOperationModel [operationId=");
		builder.append(operationId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", parameters=");
		builder.append(parameters);
		builder.append("]");
		return builder.toString();
	}

}