package org.gcube.portlets.user.workspace.client.view.windows;

import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.model.FileGridModel;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DialogGetInfo extends Dialog {

	/**
	 *
	 */
	public static final String EMPTY = "empty";
	public static final String UNKNOWN = "unknown";
	private int widthDialog = 450;
	private int heightTextArea = 50;
	private TextField<String> txtName = new TextField<String>();
	private TextField<String> txtAreaDescription = new TextArea();
	private TextField<String> txtType = new TextField<String>();
	private TextField<String> txtCategory = new TextField<String>();
	private TextField<String> txtOwner = new TextField<String>();
	private TextField<String> txtLastMofication = new TextField<String>();
	private TextField<String> txtCreated = new TextField<String>();
	private TextField<String> txtSize = new TextField<String>();
	private TextField<String> txtLocation = new TextField<String>();
	private TextField<String> txtIsPublic = new TextField<String>();
	private TextField<String> txtShared = new TextField<String>();
//	private TextArea textAreaSharedWith = new TextArea();
	private Html htmlUsersWidget = new Html();
	private Html htmlPropertiesWidget = new Html();
	private final NumberFormat number = ConstantsExplorer.numberFormatterKB;
//	private TextField<String> txtGcubeItemProperties;
	private HorizontalPanel hpGcubeProperties;
	private DialogEditProperties editProperties = null;

	public DialogGetInfo(final FileModel fileModel) {

	    FormLayout layout = new FormLayout();
	    layout.setLabelWidth(90);
	    layout.setDefaultWidth(300);
	    setLayout(layout);

	    setIcon(fileModel.getAbstractPrototypeIcon());
	    setHeading(fileModel.getName() + " Properties");

	    setButtonAlign(HorizontalAlignment.RIGHT);
	    setModal(true);
//	    setBodyBorder(true);
	    setBodyStyle("padding: 9px; background: none");
	    setWidth(widthDialog);
	    setResizable(false);
	    setButtons(Dialog.OK);

	    txtName = new TextField<String>();
	    txtName.setFieldLabel("Name");
	    txtName.setReadOnly(true);
	    textFieldSetValue(txtName,fileModel.getName());
	    add(txtName);

	    txtLocation = new TextField<String>();
	    txtLocation.setFieldLabel("Location");
	    txtLocation.setReadOnly(true);

	    if(fileModel.isRoot())
	    	txtLocation.setValue("/");
	    else
	    	loadLocation(fileModel.getIdentifier());

	    add(txtLocation);

	    if(fileModel.isDirectory()){
		    txtIsPublic = new TextField<String>();
		    txtIsPublic.setFieldLabel("Public Folder");
		    txtIsPublic.setReadOnly(true);
			txtIsPublic.setValue(fileModel.isPublic()+"");
		    add(txtIsPublic);
	    }

	    txtAreaDescription.setFieldLabel("Description");
	    txtAreaDescription.setHeight(30);
	    txtAreaDescription.setReadOnly(true);
	    add(txtAreaDescription);

	    //GCUBE PROPERTIES
		hpGcubeProperties = new HorizontalPanel();
    	hpGcubeProperties.setStyleAttribute("padding-top", "6px");
    	hpGcubeProperties.setStyleAttribute("margin-bottom", "6px");
    	Label labelProperties = new Label("Properties");
    	labelProperties.setTitle("Gcube Properties");
    	labelProperties.setStyleAttribute("padding-right", "47px");
    	hpGcubeProperties.add(labelProperties);
    	loadGcubeItemProperties(fileModel.getIdentifier());
    	htmlPropertiesWidget.setHeight(heightTextArea);
    	htmlPropertiesWidget.setWidth("275px");

    	LayoutContainer lc = new LayoutContainer();
    	lc.addStyleName("editPermissions");

    	Image imgProperties = Resources.getIconEdit().createImage(); //EDIT PROPERTIES
    	imgProperties.setTitle("Edit Properties");
    	lc.add(imgProperties);

    	hpGcubeProperties.add(lc);

    	final Command cmdReloadProperties = new Command() {

			@Override
			public void execute() {
				loadGcubeItemProperties(fileModel.getIdentifier());
			}
		};

    	imgProperties.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if(editProperties==null)
					editProperties = new DialogEditProperties(fileModel, cmdReloadProperties);

				editProperties.show();

			}
		});
    	htmlPropertiesWidget.setStyleAttribute("margin-left", "5px");
    	hpGcubeProperties.add(htmlPropertiesWidget);
    	hpGcubeProperties.setScrollMode(Scroll.AUTOY);
    	add(hpGcubeProperties);
    	setVisibleGcubeProperties(false);

	    if(fileModel.isDirectory()){
		    txtAreaDescription.setValue(fileModel.getDescription());
//		    add(txtAreaDescription);
	    }else
	    	loadDescription(fileModel.getIdentifier());

	    txtType = new TextField<String>();
	    txtType.setFieldLabel("Type");
	    txtType.setReadOnly(true);
	    textFieldSetValue(txtType,fileModel.getType());
	    add(txtType);

	    txtCategory = new TextField<String>();
	    txtCategory.setFieldLabel("Category");
	    txtCategory.setReadOnly(true);
	    if(fileModel.getShortcutCategory()!=null)
	    	textFieldSetValue(txtCategory,fileModel.getShortcutCategory().getValue());
	    add(txtCategory);

	    txtOwner = new TextField<String>();
	    txtOwner.setFieldLabel("Owner");
	    txtOwner.setReadOnly(true);
	    loadOwner(fileModel.getIdentifier());
	    add(txtOwner);

	    txtCreated = new TextField<String>();
	    txtCreated.setFieldLabel("Created");
	    txtCreated.setReadOnly(true);

	    loadCreationDate(fileModel.getIdentifier());

	    add(txtCreated);

	    txtLastMofication = new TextField<String>();
	    txtLastMofication.setFieldLabel("Last Mofication");
	    txtLastMofication.setReadOnly(true);
	    if(fileModel instanceof FileGridModel)
	    	textFieldSetValue(txtLastMofication, ((FileGridModel) fileModel).getLastModification().toString());
	    else
	    	loadLastModificationDate(fileModel.getIdentifier());

	    add(txtLastMofication);

	    txtSize = new TextField<String>();
	    txtSize.setFieldLabel("Size");
	    txtSize.setReadOnly(true);

	    if(fileModel instanceof FileGridModel)
	    	textFieldSetValue(txtSize,getFormattedSize(((FileGridModel) fileModel).getSize()));
	    else
	    	loadSize(fileModel.getIdentifier());

	    add(txtSize);

    	//SHARED
	    txtShared = new TextField<String>();
	    txtShared.setFieldLabel("Shared");
	    txtShared.setReadOnly(true);
	    textFieldSetValue(txtShared,fileModel.isShared()+"");
	    add(txtShared);

	    //USERS SHARED
	    if(fileModel.isShared()){

	    	HorizontalPanel hp = new HorizontalPanel();
	    	hp.setStyleAttribute("padding-top", "6px");
	    	Label label = new Label("Shared with");
	    	label.setStyleAttribute("padding-right", "39px");
	    	hp.add(label);
	    	loadACLsDescriptionForSharedFolder(fileModel.getIdentifier());
	    	htmlUsersWidget.setHeight(heightTextArea);
	    	htmlUsersWidget.setWidth("297px");
	    	hp.add(htmlUsersWidget);
	    	hp.setScrollMode(Scroll.AUTOY);
	    	add(hp);
	    }


        this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
        this.show();
	}

	private void setVisibleGcubeProperties(boolean bool){
		hpGcubeProperties.setVisible(bool);
	}

	/**
	 * @param identifier
	 */
	private void loadDescription(String identifier) {
		txtAreaDescription.mask();

		AppControllerExplorer.rpcWorkspaceService.getItemDescriptionById(identifier, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable arg0) {
				txtAreaDescription.unmask();

			}

			@Override
			public void onSuccess(String result) {
				if(result!=null)
					txtAreaDescription.setValue(result);
				else
					txtAreaDescription.setValue("");

				txtAreaDescription.unmask();

			}
		});

	}

	private void loadLastModificationDate(final String itemId) {

		txtLastMofication.mask();
		AppControllerExplorer.rpcWorkspaceService.loadLastModificationDateById(itemId, new AsyncCallback<Date>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("an error occured in loadLastModificationDateById "+itemId + " "+caught.getMessage());
				txtLastMofication.unmask();

			}

			@Override
			public void onSuccess(Date result) {

				if(result!=null)
					txtLastMofication.setValue(result.toString());
				else
					txtLastMofication.setValue(UNKNOWN);

				txtLastMofication.unmask();
			}
		});

	}

	private void textFieldSetValue(TextField<String> field, String value){

		if(value==null || value.isEmpty())
			field.setValue(UNKNOWN);
		else
			field.setValue(value);
	}

	private void loadOwner(final String itemId){

		txtOwner.mask();

		AppControllerExplorer.rpcWorkspaceService.getOwnerByItemId(itemId, new AsyncCallback<InfoContactModel>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("an error occured in get Owner by Id "+itemId + " "+caught.getMessage());
				txtOwner.unmask();
			}

			@Override
			public void onSuccess(InfoContactModel result) {
			    textFieldSetValue(txtOwner,result.getName());
			    txtOwner.unmask();
			}
		});
	}



	private void loadGcubeItemProperties(final String itemId){
		GWT.log("Load GcubeItemProperties");
		htmlPropertiesWidget.mask();
		AppControllerExplorer.rpcWorkspaceService.getHTMLGcubeItemProperties(itemId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				htmlPropertiesWidget.unmask();
				GWT.log("an error occured in load properties by Id "+itemId + " "+caught.getMessage());
				setVisibleGcubeProperties(true);
				htmlPropertiesWidget.setHtml("Error on recovering properties");
			}

			@Override
			public void onSuccess(String result) {
//				setVisibleGcubeProperties(true);
				htmlPropertiesWidget.unmask();
				if(result!=null){
					setVisibleGcubeProperties(true);
					htmlPropertiesWidget.setHtml(result);
				}
//				else{
//					htmlPropertiesWidget.setHeight(20);
//					htmlPropertiesWidget.setHtml("None");
//				}
			}
		});
	}


	private void loadSize(final String itemId){
		GWT.log("Load size");
		txtSize.mask();
		AppControllerExplorer.rpcWorkspaceService.loadSizeByItemId(itemId, new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("an error occured in load creation date by Id "+itemId + " "+caught.getMessage());
				txtSize.unmask();

			}

			@Override
			public void onSuccess(Long result) {
				GWT.log("Loaded size="+result);
			   	textFieldSetValue(txtSize,getFormattedSize(result));
			   	txtSize.unmask();
			}
		});
	}


	private void loadCreationDate(final String itemId){

		txtCreated.mask();
		AppControllerExplorer.rpcWorkspaceService.getItemCreationDateById(itemId, new AsyncCallback<Date>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("an error occured in load creation date by Id "+itemId + " "+caught.getMessage());
				txtCreated.unmask();
			}

			@Override
			public void onSuccess(Date result) {
				if(result!=null)
				    textFieldSetValue(txtCreated,result.toString());

				txtCreated.unmask();
			}
		});
	}

	private void loadACLsDescriptionForSharedFolder(String sharedId){

		htmlUsersWidget.mask();

		AppControllerExplorer.rpcWorkspaceService.getACLsDescriptionForWorkspaceItemById(sharedId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				htmlUsersWidget.unmask();
				htmlUsersWidget.setHtml("Error on recovering users");

			}

			@Override
			public void onSuccess(String result) {
				htmlUsersWidget.unmask();
				GWT.log("Loaded ACLs: "+result);
				htmlUsersWidget.setHtml(result);
			}
		});

	}

	private String getFormattedSize(long value){

		if(value>0){
			double kb = value/1024;
			if(kb<1)
				kb=1;
			return number.format(kb);
		}else if(value==0){
			return EMPTY;
		}else
			return "";
	}

	private void loadLocation(String itemId){

		txtLocation.mask();
		AppControllerExplorer.rpcWorkspaceService.getListParentsByItemIdentifier(itemId, false, new AsyncCallback<List<FileModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("failure get list parents by item identifier "+caught);
				txtLocation.unmask();
			}

			@Override
			public void onSuccess(List<FileModel> result) {

				String location="";
				if(result!=null){
					for (FileModel fileModel : result) {
						if(fileModel!=null)
							location+="/"+fileModel.getName();
					}
				}
				if(location.isEmpty())
					location ="/";

				txtLocation.setValue(location);
				txtLocation.unmask();
			}
		});

	}
}