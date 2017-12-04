package org.gcube.application.reporting.component;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.reporting.component.interfaces.IsMedia;
import org.gcube.application.reporting.component.interfaces.IsSequentiable;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;

public class Media extends AbstractComponent implements IsSequentiable {

	private List<ReportComponent> children; 
	/**
	 * an empy media ha at least an empty text input
	 */
	public Media() {
		children = new ArrayList<ReportComponent>();
		children.add(new TextInput());
	}
	
	public void clear() {
		children.clear();
	}
	
	public void Add(IsMedia component) {
		children.add(component);
	}

	@Override
	public ReportComponentType getType() {
		return ReportComponentType.MEDIA;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public List<ReportComponent> getChildren() {
		return children;
	}
	private  List<BasicComponent> getSequence() {
		List<BasicComponent> toReturn = new ArrayList<BasicComponent>();
		for (ReportComponent repCo : children) {
			toReturn.add(repCo.getModelComponent());
		}
		return toReturn;
	}
	@Override
	public String getStringValue() {
		return null;
	}

	@Override
	public BasicComponent getModelComponent() {
		ArrayList<BasicComponent> groupedComponents = new ArrayList<BasicComponent>();

		for (BasicComponent elem : getSequence()) {
			groupedComponents.add(elem);
		}
		RepeatableSequence toEmbed = new RepeatableSequence(groupedComponents, getId(), 0);
		BasicComponent bc = new BasicComponent(0, 0, COMP_WIDTH, COMP_HEIGHT, 
				1, ComponentType.BODY_TABLE_IMAGE, "", toEmbed, false, true, convertProperties());	
		bc.setId(getId());
		return bc;
	}

}
