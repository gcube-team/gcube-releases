package it.eng.rdlab.soa3.pm.connector.impl;

import it.eng.rdlab.soa3.connector.utils.SoapUtils;
import it.eng.rdlab.soa3.connector.utils.XMLUtils;
import it.eng.rdlab.soa3.pm.connector.beans.AttributeBean;
import it.eng.rdlab.soa3.pm.connector.beans.Obligation;
import it.eng.rdlab.soa3.pm.connector.beans.Status;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyAdder;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyManagerConstants;

import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * 
 * Standard implementation of Policy Adder
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class PolicyAdderImpl implements PolicyAdder 
{
	
	private Logger logger;
	private String url;

	public PolicyAdderImpl() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public PolicyAdderImpl(String url) 
	{
		this ();
		setUrl(url);
	}
	
	/**
	 * 
	 * @param url
	 */
	public void setUrl(String url) 
	{
		this.url = url+"/";
	}
	
	private Status sendRequestForBooleanResponse (String url,Document requestDocument, String responseTag)
	{
		SOAPMessage message = null;
		
		try 
		{
			this.logger.debug("Generating soap message");
			message = SoapUtils.generateSoapMessage(requestDocument);
			this.logger.debug("Soap Message generated");
		} catch (Exception se)
		{

			this.logger.error("Unable to generate soap message",se);
			return new Status(Status.INTERNAL_ERROR, Status.RESULT_FALSE);
		}
		
		SOAPMessage response = null;
		
		try 
		{
			this.logger.debug("Sending message");
			response = SoapUtils.performCall(message,url);
			this.logger.debug("Soap Message generated");
			
			SOAPBody responseBody = response.getSOAPBody();
			NodeList nodeList = responseBody.getElementsByTagName(responseTag);
			
			if (nodeList.getLength()>0)
			{
				Element returnElement = (Element)nodeList.item(0);
				String result = returnElement.getTextContent();
				logger.debug("Result = "+result);
				if (result != null && result.equalsIgnoreCase("true")) return new Status(Status.OPERATION_OK, Status.RESULT_TRUE);
			}

			return new Status(Status.OPERATION_OK, Status.RESULT_FALSE);
		

		} catch (Exception ge)
		{
			this.logger.error("Error in sending the message",ge);
			return new Status(Status.SERVER_ERROR, Status.RESULT_FALSE);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status addNewRule(String alias,List<AttributeBean> subjectAttributes, String action, String resource, boolean permitted, Obligation obligation, boolean moveAfter) 
	{
		try 
		{
			if (obligation == null) obligation = new Obligation();
			
			String url = new StringBuilder(this.url).append(PolicyManagerConstants.SIMPLE_POLICY_MANAGEMENT_SERVICE).toString();
			this.logger.debug("Url = "+url);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document requestDocument = builder.newDocument();

			Element root = requestDocument.createElementNS(PolicyManagerConstants.PAP_HLPS_NAMESPACE, PolicyManagerConstants.PAP_ADD_RULE_TAG);
			this.logger.debug("Creating alias element...");
			Element aliasElement = alias == null ? XMLUtils.createNullElement(requestDocument, PolicyManagerConstants.PAP_ALIAS_TAG,null) : XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_ALIAS_TAG,alias,null);
			root.appendChild(aliasElement);
			this.logger.debug("Alias element created and appended");
			this.logger.debug("Creating isPermit element with value "+permitted+"...");
			Element isPermit = permitted ? XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_IS_PERMIT_TAG, "true",null) : XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_IS_PERMIT_TAG, "false",null); 
			root.appendChild(isPermit);
			this.logger.debug("IsPermit element created and appended");
			this.logger.debug("Creating attribute list");
			Element attributeList = requestDocument.createElement(PolicyManagerConstants.PAP_ATTRIBUTE_LIST_TAG);
			
			Iterator<AttributeBean> attributesIterator = subjectAttributes.iterator();
			
			while (attributesIterator.hasNext())
			{
				AttributeBean attributeBean = attributesIterator.next();
				String key = attributeBean.getId();
				String value = attributeBean.getValue();
				logger.debug("Adding attribute "+key+ " with value "+value);
				Element policyId = requestDocument.createElement(PolicyManagerConstants.PAP_POLICY_ID_TAG);
				Text attribute = requestDocument.createTextNode(key+"="+value);
				policyId.appendChild(attribute);
				attributeList.appendChild(policyId);
				logger.debug("Attribute added");
			}
			
			root.appendChild(attributeList);
			this.logger.debug("Attribute list created");
			this.logger.debug("Creating action value");
			root.appendChild(XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_ACTION_VALUE_TAG, action,null));
			this.logger.debug("Action value created");
			this.logger.debug("Creating resource value");
			root.appendChild(XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_RESOURCE_VALUE_TAG, resource,null));
			this.logger.debug("Resource value created");
			this.logger.debug("Creating action identifier value");
			root.appendChild(XMLUtils.createNullElement(requestDocument, PolicyManagerConstants.PAP_ACTION_IDENTIFIER_TAG,null));
			this.logger.debug("Action rule value created");
			this.logger.debug("Creating action identifier value");
			root.appendChild(XMLUtils.createNullElement(requestDocument, PolicyManagerConstants.PAP_RULE_IDENTIFIER_TAG,null));
			this.logger.debug("Rule identifier value created");
			this.logger.debug("Creating obligation value");
			
			if (obligation.getValue() == null)  root.appendChild(XMLUtils.createNullElement(requestDocument, PolicyManagerConstants.PAP_OBLIGATION_VALUE_TAG,null));
			else  root.appendChild(XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_OBLIGATION_SCOPE_TAG,obligation.getValue(),null));;
			
			this.logger.debug("Obligation value created");
			this.logger.debug("Creating obligation scope");
			root.appendChild(XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_OBLIGATION_SCOPE_TAG,obligation.getObligationScope(),null));
			this.logger.debug("Obligation scope created");
			this.logger.debug("Creating moveAfter element with value "+moveAfter+"...");
			Element moveAfterElement = moveAfter ? XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_MOVE_AFTER_TAG, "true",null) : XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_MOVE_AFTER_TAG, "false",null); 
			root.appendChild(moveAfterElement);
			this.logger.debug("MoveAfter element created and appended");
			requestDocument.appendChild(root);
			
			SOAPMessage message = null;
			
			try 
			{
				this.logger.debug("Generating soap message");
				message = SoapUtils.generateSoapMessage(requestDocument);
				this.logger.debug("Soap Message generated");
			} catch (Exception se)
			{
				
				this.logger.error("Unable to generate soap message",se);
				return new Status(Status.INTERNAL_ERROR, Status.RESULT_FALSE);
			}
			
			SOAPMessage response = null;
			
			try 
			{
				this.logger.debug("Sending message");
				response = SoapUtils.performCall(message,url);
				this.logger.debug("Soap Message generated");
				
				SOAPBody responseBody = response.getSOAPBody();
				NodeList nodeList = responseBody.getElementsByTagName(PolicyManagerConstants.PAP_ADD_RULE_RETURN_TAG);
				
				if (nodeList.getLength()>0)
				{
					Element returnElement = (Element)nodeList.item(0);
					String policyId = returnElement.getTextContent();
					logger.debug("Policy Id = "+policyId);
					Status responseStatus = new Status(Status.OPERATION_OK, Status.RESULT_TRUE);
					responseStatus.setInfo(policyId);
					return responseStatus;
				}
				else 
				{
					this.logger.debug("Response false");
					return new Status(Status.OPERATION_OK, Status.RESULT_FALSE);
				}
	
			} catch (Exception ge)
			{
				this.logger.error("Error in sending the message",ge);
				return new Status(Status.SERVER_ERROR, Status.RESULT_FALSE);
			}
			
			
		} catch (ParserConfigurationException e) 
		{
			this.logger.error("Unable to create the request", e);
			return new Status(Status.INTERNAL_ERROR, Status.RESULT_FALSE);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status addXACMLPolicy(String alias, int index, String policySetId, String policyIdPrefix, PolicyType xacmlPolicy)
	{
		return addXACMLPolicySet(alias, index, xacmlPolicy.getDOM());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status addXACMLPolicySet(String alias, int index, PolicySetType xacmlPolicySet) 
	{
		return addXACMLPolicySet(alias, index, xacmlPolicySet.getDOM());
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status updateXACMLPolicy(String alias, int version, PolicyType xacmlPolicy) 
	{
		return updateXACMLPolicy(alias, version, xacmlPolicy.getDOM());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status addXACMLPolicy(String alias, int index, String policySetId, String policyIdPrefix, Element xacmlPolicy) 
	{
		try 
		{
			logger.debug("Policy class "+xacmlPolicy.getClass());
			String url = new StringBuilder(this.url).append(PolicyManagerConstants.XACML_POLICY_MANAGEMENT_SERVICE).toString();
			this.logger.debug("Url = "+url);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document requestDocument = builder.newDocument();

			Element root = requestDocument.createElementNS(PolicyManagerConstants.PAP_SERVICE_NAMESPACE, "ser:"+PolicyManagerConstants.PAP_ADD_POLICY_TAG);
			this.logger.debug("Creating alias element...");
			Element aliasElement = alias == null ? XMLUtils.createNullElement(requestDocument, PolicyManagerConstants.PAP_ALIAS_TAG,null) : XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_ALIAS_TAG,alias,null);
			root.appendChild(aliasElement);
			this.logger.debug("Alias element created and appended");
			this.logger.debug("Creating index value");
			root.appendChild(XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_INDEX_TAG, String.valueOf(index),null));
			this.logger.debug("Index value created");
			this.logger.debug("Creating policy set id");
			root.appendChild(XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_POLICY_SET_ID_TAG, policySetId,null));
			this.logger.debug("Policy set id created");
			this.logger.debug("Creating policy prefix");
			root.appendChild(XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_POLICY_ID_PREFIX_TAG, policyIdPrefix,null));
			logger.debug("Document class "+ requestDocument.getClass());
			Node policyElementNode = requestDocument.adoptNode(xacmlPolicy);
			logger.debug("Policy ELement "+policyElementNode);
			root.appendChild(policyElementNode);
			this.logger.debug("Policy element added");

			this.logger.debug("MoveAfter element created and appended");
			requestDocument.appendChild(root);
			return sendRequestForBooleanResponse(url,requestDocument, PolicyManagerConstants.PAP_ADD_POLICY_RESPONSE);
		
		} catch (ParserConfigurationException e) 
		{
			this.logger.error("Unable to create the request", e);
			return new Status(Status.INTERNAL_ERROR, Status.RESULT_FALSE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status addXACMLPolicySet(String alias, int index, Element xacmlPolicySet) 
	{
		try 
		{
			String url = new StringBuilder(this.url).append(PolicyManagerConstants.XACML_POLICY_MANAGEMENT_SERVICE).toString();
			this.logger.debug("Url = "+url);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document requestDocument = builder.newDocument();

			Element root = requestDocument.createElementNS(PolicyManagerConstants.PAP_SERVICE_NAMESPACE, "ser:"+PolicyManagerConstants.PAP_ADD_POLICY_SET_TAG);
			this.logger.debug("Creating alias element...");
			Element aliasElement = alias == null ? XMLUtils.createNullElement(requestDocument, PolicyManagerConstants.PAP_ALIAS_TAG,null) : XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_ALIAS_TAG,alias,null);
			root.appendChild(aliasElement);
			this.logger.debug("Alias element created and appended");
			this.logger.debug("Creating index value");
			root.appendChild(XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_INDEX_TAG, String.valueOf(index),null));
			Node policyElementNode = requestDocument.adoptNode(xacmlPolicySet);
			root.appendChild(policyElementNode);
			this.logger.debug("Policy set element added");
			requestDocument.appendChild(root);
			return sendRequestForBooleanResponse(url,requestDocument, PolicyManagerConstants.PAP_ADD_POLICY_SET_RESPONSE);
			
		} catch (ParserConfigurationException e) 
		{
			this.logger.error("Unable to create the request", e);
			return new Status(Status.INTERNAL_ERROR, Status.RESULT_FALSE);
		}	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status updateXACMLPolicy(String alias, int version, Element policy) 
	{
		try 
		{
			this.logger.debug("Policy element class "+policy.getClass());
			String url = new StringBuilder(this.url).append(PolicyManagerConstants.XACML_POLICY_MANAGEMENT_SERVICE).toString();
			this.logger.debug("Url = "+url);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document requestDocument = builder.newDocument();

			Element root = requestDocument.createElementNS(PolicyManagerConstants.PAP_SERVICE_NAMESPACE, "ser:"+PolicyManagerConstants.PAP_UPDATE_POLICY_TAG);
			this.logger.debug("Creating alias element...");
			Element aliasElement = alias == null ? XMLUtils.createNullElement(requestDocument, PolicyManagerConstants.PAP_ALIAS_TAG,null) : XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_ALIAS_TAG,alias,null);
			root.appendChild(aliasElement);
			this.logger.debug("Alias element created and appended");
			this.logger.debug("Creating version value");
			root.appendChild(XMLUtils.createElement(requestDocument, PolicyManagerConstants.PAP_VERSION_TAG, String.valueOf(version),null));
			Node policyElementNode = requestDocument.adoptNode(policy);
			this.logger.debug("Document  class "+requestDocument.getClass());
			
			this.logger.debug("Policy node "+policyElementNode);
			root.appendChild(policyElementNode);
			this.logger.debug("Policy element added");

			this.logger.debug("MoveAfter element created and appended");
			requestDocument.appendChild(root);
			return sendRequestForBooleanResponse(url,requestDocument, PolicyManagerConstants.PAP_UPDATE_POLICY_RESPONSE);
		
		} catch (ParserConfigurationException e) 
		{
			this.logger.error("Unable to create the request", e);
			return new Status(Status.INTERNAL_ERROR, Status.RESULT_FALSE);
		}
	}
	
	
}
