package org.gcube.application.reporting.component;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.reporting.component.interfaces.IsSequentiable;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;

public class SequenceList extends AbstractComponent implements IsSequentiable {
	private List<ReportComponent> children;
	
	public SequenceList() {
		children = new ArrayList<ReportComponent>();
	}
	
	public boolean add(ReportSequence toAdd) {
		return children.add(toAdd);
	}

	@Override
	public ReportComponentType getType() {
		return ReportComponentType.SEQUENCE_LIST;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public List<ReportComponent> getChildren() {
		return children;
	}

	@Override
	public String getStringValue() {
		return null;
	}

	@Override
	public BasicComponent getModelComponent() {

		ArrayList<BasicComponent> mergedComponents = new ArrayList<BasicComponent>();
		
		for (ReportComponent seq : children) {
			//extract the embedded sequence
			RepeatableSequence toMerge = (RepeatableSequence) seq.getModelComponent().getPossibleContent();
			
			for (BasicComponent bc : toMerge.getGroupedComponents()) {
				mergedComponents.add(bc);
			}
		}
		
		RepeatableSequence toEmbed = new RepeatableSequence(mergedComponents, getId(), 0);
		BasicComponent bc = new BasicComponent(0, 0, COMP_WIDTH, COMP_HEIGHT, 
				1, ComponentType.REPEAT_SEQUENCE, "", toEmbed, false, true, convertProperties());	
		bc.setId(getId());
		return bc;
	}

}
