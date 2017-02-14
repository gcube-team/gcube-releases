/**
 *
 */
package org.gcube.portlets.user.workspace.client.view;

import org.gcube.portlets.user.workspace.client.AppController;
import org.gcube.portlets.user.workspace.client.ConstantsPortlet;
import org.gcube.portlets.user.workspace.client.resources.Resources;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.base.AlertBase;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.github.gwtbootstrap.client.ui.constants.Trigger;
import com.github.gwtbootstrap.client.ui.event.ClosedEvent;
import com.github.gwtbootstrap.client.ui.event.ClosedHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class WorkspaceFeaturesView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 6, 2015
 */
public class WorkspaceFeaturesView extends Composite {

	@UiField
	Alert alert_ws_features;

	@UiField
	HorizontalPanel ws_features;

	@UiField
	HorizontalPanel hp_feautures;

	@UiField
	HorizontalPanel info_features;

	@UiField
	com.google.gwt.user.client.ui.Label how_to;

	@UiField
	Label shf; //Share Folders & Files

	private Popover overShF= new Popover();

	@UiField
	Label upl; //UPLOAD

	private Popover overUpload = new Popover();

	@UiField
	Label plk; //PUBLIC LINK

	private Popover overPlk = new Popover();

	@UiField
	Label flk; //PUBLIC LINK

	private Popover overFolderlk = new Popover();

	@UiField
	FlowPanel fp1;

	@UiField
	FlowPanel fp2;

	@UiField
	FlowPanel fp3;

	@UiField
	CheckBox ck_features_show_again;

	@UiField
	Button btn_hide_ws_feautures_panel;

	private static WorkspaceFeaturesUiBinder uiBinder = GWT.create(WorkspaceFeaturesUiBinder.class);


	/**
	 * The Interface WorkspaceFeaturesUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Nov 6, 2015
	 */
	interface WorkspaceFeaturesUiBinder extends
		UiBinder<Widget, WorkspaceFeaturesView> {
	}

