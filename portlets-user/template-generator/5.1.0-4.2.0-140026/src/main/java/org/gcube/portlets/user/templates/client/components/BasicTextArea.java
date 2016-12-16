package org.gcube.portlets.user.templates.client.components;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.client.uicomponents.ReportUIComponent;
import org.gcube.portlets.user.templates.client.Templates;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class BasicTextArea extends ReportUIComponent {
	private TextArea textArea = new TextArea();
	private VerticalPanel myPanel;
	private Presenter presenter;

	int currHeight = 0;


	public BasicTextArea(ComponentType type, final Presenter presenter, int left, int top, int width,  final int height) {
		super(type, left, top, width, height);
		this.presenter = presenter;
		myPanel = getResizablePanel();

		textArea.setPixelSize(width, height);

		textArea.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				presenter.storeChangeInSession((Widget) event.getSource());				
			}
		});
		switch (type) {
		case TITLE:
			textArea.setStyleName("titleArea");
			myPanel.setTitle("Title");
			break;
		case HEADING_1:
			textArea.setStyleName("heading1Area");
			myPanel.setTitle("Heading: Level 1");
			break;
		case HEADING_2:
			textArea.setStyleName("heading2Area");
			myPanel.setTitle("Heading Level 2");
			break;
		case HEADING_3:
			textArea.setStyleName("heading3Area");
			myPanel.setTitle("Heading: Level 3");
			break;
		case HEADING_4:
			textArea.setStyleName("heading4Area");
			myPanel.setTitle("Heading: Level 4");
			break;
		case HEADING_5:
			textArea.setStyleName("heading5Area");
			myPanel.setTitle("Heading: Level 5");
			break;
		case BODY_NOT_FORMATTED:
			textArea.setStyleName("simpleText");
			myPanel.setTitle("String: simple non formattable text");
			break;
		default:
			break;
		}		
		myPanel.add(textArea);
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		myPanel.setPixelSize(width, height);

		textArea.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				presenter.storeChangeInSession((Widget) event.getSource());

			}
		});

		textArea.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				HTML div = Templates.get().getDivHidden();
				GWT.log("element.getHTML():\n" + textArea.getText(), null);
				div.setHTML(textArea.getText());
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

	@Override
	public void lockComponent(ReportUIComponent toLock, boolean locked) {
		presenter.lockComponent(this, locked);
	}

	@Override
	public void removeTemplateComponent(ReportUIComponent toRemove) {
		GWT.log("presenter"+(presenter==null));
		presenter.removeTemplateComponent(this);		
	}

	public String getText() {
		return textArea.getText();
	}
	public void setText(String text) {
		textArea.setText(text);
	}

}
