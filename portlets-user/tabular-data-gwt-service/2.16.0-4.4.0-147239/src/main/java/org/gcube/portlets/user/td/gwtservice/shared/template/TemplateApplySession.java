package org.gcube.portlets.user.td.gwtservice.shared.template;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * Apply Template Session
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TemplateApplySession implements Serializable {

	private static final long serialVersionUID = -8834066207159106968L;
	private TemplateData templateData;
	private TRId trId;

	public TemplateApplySession() {
		super();

	}

	public TemplateApplySession(TemplateData templateData, TRId trId) {
		super();
		this.templateData = templateData;
		this.trId = trId;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public TemplateData getTemplateData() {
		return templateData;
	}

	public void setTemplateData(TemplateData templateData) {
		this.templateData = templateData;
	}

	@Override
	public String toString() {
		return "TemplateApplySession [templateData=" + templateData + ", trId="
				+ trId + "]";
	}

}
