package org.gcube.portlets.user.reportgenerator.client.targets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.user.reportgenerator.client.ReportService;
import org.gcube.portlets.user.reportgenerator.client.ReportServiceAsync;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateComponent;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateModel;
import org.gcube.portlets.user.reportgenerator.shared.ReportImage;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class ClientImage extends Composite {
	private ReportServiceAsync reportService = (ReportServiceAsync) GWT.create(ReportService.class);
	private String expectedContent = "";

	private final String METADATA_TITLE_KEY = "title";
	private final String METADATA_DESC_KEY = "description";
	private final String METADATA_SOURCE_KEY = "source";

	private int imageWidth;
	private int imageHeight;

	private FocusPanel focusPanel = new FocusPanel();
	private VerticalPanel mainPanel = new VerticalPanel();
	private SimplePanel imagePanel = new SimplePanel();
	private Image currImage;
	private Presenter presenter;

	private String idInBasket;

	public static final int DEFAULT_HEIGHT = 100;
	public static final int DEFAULT_WIDTH = 700;

	private TextBox titleTB = new TextBox();
	private TextBox descTB = new TextBox();
	private TextBox sourceTB = new TextBox();

	private Button resetB = new Button("Clear");
	private Button addImageB = new Button("Select from Workspace");
	private Button uploadImageB = new Button("Upload Image");
	private Button removeB = new Button("Remove Image");
	private BasicComponent basicComponent;

	/**
	 * 
	 * @param presenter
	 * @param width
	 * @param tag
	 */
	public ClientImage(BasicComponent co, Presenter presenter, int width, int height, boolean isRemovable, TextTableImage owner) {
		imageHeight = height;
		imageWidth = width;
		this.presenter = presenter;
		this.basicComponent = co;
		HorizontalPanel controlPanel = getControlPanel(isRemovable, owner);
		VerticalPanel attributesPanel = getAttributesPanel(co);
		mainPanel.add(controlPanel);
		mainPanel.add(imagePanel);
		mainPanel.add(attributesPanel);		

		mainPanel.setWidth((TemplateModel.TEMPLATE_WIDTH-95)+"px");
		imagePanel.setSize(DEFAULT_WIDTH+"px", DEFAULT_HEIGHT+"px");
		attributesPanel.setWidth((TemplateModel.TEMPLATE_WIDTH-95)+"px");

		mainPanel.setStyleName("imageWrapperPanel");
		imagePanel.setStyleName("imagePanel");
		controlPanel.setStyleName("tableControlPanel");
		attributesPanel.setStyleName("tableAttributesPanel");

		String imageURL = (String) co.getPossibleContent();

		if (imageURL == null || imageURL.trim().compareTo("") == 0 || imageURL.endsWith(TemplateComponent.DEFAULT_IMAGE_NAME)) {
			GWT.log("Empty ImageUrl="+imageURL);			
			imagePanel.addStyleName("imageEmptyPanel");
		}
		else {			
			GWT.log("Found ImageUrl="+imageURL);
			showImage(new Image(imageURL), width, height);
			enableUpload(false);
		}

		//set style for buttons
		for (int i = 0; i < controlPanel.getWidgetCount(); i++) {
			if (controlPanel.getWidget(i) instanceof Button) {
				Button b = (Button) controlPanel.getWidget(i);
				b.addStyleName("tableButton");
			}
		}
		if (isRemovable) {
			removeB.removeStyleName("tableButton");
			removeB.addStyleName("deleteEntryButton");
			removeB.getElement().getStyle().setMarginRight(10, Unit.PX);
		}

		focusPanel.add(mainPanel);
		initWidget(focusPanel);

		focusPanel.addMouseOutHandler(new MouseOutHandler() {			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				saveStatus();
			}
		});
	}
	
	public void resetImage() {
		titleTB.setText("");
		descTB.setText("");
		sourceTB.setText("");
		showImage(new Image(TemplateComponent.DEFAULT_IMAGE_NAME), DEFAULT_WIDTH, DEFAULT_HEIGHT);	
		imagePanel.addStyleName("imageEmptyPanel");
	}

	private HorizontalPanel getControlPanel(boolean isRemovable, final TextTableImage owner) {
		final HorizontalPanel toReturn = new HorizontalPanel();

		removeB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				if (owner != null) {
					remove(owner);
				}
			}
		});

		resetB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				resetImage();
				enableUpload(true);
			}
		});

		addImageB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				openFileExplorer(addImageB.getAbsoluteLeft(), addImageB.getAbsoluteTop());
			}
		});

		uploadImageB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				showUploadPopup();
			}
		});

		toReturn.setSpacing(3);
		if (isRemovable)
			toReturn.add(removeB);	
		toReturn.add(resetB);
		toReturn.add(uploadImageB);
		toReturn.add(addImageB);

		return toReturn;
	}
	
	private void showUploadPopup() {
	}

	private VerticalPanel getAttributesPanel(BasicComponent co) {
		VerticalPanel toReturn = new VerticalPanel();

		for (Metadata md : co.getMetadata()) {
			if (md.getAttribute().compareTo(METADATA_TITLE_KEY) == 0)
				titleTB.setText(md.getValue());
			if (md.getAttribute().compareTo(METADATA_DESC_KEY) == 0)
				descTB.setText(md.getValue());
			if (md.getAttribute().compareTo(METADATA_SOURCE_KEY) == 0)
				sourceTB.setText(md.getValue());
		}

		HorizontalPanel hp1 = new HorizontalPanel();
		HTML title = new HTML("<div style=\"white-space:nowrap;\">Title: <span style=\"color: red;\">*&nbsp;</span></div>", true);
		hp1.add(title);
		hp1.add(titleTB);
		titleTB.setWidth("135px");


		HTML desc = new HTML("&nbsp;Description:&nbsp;", true);

		HTML source = new HTML("Source: ", true);


		hp1.add(desc);
		hp1.add(descTB);
		descTB.setWidth("400px");
		toReturn.add(hp1);

		toReturn.add(source);
		toReturn.add(sourceTB);
		sourceTB.setWidth("693px");

		return toReturn;
	}

	public void add(Widget w) {
		mainPanel.add(w);
	}
	/**
	 * called by the select image ws light tree
	 * @param toShow the image to show
	 */
	public void showImage(Image toShow, int width, int height) {
		int checkedWidth = width;
		int checkedHeight = height;
		Double maxWidth = new Double(700);
		GWT.log("OriginalImage W="+checkedWidth+ " H="+checkedHeight);
		if (width > 700) {
			Double resizeFactor = maxWidth / ((double) width);			
			checkedWidth = new Double(width * resizeFactor).intValue();
			checkedHeight = new Double(height * resizeFactor).intValue();
		}
		imagePanel.clear();
		this.currImage = toShow;
		GWT.log("ResizedImage W="+checkedWidth+ " H="+checkedHeight);
		toShow.setWidth(checkedWidth+"px");
		imagePanel.setSize(checkedWidth+"px", checkedHeight+"px");
		imagePanel.add(toShow);
		imageWidth = checkedWidth;
		imageHeight = checkedHeight;
		saveStatus();
	}

	/**
	 * called by the constructor
	 * @param toShow the image to show
	 */
	public void showImage(Image toShow) {
		showImage(toShow, toShow.getWidth(), toShow.getHeight());
	}
	private void saveStatus() {
		Metadata title = new Metadata(METADATA_TITLE_KEY, titleTB.getText());
		Metadata desc = new Metadata(METADATA_DESC_KEY, descTB.getText());
		Metadata source = new Metadata(METADATA_SOURCE_KEY, sourceTB.getText());
		List<Metadata> mds = new ArrayList<Metadata>();
		mds.add(title);
		mds.add(desc);
		mds.add(source);
		basicComponent.setMetadata(mds);
		GWT.log("Saved in Session");
	}

	public List<Metadata> getMetadata() {
		return basicComponent.getMetadata();
	}
	/**
	 * 
	 * @param url .
	 * @param id the id in the folder
	 */
	public void dropImage(String url, String id, int width, int height)	{
		GWT.log("URL:" + url, null);
		idInBasket = id;
		showImage(new Image(url), width, height);
		enableUpload(false);
		
	}

	public void fetchImage(String identifier, final boolean isInteralImage, boolean fullDetails) {
		GWT.log("fetchImage:" + identifier);
		reportService.getImageUrlById(identifier, new AsyncCallback<ReportImage>() {

			@Override
			public void onSuccess(ReportImage image) {
				int width = image.getWidth();
				int height = image.getHeight();
				GWT.log("image.getUrl():" + image.getUrl());
				dropImage(image.getUrl(), image.getId(), width, height);

			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not fetch image from infrastructure " + caught.getCause());

			}
		});		
	}

	/**
	 * 
	 * @return the image
	 */
	public Image getDroppedImage() {
		if (currImage == null)
			return new Image();
		return currImage;
	}

	/**
	 * 
	 * @return .
	 */
	public String getIdInBasket() {
		return idInBasket;
	}
	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}
	/**
	 * 
	 * @param idInBasket .
	 */
	public void setIdInBasket(String idInBasket) {
		this.idInBasket = idInBasket;
	}
	/**
	 * 
	 * @return expectedContent
	 */
	public String getExpectedContent() {
		return expectedContent;
	}


	public BasicComponent getBasicComponent() {
		return basicComponent;
	}

	/**
	 * 
	 * @param expectedContent .
	 */
	public void setExpectedContent(String expectedContent) {
		this.expectedContent = expectedContent;	
	}

	private void openFileExplorer(final int left, final int top) {
		GWT.runAsync(WorkspaceExplorerSelectDialog.class, new RunAsyncCallback() {
			public void onSuccess() {
				
				ItemType[] types = {ItemType.IMAGE_DOCUMENT, ItemType.EXTERNAL_IMAGE};
				final WorkspaceExplorerSelectDialog wpTreepopup = new WorkspaceExplorerSelectDialog("Select the Image", Arrays.asList(types), Arrays.asList(types));

				WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {

					@Override
					public void onSelectedItem(Item item) {
						if (item.getType() == ItemType.IMAGE_DOCUMENT)
							fetchImage(item.getId(), true, true);
						else
							fetchImage(item.getId(), false, true);
						wpTreepopup.hide();
					}
					@Override
					public void onFailed(Throwable throwable) {
						Window.alert("There are networks problem, please check your connection.");            
					}				 
					@Override
					public void onAborted() {}
					@Override
					public void onNotValidSelection() {				
					}
				};
				wpTreepopup.addWorkspaceExplorerSelectNotificationListener(listener);
				wpTreepopup.show();					
			}

			public void onFailure(Throwable reason) {
				Window.alert("There are networks problem, please check your connection.");              
			}
		});
	}
	/**
	 * helper method that removes a image from the textTableImage widget
	 * @param owner
	 */
	private void remove(TextTableImage owner) {
		owner.removeFromParent(this);
		removeFromParent();	
	}
	
	private void enableUpload(boolean enabled) {
		addImageB.setVisible(enabled);
		uploadImageB.setVisible(enabled);
	}
}
