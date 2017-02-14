package org.gcube.portlets.user.templates.client;

import java.util.Date;

import org.gcube.portlets.user.templates.client.model.TemplateModel;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
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
 * @version september 2009 (0.1) 
 */
public class TitleBar extends Composite{


	private Presenter controller;

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
	private HTML pageDisplayer = new HTML(); 


	private HTML prevButton = new HTML("<img src=\"" + TGenConstants.IMAGE_PREV_PAGE + "\" />&nbsp;", true);
	private HTML nextButton = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"" + TGenConstants.IMAGE_NEXT_PAGE + "\" />", true);


	/**
	 * Constructor
	 * @param c the controller instance for this UI component
	 */
	public TitleBar(Presenter c) {
		this.controller = c;
		this.templateModel = controller.getModel();


		//initialize the template
		setTemplateName(templateModel.getTemplateName());

		setPageDisplayer(1, 1);


		mainLayout.setSize("100%", "24px");
		//			mainLayout.setStyleName("newresultset-header");



		//design the part for the template name and the pages handling

		HorizontalPanel captionPanel = new HorizontalPanel();
		HorizontalPanel innerCaptionPanel = new HorizontalPanel();
		captionPanel.setWidth("100%");

		//hide the buttons at the beginning
		nextButton.addStyleName("setVisibilityOff");
		prevButton.addStyleName("setVisibilityOff");

		HorizontalPanel pageHandlerPanel = new HorizontalPanel();
		pageHandlerPanel.setHeight("24");
		pageHandlerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		pageHandlerPanel.add(prevButton);
		pageHandlerPanel.add(pageDisplayer);
		pageHandlerPanel.add(nextButton);
		pageHandlerPanel.setWidth("100%");

		templateNameBox.setStyleName("menubar-font");

		captionPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		innerCaptionPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		innerCaptionPanel.setVerticalAlignment(HasAlignment.ALIGN_BOTTOM);
		innerCaptionPanel.add(templateNameBox);
		innerCaptionPanel.add(editedOn);
		captionPanel.add(innerCaptionPanel);
		mainLayout.add(captionPanel);
		mainLayout.add(pageHandlerPanel);
		editedOn.setStyleName("docEditedBy");
		mainLayout.setCellHorizontalAlignment(captionPanel, HasHorizontalAlignment.ALIGN_LEFT);

		mainLayout.setCellWidth(pageHandlerPanel, "200");
		initWidget(mainLayout);

		nextButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.nextPageButtonClicked(); 
			}
		});

		prevButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				controller.prevPageButtonClicked(); }		
		});
	}

	/**
	 * changes the pages label in the UI : e.g. Page x of y
	 * @param currentPage . 
	 * @param totalPages .
	 */
	public void setPageDisplayer(int currentPage, int totalPages) {
		pageDisplayer.setHTML("Section "+ currentPage + " of " + totalPages);	
	}

	/**
	 * changes the template name label in the UI
	 * @param name .
	 */
	public void setTemplateName(String name) {
		templateNameBox.setHTML("&nbsp;&nbsp;" + name);
	}

	public void clearEditedOnBy() {
		editedOn.setHTML("&nbsp;&nbsp;");
	}
	/**
	 * changes the template name label in the UI
	 * @param name .
	 */
	public void setEditedOnBy(Date date, String username) {

		if (username == null)
			editedOn.setHTML("&nbsp;&nbsp;");
		else {
			String dt = "";
			if (date != null ) {
				dt = DateTimeFormat.getShortDateFormat().format(date);
			}
			editedOn.setHTML("&nbsp;&nbsp;edited on " + dt + "&nbsp;&nbsp;by " + username);
		}

	}
	/**
	 * Shows the previous botton in the UI
	 */
	public void showPrevButton() {
		prevButton.removeStyleName("setVisibilityOff");
		prevButton.addStyleName("setVisibilityOn");
	}
	/**
	 * Shows the next botton in the UI
	 */
	public void showNextButton() {
		nextButton.removeStyleName("setVisibilityOff");
		nextButton.addStyleName("setVisibilityOn");
	}

	/**
	 * Hide the previous botton in the UI
	 */
	public void hidePrevButton() {
		prevButton.removeStyleName("setVisibilityOn");
		prevButton.addStyleName("setVisibilityOff");
	}
	/**
	 * Hide the next botton in the UI
	 */
	public void hideNextButton() {
		nextButton.removeStyleName("setVisibilityOn");
		nextButton.addStyleName("setVisibilityOff");
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


}


