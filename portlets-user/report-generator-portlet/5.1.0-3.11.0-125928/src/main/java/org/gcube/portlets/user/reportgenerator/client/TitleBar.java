package org.gcube.portlets.user.reportgenerator.client;

import java.util.Date;

import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateModel;
import org.gcube.portlets.user.reportgenerator.client.uibinder.SectionSwitchPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

	/**
	 * <code> TitleBar </code> class is the top top bar component of the UI 
	 *
	 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
	 * 
	 */
	public class TitleBar extends Composite{


		private Presenter presenter;

		/**
		 * the template Model
		 */
		private TemplateModel templateModel;

		/**
		 * mainLayout Panel
		 */
		private CellPanel mainLayout = new HorizontalPanel();

		/**
		 *  contains the current template name
		 */
		private HTML templateNameBox = new HTML();
		
		/**
		 *  contains the last edit date and the the last edit person username
		 */
		private HTML editedOn = new HTML();
		/**
		 * contains the page displayer
		 */
		private SectionSwitchPanel sectionSwitchPanel;

		private HorizontalPanel captionPanel = new HorizontalPanel();
		/**
		 * Constructor
		 * @param c the controller instance for this UI component
		 */
		public TitleBar(Presenter c) {
			this.presenter = c;
			this.templateModel = presenter.getModel();


			//initialize the template
			setTemplateName(templateModel.getTemplateName());
			
			sectionSwitchPanel = new SectionSwitchPanel(presenter); 
			sectionSwitchPanel.setPageDisplayer(1, 1);
		
			//design the part for the template name and the pages handling

			HorizontalPanel innerCaptionPanel = new HorizontalPanel();
			captionPanel.setWidth("100%");

			//hide the buttons at the beginning
			sectionSwitchPanel.hideNextButton();
			sectionSwitchPanel.hidePrevButton();
			sectionSwitchPanel.setVisible(false);
			
			templateNameBox.setStyleName("menubar-font");
			
			captionPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			innerCaptionPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			innerCaptionPanel.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
			innerCaptionPanel.add(templateNameBox);
			innerCaptionPanel.add(editedOn);
			captionPanel.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
			captionPanel.add(innerCaptionPanel);
			mainLayout.add(captionPanel);
			mainLayout.add(sectionSwitchPanel);
			editedOn.setStyleName("docEditedBy");
			mainLayout.setCellHorizontalAlignment(captionPanel, HasHorizontalAlignment.ALIGN_LEFT);
			mainLayout.setCellVerticalAlignment(captionPanel, HasVerticalAlignment.ALIGN_MIDDLE);
			
			mainLayout.setCellWidth(sectionSwitchPanel, "250px");
			initWidget(mainLayout);
			mainLayout.setStyleName("titleBar");

			
		}
		/**
		 * add the buttons well visible on the top
		 * @param isEdit
		 */
		public void addWorkflowButtons(final boolean isEdit) {
			Button update = new Button("Update (SAVE)");
			update.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.updateWorkflowDocument(isEdit);	
				}
			});
			Button back = new Button("Do not Update");
			back.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.updateWorkflowDocument(isEdit);	
				}
			});
			update.addStyleName("addEntryButton");
			back.addStyleName("deleteEntryButton");
			captionPanel.add(update);
			captionPanel.add(back);
		
		}
		
		/**
		 * changes the template name label in the UI
		 * @param name .
		 */
		public void setTemplateName(String name) {
			if (name.endsWith(".d4sR"))
				name = name.replaceAll(".d4sR", "");
			templateNameBox.setHTML("&nbsp;&nbsp;" + name);
		}

		/**
		 * changes the template name label in the UI
		 * @param username .
		 * @param date .
		 */
		public void setEditedOnBy(Date date, String username) {
			String dt = DateTimeFormat.getShortDateFormat().format(date);

			editedOn.setHTML("&nbsp;&nbsp;edited on " + dt + "&nbsp;&nbsp;by " + username);
		}
		
		/**
		 * temporary command 
		 * @return the command instance
		 */
		public Command getNullCommand() {
			Command openNothing = new Command() {	

				public void execute() {
					Window.alert("Feature not supported yet");

				}
			};

			return openNothing;
		}
		/**
		 * 
		 * @return SectionSwitchPanel
		 */
		public SectionSwitchPanel getSectionSwitchPanel() {
			return sectionSwitchPanel;
		}
}
	
	