	/**
	 * Instantiates a new workspace features view.
	 */
	public WorkspaceFeaturesView() {

		initWidget(uiBinder.createAndBindUi(this));
		alert_ws_features.addStyleName("alert_ws_features");
		hp_feautures.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		ws_features.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hp_feautures.setCellWidth(fp1, "33%");

		Image info = Resources.getIconInformation().createImage();
		info.setTitle("Workspace available features");
		info_features.insert(info, 0);
		info_features.setCellVerticalAlignment(how_to, HasVerticalAlignment.ALIGN_MIDDLE);
		how_to.addStyleName("margin-left-5");
		hp_feautures.setCellVerticalAlignment(fp1, HasVerticalAlignment.ALIGN_MIDDLE);

		hp_feautures.setCellWidth(fp2, "33%");
		hp_feautures.setCellHorizontalAlignment(fp2, HasHorizontalAlignment.ALIGN_CENTER);
		hp_feautures.setCellVerticalAlignment(fp2, HasVerticalAlignment.ALIGN_MIDDLE);
		hp_feautures.setCellWidth(fp3, "33%");
		hp_feautures.setCellHorizontalAlignment(fp3, HasHorizontalAlignment.ALIGN_RIGHT);
		hp_feautures.setCellVerticalAlignment(fp3, HasVerticalAlignment.ALIGN_MIDDLE);

//		ck_features_show_again.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
//		ck_features_show_again.getElement().getFirstChildElement().setId("check_features_donotshowagain");

		ck_features_show_again.addStyleName("margin-right-10");
		shf.addStyleName("margin-right-10");
		upl.addStyleName("margin-right-10");
		flk.addStyleName("margin-right-10");
		plk.addStyleName("margin-right-10");
		initPopupShareFeature();
		initPopupFlkFeature();
		initPopupUploadFeature();
		initPopupPlkFeature();

		alert_ws_features.addClosedHandler(new ClosedHandler<AlertBase>() {

			@Override
			public void onClosed(ClosedEvent<AlertBase> event) {
				Boolean isChecked = ck_features_show_again.getValue();
				Boolean showAgain = !isChecked;
				GWT.log("Close alert_ws_features, show again? "+showAgain.toString().toLowerCase());
				AppController.setCookie(ConstantsPortlet.GCUBE_COOKIE_WORKSPACE_AVAILABLE_FEATURES, showAgain.toString().toLowerCase(), ConstantsPortlet.COOKIE_EXPIRE_DAYS);

			}
		});

		btn_hide_ws_feautures_panel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Boolean isChecked = ck_features_show_again.getValue();
				Boolean showAgain = !isChecked;
				GWT.log("Close alert_ws_features, show again? "+showAgain.toString().toLowerCase());
				AppController.setCookie(ConstantsPortlet.GCUBE_COOKIE_WORKSPACE_AVAILABLE_FEATURES, showAgain.toString().toLowerCase(), ConstantsPortlet.COOKIE_EXPIRE_DAYS);
				WorkspaceFeaturesView.this.alert_ws_features.close();
			}
		});
	}

	/**
	 *
	 */
	private void initPopupFlkFeature() {

		overFolderlk.setAnimation(false);
		overFolderlk.setWidget(flk);
		overFolderlk.setHeading("Workspace Folder Link");
		String icon = "<span style=\"margin-right:5px\">"+Resources.getIconFolderPublic().getSafeHtml().asString()+"</span>";
		overFolderlk.setText(icon+"The owner or administrator can create the Folder Link as public link in order to allow 'guest' users to access to the content of the folder (and its sub-folders). " +
			"The 'guest' user will access in read-only mode and it will can navigate and download files of the Folder Link (and its subfolders). You can send the Folder Link to anyone by pasting it into Workspace Message, your emails, instant messages, etc.");
		overFolderlk.setHtml(true);
		overFolderlk.setPlacement(Placement.BOTTOM);
		overFolderlk.setTrigger(Trigger.HOVER);
		overFolderlk.reconfigure();
	}

	/**
	 * Inits the popup share feature.
	 */
	private void initPopupShareFeature() {
		overShF.setAnimation(false);
		overShF.setWidget(shf);
		overShF.setHeading("Workspace Share Folders and Files");
		String icon = "<span style=\"margin-right:5px\">"+Resources.getIconShareFolder().getSafeHtml().asString()+"</span>";
		overShF.setText(icon+"The quickest way to share something is using the Share Folder. Locate the folder with the files you want to share and then click 'Share'");
		overShF.setHtml(true);
		overShF.setPlacement(Placement.BOTTOM);
		overShF.setTrigger(Trigger.HOVER);
		overShF.reconfigure();
	}

	/**
	 * Inits the popup upload feature.
	 */
	private void initPopupUploadFeature() {
		overUpload.setAnimation(false);
		overUpload.setWidget(upl);
		overUpload.setHeading("Workspace Upload Files and Archives");
		String iconUploadDND = "<span style=\"margin-right:5px\">"+Resources.getIconHand().getSafeHtml().asString()+"</span>";
		String iconUploadFile = "<span style=\"margin-right:5px\">"+Resources.getIconFileUpload().getSafeHtml().asString()+"</span>";
		String iconUploadArchive = "<span style=\"margin-right:5px\">"+Resources.getIconArchiveUpload().getSafeHtml().asString()+"</span>";
		overUpload.setText("You can upload files in the Workspace in several ways:<br/>"
				+iconUploadDND+ "1 - Drop your files from Desktop;<br/>"
				+iconUploadFile+ "2 - Click 'Upload' and Browse Files;<br/>"
				+iconUploadArchive+ "3 - Upload a zip file to unzip directly its content in the Workspace.<br/>");

		overUpload.setHtml(true);
		overUpload.setPlacement(Placement.BOTTOM);
		overUpload.setTrigger(Trigger.HOVER);
		overUpload.reconfigure();
	}

	/**
	 * Inits the popup plk feature.
	 */
	private void initPopupPlkFeature() {
		overPlk.setAnimation(false);
		overPlk.setWidget(plk);
		overPlk.setHeading("Workspace Public Link");
		String icon = "<span style=\"margin-right:5px\">"+Resources.getIconPublicLink().getSafeHtml().asString()+"</span>";
		overPlk.setText(icon+"Create links to files in your Workspace to download them. You can send the links to anyone by pasting them into Workspace Message, your emails, instant messages, etc.");
		overPlk.setHtml(true);
		overPlk.setPlacement(Placement.BOTTOM);
		overPlk.setTrigger(Trigger.HOVER);
		overPlk.reconfigure();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setVisible(boolean)
	 */
	public void setVisible(boolean bool){
		this.setVisible(bool);
	}



}
