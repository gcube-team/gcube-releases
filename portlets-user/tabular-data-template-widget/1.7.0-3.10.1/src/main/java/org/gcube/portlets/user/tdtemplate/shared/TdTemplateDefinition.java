/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 20, 2014
 *
 */
public class TdTemplateDefinition implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6119123316492956065L;
	
	
	private String templateType;
	private String templateName;
	private String templateDescription;
	private String agency;
	private String onError;


	private TdTTemplateType tdTTemplateType;
	
	
	private Long serverId; //IS SERVER ID

	
	/**
	 * 
	 */
	public TdTemplateDefinition() {
	}

	/**
	 * 
	 * @param templateName
	 * @param templateDescription
	 * @param templateType
	 * @param agency
	 * @param onError
	 */
	public TdTemplateDefinition(String templateName, String templateDescription, String templateType, String agency, String onError) {
		this.templateType = templateType;
		this.templateName = templateName;
		this.templateDescription = templateDescription;
		this.agency = agency;
		this.onError = onError;
	}

	public String getTemplateType() {
		return templateType;
	}
	public String getTemplateName() {
		return templateName;
	}
	public String getTemplateDescription() {
		return templateDescription;
	}
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	
	public void setTemplateType(TdTTemplateType tdTTemplateType) {
		this.tdTTemplateType = tdTTemplateType;
	}
	
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public void setTemplateDescription(String templateDescription) {
		this.templateDescription = templateDescription;
	}

	public String getAgency() {
		return agency;
	}
	public String getOnError() {
		return onError;
	}
	public void setAgency(String agency) {
		this.agency = agency;
	}
	public void setOnError(String onError) {
		this.onError = onError;
	}

	public TdTTemplateType getTdTTemplateType() {
		return tdTTemplateType;
	}

	public Long getServerId() {
		return serverId;
	}

	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTemplateDefinition [templateType=");
		builder.append(templateType);
		builder.append(", templateName=");
		builder.append(templateName);
		builder.append(", templateDescription=");
		builder.append(templateDescription);
		builder.append(", agency=");
		builder.append(agency);
		builder.append(", onError=");
		builder.append(onError);
		builder.append(", tdTTemplateType=");
		builder.append(tdTTemplateType);
		builder.append(", serverId=");
		builder.append(serverId);
		builder.append("]");
		return builder.toString();
	}
}
