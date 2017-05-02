/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.portlets.user.tdtemplate.shared.ClientReportTemplateSaved;


/**
 * The Class ServerReportTemplateSaved.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 1, 2015
 */
public class ServerReportTemplateSaved {


	private ClientReportTemplateSaved clientReport;
	
	private List<TemplateColumn<? extends DataType>> listConvertedColumn;

	private Template template; //the generate template

	/**
	 * Instantiates a new server report template saved.
	 *
	 * @param clientReport the client report
	 * @param listConvertedColumn the list converted column
	 */
	public ServerReportTemplateSaved(ClientReportTemplateSaved clientReport, List<TemplateColumn<? extends DataType>> listConvertedColumn) {
		this.clientReport = clientReport;
		this.listConvertedColumn = listConvertedColumn;
	}

	
	/**
	 * Gets the client report.
	 *
	 * @return the client report
	 */
	public ClientReportTemplateSaved getClientReport() {
		return clientReport;
	}

	/**
	 * Gets the list converted column.
	 *
	 * @return the list converted column
	 */
	public List<TemplateColumn<? extends DataType>> getListConvertedColumn() {
		return listConvertedColumn;
	}

	/**
	 * Sets the client report.
	 *
	 * @param clientReport the new client report
	 */
	public void setClientReport(ClientReportTemplateSaved clientReport) {
		this.clientReport = clientReport;
	}

	/**
	 * Sets the list converted column.
	 *
	 * @param listConvertedColumn the new list converted column
	 */
	public void setListConvertedColumn(List<TemplateColumn<? extends DataType>> listConvertedColumn) {
		this.listConvertedColumn = listConvertedColumn;
	}

	/**
	 * Gets the template.
	 *
	 * @return the template
	 */
	public Template getTemplate() {
		return template;
	}


	/**
	 * Sets the template.
	 *
	 * @param template the template to set
	 */
	public void setTemplate(Template template) {
		this.template = template;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerReportTemplateSaved [clientReport=");
		builder.append(clientReport);
		builder.append(", listConvertedColumn=");
		builder.append(listConvertedColumn);
		builder.append(", template=");
		builder.append(template);
		builder.append("]");
		return builder.toString();
	}
}
