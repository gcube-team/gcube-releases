package org.gcube.portlets.user.templates.client;


import org.gcube.portlets.d4sreporting.common.client.CommonConstants;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.user.templates.client.components.Coords;
import org.gcube.portlets.user.templates.client.components.DoubleColumnPanel;
import org.gcube.portlets.user.templates.client.components.DroppingArea;
import org.gcube.portlets.user.templates.client.components.FakeTextArea;
import org.gcube.portlets.user.templates.client.dialogs.ImageUploaderDialog;
import org.gcube.portlets.user.templates.client.model.TemplateComponent;
import org.gcube.portlets.user.templates.client.model.TemplateModel;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * <code> WorkspacePanel </code> class is the UI Component of the user workspace area
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */

public class WorkspacePanel extends Composite {
	/**
	 * the model
	 */
	private TemplateModel templateModel;

	/**
	 * the controller
	 */
	private Presenter controller;

	/**
	 * 
	 */
	public static WorkspacePanel singleton = null;

	/**
	 * 
	 * @return .
	 */
	public static WorkspacePanel get() {
		return singleton;
	}
	/**
	 * used to place two compoenents on the same Y
	 */
	private boolean waitForNextOne = false;
	/**
	 * elements arrive one by one, to place two on the same Y this bufferedWidget is used;
	 */
	private Widget bufferedWidget;
	/**
	 * the panel for the layout of the working space
	 */
	//private AbsolutePanel mainLayout = new AbsolutePanel();
	private FlowPanel mainLayout = new FlowPanel();



	/**
	 * 
	 * @param c the controller instance
	 */
	public WorkspacePanel(TemplateModel model, Presenter control) {
		singleton = this;
		controller = control;

		templateModel = model;

		mainLayout.setPixelSize(templateModel.getPageWidth(), templateModel.getPageHeight());
		mainLayout.setStyleName("wpFlow");

		initWidget(mainLayout);

	}


	public void addFirstTextArea() 	{
		FakeTextArea toAdd = new FakeTextArea(0, controller);
		TemplateComponent tc = new TemplateComponent(templateModel, 0, 0, TemplateModel.TEMPLATE_WIDTH, 25, 
				templateModel.getCurrentPage(), ComponentType.FAKE_TEXTAREA, "", toAdd);
		templateModel.addComponentToModel(tc, 0);
		addComponentToLayout(toAdd, true);
	}
	/**
	 * 
	 * Insert from the user
	 * 
	 * @param w .
	 * @param x .
	 * @param y .
	 * @return the current index position
	 */
	public int addComponentToLayout(Widget w, boolean isFakeTextArea) {
		
		int insertTo = controller.getSelectedIndex();
		GWT.log("Inserting on " +insertTo, null);		
		int toReturn = 0;
		
		if (insertTo >=0)
					mainLayout.insert(w, insertTo);
		else
					mainLayout.add(w);
		
		toReturn =  mainLayout.getWidgetIndex(w);
		
		if (isFakeTextArea)
			controller.setCurrCursorPos(mainLayout.getWidgetCount());
		else			
			controller.setCurrCursorPos(toReturn+1);
		return toReturn;		
	}

	/**
	 * Insert forom the system
	 * @param w
	 * @param isDoubleColumnLayout
	 * @return
	 */
	public int addComponentToLayoutSystem(Widget w, boolean isDoubleColumnLayout) {
		waitForNextOne = isDoubleColumnLayout;	
		int insertTo = controller.getSelectedIndex();
		GWT.log("Inserting on " +insertTo, null);		
		int toReturn = 0;
		
		if (! waitForNextOne) {
			mainLayout.add(w);
			bufferedWidget = null;
			toReturn =  mainLayout.getWidgetIndex(w);
		}
		else {		
			if (bufferedWidget == null) {
				bufferedWidget = w;
				GWT.log("isDoubleColumnLayout buffering", null);
			}
			else {
				DoubleColumnPanel toAdd = new DoubleColumnPanel(bufferedWidget, w);
				mainLayout.add(toAdd);
				bufferedWidget = null;		
				toReturn =  mainLayout.getWidgetIndex(toAdd);
				GWT.log("isDoubleColumnLayout adding", null);
			}
		}
		controller.setCurrCursorPos(toReturn+1);
		return toReturn;		
	}
	
	/**
	 * 
	 * @param w the widget to remove
	 * @return true if the romove is successfull
	 */
	public boolean removeComponentFromLayout(Widget w) {
		int index = mainLayout.getWidgetIndex(w);
		boolean toReturn = mainLayout.remove(w);
	
		controller.getModel().removeComponentFromModel(mainLayout.getWidget(index));		
		//this removes the fake text area below
		mainLayout.remove(index);		
		
		controller.setCurrCursorPos(mainLayout.getWidgetCount());
		return toReturn;
	}

