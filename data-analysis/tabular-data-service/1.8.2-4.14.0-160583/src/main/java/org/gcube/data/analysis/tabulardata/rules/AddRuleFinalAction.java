package org.gcube.data.analysis.tabulardata.rules;

import java.util.List;

import javax.persistence.EntityManager;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.metadata.StorableRule;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.RuleMapping;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.task.RunnableTask;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;

@Slf4j
public class AddRuleFinalAction implements RunnableTask {

	long trId;
	
	private EntityManagerHelper emHelper;
	
	List<RuleMapping> ruleMappings;
	
	protected AddRuleFinalAction(long trId, List<RuleMapping> ruleMappings,  EntityManagerHelper emHelper) {
		super();
		this.trId = trId;
		this.emHelper = emHelper;
		this.ruleMappings = ruleMappings;
	}

	@Override
	public void run(Table table) {
		EntityManager em = emHelper.getEntityManager();
		try{
			em.getTransaction().begin();
			StorableTabularResource str = em.find(StorableTabularResource.class , this.trId);
			for (RuleMapping mapping: ruleMappings)	{			
				StorableRule rule = mapping.getStorableRule();
				rule.getRuleMappings().add(mapping);
				em.persist(mapping);
				em.merge(rule);
			}
			str.addRules(ruleMappings);
			em.merge(str);
			em.getTransaction().commit();
		}catch(Exception e){
			log.error("error adding rule to tabularResource",e);
		}finally{
			if (em!=null)
				em.close();
		}
	}

}
