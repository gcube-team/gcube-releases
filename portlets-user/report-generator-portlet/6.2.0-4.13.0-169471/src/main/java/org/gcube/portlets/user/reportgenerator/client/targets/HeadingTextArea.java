package org.gcube.portlets.user.reportgenerator.client.targets;

import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * <code> HeadingTextArea </code> 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version July 2011 (3.0) 
 */
public class HeadingTextArea extends ReportTextArea {

	private TextArea textArea = new TextArea();
	private VerticalPanel myPanel;
	private Presenter presenter;
	private List<Metadata> metas;
	int currHeight = 0;


	public HeadingTextArea(String id, ComponentType type, final Presenter presenter, int left, int top, int width,  final int height, boolean hasComments, boolean showClose) {
		super(id, type, presenter, left, top, width, height, hasComments, showClose);
		this.presenter = presenter;
		myPanel = getResizablePanel();

		textArea.setPixelSize(width, height);

		textArea.setStyleName("report-ui-component");
		switch (type) {
		case TITLE:
			textArea.addStyleName("title");
			myPanel.setTitle("Title");
			resizePanel(width, 30);
			break;
		case HEADING_1:
			textArea.addStyleName("heading1");
			myPanel.setTitle("Heading: Level 1");
			break;
		case HEADING_2:
			textArea.addStyleName("heading2");
			myPanel.setTitle("Heading Level 2");
			break;
		case HEADING_3:
			textArea.addStyleName("heading3");
			myPanel.setTitle("Heading: Level 3");
			break;
		case HEADING_4:
			textArea.addStyleName("heading4");
			myPanel.setTitle("Heading: Level 4");
			break;
		case HEADING_5:
			textArea.addStyleName("heading5");
			myPanel.setTitle("Heading: Level 5");
			break;
		default:
			break;
		}		
		myPanel.add(textArea);
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		myPanel.setPixelSize(width, height);

	}
	public String getText() {
		return textArea.getText();
	}
	public void setText(String text) {
		textArea.setText(text);
	}

	public List<Metadata> getMetadata() {
		return metas;
	}


	public void setMetadata(List<Metadata> metas) {
		this.metas = metas;
	}


}
