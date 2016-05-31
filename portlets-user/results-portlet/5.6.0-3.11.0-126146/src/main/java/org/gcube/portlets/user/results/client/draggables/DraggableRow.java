package org.gcube.portlets.user.results.client.draggables;

import org.gcube.portlets.user.results.client.components.ResultItem;
import org.gcube.portlets.user.results.client.constants.ImageConstants;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.model.BasketModelItemType;
import org.gcube.portlets.user.results.client.model.ResultObj;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;




/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2009 (3.0) 
 *
 */

public class DraggableRow extends Composite {

	private HorizontalPanel mainLayout = new HorizontalPanel();
	private String label;
	private String oid;
	private String uri;
	private String belongsTo;
	private BasketModelItemType type;
	private ResultObj resObject;
	private HTML caption;
	Image dragHandle = new Image(ImageConstants.DRAG_HANDLE);
	private Controller controller;

	/**
	 * 	 * constructor for multiplme rows
	 * @param oid
	 * @param objectTitle
	 * @param basketlabel
	 * @param ctrl
	 * @param clickListener
	 * @param type
	 */
	public DraggableRow(String uri, final String oid, String belongsTo, final ResultObj resObject, String basketlabel, Controller ctrl, final BasketModelItemType type) {
		controller = ctrl;
		mainLayout.setPixelSize(ResultItem.WIDTH - 170, 15);
		GWT.log("oid" + oid, null);
		this.oid = oid;
		this.uri = uri;
		this.resObject = resObject;
		this.belongsTo = belongsTo;
		this.label = basketlabel;
		this.type = type;
		caption = new HTML("<a href=\"Javascript:;\" >" + label + "<a/>", true);
		mainLayout.add(caption);
		mainLayout.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);

		initWidget(mainLayout);

		caption.addClickHandler(clickHandler);
	}


	/**
	 * single row constructor
	 * @param caption
	 * @param ctrl
	 * @param clickListener
	 */
	public DraggableRow(ResultObj resObject, HTML caption, String basketlabel, Controller ctrl, BasketModelItemType type) {
		mainLayout.setPixelSize(ResultItem.WIDTH - 170, 15);
		this.resObject = resObject;
		this.oid = resObject.getObjectURI();
		this.uri = resObject.getObjectURI();
		this.resObject = resObject;
		belongsTo = "----";

		this.type = type;
		this.label = basketlabel;
		mainLayout.add(caption);
		initWidget(mainLayout);
	}

	/**
	 * click listerner for links
	 */
	private ClickHandler clickHandler = new ClickHandler() {
		public void onClick(ClickEvent event) {
			switch (type) {
//			case METADATA:							
				//NewResultset.get().getModel().getResultService().getMetadata(getResObject().getCollectionID(), getResObject().getMetadataCollectionID(), getResObject().getOid(), metadataCallback);
//
//				MetadataViewerPopup w = new MetadataViewerPopup(mdDescriptor, 500, 350);
//				w.center();
//				w.show();

//				break;
			default:
				Window.alert("We're sorry, we couldn't find an appropriate visualizer for this item");
			break;
			}			
		}				
	};

	/*
	 * */
	/**
	 * fetch additional info for displaying them to the user depending on what he clicked
	 * @param ctrl
	 * @param type
	 */
	public void displayContent(Controller ctrl, String oid, String belongsTo, BasketModelItemType type) {
		switch (type) {
//		case ALTERNATIVE:
//			AsyncCallback<Client_DigiObjectInfo> callback = new AsyncCallback<Client_DigiObjectInfo>() {
//				public void onFailure(Throwable caught) {
//				}
//				public void onSuccess(Client_DigiObjectInfo result) {
//					mainLayout.clear();
//					label = result.getName() + " - " + result.getMimetype() + " - " + result.getLenght();
//					caption = new HTML("<a href=\"Javascript:;\" >" + label + " B<a/>", true);			
//					mainLayout.add(caption);
//					caption.addClickHandler(clickHandler);
//				}			
//			};	
//			mainLayout.clear();
//			caption = new HTML("<a href=\"Javascript:;\" >" + label + " - fetching details .. <img src=\"" + ImageConstants.INNER_RECORD_LOADER +"\" /><a/>", true); 
//			mainLayout.add(caption);			
//			ctrl.getNewresultset().getModel().getResultService().getDigitalObjectOnDemand(oid, belongsTo, type,  callback);	
//			break;
//		case PART:
//			AsyncCallback<Client_DigiObjectInfo> callback2 = new AsyncCallback<Client_DigiObjectInfo>() {
//				public void onFailure(Throwable caught) {
//				}
//				public void onSuccess(Client_DigiObjectInfo result) {
//					mainLayout.clear();
//					label = result.getName() + " - " + result.getMimetype() + " - " + result.getLenght();
//					caption = new HTML("<a href=\"Javascript:;\" >" + label + " B<a/>", true);					
//					mainLayout.add(caption);
//					caption.addClickHandler(clickHandler);
//				}	
//			};	
//			mainLayout.clear();
//			caption = new HTML("<a href=\"Javascript:;\" >" + label + " - fetching details .. <img src=\"" + ImageConstants.INNER_RECORD_LOADER +"\" /><a/>", true); 
//			mainLayout.add(caption);
//			ctrl.getNewresultset().getModel().getResultService().getDigitalObjectOnDemand(oid, belongsTo, type, callback2);	
//			break;		
//		case METADATA:
//			if (StringConstants.DEBUG) {
//				mdDescriptor = new MetadataDescriptor("111", "EiDB", "en", "MATAMATA METADATA LAPAPALAPA PATA PATA", "MATAMATA METADATA LAPAPALAPA PATA PATA");
//			} else {
//				AsyncCallback<MetadataDescriptor> callback3 = new AsyncCallback<MetadataDescriptor>() {
//					public void onFailure(Throwable caught) {
//					}
//					public void onSuccess(MetadataDescriptor metadataDesc) {
//						mdDescriptor = metadataDesc;
//						mainLayout.clear();
//						caption = new HTML("<a href=\"Javascript:;\" >" + metadataDesc.getFormat() + " - " + metadataDesc.getLanguage() + "<a/>", true);					
//						mainLayout.add(caption);
//						caption.addClickHandler(clickHandler);
//					}	
//				};	
//				mainLayout.clear();
//				caption = new HTML("<a href=\"Javascript:;\" >" + label + " - fetching metadata details .. <img src=\"" + ImageConstants.INNER_RECORD_LOADER +"\" /><a/>", true); 
//				mainLayout.add(caption);
//				ctrl.getNewresultset().getModel().getResultService().getMetadataFormatAndLang(oid, belongsTo, callback3);
//			}
//			break;
//		case ANNOTATION:
//			//caption.addClickListener(clickListener);
		default:
			break;
		}		
	}


	public String getBelongsTo() {
		return belongsTo;
	}


	public void setBelongsTo(String belongsTo) {
		this.belongsTo = belongsTo;
	}


	public String getTitle() {
		return resObject.getTitle();
	}

	public String getOid() {
		return oid;
	}
	public ResultObj getResObject() {
		return this.resObject;
	}
	public String getLabel() {
		return label;
	}

	public Image getDragHandle() {
		return dragHandle;
	}
	public BasketModelItemType getType() {
		return type;
	}


	public void setType(BasketModelItemType type) {
		this.type = type;
	}

	public String getUri() {
		return uri;
	}


	public void setUri(String uri) {
		this.uri = uri;
	}
}
