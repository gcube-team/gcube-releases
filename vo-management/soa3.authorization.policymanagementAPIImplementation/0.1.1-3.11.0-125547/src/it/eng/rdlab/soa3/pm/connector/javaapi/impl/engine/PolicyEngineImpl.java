package it.eng.rdlab.soa3.pm.connector.javaapi.impl.engine;

import it.eng.rdlab.soa3.pm.connector.beans.Status;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyAdder;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyDeleter;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyReader;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.Attribute;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.ResponseBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.RuleBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.configuration.ConfigurationManagerBuilder;
import it.eng.rdlab.soa3.pm.connector.javaapi.engine.PolicyEngine;
import it.eng.rdlab.soa3.pm.connector.javaapi.impl.beans.AttributesManagementBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.impl.factory.PolicyManagerFactory;
import it.eng.rdlab.soa3.pm.connector.javaapi.impl.utils.AttributeLoader;
import it.eng.rdlab.soa3.pm.connector.javaapi.impl.utils.PolicyRuleBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.impl.utils.Utils;
import it.eng.rdlab.soa3.pm.xacml.XACMLManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.opensaml.xacml.policy.ActionType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xacml.policy.SubjectMatchType;
import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class PolicyEngineImpl implements PolicyEngine
{
	private Logger logger;
	private PolicyReloader reloader;
	
	public PolicyEngineImpl() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.reloader = new PolicyReloader(ConfigurationManagerBuilder.getConfigurationManager().getPolicyLoaderUrl());
		
	}
	
		@Override
	public ResponseBean createRule (RuleBean ruleBean)
	{
		this.logger.debug("Creating policy...");
		PolicyAdder adder = PolicyManagerFactory.getPolicyAdder();
		AttributesManagementBean attributesBean =  Utils.generateAttributeManagerBean(ruleBean);
		Status addRuleResponse = adder.addNewRule(null,attributesBean.getSubjectAttributes(), attributesBean.getActionId(), attributesBean.getResourceId(), ruleBean.isPermitted(), null,attributesBean.isMoveAfter());
		logger.debug("Response "+addRuleResponse);
		
		if (addRuleResponse.getStatus() == Status.OPERATION_OK && addRuleResponse.getResult() == Status.OPERATION_OK)
		{
			logger.debug("Rule created");
			String ruleID = addRuleResponse.getInfo();
			logger.debug("Rule id = "+ruleID);
			return addExtraParameters(adder,ruleID, ruleBean.getDateRange(), ruleBean.getTimeRange());
			
		}
		else
		{
			logger.debug("Rule not created");
			return new ResponseBean(ResponseBean.RULE_CREATE_UPDATE_NOT_CREATED_OR_UPDATED, null);

		}
		
	}
		
		
		

