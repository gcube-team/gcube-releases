package org.gcube.portlets.user.templates.client.dialogs;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.user.templates.client.components.Coords;
import org.gcube.portlets.user.templates.client.components.DoubleColumnPanel;
import org.gcube.portlets.user.templates.client.components.DroppingArea;
import org.gcube.portlets.user.templates.client.components.FancyFileUpload;
import org.gcube.portlets.user.templates.client.components.ImageArea;
import org.gcube.portlets.user.templates.client.model.TemplateComponent;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * <code> ImageUploaderDialog </code> class is the Dialog for uploading images
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2008 (0.2) 
 */
public class ImageUploaderDialog extends DialogBox {

	private int width;
	private int height;
	private Coords start;
	private Presenter controller;

	private DroppingArea placeHolder;

	private String currTemplateName = "";
	
	private HorizontalPanel topLabel = new HorizontalPanel();

	private EventListener listener = new EventListener();
	private RadioButton local = new RadioButton("");
	private RadioButton web = new RadioButton("");
	
	/**
	 * the container panel
	 */
	private VerticalPanel mainLayout = new VerticalPanel();
	private VerticalPanel dialogPanel = new VerticalPanel();

	/**
	 * Creates the dialog
	 * @param controller my controller
	 * @param start .
	 * @param width .
	 * @param height .
	 */
	public ImageUploaderDialog(Presenter controller, Coords start, int width, int height, DroppingArea toRemove) {

		placeHolder = toRemove;
		//		Create a dialog box and set the caption text
		this.width = 400;
		this.height = 200;
		this.start = start;
		this.controller = controller;
		setText("Insert Image");
		
		local.setHTML(" From this computer");
		topLabel.add(local);
		local.setChecked(true);
		
		web.setHTML(" From the web (URL)");
		topLabel.add(web);
		web.addClickListener(listener);
		local.addClickListener(listener);
		
		dialogPanel.add(topLabel);
		
		currTemplateName = controller.getModel().getTemplateName();

		FancyFileUpload uploader = new FancyFileUpload(this, currTemplateName);

		HTML theLabel = new HTML("<strong>Browse your computer for the image file to upload:</strong>");
		dialogPanel.add(theLabel);
		dialogPanel.add(uploader);
		dialogPanel.setSpacing(4);
		dialogPanel.setStyleName("uploadDialog");
		dialogPanel.setPixelSize(this.width, this.height);
		mainLayout.add(topLabel);
		mainLayout.add(dialogPanel);
		setWidget(mainLayout);

	}
	
	/**
	 * Creates the dialog
	 * @param controller my controller
	 * @param start .
	 * @param width .
	 * @param height .
	 */
	public ImageUploaderDialog(Presenter controller) {

		
		//		Create a dialog box and set the caption text
		this.width = 400;
		this.height = 200;
		this.controller = controller;
		setText("Insert Image");
		
		local.setHTML(" From this computer");
		topLabel.add(local);
		local.setChecked(true);
		
		web.setHTML(" From the web (URL)");
		topLabel.add(web);
		web.addClickListener(listener);
		local.addClickListener(listener);
		
		currTemplateName = controller.getModel().getTemplateName();



		dialogPanel.add(getFromLocalPanel());
		dialogPanel.setPixelSize(this.width, this.height);

		mainLayout.add(topLabel);
		mainLayout.add(dialogPanel);
		setWidget(mainLayout);

	}
	
	private VerticalPanel getFromLocalPanel() {
		VerticalPanel toReturn = new VerticalPanel();
		FancyFileUpload uploader = new FancyFileUpload(this, currTemplateName);
		HTML theLabel = new HTML("<strong>Browse your computer for the image file to upload:</strong>");
		toReturn.add(theLabel);
		toReturn.add(uploader);
		toReturn.setSpacing(4);
		toReturn.setStyleName("uploadDialog");
		toReturn.setPixelSize(this.width, this.height);

		return toReturn;
	}
	
