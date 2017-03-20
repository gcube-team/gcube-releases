package org.gcube.portlets.user.reportgenerator.client.targets;

import org.gcube.portlets.d4sreporting.common.client.ImageConstants;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.dialog.CommentDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * <code> ReportTextArea </code> class represent the generic Widget that can be placed in the UI Component
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version July 2011 (3.0) 
 */
public class ReportTextArea extends Composite  {
	private String id;
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
	private Presenter presenter;

	private ComponentType type;

	protected AbsolutePanel mainPanel;

	protected HorizontalPanel topPanel; 

	protected VerticalPanel resizablePanel;

	protected Image commentImage;

	private HTML closeImage = new HTML();
	/**
	 * 
	 * @return .
	 */
	public VerticalPanel getResizablePanel() {
		return resizablePanel;
	}
	protected ReportTextArea myInstance;

	/**
	 * default constructor
	 *
	 */
	public ReportTextArea() {
		super();
	}

	/**
	 * 
	 * @param presenter .
	 * @param left left
	 * @param top top 
	 * @param width .
	 * @param height . 
	 * @param type a
	 */
	public ReportTextArea(String id, ComponentType type, final Presenter presenter, int left, int top, int width, int height, boolean hasComments, boolean isRemovable) {
		myInstance = this;

		this.id = id;
		this.type = type;
		this.presenter = presenter;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;

		mainPanel = new AbsolutePanel();

		topPanel = new HorizontalPanel();
		resizablePanel = new VerticalPanel();

		mainPanel.setPixelSize(width, height);

		topPanel.setPixelSize(30, 15);
		resizablePanel.setPixelSize(width, height);

		commentImage = new Image((hasComments) ? ImageConstants.IMAGE_COMMENTS : ImageConstants.IMAGE_COMMENTS_GRAY);
		commentImage.setTitle("Show user comments");
		topPanel.add(commentImage);

		commentImage.setStyleName("selectable");
		commentImage.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				String previousComments = "";
				int commentHeight = -1;
				previousComments = presenter.getComponentComments(myInstance).getComment();
				commentHeight = presenter.getComponentComments(myInstance).getAreaHeight();
				CommentDialog dlg = new CommentDialog(presenter.getEventBus(), myInstance, presenter.getCurrentUser(), previousComments, commentHeight);
				dlg.setPopupPosition(commentImage.getAbsoluteLeft()+20, commentImage.getAbsoluteTop());
				dlg.show();
			}
		});

		topPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		mainPanel.add(resizablePanel, 0, 0);
		mainPanel.add(topPanel, width-30, 0);
		mainPanel.setStyleName("d4sFrame");
		initWidget(mainPanel);

		if (isRemovable) {
			closeImage.setHeight("15px");
			closeImage.setStyleName("closeImage");
			closeImage.setTitle("Click to remove");
			topPanel.add(closeImage);
		}
		
		closeImage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				remove();
			}
		});
	}
	
	private void remove() {
		removeTemplateComponent();
	}

	public void removeTemplateComponent() {	}
	/**
	 * 
	 * @return .
	 */
	public ComponentType getType() {
		return type;
	}

	/**
	 *  
	 * @param type .
	 */
	public void setType(ComponentType type) {
		this.type = type;
	}


	/**
	 * used to resize the panel
	 * @param width w
	 * @param height h
	 */
	public void resizePanel(int width, int height) {
		if (height > 15 && width > 15) {
			mainPanel.setPixelSize(width, height);			
			resizablePanel.setPixelSize(width, height);
			mainPanel.setWidgetPosition(topPanel, width-15, 0);
		}
	}


	/***
	 * 
	 * @param height g
	 */
	public void setHeight(int height) {

		if (height > this.height) {
			mainPanel.setHeight(""+(height+20));			
			resizablePanel.setHeight(""+(height+20));	
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

	/**
	 * 
	 * @return the controller instance of the widget
	 */
	public Presenter getController() {
		return presenter;
	}


	/**
	 * 
	 * @return .
	 */
	public ReportTextArea getMyInstance() {
		return myInstance;
	}

	/**
	 * 
	 * @param myInstance .
	 */
	public void setMyInstance(ReportTextArea myInstance) {
		this.myInstance = myInstance;
	}

	public void removeCommentView() {
		commentImage.setUrl(ImageConstants.IMAGE_COMMENTS_GRAY);
	}
	public void addCommentView() {
		commentImage.setUrl(ImageConstants.IMAGE_COMMENTS);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
