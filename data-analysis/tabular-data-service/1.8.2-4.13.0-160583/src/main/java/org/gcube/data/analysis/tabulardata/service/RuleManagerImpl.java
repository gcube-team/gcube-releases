package org.gcube.data.analysis.tabulardata.service;

import static org.gcube.data.analysis.tabulardata.utils.Util.getUserAuthorizedObject;
import static org.gcube.data.analysis.tabulardata.utils.Util.toRuleDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jws.WebService;
import javax.persistence.EntityManager;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.rules.ColumnRule;
import org.gcube.data.analysis.tabulardata.commons.rules.RuleScope;
import org.gcube.data.analysis.tabulardata.commons.rules.TableRule;
import org.gcube.data.analysis.tabulardata.commons.rules.types.BaseColumnRuleType;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleTableType;
import org.gcube.data.analysis.tabulardata.commons.rules.types.RuleType;
import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.webservice.RuleManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchRuleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.OperationNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.AppliedRulesResponse;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.MapObject;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.exceptions.NoSuchObjectException;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.metadata.StorableRule;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.RuleMapping;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.rules.AddRuleFinalActionFactory;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.gcube.data.analysis.tabulardata.task.engine.TaskEngine;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.gcube.data.analysis.tabulardata.utils.OperationUtil;
import org.gcube.data.analysis.tabulardata.utils.Util;
import org.gcube.data.analysis.tabulardata.weld.WeldService;
import org.slf4j.Logger;


@WebService(portName = "RuleManagerPort",
serviceName = RuleManager.SERVICE_NAME,
targetNamespace = Constants.RULE_TNS,
endpointInterface = "org.gcube.data.analysis.tabulardata.commons.webservice.RuleManager")
@Singleton
@WeldService
public class RuleManagerImpl implements RuleManager{


	@Inject
	private Logger logger;

	@Inject
	private EntityManagerHelper emHelper;

	@Inject
	private TaskEngine taskEngine;

	@Inject
	private OperationUtil opUtil;

	@Inject
	private AddRuleFinalActionFactory addRuleFinalActionFactory;

	@Override
	public List<RuleDescription> getRules() throws InternalSecurityException{
		logger.info("getRules called");
		Map<String, Object> parameters = new HashMap<>(3);
		parameters.put("user", AuthorizationProvider.instance.get().getClient().getId());
		parameters.put("group", ScopeProvider.instance.get());
		parameters.put("scope",  ScopeProvider.instance.get());
		List<StorableRule> rules = emHelper.getResults("RULE.getAll", StorableRule.class, parameters);
		List<RuleDescription> descriptions = new ArrayList<RuleDescription>();
		for (StorableRule stRule: rules)
			descriptions.add(toRuleDescription(stRule));
		return descriptions;

	}

	@Override
	public List<RuleDescription> getRulesByScope(RuleScope scope) throws InternalSecurityException {
		logger.info("getRulesByScope with scope "+scope.name()+" called");
		Map<String, Object> parameters = new HashMap<>(4);
		parameters.put("user", AuthorizationProvider.instance.get().getClient().getId());
		parameters.put("group",  ScopeProvider.instance.get());
		parameters.put("scope",  ScopeProvider.instance.get());
		parameters.put("ruleScope", scope);
		List<StorableRule> rules = emHelper.getResults("RULE.getAllByScope", StorableRule.class, parameters);
		List<RuleDescription> descriptions = new ArrayList<RuleDescription>();
		for (StorableRule stRule: rules)
			descriptions.add(toRuleDescription(stRule));
		return descriptions;
	}

	@Override
	public List<RuleDescription> getApplicableBaseColumnRules(Class<? extends DataType> dataTypeClass) throws InternalSecurityException {
		logger.info("getApplyableColumnRules for datatype "+dataTypeClass.getSimpleName());
		Map<String, Object> parameters = new HashMap<>(4);
		parameters.put("user", AuthorizationProvider.instance.get().getClient().getId());
		parameters.put("group",  ScopeProvider.instance.get());
		parameters.put("scope",  ScopeProvider.instance.get());
		parameters.put("ruleScope", RuleScope.COLUMN);
		List<StorableRule> rules = emHelper.getResults("RULE.getAllByScope", StorableRule.class, parameters);
		List<RuleDescription> descriptions = new ArrayList<RuleDescription>();
		for (StorableRule stRule: rules)
			if (stRule.getRuleType() instanceof BaseColumnRuleType 
					&& ((BaseColumnRuleType) stRule.getRuleType()).getInternalType().getClass().equals(dataTypeClass))
				descriptions.add(toRuleDescription(stRule));
		return descriptions;
	}