	/**
	 * 
	 * @param model .
	 */
	public void setModel(TemplateModel model ) {
		this.templateModel = model;
	}

	/**
	 * resizes the workspace panel 
	 * @param width .
	 * @param height .
	 */
	public void resizeWorkspace(int width, int height) {
		mainLayout.setPixelSize(width, height);
	}





	/**
	 * popup the open Image Upload Dialog
	 * @param start .
	 * @param x .
	 * @param y .
	 */
	public void openImageUploadDialog(Coords start, int width, int height, DroppingArea toRemove) {

		ImageUploaderDialog dlg = new ImageUploaderDialog(controller, start, width, height, toRemove);
		dlg.setAnimationEnabled(true);
		dlg.setPopupPosition(start.getX(), start.getY());
		dlg.show();

	}

	/**
	 * 
	 * @param toResize the widget to resize
	 * @param newWidth .
	 * @param newHeight .
	 */
	public void resizeWidget(Widget toResize, int newWidth, int newHeight) {
		if (toResize instanceof DroppingArea) {
			DroppingArea dp = (DroppingArea) toResize;
			dp.resizePanel(newWidth, newHeight);
				
			
		}
		else
			toResize.setPixelSize(newWidth, newHeight);
	}

	/**
	 * 
	 * @param toMove .
	 * @param left . 
	 * @param top .
	 */
	public void moveWidget(Widget toMove, int left, int top) {
		//		try {
		//mainLayout.setWidgetPosition(toMove, left, top);	
		//		}
		//		catch (IllegalArgumentException e) {
		//			RichTextArea r = (RichTextArea) toMove;
		//			Window.alert(r.getText());
		//		}

	}

	/**
	 * 
	 * @param toResize .
	 * @return a popuppanel instance
	 */
	public PopupPanel getResizePopup(final Widget toResize){

		final PopupPanel simplePopup = new PopupPanel(true);

		int currWidth = toResize.getOffsetWidth() - 6; //-6 because was adding 6px by itself
		int currHeight = toResize.getOffsetHeight() - 6; 
		simplePopup.setAnimationEnabled(true);
		DecoratorPanel deco = new DecoratorPanel();
		VerticalPanel layout = new VerticalPanel();

		layout.setSpacing(10);

		Grid grid = new Grid(2, 2);

		final TextBox widthBox = new TextBox();
		widthBox.setText(""+currWidth);
		widthBox.setWidth("50");
		widthBox.setMaxLength(3);

		final TextBox heighBox = new TextBox();
		heighBox.setText(""+currHeight);
		heighBox.setWidth("50");
		heighBox.setMaxLength(3);

		Label labelW = new Label("Width: ");
		Label labelH = new Label("Height:");


		grid.setWidget(0, 0, labelW);
		grid.setWidget(0, 1, widthBox);

		grid.setWidget(1, 0, labelH);
		grid.setWidget(1, 1, heighBox);

		Button cancelButton = new Button("Cancel",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				simplePopup.hide();
			}
		});

		Button applyButton = new Button("Apply",new ClickHandler() {
			public void onClick(ClickEvent event) {
				int width = Integer.valueOf(widthBox.getText().replaceAll(CommonConstants.ACCEPTED_CHARS_JUST_NUM, ""));
				int height = Integer.valueOf(heighBox.getText().replaceAll(CommonConstants.ACCEPTED_CHARS_JUST_NUM, ""));
				if (width < 10 )
					Window.alert("Width must be greater than 9");
				else if (height < 10) 
					Window.alert("Height must be greater than 9");
				else {
					controller.resizeTemplateComponent(toResize, width, height);
					simplePopup.hide();
				}
			}
		});

		HorizontalPanel buttonsContainer = new HorizontalPanel();
		buttonsContainer.setSpacing(10);

		buttonsContainer.add(cancelButton);
		buttonsContainer.add(applyButton);

		layout.add(grid);
		layout.add(buttonsContainer);

		deco.add(layout);

		simplePopup.add(deco);

		return simplePopup;

	}

	/**
	 * 
	 * @return .
	 */
	public FlowPanel getMainLayout() {
		return mainLayout;
	}

	/**
	 * 
	 * @param controller .,
	 */
	public void setController(Presenter controller) {
		this.controller = controller;
	}
	/***
	 * 
	 */
	public void mask() {
		mainLayout.setStyleName("wpFlow_background");	
	}
	/**
	 * 
	 */
	public void unmask() {
		mainLayout.setStyleName("wpFlow");	
	}
}

