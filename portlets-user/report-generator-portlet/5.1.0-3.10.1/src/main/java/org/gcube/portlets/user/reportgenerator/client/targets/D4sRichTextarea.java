package org.gcube.portlets.user.reportgenerator.client.targets;

import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.user.reportgenerator.client.ReportGenerator;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;

import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * <code> D4sRichTextArea </code> class is a template component that goes into the workspace
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class D4sRichTextarea extends ReportTextArea {
	//the properties associated
	private List<Metadata> metas;
	/**
	 * the text area 
	 */
	private RichTextArea area = new RichTextArea();
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

	private TextTableImage owner;

	/**
	 * 
	 * @param presenter .
	 * @param left left
	 * @param top top 
	 * @param width .
	 * @param height . 
	 * @param type .
	 */
	public D4sRichTextarea(String id, ComponentType type, final Presenter presenter, int left, int top, final int width,  
			final int height, boolean hasComments, boolean showClose, TextTableImage owner) {
		super(id, type, presenter, left, top, width, height, hasComments, showClose);
		this.owner = owner;
		currHeight = height;
		myPanel = getResizablePanel();

		area.setPixelSize(width-6, height-2);
		area.setStyleName("d4sRichTextArea");

		switch (type) {
		case BODY:
			area.addStyleName("bodyArea");
			break;				
		default:
			break;
		}


		myPanel.add(area);
		//repositionMyPanel(0, 15);
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		myPanel.setPixelSize(width, height-15);
		setStyleName("d4sFrame");

		area.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				if (event.getNativeButton() == Event.BUTTON_RIGHT) {
					event.preventDefault();
					event.stopPropagation();
					MessageBox.alert("Warning","Please, use CTRL+V (CMD+V) for pasting into this area", null);
				}
				if (firstClick) {
					presenter.enableTextToolBar(area);
					presenter.enableBiblioEntry(area);
					firstClick = false;
					ReportGenerator.get().getDivHidden().setPixelSize(width, -1);
				}		
				HTML div = ReportGenerator.get().getDivHidden();
				div.setHTML(area.getHTML());
				int newHeight = div.getOffsetHeight()+20 ;
				if (newHeight > height-10 && newHeight != currHeight) {
					resizePanel(width, div.getOffsetHeight());
					//Window.alert("myInstance.top: " + myInstance.top);
					presenter.resizeTemplateComponentInModel(myInstance, width, newHeight);				
				}
			}			
		});

		area.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				firstClick = true;
			}

		});

		area.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (! (event.isAnyModifierKeyDown() && event.getNativeKeyCode() == 86))  { //event.getNativeKeyCode() == 86 is the V (to avoid ctrl V or cmd V)
					HTML div = ReportGenerator.get().getDivHidden();
					div.setHTML(area.getHTML());
					int newHeight = div.getOffsetHeight()+20 ;
					if (newHeight > height-10 && newHeight != currHeight) {
						resizePanel(myPanel.getOffsetWidth(), div.getOffsetHeight());
						//Window.alert("myInstance.top: " + myInstance.top);
						presenter.resizeTemplateComponentInModel(myInstance, myPanel.getOffsetWidth()+5, newHeight);
					}	
				}
			}			
		});

		area.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.isAnyModifierKeyDown() && event.getNativeKeyCode() == 86) { //event.getNativeKeyCode() == 86 is the V (to avoid ctrl V or cmd V)
					final GCubeDialog dlg = new GCubeDialog(true);
					int width = 500;
					dlg.setWidth(width+"px");
					dlg.setText("Paste operation");
					HTML tip = new HTML("Please enter your text here:");
					final TextArea ta = new TextArea();
					Button cancel = new Button("Cancel");
					Button paste = new Button("Paste it");
					HorizontalPanel hp = new HorizontalPanel();
					hp.add(cancel);
					hp.add(paste);

					ta.setWidth(width-20+"px");
					ta.setHeight("120px");

					VerticalPanel mainPanel = new VerticalPanel();
					mainPanel.add(tip);
					mainPanel.add(ta);
					mainPanel.add(hp);
					dlg.add(mainPanel);

					cancel.addClickHandler(new ClickHandler() {						
						@Override
						public void onClick(ClickEvent event) {
							dlg.hide();							
						}
					});
					paste.addClickHandler(new ClickHandler() {						
						@Override
						public void onClick(ClickEvent event) {
							area.setHTML(area.getHTML()+ta.getText()); 
							dlg.hide();				
						}
					});

					event.stopPropagation();
					event.preventDefault();
					dlg.center();

					Timer t = new Timer() {
						@Override
						public void run() {
							ta.setFocus(true);
						}
					};
					t.schedule(100);
				}

			}
		});
	}
	/**
	 * used to resize the panel
	 * @param width w
	 * @param height h
	 */
	@Override
	public void resizePanel(int width, int height) {
		if (height > 15 && width > 15) {
			mainPanel.setPixelSize(width, height+20);			
			resizablePanel.setPixelSize(width, height+20);
			mainPanel.setWidgetPosition(topPanel, width-30 , 0);
			area.setPixelSize(width, height+20);
			currHeight = height + 20;
		}
	}
	public List<Metadata> getMetadata() {
		return metas;
	}


	public void setMetadata(List<Metadata> metas) {
		this.metas = metas;
	}
	/**
	 * 
	 * @return .
	 */
	public String getHTML() {
		return area.getHTML();
	}

	/**
	 * @param html the html
	 */
	public void setHTML(String html) {
		area.setHTML(html);
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
	public void removeTemplateComponent() {
		if (owner != null) {
			owner.removeFromParent(this);
			removeFromParent();	
		}
	}
}
