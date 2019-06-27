package org.gcube.portlets.user.td.tablewidget.client.validation.tree;

import org.gcube.portlets.user.td.gwtservice.shared.task.InvocationS;
import org.gcube.portlets.user.td.gwtservice.shared.tr.ConditionCode;

/**
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ValidationDto extends BaseDto {

	private static final long serialVersionUID = -4353641080571614057L;

	private String title;
	private String description;
	private Boolean valid;
	private InvocationS invocation;
	private ConditionCode conditionCode;
	private String validationColumnColumnId;

	public ValidationDto() {

	}

	/**
	 * 
	 * @param id
	 * @param description
	 * @param valid
	 * @param conditionCode
	 * @param validationColumnColumnId
	 * @param invocation
	 */
	public ValidationDto(String id, String title, String description,
			Boolean valid, ConditionCode conditionCode,
			String validationColumnColumnId, InvocationS invocation) {
		super(id);
		this.title = title;
		this.description = description;
		this.valid = valid;
		this.conditionCode = conditionCode;
		this.validationColumnColumnId = validationColumnColumnId;
		this.invocation = invocation;

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public InvocationS getInvocation() {
		return invocation;
	}

	public void setInvocation(InvocationS invocation) {
		this.invocation = invocation;
	}

	public ConditionCode getConditionCode() {
		return conditionCode;
	}

	public void setConditionCode(ConditionCode conditionCode) {
		this.conditionCode = conditionCode;
	}

	public String getValidationColumnColumnId() {
		return validationColumnColumnId;
	}

	public void setValidationColumnColumnId(String validationColumnColumnId) {
		this.validationColumnColumnId = validationColumnColumnId;
	}

	@Override
	public String toString() {
		String response;
		if(title==null || title.isEmpty()){
			response=description;
		} else {
			response=title+": "+description;
		}
		return response;
	}

}
