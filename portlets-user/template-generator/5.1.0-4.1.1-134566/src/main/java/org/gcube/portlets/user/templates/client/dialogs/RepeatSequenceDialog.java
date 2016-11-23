package org.gcube.portlets.user.templates.client.dialogs;

import java.util.ArrayList;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.user.templates.client.components.ExtButton;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
/**
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * dialog allowing the insertion of elements having to be grouped
 *
 */
public class RepeatSequenceDialog  extends Window {

	private final static int WIDTH = 400;
	private final static int HEIGHT = 740;

	final LayoutContainer sourceContainer = new LayoutContainer();  
	final LayoutContainer container = new LayoutContainer();  

	public RepeatSequenceDialog(final Presenter presenter) {
		super();
		setHeading("Add Repeating Sequence - Use Drag & Drop to move components");  
		
		setMaximizable(false);  
		setResizable(false);

		setWidth(WIDTH);  
		setHeight(HEIGHT);  

		HorizontalPanel hp = new HorizontalPanel();  
		hp.setSpacing(10);  

		
		container.setLayoutOnChange(true);  
		container.setBorders(true);  
		container.setSize(200, 700);  
		container.setStyleName("dropHere");
		

		DropTarget target = new DropTarget(container) {  
			@Override  
			protected void onDragDrop(DNDEvent event) {  
				super.onDragDrop(event);  
				Button but = event.getData();  
				container.add(but); 
				restoreSourceButtons();
			}  

		};  
		target.setGroup("test");  
		target.setOverStyle("drag-ok");  
		target.setOperation(Operation.COPY);


		sourceContainer.setLayoutOnChange(true);
		sourceContainer.setWidth(140);
		
		addSources(sourceContainer);  

		Button reset = new Button("Reset");  
		reset.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				container.removeAll();  
				sourceContainer.removeAll();  
				addSources(sourceContainer);  
			}  
		});  
		
		Button add = new Button("Add Selected");  
		add.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				ComponentType[] toPass = new ComponentType[container.getItemCount()];
				for (int i = 0; i < container.getItemCount(); i++) {
					ExtButton selected = (ExtButton) container.getWidget(i);
					toPass[i] = selected.getComponentType();
				}
				presenter.insertGroup(toPass);
				hide();
			}  
		});  
		add.getElement().getStyle().setMarginLeft(10, Unit.PX);

		hp.add(container);  
		hp.add(sourceContainer);  
		add(hp);  
		
		addButton(reset);		
		addButton(add);
	}


	void restoreSourceButtons() {
		sourceContainer.removeAll();  
		addSources(sourceContainer);  
	}


	private void addSources(LayoutContainer container) {  

		ArrayList<ExtButton> repetitiveElements = ToolboxDialog.repetitiveElements;
		for (final ExtButton sourceButton : repetitiveElements) {

			final ExtButton button = new ExtButton(sourceButton.getText(), sourceButton.getComponentType());
			button.setSize(ToolboxDialog.buttonWidth, 25);
			button.setIconAlign(IconAlign.LEFT); 
			button.setIcon(sourceButton.getIcon());
			button.addStyleName("btn_in_detail");

			DragSource source = new DragSource(button) {  
				@Override  
				protected void onDragStart(DNDEvent event) {  
					// by default drag is allowed  
					event.setData(button);  
					event.getStatus().update(El.fly(button.getElement()).cloneNode(true));	         
				}	       
			};  
			source.setGroup("test");  
			container.add(button, new FlowData(3));  

		}		
	}  
}
