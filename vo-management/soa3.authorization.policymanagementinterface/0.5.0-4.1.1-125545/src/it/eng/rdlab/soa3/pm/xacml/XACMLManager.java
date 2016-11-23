package it.eng.rdlab.soa3.pm.xacml;

import it.eng.rdlab.soa3.connector.utils.XMLUtils;
import it.eng.rdlab.soa3.pm.connector.beans.DateCondition;
import it.eng.rdlab.soa3.pm.connector.beans.DateTimeCondition;
import it.eng.rdlab.soa3.pm.connector.beans.Obligation;
import it.eng.rdlab.soa3.pm.connector.beans.TimeCondition;
import it.eng.rdlab.soa3.pm.connector.beans.TimeRelatedCondition;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyManagerConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.opensaml.DefaultBootstrap;
import org.opensaml.xacml.policy.ApplyType;
import org.opensaml.xacml.policy.AttributeAssignmentType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeValueType;
import org.opensaml.xacml.policy.ConditionType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



/**
 *
 * Utility methods to manage XACML documents. 
 * 
 * @author Ciro Formisano (ENG)
 *
 */

public class XACMLManager 
{
	/*
	 * Organization
	 * PolicySet -> resource
	 * Policy -> action
	 * Rule -> subject
	 */
	
	
    public static final String RULE_COMBALG_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides";
    public static final String RULE_COMBALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";
    public static final String RULE_COMBALG_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides";
    public static final String COMB_ALG_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable";
    public static final String COMB_ALG_ORDERED_DENY_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-deny-overrides";
    public static final String COMB_ALG_ORDERED_PERMIT_OVERRIDS = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:ordered-permit-overrides";

	
	private static Logger log = LoggerFactory.getLogger(XACMLManager.class);

	/**
	 * 
	 * @param objectString
	 * @return
	 * @throws Exception
	 */
	private static XMLObject getObject (String objectString) throws Exception
	{
		Document doc = XMLUtils.string2Document(objectString);
		Element policyElement = doc.getDocumentElement();
		UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(policyElement);
		return  unmarshaller.unmarshall(policyElement);
	}
	

