package org.gcube.data.analysis.tabulardata.rules;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.metadata.tabularresource.RuleMapping;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;

@Singleton
public class AddRuleFinalActionFactory {

	@Inject 
	private EntityManagerHelper emHelper;
	
	public AddRuleFinalAction create(long trId, List<RuleMapping> mappings){
		return new AddRuleFinalAction(trId, mappings,  emHelper);
	}
	
}
