package org.gcube.portlets.user.templates.client.components;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.client.uicomponents.ReportUIComponent;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * <code> CommentArea </code> class 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version April 2011 (1.0) 
 */
public class CommentArea extends ReportUIComponent {

	private TextArea textArea = new TextArea();
	private VerticalPanel myPanel;
	private Presenter presenter;

	public CommentArea(final Presenter presenter, int left, int top, int width,  final int height) {
		super(ComponentType.COMMENT,left, top, width, height);
		this.presenter = presenter;
		myPanel = getResizablePanel();
		
		textArea.setPixelSize(width, height);
		
		textArea.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				presenter.storeChangeInSession((Widget) event.getSource());				
			}
		});
		myPanel.add(textArea);
		myPanel.setTitle("Comment Area");
		
		//repositionMyPanel(0, 15);
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		myPanel.setPixelSize(width, height);
		textArea.setStyleName("commentArea");
		setStyleName("d4sFrame");
	}

	/**
	 * 
	 * @return .
	 */
	public TextArea getTextArea() {
		return textArea;
	}

	public ComponentType getType() {
		return ComponentType.COMMENT;
	}
	
	public void selectText() {
		textArea.setText("Add comment here");
		textArea.selectAll();
		textArea.setFocus(true);
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