//	/**
//	 * 
//	 * {@inheritDoc}
//	 */
//	@Override
//	public ResponseBean createRule (RuleBean ruleBean)
//	{
//		this.logger.debug("Creating policy...");
//		PolicyAdder adder = PolicyManagerFactory.getPolicyAdder();
//		Map<String,String> attributes = ruleBean.getAttributes();
//		logger.debug("Attributes "+attributes);
//		String action = ruleBean.getAction();
//		logger.debug("Action = "+action);
//		String resource = ruleBean.getResource();
//		logger.debug("Resource = "+resource);
//		List<AttributeBean> attributeBeans =  Utils.generateAttributeList(attributes);
//		Status addRuleResponse = adder.addNewRule(null,attributeBeans, action, resource, ruleBean.isPermitted(), null,false);
//		logger.debug("Response "+addRuleResponse);
//		
//		if (addRuleResponse.getStatus() == Status.OPERATION_OK && addRuleResponse.getResult() == Status.OPERATION_OK)
//		{
//			logger.debug("Policy created");
//			String ruleId = addRuleResponse.getInfo();
//			PolicyReader policyReader = PolicyManagerFactory.getPolicyReader();
////			logger.debug("Reading policy "+ruleId);
////			Status getPolicyReasponse = policyReader.getPolicy(null, policyId);
//			
//			if (getPolicyReasponse.getStatus() == Status.OPERATION_OK && getPolicyReasponse.getResult() == Status.OPERATION_OK)
//			{
//				logger.debug("Policy found");
//				PolicyType policyType = XACMLManager.getPolicyFromString(getPolicyReasponse.getInfo());
//				logger.debug("Policy type found");
//				Iterator<RuleType> rules = policyType.getRules().iterator();
//				List<Attribute> attributeList = Utils.attributeBean2Attribute(attributeBeans);
//				RuleType ruleType = null;
//				
//				while (rules.hasNext() && ruleType == null)
//				{
//					RuleType r = rules.next();
//					
//					if (checkAttributes(r, attributeList)) 
//					{
//						logger.debug("Rule Found");
//						ruleType = r;
//					}
//				}
//				
//				if (ruleType != null)
//				{
//					boolean addTimeResponse = addTimeIntervals(adder, ruleBean.getDateRange(), ruleBean.getTimeRange(),policyType, ruleType);
//					logger.debug("Add time range result "+addTimeResponse);
//					
//					if (!addTimeResponse) return new ResponseBean(ResponseBean.RULE_CREATE_UPDATE_WARNING_ON_EXTRA_PARAMETERS, ruleType.getRuleId());
//					
//					else return new ResponseBean(ResponseBean.RULE_CREATE_UPDATE_OK, ruleType.getRuleId());
//				}
//			}
//
//		}
//
//		logger.error("Rule not created");
//		return new ResponseBean(ResponseBean.RULE_CREATE_UPDATE_NOT_CREATED_OR_UPDATED, null);
//
//		
//	}
	
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public RuleBean getRule (String ruleId)
	{
		this.logger.debug("Reading policy...");
		this.logger.debug("Rule ID "+ruleId);
		PolicyReader reader = PolicyManagerFactory.getPolicyReader();
		PolicyRuleBean policyRuleBean = Utils.findPolicyContainingRule(reader, ruleId);

		if (policyRuleBean != null)
		{
			logger.debug("Rule found");
			RuleBean bean = Utils.createRuleBean(policyRuleBean.getPolicySetType(),policyRuleBean.getPolicyType(),policyRuleBean.getRuleType());
			logger.debug("Response bean generated");
			return bean;
						
		}
		else
		{
			logger.debug("Rule not found");
			return null;
		}
		
	}

	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ResponseBean updateRule (String ruleId, RuleBean ruleBean)
	{
		this.logger.debug("Update rule");
		this.logger.debug("deleting old rule...");
		boolean deleteRuleResponse = deleteRule(ruleId,false);
		
		if (deleteRuleResponse)
		{
			logger.debug("Rule deleted: adding updated rule");
			return createRule(ruleBean);
		}
		else
		{
			logger.debug("Rule not found");
			return new ResponseBean(ResponseBean.RULE_UPDATE_RULE_NOT_FOUND, null);
		}
		
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public boolean deleteRule (String ruleId)
	{
		return deleteRule(ruleId,true);
		
	}
	

	/**
	 * 
	 * @param ruleId
	 * @param reloadPolicies
	 * @return
	 */
	public boolean deleteRule (String ruleId,boolean reloadPolicies)
	{
		this.logger.debug("Deleting policy...");
		this.logger.debug("For rule id "+ruleId);
		
		if (ruleId == null)
		{
			this.logger.error("Rule id null");
			return false;
		}
		else
		{
			PolicyDeleter deleter = PolicyManagerFactory.getPolicyDeleter();
			Status policyDeleterResponse = deleter.deleteRule(null, ruleId);
			
			if (policyDeleterResponse.getStatus() == Status.OPERATION_OK && policyDeleterResponse.getResult() == Status.OPERATION_OK)
			{
				logger.debug("Policy deleted");
				if (reloadPolicies) reloadPolicies();
				return true;
			}
			else
			{
				logger.debug("Policy not deleted");
				return false;
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<RuleBean> listAllRules ()
	{
		logger.debug("Retrieving all rules...");
		List<RuleBean> response = listRules(false,null);
		logger.debug("Operation completed");
		return response;
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public List<RuleBean> listRulesBySubjects  (List<Attribute>  subjects)
	{
		this.logger.debug("List rules by subject");
		List<RuleBean> response = listRules(true,subjects);
		this.logger.debug("Rules loaded");
		return response;
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public List<RuleBean>  listRulesByAction  (String action)
	{
		this.logger.debug("List rules by action");
		this.logger.debug("Action "+action);
		PolicyReader reader = PolicyManagerFactory.getPolicyReader();
		PolicyType [] policyTypes =  loadPolicies(reader);
		List<RuleBean> response = new ArrayList<RuleBean>();
		
		if (policyTypes != null)
		{
			logger.debug("Policies loaded");
			int policyIndex = 0;
			PolicySetType [] policySets = loadPolicySets(reader);
			
			if (policySets != null)
			{
			
				while (policyIndex < policyTypes.length)
				{
					try
					{
						logger.debug("Policy "+policyTypes [policyIndex].getPolicyId());
						TargetType target = policyTypes[policyIndex].getTarget();
						List<ActionType> actionList = target.getActions().getActions();
						Iterator<ActionType> actionListIterator = actionList.iterator();
						
						while (actionListIterator.hasNext())
						{
							ActionType at = actionListIterator.next();
							
							try
							{
								String actionValue = at.getActionMatches().get(0).getAttributeValue().getValue();
								logger.debug("Action value "+actionValue);
								
								if (action.matches(actionValue)) 
								{
									logger.debug("Action found");
									logger.debug("Finding policy set");
									PolicySetType policySet = Utils.findPolicySetContainingPolicy(policySets, policyTypes [policyIndex].getPolicyId());
									
									if (policySet != null)
									{
										logger.debug("Policy set found");
										addRuleTypes(response, policyTypes [policyIndex],policySet);
									}
									else logger.error("Policy set not found: inconsistent policy list");
									
								}
								
							} catch (Exception e)
							{
								logger.error("Unable to find a valid resource value",e);
								
							}
							
							
						}
						
						policyIndex ++;
					}
					catch (RuntimeException e)
					{
						logger.error("Invalid policy");
					}
	
				}
			}
			else logger.error("Policy sets not found: inconsistent policy list");
				
		}
		else
		{
			logger.error("Unable to load policies");
			return null;
		}
		
		return response;
		
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public List<RuleBean>   listRulesByResource  (String resource)
	{
		this.logger.debug("List rules by resource");
		this.logger.debug("Resource "+resource);
		PolicyReader reader = PolicyManagerFactory.getPolicyReader();
		PolicySetType [] policySetTypes = loadPolicySets(reader);
		List<RuleBean> response = new ArrayList<RuleBean>();
		
		if (policySetTypes != null)
		{
			try
			{
			
				logger.debug("Policy sets loaded");
				PolicySetType policySet = searchPolicySetByResource(policySetTypes, resource);
				
				if (policySet != null)
				{
					List<PolicyType> policyTypeList = Utils.getPoliciesFromPolicySet(reader, policySet);
					
					for (PolicyType policyType : policyTypeList)
					{
						addRuleTypes(response, policyType,policySet);

					}
					
				}
				else logger.debug("No rules associated with resource "+resource);
			
			} catch (RuntimeException e)
			{
				logger.error("Unable to load rules: invalid document",e);
				return null;
			}
		}
		else
		{
			logger.error("Unable to load policy set");
			return null;
		}
		
		logger.debug("Found "+response.size()+" rules");
		return response;
		
	}
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public List<RuleBean> listRules() 
	{
		this.logger.debug("List all the rules");
		List<RuleBean> response = listRules(false,null);
		this.logger.debug("Rules loaded");
		return response;
		
	}
	
	@Override
	public boolean deleteAll() 
	{
		this.logger.debug("Deleting the repository...");
		PolicyDeleter deleter = PolicyManagerFactory.getPolicyDeleter();
		Status eraseRepository = deleter.clear();
		
		if (eraseRepository.getStatus() == Status.OPERATION_OK && eraseRepository.getResult() == Status.OPERATION_OK)
		{
			logger.debug("Repository deleted");
			reloadPolicies();
			return true;
		}
		else
		{
			logger.debug("Repository not deleted");
			return false;
		}
		
	}
	
	@Override
	public String getRuleId(RuleBean ruleBean) 
	{
		logger.debug("Getting rule id of the rule with ");
		logger.debug("Attributes "+ruleBean.getAttributes());
		logger.debug("Action "+ruleBean.getAction());
		logger.debug("Resource "+ruleBean.getResource());
		String response = null;
		
		if (ruleBean.getAttributes() != null && ruleBean.getAttributes().size()>0 && ruleBean.getAction() != null && ruleBean.getResource() != null)
		{
			logger.debug("loading rule...");
			List<RuleBean> rulesByResource = listRulesByResource(ruleBean.getResource());
			
			if (rulesByResource != null && rulesByResource.size() > 0)
			{
				logger.debug("Applying action filter...");
				List<RuleBean> rulesByAction = new ArrayList<RuleBean>();
				
				for (RuleBean bean : rulesByResource)
				{
					if (ruleBean.getAction().equals(bean.getAction()))
					{
						logger.debug("Found valid rule");
						rulesByAction.add(bean);
					}
				}
				logger.debug("Action filter applied");
				
				Iterator<RuleBean> ruleBeanIterator = rulesByAction.iterator();

				
				logger.debug("Applying attributes filter...");

				
				while (ruleBeanIterator.hasNext() && response == null)
				{
					RuleBean bean = ruleBeanIterator.next();

					if (checkRuleBeanAttributes(bean, ruleBean.getAttributes()))
					{
						logger.debug("Rule id found "+bean.getRuleId());
						logger.debug("Setting rule id");
						response = bean.getRuleId();
					}
					
				}
			}
			
		}
		else logger.error("Missing parameters");
		
		return response;
		
	}
	
	

	
	private List<RuleBean> listRules (boolean enableFilter, List<Attribute> subjects)
	{
		this.logger.debug("Loading rules");
		this.logger.debug("Filter "+enableFilter);
		PolicyReader reader = PolicyManagerFactory.getPolicyReader();
		PolicyType [] policyTypes = loadPolicies(reader);
		List<RuleBean> response = new ArrayList<RuleBean>();
		
		if (policyTypes != null)
		{
			logger.debug("Policies loaded");
			int policyIndex = 0;
			PolicySetType [] policySetTypes = loadPolicySets(reader);
			
			if (policySetTypes != null)
			{
			
				while (policyIndex < policyTypes.length)
				{
					try
					{
						logger.debug("Policy "+policyTypes [policyIndex].getPolicyId());
						logger.debug("Loading rules from policy "+policyTypes [policyIndex].getPolicyId());
						List<RuleType> ruleTypeList = policyTypes [policyIndex].getRules();
						
						for (RuleType ruleType : ruleTypeList)
						{
							
							if (!enableFilter || checkAttributes(ruleType, subjects))
							{
								logger.debug("Converting rule "+ruleType.getRuleId());
								logger.debug("Loading policy set");
								PolicySetType policySet = Utils.findPolicySetContainingPolicy(policySetTypes, policyTypes [policyIndex].getPolicyId());
								
								if (policySet != null)
								{
									response.add(Utils.createRuleBean(policySet, policyTypes [policyIndex], ruleType));
									logger.debug("Conversion completed");
								}
								else logger.error("Unable to find the policy set: inconsistent policies list");

							}
							
						}
						
						logger.debug("Operation completed");
						

					}
					catch (RuntimeException e)
					{
						logger.error("Invalid policy",e);
					}
					policyIndex ++;
				}
			}
			else logger.error("Unable to find the policy sets: inconsistent policies list");
			
		}
		else
		{
			logger.error("Unable to load policies");
			return null;
		}
		
		return response;
	}
	
	
	/**
	 * 
	 * @param ruleType
	 * @param attributes
	 * @return
	 */
	private boolean checkAttributes (RuleType ruleType, List<Attribute> attributes)
	{
		logger.debug("Check attributes...");
		boolean result = false;
		
		if (attributes == null || attributes.size() == 0)
		{
			logger.debug("No attributes found");
			result = true;
		}
		else
		{
			try
			{
				logger.debug("Checking attributes");
				TargetType targetType = ruleType.getTarget();
				List<SubjectType> subjectTypeList = targetType.getSubjects().getSubjects();
				logger.debug("Attributes loaded, checking...");
				Iterator<SubjectType> subjectTypeIterator = subjectTypeList.iterator();

				
				while (subjectTypeIterator.hasNext() && !result)
				{
					logger.debug("Checking subject");
					SubjectType subjectType = subjectTypeIterator.next();
					List<Attribute> localList = new ArrayList<Attribute> (attributes);
					List<SubjectMatchType> matches = subjectType.getSubjectMatches();
					Iterator<SubjectMatchType> matchesIterator = matches.iterator();
					
					while (matchesIterator.hasNext() && !localList.isEmpty())
					{
						SubjectMatchType matchType = matchesIterator.next();
						String id = matchType.getSubjectAttributeDesignator().getAttributeId();
						logger.debug("Attribute id = "+id);
						String value = matchType.getAttributeValue().getValue();
						logger.debug("Attribute value = "+value);
						Iterator<Attribute> attributesIterator = localList.iterator();
						boolean found = false;
						
						while (attributesIterator.hasNext() && !found)
						{
							Attribute attribute = attributesIterator.next();
							String attributeKey = AttributeLoader.getInstance().getAttribute(attribute.getId());
							String attributeValue = attribute.getValue();
							logger.debug("To be checked against");
							logger.debug("ID "+attributeKey);
							logger.debug("Value "+attributeValue);
							
							if (attributeKey.equals(id) && value.matches(attributeValue))
							{
								found = true;
								logger.debug("Attributes matches");
								localList.remove(attribute);
								logger.debug("Attribute removed from local map");
							}
						}
						
					}
					
					if (localList.isEmpty())
					{
						logger.debug("Rule found");
						result = true;
					}
					
				}
				
				
				
			} catch (RuntimeException e)
			{
				logger.debug("Invalid xacml rule",e);
			}
			

			
		}
		
		logger.debug("Result "+result);
		return result;
	}
	
	/**
	 * 
	 * @param ruleBeanList
	 * @param policyType
	 */
	private void addRuleTypes  (List<RuleBean> ruleBeanList, PolicyType policyType, PolicySetType policySetType)
	{
		logger.debug("Loading rules from policy "+policyType.getPolicyId());
		List<RuleType> ruleTypeList = policyType.getRules();
		
		for (RuleType ruleType : ruleTypeList)
		{
			logger.debug("Converting rule "+ruleType.getRuleId());
			ruleBeanList.add(Utils.createRuleBean(policySetType, policyType,ruleType ));
			logger.debug("Conversion completed");
		}
		
		logger.debug("Operation completed");
	}

	/**
	 * 
	 * @param policySetTypes
	 * @param resource
	 * @return
	 */
	private PolicySetType searchPolicySetByResource (PolicySetType [] policySetTypes,String resource)
	{
		logger.debug("Scanning policy sets");
		PolicySetType policySet = null;
		int policySetIndex = 0;
		logger.debug("Policy sets list size "+policySetTypes.length);
		
		while (policySetIndex < policySetTypes.length && policySet == null)
		{
			logger.debug("Policy set "+policySetTypes [policySetIndex].getPolicySetId()+ " index "+policySetIndex);
			TargetType target = policySetTypes[policySetIndex].getTarget();
			
			if (target != null && target.getResources() != null && target.getResources().getResources()!= null)
			{
				List<ResourceType> resourceList = target.getResources().getResources();
				Iterator<ResourceType> resourceListIterator = resourceList.iterator();
				
				while (resourceListIterator.hasNext() && policySet == null)
				{
					ResourceType rt = resourceListIterator.next();
					
					try
					{
						String resourceValue = rt.getResourceMatches().get(0).getAttributeValue().getValue();
						logger.debug("Resource value "+resourceValue);
						
						if (resource.matches(resourceValue)) 
						{
							logger.debug("Resource found");
							policySet = policySetTypes[policySetIndex];
						}
						
					} catch (Exception e)
					{
						logger.error("Unable to find a valid resource value",e);
						
					}
		
				}
				
			}
			
			policySetIndex ++;
		

		}
		
		return policySet;
	}
	
	/**
	 * 
	 * @param policy
	 * @param ruleID
	 * @param dateRange
	 * @param timeRange
	 */
	private void setTimeParametersInRuleType (RuleType ruleType, String dateRange, String timeRange)
	{

		logger.debug("Setting time parameters in the policy");
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		
		
		if (dateRange != null && timeRange != null)
		{
			logger.debug("Adding date-time parameter...");
			Utils.setDateParameters(dateRange,startCalendar, endCalendar);
			Utils.setTimeParameters(timeRange,startCalendar, endCalendar);
			logger.debug("Date-time parameters added");
			ruleType.setCondition(XACMLManager.generateComplexDateTimeComparison(endCalendar.getTime(), startCalendar.getTime() , true, true));
			logger.debug("Date-time parameter added");
		}
		else if (dateRange != null)
		{
			logger.debug("Adding date parameter...");
			Utils.setDateParameters(dateRange,startCalendar, endCalendar);
			startCalendar.set(Calendar.HOUR_OF_DAY,0);
			startCalendar.set(Calendar.MINUTE, 0);
			startCalendar.set(Calendar.SECOND, 0);
			endCalendar.set(Calendar.HOUR_OF_DAY, 0);
			endCalendar.set(Calendar.MINUTE, 0);
			endCalendar.set(Calendar.SECOND, 0);
			logger.debug("Date parameters added");
			ruleType.setCondition(XACMLManager.generateComplexDateComparison(endCalendar.getTime(), startCalendar.getTime() , true, true));
			logger.debug("Date parameter added");
		}
		else if (timeRange != null)
		{
			logger.debug("Adding time parameter...");
//			startCalendar.set(Calendar.MONTH, 0);
//			startCalendar.set(Calendar.DAY_OF_MONTH, 1);
//			endCalendar.set(Calendar.MONTH, 0);
//			endCalendar.set(Calendar.DAY_OF_MONTH, 1);
			Utils.setTimeParameters(timeRange,startCalendar, endCalendar);
			logger.debug("Time parameters added");
			ruleType.setCondition(XACMLManager.generateComplexTimeComparison(endCalendar.getTime(), startCalendar.getTime(), true, true));
			logger.debug("Time parameter added");
		}
		
	}




	/**
	 * 
	 * @param adder
	 * @param dateRange
	 * @param timeRange
	 * @param ruleID
	 * @return
	 */
	private boolean addTimeIntervals (PolicyAdder adder, String dateRange, String timeRange, PolicyType policyType, RuleType ruleType)
	{
		logger.debug("Checking time intervals ");
		logger.debug("Date Range "+dateRange);
		logger.debug("Time range "+timeRange);
		boolean response = false;
		logger.debug("Adding extra parameters");
		setTimeParametersInRuleType(ruleType, dateRange, timeRange);
		
		if (executeUpdate(adder, policyType))
		{
			logger.debug("Extra parameters added");
			response = true;
		}
		else
		{
			logger.error("Unable to add the extra parameters: an error occourred in the update process");
		}
		
		return response;
	}
	
	/**
	 * 
	 * @param adder
	 * @param policyType
	 * @return
	 */
	private boolean executeUpdate (PolicyAdder adder, PolicyType policyType)
	{
		logger.debug("Executing update operation...");
		int version = Integer.parseInt(policyType.getVersion());
		System.out.println("DOM "+policyType.getDOM());
		Status response = adder.updateXACMLPolicy(null, version,XACMLManager.policy2Element(policyType));
		logger.debug("Operation executed");
		boolean result =  (response.getStatus() == Status.OPERATION_OK && response.getResult() == Status.RESULT_TRUE);
		logger.debug("Result "+result);
		return result;
	}

	
	/**
	 * 
	 * @param policyReader
	 * @return
	 */
	private PolicySetType [] loadPolicySets (PolicyReader policyReader)
	{
		logger.debug("Loading all policy sets...");
		Status listPolicySetsResponse =  policyReader.listPolicySets(null);
		PolicySetType [] policySetTypes = null;
		
		if (listPolicySetsResponse.getStatus() == Status.OPERATION_OK && listPolicySetsResponse.getResult() == Status.RESULT_TRUE)
		{
			logger.debug("Policy sets loaded");
			String policySetString = listPolicySetsResponse.getInfo();
			logger.debug("Policy Set "+policySetString);
			policySetTypes = XACMLManager.getPolicySetsFromString(policySetString);
		}
		else logger.debug("Policy sets loaded");

		return policySetTypes;

	}
	
	/**
	 * 
	 * @param policyReader
	 * @return
	 */
	private PolicyType [] loadPolicies (PolicyReader policyReader)
	{
		logger.debug("Loading all policy sets...");
		Status listPoliciesResponse =  policyReader.listPolicies(null);
		PolicyType [] policyTypes = null;
		
		
		if (listPoliciesResponse.getStatus() == Status.OPERATION_OK && listPoliciesResponse.getResult() == Status.RESULT_TRUE)
		{
			logger.debug("Policies loaded");
			String policyString = listPoliciesResponse.getInfo();
			logger.debug("Policy"+policyString);
			policyTypes = XACMLManager.getPolicesFromString(policyString);
		}
		else logger.debug("Policy sets loaded");

		return policyTypes;

	}
	
	/**
	 * 
	 * @param ruleBean
	 * @param attributeMap
	 * @return
	 */
	private boolean checkRuleBeanAttributes (RuleBean ruleBean, Map<String, String> attributeMap)
	{
		logger.debug("Checking if the attributes are present in the rule bean");
		Map<String, String> attributeBeanLocalMap = new HashMap<String, String> (ruleBean.getAttributes());
		Iterator<String> keys = attributeMap.keySet().iterator();
		boolean equal = true;
		
		while (keys.hasNext() && equal)
		{
			String comparismKey = keys.next();
			String comparismValue = attributeMap.get(comparismKey);
			logger.debug("Checking key "+comparismKey+ " value "+comparismValue);
			String value = attributeBeanLocalMap.remove(comparismKey);
			logger.debug("Compared value "+value);

			if (value == null || !comparismValue.equals(value))
			{
				logger.debug("Value not equal");
				equal = false;
			}
			else logger.debug("Value found");
			
		}
		
		boolean response = equal && (attributeBeanLocalMap.size() == 0);
		logger.debug("Response "+response);
		return response;
	}
	

	private ResponseBean addExtraParameters (PolicyAdder adder, String ruleID, String dateRange, String timeRange)
	{
		if ((dateRange == null || dateRange.trim().length()==0) && (timeRange == null || timeRange.trim().length()==0))
		{
			logger.debug("No extra parameters to be added");
			reloadPolicies ();
		}
		else
		{
			logger.debug("Adding extra parameters");
			PolicyReader policyReader = PolicyManagerFactory.getPolicyReader();
			logger.debug("Reading policy containing created rule");
			PolicyRuleBean policyRuleBean = Utils.findPolicyContainingRule(policyReader,ruleID);

			if (policyRuleBean == null || policyRuleBean.getPolicyType() == null || policyRuleBean.getRuleType() == null || !addTimeIntervals(adder, dateRange, timeRange,policyRuleBean.getPolicyType(), policyRuleBean.getRuleType()))

			{
				logger.error("Unable to add the extra parameters: an error occourred in the update process");
				reloadPolicies ();
				
				 return new ResponseBean(ResponseBean.RULE_CREATE_UPDATE_WARNING_ON_EXTRA_PARAMETERS, ruleID);
			}
			
			
		}

		logger.debug("Rule completed");
		reloadPolicies ();
		return new ResponseBean(ResponseBean.RULE_CREATE_UPDATE_OK, ruleID);

	}
	
	/**
	 * 
	 */
	private void reloadPolicies ()
	{
		if (!this.reloader.reloadPolicies())
		{
			this.logger.warn("Unable to reload policies!!");
		}
	}
	
	
//	public static void main(String[] args) throws Exception {
//		DefaultBootstrap.bootstrap();
//		ConfigurationManagerBuilder.setConfigurationManagerInstance(new TestConfigurationManager());
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
////		SecurityManager.getInstance().setKeyFile("/home/ciro/Progetti/Vision/Esecuzione/certificati/vision-test-ips/host2.pem");
////		SecurityManager.getInstance().setCertFile("/home/ciro/Progetti/Vision/Esecuzione/certificati/vision-test-ips/hostcert.pem");
//		SecurityManager.getInstance().loadCertificate();
//		PolicyEngine engine = new PolicyEngineImpl();
//		RuleBean bean = new RuleBean();
//		//bean.getAttributes().put("urn:oasis:names:tc:xacml:1.0:subject:subject-id", "Ciro");
//		//bean.getAttributes().put("subject", "urn:d4science:roles:role-values:cirone2");
//		bean.getAttributes().put("role", "urn:d4science:roles:role-values:capoccia");
//		bean.setAction("playror");
//		bean.setResource(".*");
//		//bean.setTimeRange("1530-1630");
//		bean.setPermitted(true);
//		System.out.println(engine.createRule(bean));
//		//System.out.println(engine.deleteAll());
//////		//dc479433-cade-43ac-b62c-e476ef468440
////		List<Attribute> attrs = new ArrayList<Attribute>();
////		attrs.add(new Attribute("subject","urn:d4science:roles:role-values:ciro"));
////		List<RuleBean> beans = engine.listRules();
////		
////		for (RuleBean responseBean : beans)
////		{
////			System.out.println(responseBean);
////		}
//	//System.out.println(engine.getRule("31913f61-d10d-46b5-b73e-fb16f1ceee35"));
////		
////		//System.out.println(engine.deleteRule("66525e88-8d4c-46ce-8644-8e16fce515e0"));
//////
//		
////		System.out.println(engine.listRules());
//	//	engine.deleteAll();
//	}
//
//	
////	public static void main(String[] args) {
////		System.out.println(Calendar.getInstance().getTimeZone());
////	}







	
}
