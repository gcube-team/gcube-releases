package org.gcube.portlets.d4sreporting.common.client.uicomponents;

import org.gcube.portlets.d4sreporting.common.client.uicomponents.resources.Images;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * <code> ReportUIComponent </code> class represent the generic Widget that can be placed in the UI Component
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public abstract class ReportUIComponent extends Composite {

	/**
	 * the allowance when mouse cursor is on borders
	 */
	public final int DELTA = 7;
	/**
	 * variables of the components
	 */
	protected int left, top, width, height;

	/**
	 * the controller instance of the widget
	 */
	//private Presenter presenter;

	private ComponentType type;

	protected AbsolutePanel mainPanel;

	protected HorizontalPanel topPanel; 
	
	protected VerticalPanel resizablePanel;

	private Image lockImage = new Image();
	
	Images images = GWT.create(Images.class);

	/**
	 * 
	 * @return .
	 */
	public VerticalPanel getResizablePanel() {
		return resizablePanel;
	}
	private ReportUIComponent singleton;

	/**
	 * default constructor
	 *
	 */
	public ReportUIComponent() {
		super();
	}
	Widget myFakeTextArea;

	boolean isLocked = false;

	final HTML closeImage = new HTML();

	/**
	 *  final Presenter controller removde
	 * @param controller .
	 * @param left left
	 * @param top top 
	 * @param width .
	 * @param height . 
	 */
	public ReportUIComponent(ComponentType type, int left, int top, int width, int height) {
		singleton = this;

		this.type = type;
		//this.presenter = controller;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		closeImage.setHeight("15px");
		closeImage.setStyleName("closeImage");
		closeImage.setTitle("Click to remove this area");

		mainPanel = new AbsolutePanel();

		topPanel = new HorizontalPanel();
		resizablePanel = new VerticalPanel();

		mainPanel.setPixelSize(width, height);

		topPanel.setPixelSize(30, 15);
		resizablePanel.setPixelSize(width, height);
		mainPanel.setStyleName("d4sFrame");


		if (type == ComponentType.TITLE || 
				type == ComponentType.HEADING_1 || 
				type == ComponentType.HEADING_2 ||
				type == ComponentType.HEADING_3 || 
				type == ComponentType.HEADING_4 || 
				type == ComponentType.HEADING_5 || 
				type == ComponentType.TITLE ||
				type == ComponentType.BODY_NOT_FORMATTED || 
				type == ComponentType.BODY ||
				type == ComponentType.FLEX_TABLE) {
			
			lockImage = new Image(images.unlocked_darker());
			lockImage.setPixelSize(14, 14);
			lockImage.setStyleName("closeButton");
			if (type == ComponentType.FLEX_TABLE)
				lockImage.setTitle("Click to lock this table (its structure will not be editable in Reports)");
			else
				lockImage.setTitle("Click to lock this area (will not be editable in Reports)");
			topPanel.add(lockImage);

			lockImage.addMouseOverHandler(new MouseOverHandler() {			
				@Override
				public void onMouseOver(MouseOverEvent event) {	
					if (isLocked) {
						lockImage.setUrl(images.locked().getSafeUri());			
					} else {	
						lockImage.setUrl(images.unlocked().getSafeUri());	
					}
				}
			});

			lockImage.addMouseOutHandler(new MouseOutHandler() {			
				@Override
				public void onMouseOut(MouseOutEvent event) {	
					if (isLocked) {
						lockImage.setUrl(images.locked_darker().getSafeUri());							
					} else {					
						lockImage.setUrl(images.unlocked_darker().getSafeUri());	
					}
				}
			});
		}
	
		topPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		topPanel.add(closeImage);

		mainPanel.add(resizablePanel, 0, 0);
		mainPanel.add(topPanel, width-30, 0);

		initWidget(mainPanel);

		
		closeImage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				removeTemplateComponent(singleton);
			}
		});

		if (lockImage != null) {
			lockImage.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (isLocked) {
						lockComponent(singleton, false);
						setLocked(false);
					} else {
						lockComponent(singleton, true);
						setLocked(true);
					}					
				}
			});
		}
		
	}
	/**
	 * to be implemented by the implementing classes
	 * 
	 * @param toRemove the instance to remove from the working space
	 */
	public abstract void removeTemplateComponent(ReportUIComponent toRemove);
	/**
	 * to be implemented by the implementing classes
	 * 
	 * @param toLock the instance to lock 
	 */
	public abstract void lockComponent(ReportUIComponent toLock, boolean locked);


	public ComponentType getType() {
		return type;
	}

	public void setType(ComponentType type) {
		this.type = type;
	}

	/**
	 * 
	 * @param locked .
	 */
	public void setLocked(boolean locked) {
		isLocked = locked;
		if (locked) {
			lockImage.setUrl(images.locked_darker().getSafeUri());
			if (type != ComponentType.FLEX_TABLE)
				lockImage.setTitle("This area is locked  (will not be editable in Reports)");
			else
				lockImage.setTitle("This table is locked  (its structure will NOT be editable in Reports)");
		}
		else {
			lockImage.setUrl(images.unlocked_darker().getSafeUri());
			if (type != ComponentType.FLEX_TABLE)
				lockImage.setTitle("This area is not locked  (will be editable in Reports)");
			else
				lockImage.setTitle("This table is not locked  (its structure will be editable in Reports)");
		}

	}
	/**
	 * remove the close control from the UI
	 */
	public void hideCloseButton() {
		closeImage.setVisible(false);
	}
	/**
	 * used to resize the panel
	 * @param width w
	 * @param height h
	 */
	public void resizePanel(int width, int height) {

		mainPanel.setPixelSize(width, height);			
		resizablePanel.setPixelSize(width, height);
		mainPanel.setWidgetPosition(topPanel,  width-30, 0);

	}

	public void setHeight(int height) {
		if (height > this.height) {
			mainPanel.setWidgetPosition(topPanel, width-15, 0);
		}
	}
	/**
	 * 
	 * @param left .
	 * @param top .
	 */
	public void repositionMyPanel(int left, int top) {
		//mainPanel.setWidgetPosition(resizablePanel, left, top);
	}

	public Widget getMyFakeTextArea() {
		return myFakeTextArea;
	}

	public void setMyFakeTextArea(Widget myFakeTextArea) {
		this.myFakeTextArea = myFakeTextArea;
	}

	/**
	 * @return .
	 */
	public int getLeft() {
		return left;
	}



	/**
	 * 
	 * @param left .
	 */
	public void setLeft(int left) {
		this.left = left;
	}

	/**
	 * 
	 * @return -.
	 */
	public int getTop() {
		return top;
	}

	/**
	 * 
	 * @param top .
	 */
	public void setTop(int top) {
		this.top = top;
	}

	public ReportUIComponent get() {
		return singleton;
	}
	public HorizontalPanel getTopPanel() {
		return topPanel;
	}
}

