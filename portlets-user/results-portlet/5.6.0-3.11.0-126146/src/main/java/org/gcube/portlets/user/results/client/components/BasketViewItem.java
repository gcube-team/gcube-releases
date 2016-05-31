package org.gcube.portlets.user.results.client.components;

import org.gcube.portlets.user.results.client.constants.FileTypeImagesConstants;
import org.gcube.portlets.user.results.client.constants.ImageConstants;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.model.BasketModelItem;
import org.gcube.portlets.user.results.client.model.BasketModelItemType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <code> BasketItem </code> is represent the single basket item in the basket
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (2.0) 
 */
public class BasketViewItem extends Composite {

	public final static int WIDTH = BasketView.BASKET_WIDTH;
	public final static int HEIGHT = 25;
	private BasketViewItem myInstance;
	private BasketModelItem myItem;
	private FlexTable myTable;
	private Controller controller;
	private Image thumb = new Image();
	private Label caption;

	private CellPanel mainLayout = new HorizontalPanel(); 

	public BasketViewItem(Controller controller, String label, FlexTable table, BasketModelItem myItem) {
		myInstance = this;
		this.myItem = myItem;
		this.myTable = table;
		this.controller = controller;
		//set the image icon to be shown depending on the type
		applyImage(label, myItem.getItemType());

		String toDisplay= label;

		if (label.length() > 60) {
			toDisplay = label.substring(0, 60);
			toDisplay += " ...";
		}
		caption = new Label(" " + toDisplay);
		caption.setTitle(label);
		this.thumb.setPixelSize(16, 16);
		caption.setPixelSize(WIDTH- 2*HEIGHT, HEIGHT);

		VerticalPanel menuPanel = new VerticalPanel();

		menuPanel.setPixelSize(40, 20);

		menuPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		menuPanel.add(getActionsMenu());

		//this.more.setPixelSize(18, 18);

		mainLayout.setPixelSize(WIDTH, HEIGHT);

		mainLayout.add(thumb);
		mainLayout.add(new HTML("&nbsp;&nbsp;", true));
		mainLayout.add(caption);
		mainLayout.add(getCloseButton());

		mainLayout.setCellVerticalAlignment(thumb, HasVerticalAlignment.ALIGN_MIDDLE);
		mainLayout.setCellVerticalAlignment(caption, HasVerticalAlignment.ALIGN_MIDDLE);
		mainLayout.setSpacing(1);
		mainLayout.setStyleName("gcube_basket_item_new");

		initWidget(mainLayout);		
	}


	public String getCaption() {
		return caption.getText();
	}


	public void setCaption(String caption) {
		this.caption.setText(caption);
	}

	public Image getThumb() {
		return thumb;
	}

	public void setThumb(Image thumb) {
		this.thumb = thumb;
	}

	
	private Image getCloseButton() {
		Image close = new Image(ImageConstants.CLOSE);
		close.setStyleName("button_help");
		
		close.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent arg0) {
				boolean result = controller.removeBasketItemFromModel(myItem);
				if (result) {
					myInstance.removeFromParent();
				}
			}
		});
		
		return close;
	}
	/**
	 * build the Actions Menu
	 * @return the built menu
	 */
	private MenuBar getActionsMenu() {

		Command openPreview = new Command() {
			public void execute() {

			}
		};
		Command deleteBasket = new Command() {
			public void execute() {
				boolean result = controller.removeBasketItemFromModel(myItem);
				if (result) {
					myInstance.removeFromParent();
				}
			}
		};

//		Create the file menu
		MenuBar actionsMenu = new MenuBar(true);

		actionsMenu.setAnimationEnabled(true);
		//actionsMenu.addItem("Show Preview", openPreview);
		actionsMenu.addItem("Delete from basket", deleteBasket);
		actionsMenu.setWidth("120");

		MenuItem menuitem = new MenuItem("<img class=\"button_help\" src=\""+ ImageConstants.ICO +"\"/>&nbsp;", true, actionsMenu);
		
		boolean menuNextToRecord =  true;

		MenuBar newMenu = new MenuBar(menuNextToRecord);
		newMenu.setStyleName("custom-menu");
		newMenu.getElement().getStyle().setProperty( "background", "transparent");
		menuitem.getElement().getStyle().setProperty( "background", "transparent");
		newMenu.addItem(menuitem);
		return newMenu;
	}

	/**
	 * set the image icon to be shown depending on the type
	 * @param label the item label
	 * @param type the item type
	 */
	public void applyImage(String label, BasketModelItemType type) {
		if (type == BasketModelItemType.EXTERNAL_FILE) {

			if (label.lastIndexOf(".") >= 0) {
				String extension = label.substring(label.lastIndexOf("."), label.length());
				if (extension.equals(".mp3")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_MP3);
				} else if (extension.equals(".txt")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_TXT);
				} else if (extension.equals(".jpg")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_JPG);
				} else if (extension.equals(".xml")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_TXT);
				} else if (extension.equals(".doc")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_DOC);
				} else if (extension.equals(".pdf")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_PDF);
				} else if (extension.equals(".zip")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_ZIP);
				} else if (extension.equals(".docx")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_DOC);
				} else if (extension.equals(".doc")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_DOC);
				} else if (extension.equals(".html")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_HTML);
				} else if (extension.equals(".htm")) {
					thumb = new Image(FileTypeImagesConstants.BASKET_HTML);
				}				
			} else
				thumb = new Image(ImageConstants.BASKET_EXTERNAL);
		} else {
			switch (type) {
			case EXTERNAL_IMAGE:
				thumb = new Image(ImageConstants.BASKET_EXTERNAL_IMAGE);
				break;
			case EXTERNAL_PDF_FILE:
				thumb = new Image(ImageConstants.BASKET_EXTERNAL_PDF);
				break;
			case INFO_OBJECT:
				thumb = new Image(ImageConstants.BASKET_GENERIC_DSO);
				break;
			case INFO_OBJECT_LINK:
				thumb = new Image();
				break;
			case QUERY:
				thumb = new Image(ImageConstants.BASKET_QUERY);
				break;
			case REPORT:	
				thumb = new Image(ImageConstants.BASKET_REPORT);
				break;
			case REPORT_TEMPLATE:	
				thumb = new Image(ImageConstants.BASKET_TEMPLATE);
				break;
			case TIMESERIES:
				thumb = new Image(ImageConstants.BASKET_TS);
				thumb.setTitle("Time Series");
				break;
			case FOLDER:
				thumb = new Image(ImageConstants.FOLDER);
				thumb.setTitle("Time Series");
				break;
			case SHARED_FOLDER:
				thumb = new Image(ImageConstants.SHARED_FOLDER);
				thumb.setTitle("Shared Folder");
				break;
			default:
				thumb = new Image(ImageConstants.BASKET_EXTERNAL);
			break;
			}
		}
	}
}
