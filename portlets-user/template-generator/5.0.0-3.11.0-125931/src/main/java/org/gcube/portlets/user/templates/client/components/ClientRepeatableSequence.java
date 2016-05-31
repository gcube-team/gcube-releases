package org.gcube.portlets.user.templates.client.components;

import java.util.ArrayList;

import org.gcube.portlets.d4sreporting.common.client.uicomponents.ReportUIComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.user.templates.client.TGenConstants;
import org.gcube.portlets.user.templates.client.model.TemplateComponent;
import org.gcube.portlets.user.templates.client.model.TemplateModel;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ClientRepeatableSequence extends ReportUIComponent {
	private ArrayList<TemplateComponent> groupedComponents = new ArrayList<TemplateComponent>();

	VerticalPanel myPanel;
	Presenter p;

	/**
	 * used to create a new empty repeatable sequence
	 * @param types
	 */
	public ClientRepeatableSequence(Presenter p, TemplateModel model, ComponentType ... types) {		
		super(ComponentType.REPEAT_SEQUENCE, 0, 0, TemplateModel.TEMPLATE_WIDTH - 50, 100);
		this.p = p;
		myPanel = getResizablePanel();
		
		Coords start = p.getInsertionPoint();
		//*** insert the starting delimiter
		GroupingDelimiterArea toAdd = new GroupingDelimiterArea(TemplateModel.TEMPLATE_WIDTH - 50, 10);		
		TemplateComponent delimiter = new TemplateComponent(model, 0, start.getY(), 
				TemplateModel.TEMPLATE_WIDTH, 25, model.getCurrentPage(), ComponentType.REPEAT_SEQUENCE_DELIMITER, "", toAdd);
		add(delimiter);
		height += 35;
		//***

		myPanel = getResizablePanel();
		int height = 0;
		//for each item of the repeat seq.
		for (int i = 0; i < types.length; i++) {
			switch (types[i]) {
			case HEADING_1:
			case HEADING_2:
			case HEADING_3:
			case HEADING_4:
			case HEADING_5:
			case BODY_NOT_FORMATTED:
				TemplateComponent tc = p.createStaticTextArea(types[i], TemplateModel.TEMPLATE_WIDTH - 50, 35, true);
				add(tc);
				break;
			case BODY:
				TemplateComponent body = p.createStaticTextArea(ComponentType.BODY, TemplateModel.TEMPLATE_WIDTH - 50, 50, true);
				height += 25; //body is higher than headings
				add(body);
				break;
			case IMAGE:
				TemplateComponent image = p.createDroppingArea(ComponentType.DYNA_IMAGE, TGenConstants.DEFAULT_IMAGE_WIDTH, TGenConstants.DEFAULT_IMAGE_HEIGHT, true);
				height += TGenConstants.DEFAULT_IMAGE_HEIGHT-35;
				add(image);
				break;
			case FLEX_TABLE:				
				TemplateComponent tableCompo = p.createTable(ComponentType.FLEX_TABLE, GenericTable.DEFAULT_ROWS_NUM, GenericTable.DEFAULT_COLS_NUM, true);
				height += GenericTable.DEFAULT_HEIGHT-35;
				add(tableCompo);
				break;
			case ATTRIBUTE_UNIQUE:
				String attrName = "To Edit";
				String[] values =  {"To Edit Value"};
				TemplateComponent attrUnique = p.createAttributArea(TemplateModel.TEMPLATE_WIDTH - 50, 50, attrName, values, false, false, true);
				height += 15; //attr is higher than headings
				add(attrUnique);				
				break;
			case ATTRIBUTE_MULTI:
				String attrNameM = "To Edit";
				String[] valuesM =  {"To Edit Value"};
				TemplateComponent attrMulti = p.createAttributArea(TemplateModel.TEMPLATE_WIDTH - 50, 50, attrNameM, valuesM, true, false, true);
				height += 15; //attr is higher than headings
				add(attrMulti);
				break;
			}
			height += 35;
			//add the spacer except for the last one
			if (i < types.length-1) {
				add(p.getGroupInnerArea());
				height += 6;
			}			
		}
		//insert the ending delimiter
		toAdd = new GroupingDelimiterArea(TemplateModel.TEMPLATE_WIDTH - 50, 10);
		TemplateComponent delimiterEnd = new TemplateComponent(model, 0, start.getY(), TemplateModel.TEMPLATE_WIDTH, 25, 
				model.getCurrentPage(), ComponentType.REPEAT_SEQUENCE_DELIMITER, "", toAdd);
		add(delimiterEnd);
		height += 35;
		
		resizePanel(TemplateModel.TEMPLATE_WIDTH - 50, height);
	}
	/**
	 * constructor used by the system when reading the model 
	 * @param sRS
	 */
	public ClientRepeatableSequence(Presenter p, RepeatableSequence sRS) {
		super(ComponentType.REPEAT_SEQUENCE, 0, 0, TemplateModel.TEMPLATE_WIDTH - 50, sRS.getHeight());
		this.p = p;
		myPanel = getResizablePanel();

		int size = sRS.getGroupedComponents().size();
		for (int j = 0; j < size; j++) {
			BasicComponent sComp = sRS.getGroupedComponents().get(j);
			
			add(new TemplateComponent(p.getModel(), sComp, p, false));
		}
	}

	public void add(TemplateComponent toAdd) {
		groupedComponents.add(toAdd);
		GWT.log("ToAdd= getType " + toAdd.getType() + " locked?"+toAdd.isLocked());
		myPanel.add(toAdd.getContent());
		
		//this is needed because you need to set the component widget in this case for editing
		if (toAdd.getType() == ComponentType.ATTRIBUTE_MULTI || toAdd.getType() == ComponentType.ATTRIBUTE_UNIQUE) {
			AttributeArea attrArea = (AttributeArea) toAdd.getContent();
			attrArea.setComponent(toAdd);
		}
	}

	@Override
	public void removeTemplateComponent(ReportUIComponent toRemove) {
		p.removeTemplateComponent(this);	

	}

	@Override
	public void lockComponent(ReportUIComponent toLock, boolean locked) {
	}

	public ArrayList<TemplateComponent> getGroupedComponents() {
		return groupedComponents;
	}

	public void setGroupedComponents(ArrayList<TemplateComponent> groupedComponents) {
		this.groupedComponents = groupedComponents;
	}
	
	public int getHeight() {
		return super.mainPanel.getOffsetHeight();
	}
}
