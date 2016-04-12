package org.gcube.portlets.user.results.client.components;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.gcube.portlets.user.results.client.constants.ImageConstants;
import org.gcube.portlets.user.results.client.constants.StringConstants;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.control.FlexTableRowDragController;
import org.gcube.portlets.user.results.client.draggables.DraggableRow;
import org.gcube.portlets.user.results.client.model.ActionType;
import org.gcube.portlets.user.results.client.model.BasketModelItemType;
import org.gcube.portlets.user.results.client.model.Client_DigiObjectInfo;
import org.gcube.portlets.user.results.client.model.ResultObj;
import org.gcube.portlets.user.results.client.panels.RecordSubpanel;
import org.gcube.portlets.user.results.client.util.MimeTypeImagecreator;
import org.gcube.portlets.user.results.client.util.MyCommand;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * <code> ResultItem </code> represents the single result displayed from the portlet
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (2.0) 
 */
public class ResultItem extends Composite {
	public final static int WIDTH = 635;
	public final static int HEIGHT = 55;

	private Client_DigiObjectInfo myDigitalObject;

	private Image thumb;
	private HTML caption = new HTML();
	private HTML collectionDisplayer = new HTML();
	private Image more;
	//private Label viewMetadataLabel = new Label("View Metadata");
	private  ResultObj resObject;


	private CellPanel mainLayout = new VerticalPanel();

	private HorizontalPanel roundedHeader = new HorizontalPanel(); 
	private CellPanel headerpart; 
	private CellPanel contentPart = new HorizontalPanel();
	private HorizontalPanel roundedFooter = new HorizontalPanel();
	private VerticalPanel thumbPanel = new VerticalPanel();
	private Vector<ToggleButton> buttons = new Vector<ToggleButton>();
	/**
	 * the main panel of the record
	 */
	private VerticalPanel captionPanel = new VerticalPanel();
	protected SimplePanel collectionPanel = new SimplePanel();

	private FlexTableRowDragController dragController;

	private Controller controller;

	private HeaderControlPanel myHeaderControlPanel = new HeaderControlPanel();

