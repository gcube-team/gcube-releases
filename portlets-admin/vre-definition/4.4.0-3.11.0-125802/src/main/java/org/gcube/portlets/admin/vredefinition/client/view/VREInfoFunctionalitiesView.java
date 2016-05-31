package org.gcube.portlets.admin.vredefinition.client.view;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.vredefinition.client.AppController;
import org.gcube.portlets.admin.vredefinition.client.VREDefinitionServiceAsync;
import org.gcube.portlets.admin.vredefinition.client.model.VREFunctionalityModel;
import org.gcube.portlets.admin.vredefinition.client.presenter.VREInfoFunctionalitiesPresenter;
import org.gcube.portlets.admin.vredefinition.shared.ExternalResourceModel;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * @version 0.2 Sep 2012
 * 
 */
public class VREInfoFunctionalitiesView extends ContentPanel implements VREInfoFunctionalitiesPresenter.Display{

	private ContentPanel gridPanel;
	private String toAdd = "";
	
	private HTML info = new HTML();

	public VREInfoFunctionalitiesView() {
		setHeaderVisible(false);
		setBodyBorder(true);
		String toAdd = "</br><h3> Select a functionality to show info </h3>";
		add(new HTML(toAdd, true ));	
		setStyleAttribute("margin", "10px");
	}

	public VREInfoFunctionalitiesView(final VREFunctionalityModel functionality, VREDefinitionServiceAsync rpcService) {
		mask("Loading available resources ....");
		setHeaderVisible(false);
		setBodyBorder(false);
		toAdd = "<div class=\"theTitle\">" + functionality.getName() + "</div>";
		toAdd += "<div class=\"functionalityText\">" +  functionality.getDescription()+"</div>";
		info.setHTML(toAdd);
		add(info);	
		
		setStyleAttribute("margin", "10px");

		//get that from the server
		rpcService.getResourceCategoryByFunctionality(functionality.getId(), new AsyncCallback<ArrayList<ExternalResourceModel>>() {

			public void onSuccess(ArrayList<ExternalResourceModel> cats) {	
				if (cats.size() == 0) {
					unmask();
					toAdd += "<div class=\"theResources\">There are no external Resources to select for this functionality</div>";
					info.setHTML(toAdd);
				} else {
					//TODO: REMOVE THIS  BUT IT DOESNT RENDER OK OTHERWISE, Have no time to investigate it
					//if i loaded them yet
					toAdd += "<div class=\"theResources\">Available Resources for " + functionality.getName() + "</div>";
					info.setHTML(toAdd);
					if (AppController.getAppController().getExtResourcesFromApp(functionality.getId()) != null) {
						List<ExternalResourceModel> categs = AppController.getAppController().getExtResourcesFromApp(functionality.getId());
						addGridPanel(functionality, categs);
					} 
					else { 
						addGridPanel(functionality, cats);
					}
				}
			}
	
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub

			}
		});


	}

	private void addGridPanel(VREFunctionalityModel func, List<ExternalResourceModel> cats) {
		ResourcesGrid grid = new ResourcesGrid(func, cats);
		gridPanel = grid.getGrid();
		gridPanel.setHeight(260);
		add(gridPanel);
		gridPanel.layout(true);
		layout(true);
		setLayout(new FitLayout());
		unmask();
	}

}
