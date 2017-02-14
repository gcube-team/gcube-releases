package org.gcube.portlets.user.templates.client.components;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.client.uicomponents.ReportUIComponent;
import org.gcube.portlets.user.templates.client.Templates;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * <code> D4sRichTextArea </code> class is a template component that goes into the workspace
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version April 2011 (1.0) 
 */
public class D4sRichTextarea extends ReportUIComponent {

	/**
	 * the text area 
	 */
	private RichTextArea area = new RichTextArea();

	private Presenter presenter;

	/**
	 * a flag
	 */
	boolean firstClick = true;

	int currHeight = 0;

	VerticalPanel myPanel;
	/**
	 * 
	 *
	 */
	public D4sRichTextarea() {}

	/**
	 * 
	 * @param controller .
	 * @param left left
	 * @param top top 
	 * @param width .
	 * @param height . 
	 */
	public D4sRichTextarea(ComponentType type, final Presenter presenter, int left, int top, int width,  final int height) {
		super(type, left, top, width, height);
		this.presenter = presenter;
		currHeight = height;
		myPanel = getResizablePanel();

		area.setPixelSize(width-6, height-2);
		area.setStyleName("d4sRichTextArea");

		myPanel.add(area);
		//repositionMyPanel(0, 15);
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		myPanel.setPixelSize(width, height-15);
		setStyleName("d4sFrame");

		area.addMouseDownHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (event.getNativeButton() == Event.BUTTON_RIGHT) {
					event.preventDefault();
					event.stopPropagation();
					Info.display("Warning", "Please, use CTRL+V (CMD+V) for pasting into this area");
				}
				else {
					setSelected();
					GWT.log("onMouseDown", null);
					if (firstClick) {
						presenter.enableTextToolBar(area);
						firstClick = false;
						Templates.get().getDivHidden().setPixelSize(area.getOffsetWidth(), -1);
						//DOM.setElementAttribute(TemplateGenerator.get().getDivHidden().getElement(), "style", "width:" + area.getOffsetWidth() +";");					
					}			
				}
			}
		});



		area.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				firstClick = true;
				presenter.storeChangeInSession((Widget) event.getSource());

			}
		});

		area.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.isAnyModifierKeyDown() && event.getNativeKeyCode() == 86) { //event.getNativeKeyCode() == 86 is the V (to avoid ctrl V or cmd V)
					final MessageBox box = MessageBox.prompt("Paste", "Please enter your text here (PLAIN)", true);  
					box.addCallback(new Listener<MessageBoxEvent>() {  
						public void handleEvent(MessageBoxEvent be) { 
							area.setText(be.getValue());  

							//resize if needed
							HTML div = Templates.get().getDivHidden();
							//GWT.log("element.getHTML():\n" + area.getHTML(), null);
							div.setHTML(area.getHTML());
							int newHeight = div.getOffsetHeight();
							if (newHeight > height-10 && newHeight != currHeight) {
								resizeMe(myPanel.getOffsetWidth(), newHeight);
							}						
						}  
					});  
					event.stopPropagation();
					event.preventDefault();
				}

			}
		});

		area.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				HTML div = Templates.get().getDivHidden();
				//GWT.log("element.getHTML():\n" + area.getHTML(), null);
				div.setHTML(area.getHTML());
				int newHeight = div.getOffsetHeight();
				if (newHeight > height-10 && newHeight != currHeight) {
					resizeMe(myPanel.getOffsetWidth(), newHeight);
				}

			}
		});


	}

	/**
	 * resize the panel
	 * @param w
	 * @param h
	 */
	private void resizeMe(int w, int h) {
		resizePanel(w, h);
		//Window.alert("myInstance.top: " + myInstance.top);
		presenter.resizeTemplateComponentInModel(this, myPanel.getOffsetWidth(), h+5);
	}
	/**
	 * tells the presenter this tableb is selected
	 */
	private void setSelected() {
		presenter.setSelectedComponent(this);
	}


	/**
	 * used to resize the panel
	 * @param width w
	 * @param height h
	 */
	@Override
	public void resizePanel(int width, int height) {
		if (height > 15 && width > 15) {
			mainPanel.setPixelSize(width, height);			
			resizablePanel.setPixelSize(width, height);
			mainPanel.setWidgetPosition(topPanel, width-30 , 0);
			area.setPixelSize(width-4, height);
			currHeight = height;
		}
	}

	/**
	 * 
	 * @return .
	 */
	public RichTextArea getArea() {
		return area;
	}

	/**
	 * 
	 * @param firstClick .
	 */
	public void setFirstClick(boolean firstClick) {
		this.firstClick = firstClick;
	}


	@Override
	public void lockComponent(ReportUIComponent toLock, boolean locked) {
		presenter.lockComponent(this, locked);
	}

	@Override
	public void removeTemplateComponent(ReportUIComponent toRemove) {
		presenter.removeTemplateComponent(this);		
	}
}
