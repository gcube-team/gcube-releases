package org.gcube.portlets.admin.vredeployer.client.view.panels;


import org.gcube.portlets.admin.vredeployer.client.VredeployerService;
import org.gcube.portlets.admin.vredeployer.client.VredeployerServiceAsync;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ReportPanel {
	private final VredeployerServiceAsync deployerService = GWT.create(VredeployerService.class);

	public ReportPanel(final CenterPanel myPanel) {

		deployerService.getHTMLReport(new AsyncCallback<String>() {

			public void onSuccess(String result) {
				final Window window = new Window();  
				window.setSize(600, 350);  
				window.setPlain(true);  
				window.setModal(true);  
				window.setBlinkModal(true);  
				window.setHeading("Textual Report");  
				
				ContentPanel cp = new ContentPanel();
				cp.setHeaderVisible(false);

				ScrollPanel scroller = new ScrollPanel();
				scroller.setSize("600", "300");
				scroller.add(new Html(result));

				cp.add(scroller);
				
				cp.setLayout(new FitLayout());

				window.add(cp);
				window.setLayout(new FitLayout());  
				window.addButton(new Button("Close", new SelectionListener<ButtonEvent>() {  
					@Override  
					public void componentSelected(ButtonEvent ce) {  
						window.hide();  
					}  
				}));  
				
				cp.layout();
				myPanel.unmask();
				window.show();
			}

			public void onFailure(Throwable caught) {
				myPanel.unmask();
				Info.display("Error", "Could not locate textual report");

			}
		});
	}


}