	private VerticalPanel getFromURLPanel() {
		VerticalPanel toReturn = new VerticalPanel();
		
		toReturn.setSpacing(5);		
	
		HTML theLabel = new HTML("<strong>Enter image web address:</strong>");
		final TextBox urlTextbox = new TextBox();
		urlTextbox.setWidth("90%");
		
		final HTML previewBox = new HTML("<div style=\"text-align:center;\"><font style=\"color: rgb(136, 136, 136);\"><br /> Image preview will be displayed here. <br><br> *Remember: Using others' images on the web without their permission may be bad manners, or worse, copyright infringement. <br><br></font></div>", true);
		previewBox.setStyleName("imagePreviewBox");
		
		toReturn.add(theLabel);
		toReturn.add(urlTextbox);
		toReturn.add(previewBox);
		
		urlTextbox.addChangeListener(new ChangeListener() {

			public void onChange(Widget sender) {
				previewBox.setHTML("<img height=\"75\" width=\"120\" src=\"" + urlTextbox.getText()+ "\" >");		
				previewBox.removeStyleName("imagePreviewBox");
			}
			
		});
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		

		
		buttonsPanel.add(new Button("Insert", new ClickListener() {
			public void onClick(Widget sender) {
				hide();
				controller.getCurrentSelected().getExtendedFormatter().insertImage(urlTextbox.getText());
			}
		}));
		
		buttonsPanel.add(new Button("Cancel", new ClickListener() {
			public void onClick(Widget sender) {
				hide();
			}
		}));
		
		buttonsPanel.setSpacing(5);
		toReturn.add(buttonsPanel);
		
		return toReturn;
	}

	
	public void insertImage(String imageName) {
		String imgURL = getImageURL(imageName, "CURRENT_OPEN");
		controller.getCurrentSelected().getExtendedFormatter().insertImage(imgURL);
	}
	/**
	 * 
	 * @param imageName .
	 */
	public void placeImage(String imageName) {

		//adjust the coordinates for the workspace panel
		start.setX(start.getX()-1);
		start.setY(start.getY() - controller.getWp().getAbsoluteTop()-1);
		String templateName = controller.getModel().getTemplateName();

		//get the index of the Dropping area to replace

		int index = controller.getWorkSpacePanel().getMainLayout().getWidgetIndex(placeHolder);

		ImageArea imgToPlace = 
			new ImageArea(controller, false,  imageName, templateName, start.getX(), start.getY(), width, height); 

		//then is a double column image
		if (index == -1) {
			index = controller.getWorkSpacePanel().getMainLayout().getWidgetIndex(placeHolder.getParent());
			DoubleColumnPanel dblCp = (DoubleColumnPanel) controller.getWorkSpacePanel().getMainLayout().getWidget(index);
			dblCp.insertImage(placeHolder, imgToPlace);
			
			TemplateComponent tc = controller.getModel().removeComponentFromModel(placeHolder);
			tc.setType(ComponentType.STATIC_IMAGE);
			tc.setContent(imgToPlace);
			tc.setDoubleColLayout(true);
			controller.setCurrCursorPos(index);
			controller.getModel().addComponentToModel(tc, index);
			
		}
		else {	
			controller.getWp().removeComponentFromLayout(placeHolder);
			TemplateComponent tc = controller.getModel().removeComponentFromModel(placeHolder);
			//		
			//		GWT.log("ImageUploaderDialog IMAGE " + start.getX() + " y=" + start.getY(), null);
			controller.setCurrCursorPos(index);
			controller.insertStaticImage(start, width, height, imgToPlace, tc.isDoubleColLayout());
		}
		controller.setCurrCursorPos(index);

	}
	
	
	
	
	 
	/**
	 * use an inner EventListener class to avoid exposing event methods on the dialog class itself.
	 */
	
	private class EventListener implements ClickListener {
 
		public void onClick(Widget sender) {
			if (sender == web && web.isChecked()) {				
				dialogPanel.clear();
				dialogPanel.add(getFromURLPanel());
			} else if  (sender == local && local.isChecked())  {
				dialogPanel.clear();
				dialogPanel.add(getFromLocalPanel());
			}
			
		}
		
	}
	
	/**
	 * return a URL which is lookable for on the web
	 * @param imageName .
	 * @param templateName .
	 * @return .
	 */
	public String getImageURL(String imageName, String templateName) {
		String currentUser = controller.getCurrentUser();
		String currentScope = controller.getCurrentScope();	
		/**
		 * Images will be stored under webapps/usersArea...
		 * GWT.getModuleBaseURL() returns 	 * e.g. http://dlib28.isti.cnr.it/templatecreator/html/
		 * need to get just http://dlib28.isti.cnr.it/
		 */
		//remove "/html/" and get e.g. http://dlib28.isti.cnr.it/templatecreator
		String host = GWT.getModuleBaseURL().substring(0, GWT.getModuleBaseURL().length()-6);

		//loog for last slash
		int lastSlash = host.lastIndexOf("/");
		
		//get what i need : e.g. http://dlib28.isti.cnr.it/ or  host = "http://localhost:8080/";
		host = host.substring(0, lastSlash +1 );
		//host = "http://localhost:8080/";
		
		String imgURL = host + "usersArea/"	+ currentScope + "/templates/" 
							+ currentUser + "/" + "CURRENT_OPEN" + "/images/" + imageName;
		
		return imgURL;
	}
}


