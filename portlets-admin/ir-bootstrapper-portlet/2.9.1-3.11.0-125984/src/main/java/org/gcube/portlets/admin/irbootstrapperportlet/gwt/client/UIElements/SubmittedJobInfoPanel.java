package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElements;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.SubmittedJobInfoUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.TaskInfoUIElement;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SubmittedJobInfoPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();

	private SubmittedJobInfoUIElement data;

	public SubmittedJobInfoPanel(SubmittedJobInfoUIElement data) {
		this.data = data;
		this.mainPanel.setWidth("100%");
		displayData();

		initWidget(mainPanel);
	}

	private void displayData() {
		if (this.data != null) {
			FlexTable infoTable = new FlexTable();
			int index = 0;
			infoTable.setWidget(0, 0, new HTML("<span style=\"color:darkblue\">Job Execution Info</span>"));
			infoTable.getFlexCellFormatter().setColSpan(0, 0, 2);
			infoTable.getFlexCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
			index++;
			infoTable.setWidget(1, 0, new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
			infoTable.getFlexCellFormatter().setColSpan(1, 0, 2);
			infoTable.getFlexCellFormatter().setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
			index++;
			infoTable.setWidget(index, 0, new HTML("<span style=\"color:darkblue\">" + "Job Name: " + "</span>"));
			infoTable.setWidget(index, 1,  new HTML("<span>" + this.data.getName() + "</span>"));
			index++;
			infoTable.setWidget(index, 0, new HTML("<span style=\"color:darkblue\">" + "Job Type: " + "</span>"));
			infoTable.setWidget(index, 1,  new HTML("<span>" + this.data.getType() + "</span>"));
			index++;
			infoTable.setWidget(index, 0, new HTML("<span style=\"color:darkblue\">" + "Job ID: " + "</span>"));
			infoTable.setWidget(index, 1,  new HTML("<span>" + this.data.getUid() + "</span>"));
			index++;
			infoTable.setWidget(index, 0, new HTML("<span style=\"color:darkblue\">" + "Description: " + "</span>"));
			infoTable.setWidget(index, 1,  new HTML("<span>" + this.data.getDescription() + "</span>"));
			index++;
			infoTable.setWidget(index, 0, new HTML("<span style=\"color:darkblue\">" + "Started at: " + "</span>"));
			infoTable.setWidget(index, 1,  new HTML("<span>" + this.data.getStartDate() + "</span>"));
			index++;
			infoTable.setWidget(index, 0, new HTML("<span style=\"color:darkblue\">" + "Finished at: " + "</span>"));
			infoTable.setWidget(index, 1,  new HTML("<span>" + this.data.getEndDate() + "</span>"));
			index++;
			infoTable.setWidget(index, 0, new HTML("<span style=\"color:darkblue\">" + "Submitter: " + "</span>"));
			infoTable.setWidget(index, 1,  new HTML("<span>" + this.data.getAuthor() + "</span>"));
			index++;
			infoTable.setWidget(index, 0, new HTML("<span style=\"color:darkblue\">" + "Status: " + "</span>"));
			infoTable.setWidget(index, 1,  new HTML("<span>" + this.data.getStatus() + "</span>"));
			index++;
			infoTable.setWidget(index, 0, new HTML("<span style=\"color:darkblue\">" + "In scope: " + "</span>"));
			infoTable.setWidget(index, 1,  new HTML("<span>" + this.data.getScope() + "</span>"));
			index++;
			infoTable.setWidget(index, 0, new HTML("<span style=\"color:darkgreen\">Workflow Messages</span>"));
			infoTable.getFlexCellFormatter().setColSpan(index, 0, 2);
			infoTable.getFlexCellFormatter().setAlignment(index, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
			index++;
			infoTable.setWidget(index, 0, new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
			infoTable.getFlexCellFormatter().setColSpan(index, 0, 2);
			infoTable.getFlexCellFormatter().setAlignment(index, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
			index++; 
			if (this.data.getEntries() != null) {
				int i = index;
				for (TaskInfoUIElement tInfo : this.data.getEntries()) {
					infoTable.setWidget(i, 0, new HTML("<span style=\"color:darkblue\">" + tInfo.getLevel() + " </span>"));
					infoTable.setWidget(i, 1,  new HTML("<span>" + tInfo.getMessage() + "</span>"));
					i++;
				}
			}

			infoTable.setCellSpacing(3);
			infoTable.setCellPadding(2);
			infoTable.setBorderWidth(0);
			infoTable.setWidth("700px");
			
			this.mainPanel.add(infoTable);
			this.mainPanel.setCellHorizontalAlignment(infoTable, HasHorizontalAlignment.ALIGN_CENTER);
		}
	}
}
