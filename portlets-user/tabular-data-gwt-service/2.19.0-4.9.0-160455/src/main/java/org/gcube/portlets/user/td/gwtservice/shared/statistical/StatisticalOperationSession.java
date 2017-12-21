/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.statistical;

import java.io.Serializable;
import java.util.Map;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class StatisticalOperationSession implements Serializable {

	private static final long serialVersionUID = -8968614490778086448L;
	private TRId trId;
	private Map<String, String> parameters;
	private String description;
	private String title;
	private String operatorId;
	private String operatorName;
	private String operatorBriefDescription;

	public StatisticalOperationSession() {
		super();
	}

	public StatisticalOperationSession(TRId trId,
			Map<String, String> parameters, String description, String title,
			String operatorId, String operatorName,
			String operatorBriefDescription) {
		super();
		this.trId = trId;
		this.parameters = parameters;
		this.description = description;
		this.title = title;
		this.operatorId = operatorId;
		this.operatorName = operatorName;
		this.operatorBriefDescription = operatorBriefDescription;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getOperatorBriefDescription() {
		return operatorBriefDescription;
	}

	public void setOperatorBriefDescription(String operatorBriefDescription) {
		this.operatorBriefDescription = operatorBriefDescription;
	}

	@Override
	public String toString() {
		return "StatisticalOperationSession [trId=" + trId + ", parameters="
				+ parameters + ", description=" + description + ", title="
				+ title + ", operatorId=" + operatorId + ", operatorName="
				+ operatorName + ", operatorBriefDescription="
				+ operatorBriefDescription + "]";
	}

	

}
