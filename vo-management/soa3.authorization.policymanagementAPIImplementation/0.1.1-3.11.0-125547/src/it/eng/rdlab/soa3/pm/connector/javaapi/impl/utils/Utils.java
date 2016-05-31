package it.eng.rdlab.soa3.pm.connector.javaapi.impl.utils;


import it.eng.rdlab.soa3.pm.connector.beans.AttributeBean;
import it.eng.rdlab.soa3.pm.connector.beans.DateCondition;
import it.eng.rdlab.soa3.pm.connector.beans.DateTimeCondition;
import it.eng.rdlab.soa3.pm.connector.beans.Status;
import it.eng.rdlab.soa3.pm.connector.beans.TimeCondition;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyReader;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.Attribute;
import it.eng.rdlab.soa3.pm.connector.javaapi.beans.RuleBean;
import it.eng.rdlab.soa3.pm.connector.javaapi.impl.beans.AttributesManagementBean;
import it.eng.rdlab.soa3.pm.xacml.XACMLConstants;
import it.eng.rdlab.soa3.pm.xacml.XACMLManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opensaml.xacml.policy.ActionType;
import org.opensaml.xacml.policy.ApplyType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.ConditionType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ExpressionType;
import org.opensaml.xacml.policy.IdReferenceType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.ResourceType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xacml.policy.SubjectMatchType;
import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils 
{
	/**
	 * 
	 * @param policySet
	 * @param policy
	 * @param rule
	 * @return
	 */
	public static RuleBean createRuleBean (PolicySetType policySet, PolicyType policy, RuleType rule)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Creating rule bean");
		logger.debug("Loading attributes...");
		Map<String, String> attributes = generateAttributeMap(rule.getTarget());
		logger.debug("Attributes loaded");
		logger.debug("Loading action...");
		String action = loadActionString(policy.getTarget());
		logger.debug("Action loaded");
		logger.debug("Loading resource...");
		String resource = loadResourceString(policySet.getTarget());
		logger.debug("Resource loaded");
		RuleBean response = new RuleBean();
		response.setAttributes(attributes);
		response.setAction(action);
		response.setResource(resource);
		logger.debug("Setting time condition (if exists)");
		setTimeCondition(response, rule.getCondition());
		boolean effect = rule.getEffect() == EffectType.Permit;
		logger.debug("Effect = "+effect);
		response.setPermitted(effect);
		response.setRuleId(rule.getRuleId());
		logger.debug("Operation completed");
		return response;
	}
	

	
	/**
	 * 
	 * @param url
	 * @param ruleId
	 * @return
	 */
	public static  PolicyRuleBean findPolicyContainingRule (PolicyReader reader,String ruleId)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("finding policy associated to rule "+ruleId);
		Status response = reader.listPolicies(null);
		logger.debug("response status "+response.getStatus());
		logger.debug("response result "+response.getResult());
		PolicyRuleBean policyRuleResponse = null;
		
		if (response.getStatus() == Status.OPERATION_OK && response.getResult() == Status.RESULT_TRUE)
		{
			PolicyType [] types = XACMLManager.getPolicesFromString(response.getInfo());
			int i=0;

			PolicyType policy = null;
			
			while (i<types.length && (policyRuleResponse == null))
			{
				policy = types [i];
				List<RuleType> rules = policy.getRules();
		
				int y=0;
				
				while (y<rules.size() &&  (policyRuleResponse == null))
				
				{
					RuleType rule = rules.get(y);
					logger.debug("Rule ID "+rule.getRuleId());
					
					if (rule.getRuleId().equalsIgnoreCase(ruleId))
					{
						logger.debug("Rule found");
						policyRuleResponse = new PolicyRuleBean();
						policyRuleResponse.setPolicyType(policy);
						policyRuleResponse.setRuleType(rule);
					}
					else y++;
				}
				
				i++;			
			}
		}
		else
		{
			
			logger.error("policy not found");
			
		}
		
		if (policyRuleResponse != null)
		{
			String policyId = policyRuleResponse.getPolicyType().getPolicyId();
			logger.debug("Finding policy Set");
			Status listPolicySetsStatus = reader.listPolicySets(null);
			
			if (listPolicySetsStatus.getStatus() == Status.OPERATION_OK && listPolicySetsStatus.getResult() == Status.RESULT_TRUE)
			{
				PolicySetType [] psTypes = XACMLManager.getPolicySetsFromString(listPolicySetsStatus.getInfo());
				PolicySetType policySet = findPolicySetContainingPolicy(psTypes, policyId);
				policyRuleResponse.setPolicySetType(policySet);
			}
			else logger.error("Policy Set not found");
			
		}
		
		return policyRuleResponse;
	}
	
	
	/**
	 * 
	 * @param ruleId
	 * @param policy
	 * @return
	 */
	public static RuleType findRule (String ruleId, PolicyType policy)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Finding rule "+ruleId);
		RuleType response = null;
		Iterator<RuleType> rules = policy.getRules().iterator();
		
		while (rules.hasNext() && response == null)
		{
			RuleType rule = rules.next();
			String ruleIDFound = rule.getRuleId();
			logger.debug("Rule id found "+ruleIDFound);
			
			if (ruleIDFound.equals(ruleId)) response = rule;
		}
		
		logger.debug("Rule found "+(response != null));
		return response;
	}
	
	


	/**
	 * 
	 * @param reader
	 * @param policySet
	 * @return
	 */
	public static List<PolicyType> getPoliciesFromPolicySet (PolicyReader reader,PolicySetType policySet)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Retrieving the policies");
		List<IdReferenceType> idReferenceList = policySet.getPolicyIdReferences();
		List<PolicyType> response = new ArrayList<PolicyType>();
		
		for (IdReferenceType idReference : idReferenceList)
		{
			String policyId = idReference.getValue();
			logger.debug("Policy Id = "+policyId);
			Status getPolicyStatus =  reader.getPolicy(null, policyId);
			
			if (getPolicyStatus.getStatus() == Status.OPERATION_OK && getPolicyStatus.getResult() == Status.RESULT_TRUE)
			{
				logger.debug("Policy "+policyId+ " get");
				PolicyType policyType = XACMLManager.getPolicyFromString(getPolicyStatus.getInfo());
				response.add(policyType);
				logger.debug("Policy added to the list");
			}
			else
			{
				logger.error("Policy "+policyId+ " not correctly retrieved: status = "+getPolicyStatus.getStatus()+ " result "+getPolicyStatus.getResult());
			}
			
		}
		return response;
	}
	
	
	/**
	 * 
	 * @param policySets
	 * @param policyId
	 * @return
	 */
	public static PolicySetType findPolicySetContainingPolicy (PolicySetType [] policySets,String policyId)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Retrieving the policy sets");
		logger.debug("For Policy id = "+policyId);
		logger.debug("Policy set lenght = "+policySets.length);
		PolicySetType policySet = null;
		int psIndex = 0;
		
		while (psIndex < policySets.length && policySet == null)
		{
			List<IdReferenceType> idRefedences = policySets [psIndex].getPolicyIdReferences();
			Iterator<IdReferenceType> idReferencesIterator = idRefedences.iterator();
			
			while (idReferencesIterator.hasNext() && policySet == null)
			{
				IdReferenceType ref = idReferencesIterator.next();
				String idReferenceValue = ref.getValue();
				logger.debug("Reference value = "+idReferenceValue);
				
				if (ref.getValue().equals(policyId) ) 
				{
					logger.debug("Policy Set found");
					logger.debug("Policy Set id = "+policySets[psIndex].getPolicySetId());
					policySet =policySets[psIndex]; 
				}

			}
			
			psIndex ++;
		}
		
		return policySet;
		
	}
	
	/**
	 * 
	 * @param attributeStringList
	 * @return
	 */
	public static AttributesManagementBean generateAttributeManagerBean (RuleBean ruleBean)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Generating attribute manager...");
		Map<String, String>attributeMap = ruleBean.getAttributes();
		logger.debug("Attributes "+attributeMap);
		String action = ruleBean.getAction();
		logger.debug("Action = "+action);
		String resource = ruleBean.getResource();
		logger.debug("Resource = "+resource);
		logger.debug("Getting attribute list...");
		AttributesManagementBean response = new AttributesManagementBean ();
		response.setMoveAfter(false);
		List<AttributeBean> subjectAttributes = new ArrayList<AttributeBean>();
		Set<String> keys = attributeMap.keySet();
		
		for (String attributeString : keys)
		{
			String value = attributeMap.get(attributeString);
			
			if (value != null)
			{
				if (value.equals("*")) 		response.setMoveAfter(true);
			
				subjectAttributes.add(new AttributeBean(attributeString,value));
			}
		}
		
		logger.debug("Attribute list get");
		response.setSubjectAttributes(subjectAttributes);
		logger.debug("Action id");
		
		if (action.equals("*")) response.setMoveAfter(true);
		
		response.setActionId(action);
		logger.debug("Resource id");
		
		if (resource.equals("*")) response.setMoveAfter(true);
		
		response.setResourceId(resource);
		return response;
	}
	
	
	/**
	 * 
	 * @param attributes
	 * @return
	 */
	public static Map<String, List<String>> generateAttributeMap (List<Attribute> attributes)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Getting attribute list...");
		Map<String, List<String>> response = new HashMap<String, List<String>>();
		
		for (Attribute attribute : attributes)
		{
			String id = attribute.getId();
			String value = attribute.getValue();
			logger.debug("id = "+id);
			logger.debug("Value = "+value);
			
			if (value != null)
			{
				List<String> attributeList = response.get(id);
				
				if (attributeList == null)
				{
					attributeList = new ArrayList<String>();
					response.put(id, attributeList);
				}
				
				attributeList.add(value);
			}
		
		}
		
		logger.debug("Attribute list get");
		return response;
	}
	
	/**
	 * 
	 * @param attributes
	 * @return
	 */
	public static List<Attribute> attributeBean2Attribute (List<AttributeBean> attributes)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Getting attribute list...");
		List<Attribute> response = new ArrayList<Attribute>();

		for (AttributeBean attributeBean : attributes)
		{
			
			response.add(new Attribute(attributeBean.getId(),attributeBean.getValue()));
			
		}
		
		logger.debug("Attribute list get");
		return response;
	}

	/**
	 * 
	 * @param targetType
	 * @return
	 */
	public static Map<String, String> generateAttributeMap (TargetType targetType)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Getting attribute list...");
		List<SubjectType> subjectTypeList = targetType.getSubjects().getSubjects();
		logger.debug("Attributes loaded, checking...");
		Iterator<SubjectType> subjectTypeIterator = subjectTypeList.iterator();
		Map<String, String> response = new HashMap<String, String> ();
		
		while (subjectTypeIterator.hasNext())
		{
			logger.debug("Checking subject");
			SubjectType subjectType = subjectTypeIterator.next();
			List<SubjectMatchType> matches = subjectType.getSubjectMatches();
			Iterator<SubjectMatchType> matchesIterator = matches.iterator();
			
			while (matchesIterator.hasNext())
			{
				SubjectMatchType matchType = matchesIterator.next();
				String id = AttributeLoader.getInstance().getAttributeId(matchType.getSubjectAttributeDesignator().getAttributeId());
				logger.debug("Attribute id = "+id);
				String value = matchType.getAttributeValue().getValue();
				logger.debug("Attribute value = "+value);
				response.put(id, value);
				
			}
		}

		logger.debug("Attribute list loaded");
		
		return response;
	}
	
	/**
	 * 
	 * @param targetType
	 * @return
	 */
	public static String loadActionString (TargetType targetType)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		List<ActionType> actionList = targetType.getActions().getActions();
		Iterator<ActionType> actionListIterator = actionList.iterator();
		String action = null;
		
		while (actionListIterator.hasNext() && action==null)
		{
			ActionType at = actionListIterator.next();
			
			try
			{
				String actionValue = at.getActionMatches().get(0).getAttributeValue().getValue();
				
				if (actionValue != null && actionValue.trim().length()>0)
				{
					logger.debug("Action found");
					action = actionValue.trim();
				}
				
			} catch (Exception e)
			{
				logger.error("Unable to find a valid resource value",e);
				
			}
			
			
		}
		
	return action;

	}
	
	
	/**
	 * 
	 * @param targetType
	 * @return
	 */
	public static String loadResourceString (TargetType targetType)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		List<ResourceType> resourceList = targetType.getResources().getResources();
		Iterator<ResourceType> resourceListIterator = resourceList.iterator();
		String resource = null;
		
		while (resourceListIterator.hasNext())
		{
			ResourceType rt = resourceListIterator.next();
			
			try
			{
				String resourceValue = rt.getResourceMatches().get(0).getAttributeValue().getValue();
				logger.debug("Resource value "+resourceValue);
				
				if (resourceValue != null && resourceValue.trim().length()>0)
				{
					logger.debug("Resource found");
					resource = resourceValue.trim();
				}
				
			} catch (Exception e)
			{
				logger.error("Unable to find a valid resource value",e);
				
			}
			
			
		}
		
		return resource;
		

	}
	
	

	
	public static void setTimeCondition (RuleBean ruleBean, ConditionType condition)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
	
		if (condition != null)
		{
			logger.debug("Loading time condition");
			ExpressionType function = condition.getExpression();
			
			if (function != null && function instanceof ApplyType && ((ApplyType) function).getFunctionId().equals(XACMLConstants.FUNCTION_AND))
			{
				logger.debug("Parsing apply type...");
				ApplyType functionAnd = (ApplyType) function;
				List<ExpressionType> expressions = functionAnd.getExpressions();
				
				if (expressions.size() == 2)
				{
					logger.debug("Loading conditions");
					TimeBean timeBean = new TimeBean();
					
					try
					{
						setIntervalCondition(timeBean,(ApplyType)expressions.get(0));
						setIntervalCondition(timeBean,(ApplyType)expressions.get(1));
						
						if (timeBean.lowerDate != null) ruleBean.setDateRange(timeBean.lowerDate+"-"+timeBean.upperDate);
						if (timeBean.lowerTime != null) ruleBean.setTimeRange(timeBean.lowerTime+"-"+timeBean.upperTime);
						
					}
					catch (Exception e)
					{
						logger.debug("Invalid time condition",e);
					}
					
				}
			}
		}
		else logger.debug("No time condition");
		

		
	}
	
	private static void setIntervalCondition (TimeBean bean, ApplyType condition) throws Exception
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Setting interval condition...");
		List<ExpressionType> expressions = condition.getExpressions();
		String comparisonCondition = condition.getFunctionId();
		logger.debug("Comparison Condition "+comparisonCondition);
		boolean upperValue = (comparisonCondition.equals(TimeCondition.LESSER_EQUAL) || comparisonCondition.equals(TimeCondition.LESSER))||
							(comparisonCondition.equals(DateCondition.LESSER_EQUAL) || comparisonCondition.equals(DateCondition.LESSER)) || 
							(comparisonCondition.equals(DateTimeCondition.LESSER_EQUAL) || comparisonCondition.equals(DateTimeCondition.LESSER));
		
		if (expressions.size() == 2)
		{
			logger.debug("Loading time conditions");
			ExpressionType condition1 = expressions.get(0);
			ExpressionType condition2 = expressions.get(1);
			AttributeValueType  comparatorTimeCondition = (condition1 instanceof AttributeValueType) ? (AttributeValueType) condition1 : (AttributeValueType) condition2;
			ApplyType  currentTimeCondition  = (condition2 instanceof ApplyType) ? (ApplyType) condition2 : (ApplyType) condition1;
			String dateType = getDateType (currentTimeCondition);
			
			if (dateType != null)
			{
				logger.debug("Getting comparator...");
				setComparisonTimeCondition (bean,comparatorTimeCondition,upperValue,dateType);
				logger.debug("Comparator completed");
			}
			else throw new Exception ("Invalid date type");
			
		}
		
	}
	
	private static void setComparisonTimeCondition (TimeBean bean, AttributeValueType condition, boolean upperValue, String dateType) throws ParseException 
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Setting condition...");
		logger.debug("Upper value "+upperValue);
		StringBuilder timeExpression = new StringBuilder(condition.getValue());
		logger.debug("Time expression = "+timeExpression);
		String format = null;
		boolean getTime = true;
		boolean getDate = true;
		logger.debug("Date type "+dateType);
		
		if (dateType.equals(XACMLConstants.TIME_ONE_ONLY))
		{
			format = XACMLConstants.TIME_FORMAT;
			getDate = false;
		}
		else if (dateType.equals(XACMLConstants.DATE_ONE_ONLY))
		{
			format = XACMLConstants.DATE_FORMAT;
			getTime = false;
		}
		else format = XACMLConstants.DATE_TIME_FORMAT;
 	
		logger.debug("Format "+format);
		SimpleDateFormat timeFormat = new SimpleDateFormat(format+"Z");
		//timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		timeExpression.deleteCharAt(timeExpression.length()-3);
		Date date = timeFormat.parse(timeExpression.toString());
		String dateString = null;
		String timeString = null;
		
		if (getDate) dateString = getDateParameter (date);
		if (getTime) timeString = getTimeParameter (date);
		
		if (upperValue)
		{
			bean.upperDate = dateString;
			bean.upperTime = timeString;
		}
		else
		{
			bean.lowerDate = dateString;
			bean.lowerTime = timeString;
		}
		
		logger.debug("Operation completed");

	}
	
	private static String getDateType (ApplyType condition)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Getting datetype");
		String dateType = condition.getFunctionId();
		logger.debug("datetype = "+dateType);
		return dateType;
	}
	

	public static String getDateParameter (Date date)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Parsing date "+date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		StringBuilder responseBuilder = new StringBuilder(""+calendar.get(Calendar.YEAR));
		logger.debug("Year "+responseBuilder);
		responseBuilder.append(int2String(calendar.get(Calendar.MONTH)+1));
		logger.debug("Yearmonth "+responseBuilder);
		responseBuilder.append(int2String(calendar.get(Calendar.DAY_OF_MONTH)));
		logger.debug("Yearmonthday "+responseBuilder);
		logger.debug("Date = "+responseBuilder.toString());
		return responseBuilder.toString();
	}
	
	public static String int2String (int number)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Converting integer to string");
		
		String response = number >9 ? String.valueOf(number) : "0"+number;
		
		if (response.length()>2) response = response.substring(1);
		
		logger.debug("response "+response);
		return response;
		
	}
	
	public static String getTimeParameter (Date date)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Parsing time "+date);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		StringBuilder responseBuilder = new StringBuilder(int2String(calendar.get(Calendar.HOUR_OF_DAY))).append(int2String(calendar.get(Calendar.MINUTE)));
		logger.debug("Time = "+responseBuilder.toString());
		return responseBuilder.toString();
	}
	
	/**
	 * 
	 * 
	 * @param dateRange
	 * @param startDate
	 * @param endDate
	 */
	public static void setDateParameters (String dateRange,Calendar startDate, Calendar endDate)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Parsing date range "+dateRange);
		try
		{
			String [] limits = dateRange.split("-");
			logger.debug("lower limit = "+limits [0]);
			logger.debug("Upper limit = "+limits[1]);
			limits [0] = limits [0].trim();
			limits [1] = limits [1].trim();
			String lowerYear = limits[0].substring(0, 4);
			String upperYear = limits[1].substring(0, 4);
			String lowerMonth = limits[0].substring(4, 6);
			String upperMonth = limits[1].substring(4, 6);
			String lowerDay = limits[0].substring(6);
			String upperDay = limits[1].substring(6);
			logger.debug("Date defined");
			startDate.set(Calendar.YEAR, Integer.parseInt(lowerYear));
			startDate.set(Calendar.MONTH, Integer.parseInt(lowerMonth)-1);
			startDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(lowerDay));
			logger.debug("Start date defined");
			logger.debug("Defining end date");
			endDate.set(Calendar.YEAR, Integer.parseInt(upperYear));
			endDate.set(Calendar.MONTH, Integer.parseInt(upperMonth)-1);
			endDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(upperDay));
			logger.debug("End date defined");
			
		} 
			catch (Exception e)
		{
			logger.error("Unable to correctly parse the date range",e);
		}
	}
	
	
	/**
	 * 
	 * @param timeRange
	 * @param startTime
	 * @param endTime
	 */
	public static void setTimeParameters (String timeRange,Calendar startTime, Calendar endTime)
	{
		Logger logger = LoggerFactory.getLogger(Utils.class);
		logger.debug("Parsing time range "+timeRange);
		//logger.debug(""+Calendar.getInstance().getTime());
		try
		{
			String [] limits = timeRange.split("-");
			logger.debug("lower limit = "+limits [0]);
			logger.debug("Upper limit = "+limits[1]);
			limits [0] = limits [0].trim();
			limits [1] = limits [1].trim();
			String lowerhour = limits[0].substring(0, 2);
			String lowerMinute = limits[0].substring(2);
			String upperhour = limits[1].substring(0, 2);
			String upperMinute = limits[1].substring(2);
			logger.debug("Time defined");
			startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(lowerhour));
			startTime.set(Calendar.MINUTE, Integer.parseInt(lowerMinute));
			startTime.set(Calendar.SECOND, 0);
			logger.debug("Start time defined as "+startTime.getTime());
			logger.debug("Defining end time");
			endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(upperhour));
			endTime.set(Calendar.MINUTE, Integer.parseInt(upperMinute));
			endTime.set(Calendar.SECOND, 0);
			logger.debug("End time defined as "+endTime.getTime());
			
		} 
			catch (Exception e)
		{
			logger.error("Unable to correctly parse the time range",e);
		}
	}
	
	public static void main(String[] args) throws ParseException {
		StringBuilder timeExpression = new StringBuilder("2013-03-29T15:35:47+01:00");
		timeExpression.deleteCharAt(timeExpression.length()-3);
		System.out.println(timeExpression);
		SimpleDateFormat timeFormat = new SimpleDateFormat(XACMLConstants.DATE_TIME_FORMAT+"Z");
		Date date = timeFormat.parse(timeExpression.toString());
		System.out.println(date);
		
	}
	
}