	/**
	 * 
	 * @param policyString
	 * @return
	 */
	public static PolicyType [] getPolicesFromString (String policyString)
	{
		try
		{	
			Document doc = XMLUtils.string2Document(policyString);
			Element policiesElement = doc.getDocumentElement();
			NodeList policyNodes = policiesElement.getElementsByTagNameNS(PolicyManagerConstants.XACML_NAMESPACE, PolicyManagerConstants.PAP_POLICY_ELEMENT_TAG);
			PolicyType [] policyTypeArray = new PolicyType [policyNodes.getLength()];
			
			for (int i=0; i<policyNodes.getLength();i++)
			{
				Element policy = (Element) policyNodes.item(i);
				UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
				Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(policy);
				policyTypeArray [i] = (PolicyType) unmarshaller.unmarshall(policy);
				
			}

			return  policyTypeArray;
		}
		catch (Exception e)
		{
			log.error("Unable to create Policy array ",e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param policySetsString
	 * @return
	 */
	public static PolicySetType [] getPolicySetsFromString (String policySetsString)
	{
		try
		{	
			Document doc = XMLUtils.string2Document(policySetsString);
			Element policySetsElement = doc.getDocumentElement();
			NodeList policySetNodes = policySetsElement.getElementsByTagNameNS(PolicyManagerConstants.XACML_NAMESPACE, PolicyManagerConstants.PAP_POLICY_SET_ELEMENT_TAG);
			PolicySetType [] policySetsTypeArray = new PolicySetType [policySetNodes.getLength()];
			
			for (int i=0; i<policySetNodes.getLength();i++)
			{
				Element policySet = (Element) policySetNodes.item(i);
				UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
				Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(policySet);
				policySetsTypeArray [i] = (PolicySetType) unmarshaller.unmarshall(policySet);
				
			}

			return  policySetsTypeArray;
		}
		catch (Exception e)
		{
			log.error("Unable to create Policy array ",e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param policyString
	 * @return
	 */
	public static PolicyType getPolicyFromString (String policyString)
	{
		try
		{
			return (PolicyType) getObject(policyString);
		}
		catch (Exception e)
		{
			log.error("Unable to create Policy Element",e);
			return null;
		}
	}

	/**
	 * 
	 * @param policySetString
	 * @return
	 */
	public static PolicySetType getPolicySetFromString (String policySetString)
	{
		try
		{
			return (PolicySetType) getObject(policySetString);
		}
		catch (Exception e)
		{
			log.error("Unable to create Policy Element",e);
			return null;
		}
	}

	/**
	 * 
	 * @param policy
	 * @return
	 */
	public static Element policy2Element (PolicyType policy)
	{
		try
		{
	        MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
	        Marshaller marshaller = marshallerFactory.getMarshaller(policy);
	        return marshaller.marshall(policy);
		} catch (Exception e)
		{
			log.error("Unable to marshall the policy",e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param obligation
	 * @return
	 */
	public static ObligationType generateObligation (Obligation obligation)
	{
		XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
		ObligationType obligationType = (ObligationType) builderFactory.getBuilder(ObligationType.DEFAULT_ELEMENT_QNAME).buildObject(ObligationType.DEFAULT_ELEMENT_QNAME);
		obligationType.setObligationId(obligation.getValue());
		
		if (obligation.isFulfillOn()) obligationType.setFulfillOn(EffectType.Permit);
		else obligationType.setFulfillOn(EffectType.Deny);
		
		Map<String, Map<String, String>> attributeMap = obligation.getAttributes();
		
		Set<String> keySet = attributeMap.keySet();
		
		for (String datatype :keySet)
		{
			log.debug("Setting elements of datatype "+datatype);
			
			Map<String, String> attributes = attributeMap.get(datatype);
			
			Set<String> attributeNames = attributes.keySet();
			
			for (String attributeName : attributeNames)
			{
				String attributeValue = attributes.get(attributeName);
				log.debug("Attribute id = "+attributeName);
				log.debug("Attribute value = "+attributeValue);
				AttributeAssignmentType attribute = (AttributeAssignmentType)  builderFactory.getBuilder(AttributeAssignmentType.DEFAULT_ELEMENT_NAME).buildObject(AttributeAssignmentType.DEFAULT_ELEMENT_NAME);
				attribute.setDataType(datatype);
				attribute.setAttributeId(attributeName);
				attribute.setValue(attributeValue);
				obligationType.getAttributeAssignments().add(attribute);
			} 
			
		}
		
		return obligationType;
	}
	
	private static ApplyType generateAtomicTimeRelatedCondition (TimeRelatedCondition condition, Date time, String functionID, String attributeID, String dataType, String format)
	{
		log.debug("Generating atomic time condition "+condition);
		XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
		ApplyType functionApply = (ApplyType) builderFactory.getBuilder(ApplyType.DEFAULT_ELEMENT_NAME).buildObject(ApplyType.DEFAULT_ELEMENT_NAME);
		functionApply.setFunctionId(condition.getCondition());
		ApplyType currentTimeApply = (ApplyType) builderFactory.getBuilder(ApplyType.DEFAULT_ELEMENT_NAME).buildObject(ApplyType.DEFAULT_ELEMENT_NAME);
		currentTimeApply.setFunctionId(functionID);
		AttributeDesignatorType envAttributeDesignator = (AttributeDesignatorType) builderFactory.getBuilder(AttributeDesignatorType.ENVIRONMENT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME).buildObject(AttributeDesignatorType.ENVIRONMENT_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
		envAttributeDesignator.setAttribtueId(attributeID);
		envAttributeDesignator.setDataType(dataType);
		currentTimeApply.getExpressions().add(envAttributeDesignator);
		AttributeValueType attributeValue = (AttributeValueType) builderFactory.getBuilder(AttributeValueType.DEFAULT_ELEMENT_NAME).buildObject(AttributeValueType.DEFAULT_ELEMENT_NAME);
		attributeValue.setDataType(dataType);
		String timeExpression = formatTime(time, format);
		log.debug("Time expression "+timeExpression);
		attributeValue.setValue(timeExpression);
		functionApply.getExpressions().add(currentTimeApply);
		functionApply.getExpressions().add(attributeValue);
		return functionApply;
	}
	

	/**
	 * 
	 * @param condition
	 * @param time
	 * @return 
	 */
	public static ApplyType generateAtomicTimeCondition (TimeCondition condition, Date time)
	{
		return generateAtomicTimeRelatedCondition (condition,time, XACMLConstants.TIME_ONE_ONLY, XACMLConstants.TIME_CURRENT,XACMLConstants.TIME_DATA_TYPE, XACMLConstants.TIME_FORMAT );
	}
	
	public static ApplyType generateAtomicDateCondition (DateCondition condition, Date time)
	{
		return generateAtomicTimeRelatedCondition (condition,time, XACMLConstants.DATE_ONE_ONLY, XACMLConstants.DATE_CURRENT,XACMLConstants.DATE_DATA_TYPE, XACMLConstants.DATE_FORMAT );
	}
	
	public static ApplyType generateAtomicDateTimeCondition (DateTimeCondition condition, Date time)
	{
		return generateAtomicTimeRelatedCondition (condition,time, XACMLConstants.DATE_TIME_ONE_ONLY, XACMLConstants.DATE_TIME_CURRENT,XACMLConstants.DATE_TIME_DATA_TYPE, XACMLConstants.DATE_TIME_FORMAT );
	}
	
	/**
	 * 
	 * @param upperTime
	 * @param lowerTime
	 * @param upperEqual
	 * @param lowerEqual
	 * @return
	 */
	public static ConditionType generateComplexTimeComparison (Date upperTime, Date lowerTime, boolean upperEqual, boolean lowerEqual)
	{
		SimpleDateFormat format = new SimpleDateFormat(XACMLConstants.TIME_FORMAT);
		log.debug("The current time must be between "+format.format(lowerTime)+" and "+format.format(upperTime)+" upper equal "+upperEqual+ "lower equal "+lowerEqual);
		XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
		ConditionType condition = (ConditionType) builderFactory.getBuilder(ConditionType.DEFAULT_ELEMENT_NAME).buildObject(ConditionType.DEFAULT_ELEMENT_NAME);
		ApplyType functionAnd = (ApplyType) builderFactory.getBuilder(ApplyType.DEFAULT_ELEMENT_NAME).buildObject(ApplyType.DEFAULT_ELEMENT_NAME);
		functionAnd.setFunctionId(XACMLConstants.FUNCTION_AND);
		log.debug("Generating upper time function...");
		TimeCondition upperTimeCondition = upperEqual ?  new TimeCondition(TimeCondition.LESSER_EQUAL) : new TimeCondition(TimeCondition.LESSER);
		ApplyType upperLimit = generateAtomicTimeCondition(upperTimeCondition, upperTime);
		log.debug("Upper time function generated");
		log.debug("Generating upper time function...");
		TimeCondition lowerTimeCondition = lowerEqual ?  new TimeCondition(TimeCondition.GREATER_EQUAL) : new TimeCondition(TimeCondition.GREATER);
		ApplyType lowerLimit = generateAtomicTimeCondition(lowerTimeCondition, lowerTime);
		log.debug("Lower time function generated");
		functionAnd.getExpressions().add(upperLimit);
		functionAnd.getExpressions().add(lowerLimit);
		condition.setExpression(functionAnd);
		log.debug("Condition type generated");
		return condition;
	}
	
	
	public static ConditionType generateComplexDateComparison (Date upperDate, Date lowerDate, boolean upperEqual, boolean lowerEqual)
	{
		SimpleDateFormat format = new SimpleDateFormat(XACMLConstants.DATE_FORMAT);
		log.debug("The current time must be between "+format.format(lowerDate)+" and "+format.format(upperDate)+" upper equal "+upperEqual+ "lower equal "+lowerEqual);
		XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
		ConditionType condition = (ConditionType) builderFactory.getBuilder(ConditionType.DEFAULT_ELEMENT_NAME).buildObject(ConditionType.DEFAULT_ELEMENT_NAME);
		ApplyType functionAnd = (ApplyType) builderFactory.getBuilder(ApplyType.DEFAULT_ELEMENT_NAME).buildObject(ApplyType.DEFAULT_ELEMENT_NAME);
		functionAnd.setFunctionId(XACMLConstants.FUNCTION_AND);
		log.debug("Generating upper time function...");
		DateCondition upperDateCondition = upperEqual ?  new DateCondition(DateCondition.LESSER_EQUAL) : new DateCondition(DateCondition.LESSER);
		ApplyType upperLimit = generateAtomicDateCondition(upperDateCondition, upperDate);
		log.debug("Upper time function generated");
		log.debug("Generating upper time function...");
		DateCondition lowerDateCondition = lowerEqual ?  new DateCondition(DateCondition.GREATER_EQUAL) : new DateCondition(DateCondition.GREATER);
		ApplyType lowerLimit = generateAtomicDateCondition(lowerDateCondition, lowerDate);
		log.debug("Lower time function generated");
		functionAnd.getExpressions().add(upperLimit);
		functionAnd.getExpressions().add(lowerLimit);
		condition.setExpression(functionAnd);
		log.debug("Condition type generated");
		return condition;
	}
	
	public static ConditionType generateComplexDateTimeComparison (Date upperDateTime, Date lowerDateTime, boolean upperEqual, boolean lowerEqual)
	{
		SimpleDateFormat format = new SimpleDateFormat(XACMLConstants.DATE_TIME_FORMAT);
		log.debug("The current time must be between "+format.format(lowerDateTime)+" and "+format.format(upperDateTime)+" upper equal "+upperEqual+ "lower equal "+lowerEqual);
		XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
		ConditionType condition = (ConditionType) builderFactory.getBuilder(ConditionType.DEFAULT_ELEMENT_NAME).buildObject(ConditionType.DEFAULT_ELEMENT_NAME);
		ApplyType functionAnd = (ApplyType) builderFactory.getBuilder(ApplyType.DEFAULT_ELEMENT_NAME).buildObject(ApplyType.DEFAULT_ELEMENT_NAME);
		functionAnd.setFunctionId(XACMLConstants.FUNCTION_AND);
		log.debug("Generating upper time function...");
		DateTimeCondition upperDateTimeCondition = upperEqual ?  new DateTimeCondition(DateTimeCondition.LESSER_EQUAL) : new DateTimeCondition(DateTimeCondition.LESSER);
		ApplyType upperLimit = generateAtomicDateTimeCondition(upperDateTimeCondition, upperDateTime);
		log.debug("Upper time function generated");
		log.debug("Generating upper time function...");
		DateTimeCondition lowerDateTimeCondition = lowerEqual ?  new DateTimeCondition(DateTimeCondition.GREATER_EQUAL) : new DateTimeCondition(DateTimeCondition.GREATER);
		ApplyType lowerLimit = generateAtomicDateTimeCondition(lowerDateTimeCondition, lowerDateTime);
		log.debug("Lower time function generated");
		functionAnd.getExpressions().add(upperLimit);
		functionAnd.getExpressions().add(lowerLimit);
		condition.setExpression(functionAnd);
		log.debug("Condition type generated");
		return condition;
	}
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	private static String formatTime (Date date, String format)
	{		
		SimpleDateFormat timeFormat = new SimpleDateFormat(format+"Z");
		timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		StringBuilder timeString = new StringBuilder(timeFormat.format(date));
		timeString.insert(timeString.length()-2, ':');
		return timeString.toString();
	}
	

	

	public static void main(String[] args) throws ConfigurationException {
//		SimpleDateFormat format = new SimpleDateFormat(XACMLConstants.TIME_FORMAT+"Z");
//		format.setTimeZone(TimeZone.getTimeZone("UTC"));
//		StringBuilder timeString = new StringBuilder(format.format(new Date()));
//		timeString.insert(timeString.length()-2, ':');
//		System.out.println(timeString.toString());
//		System.out.println(formatTime(new Date(), XACMLConstants.TIME_FORMAT));
		System.out.println(Calendar.getInstance().getTimeZone().getRawOffset());
		System.out.println(new Date());
		Calendar c = Calendar.getInstance();
		//c.setTimeZone(TimeZone.getTimeZone("CEST"));
		c.set(Calendar.HOUR_OF_DAY, 14);
		c.set(Calendar.MINUTE, 55);
		c.set(Calendar.SECOND, 0);
		System.out.println(c.getTime());
	}
	
}
