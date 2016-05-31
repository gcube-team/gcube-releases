package org.gcube.portlets.admin.vredefinition.client.view;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.user.client.ui.HTML;




import org.gcube.portlets.admin.vredefinition.client.presenter.VREStartWizardPresenter;

public class VREStartWizardView extends Composite implements VREStartWizardPresenter.Display {

	public VREStartWizardView() {
		
		VerticalPanel container = new VerticalPanel();
		container.setSize("100%", "100%");
		container.setTableHeight("50%");
		container.setTableWidth("100%");
		
		TableData tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.MIDDLE);
		
		LayoutContainer cp = new LayoutContainer();
		cp.setLayout(new FitLayout());
		cp.setStyleAttribute("margin", "10px");
		
		String toAdd = "<div align=\"center\"><h3> This wizard will guide you on the VRE Creation </h3></div>";
		toAdd += "<br />";
		toAdd += "<div align=\"center\">Click 'Next' to continue ...</div>";
		cp.add(new HTML(toAdd, true ));	
				
		container.add(cp,tableData);
		initComponent(container);
	}

}