	/**
	 * 
	 * @param pos
	 * @param thumb
	 * @param html
	 */
	public ResultItem(Controller controller, final ResultObj resObject, int pos, Image thumb, FlexTableRowDragController dragController) {
		super();
		mainLayout.setStyleName("resultItem");
		this.dragController = dragController;
		this.controller = controller;
		this.resObject = resObject;

		headerpart = getHeaderPanel(pos);

		headerpart.setWidth("100%");
		roundedHeader.setWidth("100%");
		roundedFooter.setWidth("100%");

		headerpart.setStyleName("newresultset-resultItem-header");
		String collName = "";
		if (resObject.getCollectionName() != null)
			collName = (resObject.getCollectionName().length() > 85) ? resObject.getCollectionName().substring(0, 85) + " ... ": resObject.getCollectionName() ;
		collectionDisplayer = new HTML("&nbsp;<b>" + collName +"</b>", true);
		collectionDisplayer.setStyleName("collectionName");

		collectionPanel.setStyleName("newresultset-resultItem");
		collectionPanel.add(collectionDisplayer);

		mainLayout.add(roundedHeader);
		mainLayout.add(headerpart);
		mainLayout.add(contentPart);		
		mainLayout.add(collectionPanel);
		mainLayout.add(roundedFooter);

		//header part
		mainLayout.setCellHeight(headerpart, "5");
		mainLayout.setCellHeight(contentPart, "100%");
		mainLayout.setSpacing(0);

		//content part
		this.thumb = thumb;
		caption = new HTML(resObject.getHtmlText(), true); 

		caption.setWidth("100%");
		more = new Image(ImageConstants.D4S_EYE);
		more.setStyleName("selectable");

		contentPart.setPixelSize(WIDTH, HEIGHT);

		thumbPanel.setPixelSize(HEIGHT-10, HEIGHT-10);
		thumbPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		thumbPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		thumbPanel.add(thumb);

		contentPart.add(thumbPanel);
		contentPart.add(new HTML("&nbsp;&nbsp;", true));
		captionPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		captionPanel.add(caption);
		contentPart.add(captionPanel);

		final Image newThumb = thumb;

		if (StringConstants.DEBUG) {
			myDigitalObject = new Client_DigiObjectInfo();
			myDigitalObject.setMimetype("image/gif");
			myDigitalObject.setName("nome");
			myDigitalObject.setObjectURI("12345678");

		}
		//TODO check this on what to do regarding the mime type
		else {
			AsyncCallback<Client_DigiObjectInfo> callback = new AsyncCallback<Client_DigiObjectInfo>() {

				public void onFailure(Throwable caught) {
					newThumb.setUrl(MimeTypeImagecreator.getThumbImage("unknown").getUrl());
				}

				public void onSuccess(Client_DigiObjectInfo result) {
					newThumb.setUrl(MimeTypeImagecreator.getThumbImage(result.getMimetype()).getUrl());	
					myDigitalObject = result;
					resObject.setMimetype(result.getMimetype());
					enableTabs(result);
				}			
			};		
			controller.getNewresultset().getModel().getResultService().getDigitalObjectInitialInfo(pos, callback);

		}


		viewObject(resObject);

		MenuBar newMenu = getActionsMenu(resObject);

		contentPart.add(newMenu);

		contentPart.setCellVerticalAlignment(thumb, HasVerticalAlignment.ALIGN_MIDDLE);
		contentPart.setCellVerticalAlignment(captionPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		contentPart.setCellVerticalAlignment(newMenu, HasVerticalAlignment.ALIGN_MIDDLE);
		contentPart.setSpacing(1);
		contentPart.setStyleName("newresultset-resultItem");

		setCollectionVisibility(false);

		initWidget(mainLayout);
	}

	/**
	 * 
	 * @param pos
	 * @return
	 */
	public CellPanel getHeaderPanel(int pos) {
		CellPanel toReturn = new HorizontalPanel();

		HorizontalPanel left = new HorizontalPanel();
		HorizontalPanel right = new HorizontalPanel();

		right.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		left.add(myHeaderControlPanel);

		right.add(new HTML(pos+"&nbsp;&nbsp;"));

		toReturn.add(left);
		toReturn.add(right);

		toReturn.setCellWidth(left, "300");
		toReturn.setCellHorizontalAlignment(right, HasAlignment.ALIGN_RIGHT);

		left.setStyleName("newresultset-resultItem-header");
		right.setStyleName("newresultset-resultItem-header");
		toReturn.setStyleName("newresultset-resultItem-header");

		return toReturn;
	}


	/**
	 * build the Actions Menu
	 * @return the built menu
	 */
	private MenuBar getActionsMenu(ResultObj resObj) {

		String objectURI = (resObj.getObjectURI() == null) ? "0000" : resObject.getObjectURI();
		String colID = (resObj.getCollectionID() == null) ? "0000" : resObject.getCollectionID();
		String title = (resObj.getTitle() == null) ? "0000" : resObject.getTitle(); 
		String currUsername = (resObj.getCurrUserName() == null) ? "0000" : resObject.getCurrUserName();

		//Create the eye menu
		MenuBar actionsMenu = new MenuBar(true);
		actionsMenu.setAutoOpen(true);

		//TODO: Now the menu bar contains only the VIEW and SAVE content options
		actionsMenu.addItem("View Metadata", new MyCommand(controller, ActionType.VIEW_METADATA, objectURI, colID, objectURI, title, currUsername));
		actionsMenu.addSeparator();
		actionsMenu.addItem("View Content", new MyCommand(controller, ActionType.VIEW_CONTENT, objectURI, colID, objectURI, title, currUsername));
		actionsMenu.addSeparator();
		//TODO: I may have to re-enable this
		//	actionsMenu.addItem("Save Content", new MyCommand(controller, ActionType.SAVE_CONTENT, objectURI, colID, objectURI, title, currUsername));
		//	actionsMenu.addSeparator();

		MenuItem menuitem = new MenuItem("<img class=\"button_help\" alt=\"click to see the available actions for this result\" src=\""+ ImageConstants.D4S_EYE +"\"/>&nbsp;", true, actionsMenu);
		boolean menuNextToRecord =  (Window.getClientWidth() > 1050) ? true : false;
		//check if in the window there's room for setting the menu besides the "eye" or below it
		MenuBar newMenu = new MenuBar(menuNextToRecord);
		//newMenu.setAutoOpen(true);
		newMenu.setStyleName("custom-menu");
		newMenu.getElement().getStyle().setProperty( "background", "transparent");
		menuitem.getElement().getStyle().setProperty( "background", "transparent");
		newMenu.addItem(menuitem);
		return newMenu;
	}

	public VerticalPanel getCaptionPanel() {
		return captionPanel;
	}

	/**
	 * 
	 *
	 */
	public void viewObject(ResultObj resObject) {
		this.captionPanel.clear();
		List<DraggableRow> list = new LinkedList<DraggableRow>(); 
		//TODO on 22
		DraggableRow toAdd = null;
		if (resObject.getObjectURI().startsWith("http://") && !resObject.getObjectURI().contains("/tree/"))
			toAdd = new DraggableRow(resObject, caption, resObject.getTitle(), controller, BasketModelItemType.OPENSEARCH);
		else
			toAdd = new DraggableRow(resObject, caption, resObject.getTitle(), controller, BasketModelItemType.INFO_OBJECT);
		list.add(toAdd);
		RecordSubpanel panel = new RecordSubpanel(list, dragController, false);

		this.captionPanel.add(panel);
	}

	/**
	 *
	 */
//	public void viewMetadata(final ResultObj resObject) {
//		this.captionPanel.clear();
//		Label metadataLabel = new Label("View Metadata");
//		metadataLabel.setStyleName("hyperlink_style_label");
//		this.captionPanel.add(new HTML(""));
//		this.captionPanel.add(metadataLabel);
//		this.captionPanel.setCellWidth(metadataLabel, "100%");
//
//		metadataLabel.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				String objectURI = resObject.getObjectURI();
//				if(objectURI.startsWith("http") && objectURI.contains("/tree/")) {
//					AsyncCallback<GenericTreeRecordBean> getObjectInfoCalback = new AsyncCallback<GenericTreeRecordBean>() {
//
//						@Override
//						public void onFailure(Throwable caught) {
//							ResultsDisplayer.unmask();
//							Window.alert("Failed to retrieve the object's metadata. Please try again");
//						}
//
//						@Override
//						public void onSuccess(GenericTreeRecordBean result) {
//							ResultsDisplayer.unmask();
//							if (result != null) {
//								// Use the oai_dc viewer
//								if (result.getType().equals(ObjectType.OAI)) {
//									AsyncCallback<String> metadataCallback = new AsyncCallback<String>() {
//
//										@Override
//										public void onFailure(Throwable caught) {
//											ResultsDisplayer.unmask();
//											Window.alert("Failed to retrieve the object's metadata. Please try again");
//										}
//
//										@Override
//										public void onSuccess(String result) {
//											ResultsDisplayer.unmask();
//											// Add here the new OAI metadata viewer
//											if (result != null)
//												new MetadataViewerPopup(result, 600, 450);
//										}
//									};controller.getNewresultset().getModel().getResultService().transformOAIMetadata(result.getPayload(), metadataCallback);
//									ResultsDisplayer.mask();
//								}
//								// Use the Generic XML Viewer for all other types
//								else
//									new GenericXMLViewerPopup(result.getPayload(), "xmlviewer", true);
//							}
//							else
//								Window.alert("Metadata are not available for this object");
//						}
//					};controller.getNewresultset().getModel().getResultService().getObjectInfo(objectURI, getObjectInfoCalback);
//					ResultsDisplayer.mask();
//				}
//				else 
//					Window.alert("No metadata are available for this type of object");
//			}
//		});
//	}


	/**
	 * 
	 * @param sender the only toggle button not to be put up
	 */

	public void setAllButtonsUp(Widget sender) {
		for (ToggleButton button : buttons) 
			if (! button.equals(sender)) button.setDown(false);	
	}

	public String getCaption() {
		return caption.getText();
	}


	public void setCaption(String caption) {
		this.caption.setText(caption);
	}


	public Image getMore() {
		return more;
	}


	public void setMore(Image more) {
		this.more = more;
	}


	public Image getThumb() {
		return thumb;
	}


	public void setThumb(Image thumb) {
		this.thumb = thumb;
	}
	
	/**
	 * 
	 * @param result
	 */
	public void enableTabs(Client_DigiObjectInfo result) {
		// The metadata tab is enabled only when the object is a TM object.
		// Currently the only way to distinguish this is to check the format of the http URL
		//if (result.getURI().startsWith("http") && result.getURI().contains("/tree/"))
		//	myHeaderControlPanel.enableTab(true);
		//else
		//	myHeaderControlPanel.enableTab(false);
	}

	/**
	 * 
	 * @author Massimiliano Assante
	 * private class for constructing the inner record tab panel
	 */
	private class HeaderControlPanel extends Composite {

		protected HorizontalPanel mainLayout = new HorizontalPanel();

		protected ToggleButton objectTab = new ToggleButton();
	//	protected ToggleButton metadataTab = new ToggleButton("Metadata");

		public HeaderControlPanel() {
			objectTab.setWidth("65px");
			objectTab.setHTML("<img src=\""+ ImageConstants.BASKET_GENERIC_DSO_TINY +"\"/> Object");


	//		metadataTab.setWidth("75px");
	//		metadataTab.setHTML("<img src=\""+ ImageConstants.BASKET_METADATA_TINY +"\"/> Metadata");

			buttons.add(objectTab);
	//		buttons.add(metadataTab);			
			mainLayout.add(new HTML("&nbsp;&nbsp;"));

			//the object tab is always present
			mainLayout.add(objectTab);
		
			objectTab.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					setAllButtonsUp((Widget) event.getSource());
					viewObject(resObject);
				}		
			});

//			metadataTab.addClickHandler(new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					setAllButtonsUp((Widget) event.getSource());
//					viewMetadata(resObject);
//				}		
//			});

			objectTab.setDown(true);
			initWidget(mainLayout);
		}

//		public void enableTab(boolean enable) {
//
//			if (enable) 
//				mainLayout.add(metadataTab);
//			else 
//				mainLayout.remove(metadataTab);
//		}
	}

	protected enum TabType { OBJECT, ALTERNATIVE, METADATA, ANNOTATION, PART; }

	public ResultObj getResObject() {
		return resObject;
	}

	/**
	 * show or hide the collection name in the result record
	 * @param visible
	 */
	public void setCollectionVisibility(boolean visible){
		collectionPanel.setVisible(visible);
	}

}