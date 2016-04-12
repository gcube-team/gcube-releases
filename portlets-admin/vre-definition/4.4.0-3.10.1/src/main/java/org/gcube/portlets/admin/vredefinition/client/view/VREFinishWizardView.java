package org.gcube.portlets.admin.vredefinition.client.view;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.vredefinition.client.AppController;
import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredefinition.client.presenter.VREFinishWizardPresenter;
import org.gcube.portlets.admin.vredefinition.shared.ExternalResourceModel;
import org.gcube.portlets.admin.vredefinition.shared.VRECollectionBean;
import org.gcube.portlets.admin.vredefinition.shared.VREDescriptionBean;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;


public class VREFinishWizardView extends Composite implements VREFinishWizardPresenter.Display{

	public VREFinishWizardView(VREDescriptionBean vre, List<ModelData> categories) {




		VerticalPanel container = new VerticalPanel();
		container.setScrollMode(Scroll.AUTO);


		container.setStyleAttribute("padding", "10px");
		container.setTableWidth("100%");

		TableData tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.MIDDLE);

		//STYLE=\"font-size:18px;\"
		String vreDescription = "<div STYLE=\"font-size:18px;\" align=\"center\" ><h1> Summary </h1></div>";
		vreDescription+="<div><b>Name: </b>" + vre.getName() +"</div>";
		vreDescription+="<div><b>VRE Designer: </b>" + vre.getDesigner() +"</div>";
		vreDescription+="<div><b>VRE Manager: </b>" + vre.getManager() +"</div>";
		DateTimeFormat fmt = DateTimeFormat.getFormat("MMMM dd, yyyy");   
		vreDescription+="<div ><b>From: </b>" 
				+ fmt.format(vre.getStartTime())
				+ " <b> To: </b>"
				+ fmt.format(vre.getEndTime())
				+ "</div>";

		vreDescription+= "<br />";
		vreDescription+="<div><b>Description: </b>" + vre.getDescription() +"</div>";
		vreDescription += "<br />";

		container.add(new HTML(vreDescription, true ));	

		List<ModelData> functionalitiesSelected = new ArrayList<ModelData>();
		for (ModelData category : categories) {
			for(ModelData func : ((VREFunctionalityModel)category).getChildren()) {
				if(((VREFunctionalityModel)func).isSelected())
					functionalitiesSelected.add(func);
			}
		}

		int sizeFunctionalities = functionalitiesSelected.size();



		String table = "<table width=\"100%\" align=\"center\" >";
		table += "<tr>";

		if (sizeFunctionalities > 0) {
			table += "<td width=\"50%\" >";
			table += "<div STYLE=\"font-size:14px;\"><h1><u>Functionalities</u></h1></div>";
			table += "</td>";
			table += "</tr>";
		}

		table += "<tr>";
		table += "<td width=\"50%\" >";
		table += "</td>";
		table += "<td width=\"50%\" >";
		table += "</td>";
		table += "</tr>";


		int maxSize = Math.max(0, sizeFunctionalities);

		for(int i = 0; i < maxSize; i++) {

			table += "<tr>";
			if (sizeFunctionalities > 0) { 
				table += "<td width=\"50%\">";
				ModelData functionality = null;
				if(i < sizeFunctionalities) {
					functionality = functionalitiesSelected.get(i);
					table +=" - <span style=\"font-weight: bold; color: #333;\">" + functionality.get("name")+"</span>";
					List<ExternalResourceModel> extResources = AppController.getAppController().getExtResourcesFromApp(functionality.get("id").toString());
					if (extResources != null) {
						table += "<br />";
						for (ExternalResourceModel ext : extResources) {
							if (ext.isSelected()) {
								table += "<span class=\"extres\" >" + ext.getName() + "</span>";
								table += "<br />";
							}
						}
						table += "<br /><br />";
					}
				}
				table += "</td>";
			}

			table += "</tr>";
		}
		table += "</table>";

		container.add(new HTML(table, true));

		initComponent(container);
	}

	@Override
	public Widget asWidget() {
		return this;
	}


}
