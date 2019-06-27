/**
 *
 */

package org.gcube.common.workspacetaskexecutor.shared.dataminer;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.gcube.common.workspacetaskexecutor.shared.BaseTaskConfiguration;
import org.gcube.common.workspacetaskexecutor.shared.TaskParameter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class TaskConfiguration.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it May 2, 2018
 */
public class TaskConfiguration implements BaseTaskConfiguration, Serializable {

	/**
	 *
	 */
	@JsonIgnoreProperties
	public static final String FIELD_CONFIGURATION_KEY = "configurationKey";
	/**
	 *
	 */
	private static final long serialVersionUID = -3380573762288127547L;
	private String taskId;
	private String taskName;
	@JsonIgnoreProperties
	private String taskDescription; // optional
	/*
	 * The encrypted VRE Token of the user in the VRE where submit the
	 * computation
	 */
	private String scope;
	private String maskedToken;
	private String workspaceItemId;
	@JsonIgnoreProperties
	private List<TaskParameter> listParameters; // optional
	private String configurationKey;
	private String owner;


	/**
	 * Instantiates a new task configuration.
	 */
	public TaskConfiguration() {

	}

	/**
	 * Instantiates a new task configuration.
	 *
	 * @param configurationKey            the configuration key
	 * @param taskId            the task id
	 * @param taskName the task name
	 * @param taskDescription            the task description
	 * @param owner the owner
	 * @param scope the scope
	 * @param maskedToken            the token
	 * @param workspaceItemId            the workspace item id
	 * @param listParameters            the map parameters
	 */
	public TaskConfiguration(
		String configurationKey, String taskId, String taskName, String taskDescription, String owner, String scope,
		String maskedToken, String workspaceItemId,
		List<TaskParameter> listParameters) {

		setConfigurationKey(configurationKey);
		this.taskId = taskId;
		this.taskName = taskName;
		this.taskDescription = taskDescription;
		this.owner = owner;
		this.scope = scope;
		this.maskedToken = maskedToken;
		this.workspaceItemId = workspaceItemId;
		this.listParameters = listParameters;
	}


	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {

		return scope;
	}


	/**
	 * Sets the scope.
	 *
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {

		this.scope = scope;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.shared.BaseTaskConfiguration#
	 * getAccessKey()
	 */
	@Override
	public String getConfigurationKey() {

		if (configurationKey == null)
			configurationKey = hashCode() + "";
		return configurationKey;
	}

	/**
	 * Sets the configuration key.
	 *
	 * @param configurationKey the new configuration key
	 */
	public void setConfigurationKey(String configurationKey) {

		this.configurationKey = configurationKey;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.shared.BaseTaskConfiguration#getOwner()
	 */
	@Override
	public String getOwner() {

		return owner;
	}


	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {

		this.owner = owner;
	}
	/*
	 * (non-Javadoc)
	 * @see org.gcube.common.workspacetaskexecutor.TaskConfiguration#getTaskId()
	 */
	@Override
	public String getTaskId() {

		return taskId;
	}

	/**
	 * Gets the task description.
	 *
	 * @return the taskDescription
	 */
	public String getTaskDescription() {

		return taskDescription;
	}

	/**
	 * Gets the workspace item id.
	 *
	 * @return the workspaceItemId
	 */
	public String getWorkspaceItemId() {

		return workspaceItemId;
	}


	/**
	 * Gets the list parameters.
	 *
	 * @return the listParameters
	 */
	public List<TaskParameter> getListParameters() {

		return listParameters;
	}


	/**
	 * Sets the list parameters.
	 *
	 * @param listParameters the listParameters to set
	 */
	public void setListParameters(List<TaskParameter> listParameters) {

		this.listParameters = listParameters;
	}

	/**
	 * Sets the task description.
	 *
	 * @param taskDescription
	 *            the taskDescription to set
	 */
	public void setTaskDescription(String taskDescription) {

		this.taskDescription = taskDescription;
	}

	/**
	 * Sets the task id.
	 *
	 * @param taskId
	 *            the taskId to set
	 */
	public void setTaskId(String taskId) {

		this.taskId = taskId;
	}

	/**
	 * Sets the workspace item id.
	 *
	 * @param workspaceItemId
	 *            the workspaceItemId to set
	 */
	public void setWorkspaceItemId(String workspaceItemId) {

		this.workspaceItemId = workspaceItemId;
	}



	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int hash = 1;
		hash = hash * 13 + (taskId == null ? 0 : taskId.hashCode());
		hash =
			hash * 17 +
				(workspaceItemId == null ? 0 : workspaceItemId.hashCode());
		hash = hash * new Random().nextInt();
		return hash;
	}


	/**
	 * Gets the masked token.
	 *
	 * @return the maskedToken
	 */
	public String getMaskedToken() {

		return maskedToken;
	}




	/**
	 * @return the taskName
	 */
	public String getTaskName() {

		return taskName;
	}


	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {

		this.taskName = taskName;
	}

	/**
	 * Sets the masked token.
	 *
	 * @param maskedToken the maskedToken to set
	 */
	public void setMaskedToken(String maskedToken) {

		this.maskedToken = maskedToken;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TaskConfiguration [taskId=");
		builder.append(taskId);
		builder.append(", taskName=");
		builder.append(taskName);
		builder.append(", taskDescription=");
		builder.append(taskDescription);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", maskedToken=");
		builder.append(maskedToken);
		builder.append(", workspaceItemId=");
		builder.append(workspaceItemId);
		builder.append(", listParameters=");
		builder.append(listParameters);
		builder.append(", configurationKey=");
		builder.append(configurationKey);
		builder.append(", owner=");
		builder.append(owner);
		builder.append("]");
		return builder.toString();
	}

}
