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
 * 
 * @author massi
 *
 */
public class InstructionArea extends ReportUIComponent {
	private TextArea textArea = new TextArea();
	private VerticalPanel myPanel;
	private Presenter presenter;

	public InstructionArea(final Presenter presenter, int left, int top, int width,  final int height) {
		super(ComponentType.INSTRUCTION, left, top, width, height);
		this.presenter = presenter;
		myPanel = getResizablePanel();
		
		textArea.setPixelSize(width-10, height);
		
		textArea.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				presenter.storeChangeInSession((Widget) event.getSource());				
			}
		});
		myPanel.add(textArea);
		myPanel.setTitle("Instruction Area");
		
		//repositionMyPanel(0, 15);
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		myPanel.setPixelSize(width, height);
		textArea.setStyleName("instructionArea");
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
		return ComponentType.INSTRUCTION;
	}
	
	public void selectText() {
		textArea.setText("Add instructions here");
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
