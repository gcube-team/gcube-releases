package org.gcube.portlets.user.workspace.client.view;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class GxtDetailsContainer extends Viewport{
	
	private TextField<String> txfName = new TextField<String>();
	private TextArea txtDescription = new TextArea();
	private Text txtLabelOwner = new Text("Owner:");
	private Text txtLabelCreationTime = new Text("Creation Time:");
	private Text txtLabelDimension = new Text("Dimension:");
	private Text txtOwner = new Text("Empty");
	private Text txtCreationTime = new Text("Empty");
	private Text txtDimension = new Text("Empty");
	private ContentPanel west = new ContentPanel();
	private ContentPanel center = new ContentPanel();
	
	public GxtDetailsContainer(){
		
		this.initBorderLayout();
		this.initItemsPropertiesLayout();
		this.initItemsDetailsLayout();
	}
	
	private void initItemsDetailsLayout() {
		
		LayoutContainer itemsDetails = new LayoutContainer();
	    itemsDetails.setLayout(new ColumnLayout());
	    
		VerticalPanel vp = new VerticalPanel();
		vp.setStyleAttribute("padding", "5px");
		vp.setLayout(new FitLayout());
		Text txtTitle = new Text("Items-Details");
		txtTitle.setStyleAttribute("font-weight", "bold");
		vp.add(txtTitle);

		LayoutContainer left = new LayoutContainer();
	    left.setStyleAttribute("paddingRight", "20px");
	    
	    HorizontalPanel hpOwner = new HorizontalPanel();
	    hpOwner.setSpacing(5);
	    txtLabelOwner.setStyleAttribute("padding-left", "5px");
	    hpOwner.add(txtLabelOwner);
	    hpOwner.add(txtOwner);
	    
	    HorizontalPanel hpCreationTime = new HorizontalPanel();
	    hpCreationTime.setSpacing(5);
	    txtLabelCreationTime.setStyleAttribute("padding-left", "5px");
	    hpCreationTime.add(txtLabelCreationTime);
	    hpCreationTime.add(txtCreationTime);
	    
	    HorizontalPanel hpDimension = new HorizontalPanel();
	    hpDimension.setSpacing(5);
	    txtLabelDimension.setStyleAttribute("padding-left", "5px");
	    hpDimension.add(txtLabelDimension);
	    hpDimension.add(txtDimension);

		LayoutContainer right = new LayoutContainer();
	    right.setStyleAttribute("paddingLeft", "20px");

	    itemsDetails.add(left, new ColumnData(.5));
	    itemsDetails.add(right, new ColumnData(.5));
	    
	  
	    vp.add(hpOwner);
	    vp.add(hpCreationTime);
	    vp.add(hpDimension);
	    vp.add(itemsDetails);
	    center.add(vp);
	}

	private void initBorderLayout(){
		
		BorderLayout bl = new BorderLayout();
		setLayout(bl);

		west.setHeaderVisible(false);
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 400);

		westData.setCollapsible(false);
		westData.setFloatable(false);
		westData.setSplit(false);
		westData.setMargins(new Margins(3, 0, 3, 3));
		
		center.setHeaderVisible(false);
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(3));
		centerData.setCollapsible(false);
		centerData.setFloatable(true);
		centerData.setSplit(true);
		
		add(west, westData);
		add(center, centerData);
	}
	
	private void initItemsPropertiesLayout() {
		
		LayoutContainer itemsDetails = new LayoutContainer();
	    itemsDetails.setLayout(new ColumnLayout());
	    
		VerticalPanel vp = new VerticalPanel();
		vp.setStyleAttribute("padding", "5px");
		vp.setLayout(new FitLayout());
		Text txtTitle = new Text("Items-Properties");
		txtTitle.setStyleAttribute("font-weight", "bold");
		vp.add(txtTitle);
		
		FormPanel formPanel = new FormPanel();
//		formPanel.setFrame(true);
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		FormData formData = new FormData("100%"); 
		
		LayoutContainer left = new LayoutContainer();
	    left.setStyleAttribute("paddingRight", "20px");
	    FormLayout layout = new FormLayout();
	    layout.setLabelAlign(LabelAlign.TOP);
//	    layout.setLabelWidth(40);
	    left.setLayout(layout);

//	    txtName.setEnabled(false);
	    txfName.setReadOnly(true);
	    txfName.setFieldLabel("Name");
	    left.add(txfName, formData);
		
		LayoutContainer right = new LayoutContainer();
	    right.setStyleAttribute("paddingLeft", "20px");
	    layout = new FormLayout();
//	    layout.setLabelWidth(40);
	    layout.setLabelAlign(LabelAlign.TOP);
	    right.setLayout(layout);

	   
//	    txtDescription.setEnabled(false);
	    txtDescription.setReadOnly(true);
	    txtDescription.setHeight(50);
	    txtDescription.setFieldLabel("Description");
	    right.add(txtDescription, formData);
	    
	    itemsDetails.add(left, new ColumnData(.5));
	    itemsDetails.add(right, new ColumnData(.5));
	    
	    formPanel.add(itemsDetails,new FormData("100%"));
	    
	    west.add(vp);
		west.add(formPanel);	
		
	}

	public TextField<String> getTxtName() {
		return txfName;
	}

	public void setTxtName(TextField<String> txtName) {
		this.txfName = txtName;
	}

	public TextArea getTxtDescription() {
		return txtDescription;
	}

	public void setTxtDescription(TextArea txtDescription) {
		this.txtDescription = txtDescription;
	}

	public Text getTxtOwner() {
		return txtOwner;
	}

	public void setTxtOwner(Text txtOwner) {
		this.txtOwner = txtOwner;
	}

	public Text getTxtCreationTime() {
		return txtCreationTime;
	}

	public void setTxtCreationTime(Text txtCreationTime) {
		this.txtCreationTime = txtCreationTime;
	}

	public Text getTxtDimension() {
		return txtDimension;
	}

	public void setTxtDimension(Text txtDimension) {
		this.txtDimension = txtDimension;
	}
	
	
	public void resetDetails(){
		
		this.txtDimension.setText("");
		this.txtCreationTime.setText("");
		this.txfName.reset();
		this.txtDescription.reset();
		this.txtOwner.setText("");
	}
	
	public void setDetails(String itemName, String description, String dimension, String creationTime, String owner){
		
		this.resetDetails();
		
		this.txtDimension.setText(dimension);
		this.txtCreationTime.setText(creationTime);
		this.txfName.setValue(itemName);
		this.txtDescription.setValue(description);
		this.txtOwner.setText(owner);
		
	}
}