	@Override
	public long saveRule(String name, String description,
			Expression rule, RuleType ruleType) throws InternalSecurityException {
		logger.info("saveColumnRule called with parameters : name "+name+", description "+description+", expression "+rule+", columnType "+ruleType);

		if (name==null || description==null || rule==null || ruleType==null)
			throw new RuntimeException("null value passed");

		final StorableRule stRule;
		if (ruleType instanceof RuleTableType) 
			stRule = new StorableRule(name, description, new TableRule(rule, ((RuleTableType) ruleType).getInternalType() ), AuthorizationProvider.instance.get().getClient().getId(), ruleType);
		else
			stRule = new StorableRule(name, description, new ColumnRule(rule), AuthorizationProvider.instance.get().getClient().getId(), ruleType);
		stRule.addScope( ScopeProvider.instance.get());

		EntityManager em = emHelper.getEntityManager();
		try{
			em.getTransaction().begin();
			em.persist(stRule);
			em.getTransaction().commit();
		}finally{
			if(em!=null)
				em.close();
		}

		return stRule.getId();
	}

	
	
	
	
	@Override
	public void updateColumnRule(RuleDescription descriptor) throws InternalSecurityException, NoSuchRuleException {
		logger.info("updateColumnRule called with parameters : id "+descriptor.getId()+", name "+descriptor.getName()+", description "+descriptor.getDescription());

		EntityManager em = emHelper.getEntityManager();

		try{
			StorableRule rule = null;
			try {
				rule =Util.getOwnerhipAuthorizedObject(descriptor.getId(), StorableRule.class, em);
			} catch (NoSuchObjectException e) {
				logger.error("Rule with id "+descriptor.getId()+" not found");
				throw new NoSuchRuleException(descriptor.getId());
			}
			em.getTransaction().begin();
			
			if(rule.getRuleScope()!= RuleScope.COLUMN) throw new NoSuchRuleException(descriptor.getId());
			rule.setName(descriptor.getName());
			rule.setDescription(descriptor.getDescription());
			em.merge(rule);
			em.getTransaction().commit();
		}finally{
			if(em!=null)
				em.close();
		}
	}

	@Override
	public TaskInfo applyColumnRule(Long tabularResourceId, String columnId,  List<Long> ruleIds) throws InternalSecurityException, NoSuchRuleException, NoSuchTabularResourceException{
		logger.info("applying column rules with id "+ruleIds+" on tabularResource "+tabularResourceId);
		EntityManager em = emHelper.getEntityManager();
		try{
			StorableTabularResource tr = null;
			try {
				tr = Util.getUserAuthorizedObject(tabularResourceId, StorableTabularResource.class, em);
			} catch (NoSuchObjectException e) {
				logger.error("TabularResource with id "+tabularResourceId+" not found");
				throw new NoSuchTabularResourceException(tabularResourceId);
			}
			List<RuleMapping> ruleMappings = new ArrayList<>(); 
			for (long ruleId : ruleIds){
				StorableRule rule = em.find(StorableRule.class, ruleId);
				RuleMapping mapping = new RuleMapping(rule, columnId);
				if (!tr.getRules().contains(mapping))
					ruleMappings.add(mapping);
			}
			if (ruleMappings.isEmpty()) throw new RuntimeException("no new rules to apply"); 

			TaskContext context = new TaskContext(Collections.singletonList(opUtil.getRulesInvocation(ruleMappings, tr.getTableId())), OnRowErrorAction.ASK);
			opUtil.addPostOperations(context);
			opUtil.addPostValidations(context, tr);
			return taskEngine.createTask(AuthorizationProvider.instance.get().getClient().getId(), context, tr, addRuleFinalActionFactory.create(tr.getId(), ruleMappings));
		}catch(OperationNotFoundException e){
			logger.error("operation not found",e);
			throw new RuntimeException("unexpected error");
		}finally{
			if(em!=null)
				em.close();
		}

	}

	@Override
	public TaskInfo applyTableRule(Long tabularResourceId, MapObject<String, String > mappingPlaceholderIDToColumnId,  Long ruleId) throws InternalSecurityException, NoSuchRuleException, NoSuchTabularResourceException{
		logger.info("applying table rule with id "+ruleId+" on tabularResource "+tabularResourceId);
		EntityManager em = emHelper.getEntityManager();
		try{
			StorableTabularResource tr = null;
			try {
				tr = Util.getUserAuthorizedObject(tabularResourceId, StorableTabularResource.class, em);
			} catch (NoSuchObjectException e) {
				logger.error("TabularResource with id "+tabularResourceId+" not found");
				throw new NoSuchTabularResourceException(tabularResourceId);
			}
		
			StorableRule rule = em.find(StorableRule.class, ruleId);
			RuleMapping mapping = new RuleMapping(rule, mappingPlaceholderIDToColumnId.getMap());
			
			
			TaskContext context = new TaskContext(Collections.singletonList(opUtil.getRulesInvocation(Collections.singletonList(mapping), tr.getTableId())), OnRowErrorAction.ASK);
			opUtil.addPostOperations(context);
			opUtil.addPostValidations(context, tr);
			return taskEngine.createTask(AuthorizationProvider.instance.get().getClient().getId(), context, tr, addRuleFinalActionFactory.create(tr.getId(), Collections.singletonList(mapping)));
		}catch(OperationNotFoundException e){
			logger.error("operation not found",e);
			throw new RuntimeException("unexpected error");
		}finally{
			if(em!=null)
				em.close();
		}

	}
	
	
	@Override
	public void detachColumnRules(Long tabularResourceId, String columnId,  List<Long> ruleIds) throws InternalSecurityException, NoSuchTabularResourceException{
		logger.info("detaching column rules with id "+ruleIds+" on tabularResource "+tabularResourceId);
		EntityManager em = emHelper.getEntityManager();
		try{
			StorableTabularResource tr = null;
			try {
				tr = Util.getOwnerhipAuthorizedObject(tabularResourceId, StorableTabularResource.class, em);
			} catch (NoSuchObjectException e) {
				logger.error("TabularResource with id "+tabularResourceId+" not found");
				throw new NoSuchTabularResourceException(tabularResourceId);
			}
			
			List<RuleDescription> descriptions = this.getAppliedRulesByTabularResourceId(tabularResourceId).getColumnRuleMapping().get(columnId);
			if (descriptions!=null){
				em.getTransaction().begin();
				for (RuleDescription description: descriptions)
					if (ruleIds.contains(description.getId())){
						tr.removeColumnRuleMapping(description.getId(), columnId);
						em.merge(tr);
					}
				
				em.getTransaction().commit();
			}
		}finally{
			if(em!=null)
				em.close();
		}

	}

