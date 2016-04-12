package org.gcube.portlets.user.results.client.components;

import org.gcube.portlets.user.results.client.constants.ImageConstants;
import org.gcube.portlets.user.results.client.constants.StringConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class WPTreeItem extends Composite {

	private CellPanel mainLayout = new HorizontalPanel();

	private Image image;
	private Label text;
	private int type;
	private String id;
	

	public WPTreeItem(String label, String id, int type) {
		this.id = id;
		switch (type) {
		case StringConstants.TYPE_BASKET:
			this.image = new Image(ImageConstants.IMAGE_BASKET);
			this.type = StringConstants.TYPE_BASKET;
			break;
		case StringConstants.TYPE_HOME:
			GWT.log("image home", null);
			this.image = new Image(ImageConstants.IMAGE_HOME);
			this.type = StringConstants.TYPE_HOME;
			break;
		case StringConstants.TYPE_FOLDER:
			this.image = new Image(ImageConstants.IMAGE_FOLDER);
			this.type = StringConstants.TYPE_FOLDER;
			break;
		}
		mainLayout.setSpacing(1);
		this.text = new Label(label);
		
		//image.setPixelSize(10, 10);
		mainLayout.add(image);
		mainLayout.add(text);
		mainLayout.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);
		mainLayout.setCellVerticalAlignment(text, HasVerticalAlignment.ALIGN_BOTTOM);

		mainLayout.setCellWidth(image, "10");
		
		mainLayout.setStyleName("selectable");	
		
	//.setHeight("15");
		//mainLayout.setStyleName("frame");
		initWidget(mainLayout);
	}
	
	
	public Image getItemImage() {
		return image;
	}
	public Label getItemText() {
		return text;
	}
	public int getType() {
		return type;
	}
	public void setItemImage(Image itemImage) {
		this.image = itemImage;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return text.getText();
	}
	
}
