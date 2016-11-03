package org.gcube.portlets.user.reportgenerator.client.dialog;

import java.util.ArrayList;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.user.reportgenerator.client.events.RemovedCitationEvent;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateComponent;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateSection;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HTML;
import com.extjs.gxt.ui.client.widget.button.Button;
/**
 * The <code> ManageBiblioDialog </code> class 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version July 2011 (3.0) 
 */
public class DeleteCitationsDialog extends Window {

	TabPanel tabPanel = new TabPanel();  
//
	public DeleteCitationsDialog(final HandlerManager eventBus, TemplateSection bibliosection) {
		setTitle("Delete Citations");  
		setClosable(true);  
		setWidth(400);  
		setHeight(250);  
		setPlain(true);  
		setLayout(new FitLayout());  

		Button deleteButton = new Button("Delete") {  
			@Override
			protected void onClick(final ComponentEvent ce) {
				if (tabPanel.getItemCount() > 1) {
					eventBus.fireEvent(new RemovedCitationEvent(tabPanel.getSelectedItem().getTitle()));
					tabPanel.remove(tabPanel.getSelectedItem());
				}
				else {
					MessageBox.alert("","You cannot have a Bibliography with no citations, if you want to remove it use Section > Discard current", null);
				}
			}  
		};  
//
		Button cancelButton = new Button("Close") {  
		@Override
		protected void onClick(final ComponentEvent ce) {
				close();
			}  
		};  

		addButton(deleteButton);  
		addButton(cancelButton);
//
		ArrayList<String> citations = new ArrayList<String>();
		for (TemplateComponent tc : bibliosection.getAllComponents()) {
			if (tc.getType() == ComponentType.BODY) {
				BasicComponent sc = tc.getSerializable();
				citations.add(sc.getPossibleContent().toString());
			}
		}


		for (String cite : citations) {
			TabItem tabI = new TabItem();  
			HTML html = new HTML(cite.split("&nbsp;")[0], true);
			String citekey = html.getText();
			if (citekey.endsWith(".")) //remove dot
				citekey = citekey.substring(0, citekey.length()-1);

			tabI.setTitle(citekey);  
			String citation = cite.split("&nbsp;")[1];
			tabI.setText(citekey); 
			tabI.add(new HTML(citation));
			tabI.setClosable(false);
			tabPanel.add(tabI); 

			//TODO:

		}        
		add(tabPanel);
	}


}