	@Override
	public void detachTableRules(Long tabularResourceId, List<Long> ruleIds) throws InternalSecurityException, NoSuchTabularResourceException{
		logger.info("detaching table rules with id "+ruleIds+" on tabularResource "+tabularResourceId);
		EntityManager em = emHelper.getEntityManager();
		try{
			StorableTabularResource tr = null;
			try {
				tr = Util.getOwnerhipAuthorizedObject(tabularResourceId, StorableTabularResource.class, em);
			} catch (NoSuchObjectException e) {
				logger.error("TabularResource with id "+tabularResourceId+" not found");
				throw new NoSuchTabularResourceException(tabularResourceId);
			}
			
			List<RuleDescription> descriptions = this.getAppliedRulesByTabularResourceId(tabularResourceId).getTableRules();
			if (descriptions!=null){
				em.getTransaction().begin();
				for (RuleDescription description: descriptions)
					if (ruleIds.contains(description.getId())){
						tr.removeTableRuleMapping(description.getId());
						em.merge(tr);
					}
				
				em.getTransaction().commit();
			}
		}finally{
			if(em!=null)
				em.close();
		}

	}
	

	@Override
	public AppliedRulesResponse getAppliedRulesByTabularResourceId(Long id)
			throws NoSuchTabularResourceException, InternalSecurityException {

		EntityManager em = emHelper.getEntityManager();
		List<RuleDescription> tableRules = new ArrayList<>();
		Map<String, List<RuleDescription>> columnRules = new HashMap<>();
		try{
			StorableTabularResource str = getUserAuthorizedObject(id, StorableTabularResource.class, em);
			for (RuleMapping mapping :str.getRules()){
				StorableRule rule = mapping.getStorableRule();
				RuleDescription descriptor = toRuleDescription(rule);
				if (mapping.getColumnLocalId()==null)
					tableRules.add(descriptor);					
				else {
					if (!columnRules.containsKey(mapping.getColumnLocalId()))
						columnRules.put(mapping.getColumnLocalId(), new ArrayList<RuleDescription>());
					columnRules.get(mapping.getColumnLocalId()).add(descriptor);
				}

			}
		}catch(NoSuchObjectException e){
			throw new NoSuchTabularResourceException(id);
		}finally{
			em.close();
		}
		return new AppliedRulesResponse(tableRules, columnRules);
	}


	@Override
	public void remove(Long id) throws InternalSecurityException, NoSuchRuleException {
		logger.info("deleting column rule with id "+id );
		EntityManager em = emHelper.getEntityManager();
		try{
			StorableRule rule;
			try {
				rule = Util.getOwnerhipAuthorizedObject(id, StorableRule.class, em);
			} catch (NoSuchObjectException e) {
				logger.error("Rule with id "+id+" not found");
				throw new NoSuchRuleException(id);
			}
			em.getTransaction().begin();

			if (rule.getRuleMappings().isEmpty()){
				logger.trace("ruleMappings for rule "+rule.getId()+" is empty");
				em.remove(rule);
			}else {
				rule.setDeleted(true);
				em.merge(rule);
			}
			em.getTransaction().commit();
		}finally{
			if(em!=null)
				em.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.commons.webservice.Sharable#share(java.lang.Object, org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken[])
	 */
	@Override
	public RuleDescription share(Long entityId,
			SharingEntity... entities) throws
			NoSuchRuleException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		try{
			StorableRule str = SharingHelper.share(StorableRule.class, entityId, em, entities);
			return toRuleDescription(str);
		}catch(NoSuchObjectException e){
			throw new NoSuchRuleException(entityId);
		}finally{
			em.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.commons.webservice.Sharable#unshare(java.lang.Object, org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken[])
	 */
	@Override
	public RuleDescription unshare(Long entityId,
			SharingEntity... entities) throws NoSuchRuleException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		try{
			return toRuleDescription(SharingHelper.unshare(StorableRule.class, entityId, em, entities));
		}catch(NoSuchObjectException e){
			throw new NoSuchRuleException(entityId);
		}finally{
			em.close();
		}
	}

}
