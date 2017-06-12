package org.gcube.portlets.user.reportgenerator.client.targets;

import java.util.ArrayList;

import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateComponent;

public interface ClientSequence {
	
	 ArrayList<TemplateComponent> getGroupedComponents();
	 
	void addTemplateComponent(TemplateComponent toAdd);
	
	boolean add(String id, RepeatableSequence sequence, boolean isSingleRelation);
	
	SequenceWidget remove(SequenceWidget toRemove);
	
	void AddButtonClicked(RepeatableSequence sequence);
	
	void cleanInModel();
}
