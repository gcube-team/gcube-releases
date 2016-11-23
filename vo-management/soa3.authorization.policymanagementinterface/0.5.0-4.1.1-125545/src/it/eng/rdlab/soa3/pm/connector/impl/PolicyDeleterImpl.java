package it.eng.rdlab.soa3.pm.connector.impl;

import it.eng.rdlab.soa3.connector.utils.SoapUtils;
import it.eng.rdlab.soa3.pm.connector.beans.Status;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyDeleter;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyManagerConstants;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * Standard implementation of Policy Deleter
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class PolicyDeleterImpl implements PolicyDeleter 
{
	private Logger logger;
	private String url;

	public PolicyDeleterImpl() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public PolicyDeleterImpl(String url) 
	{
		this ();
		setUrl(url);
	}
	
	public void setUrl(String url) 
	{
		this.url = url+"/";
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status clear() 
	{
		try 
		{
			String url = new StringBuilder(this.url).append(PolicyManagerConstants.SIMPLE_POLICY_MANAGEMENT_SERVICE).toString();
			this.logger.debug("Url = "+url);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document requestDocument = builder.newDocument();

			Element root = requestDocument.createElementNS(PolicyManagerConstants.PAP_SERVICE_NAMESPACE, PolicyManagerConstants.PAP_ERASE_REPOSITORY_TAG);
			this.logger.debug("Creating alias element...");
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
				this.logger.debug(SoapUtils.soapMessage2String(response));
				
				SOAPBody responseBody = response.getSOAPBody();
				NodeList nodeList = responseBody.getElementsByTagName(PolicyManagerConstants.PAP_ERASE_REPOSITORY_RESPONSE_TAG);
	
				if (nodeList.getLength()>0)
				{

					return new Status(Status.OPERATION_OK, Status.RESULT_TRUE);
				}
				else 
				{
					this.logger.debug("Response false");
					return new Status(Status.OPERATION_OK, Status.RESULT_FALSE);
				}
	
			} catch (Exception ge)
			{
				ge.printStackTrace();
				this.logger.error("Error in sending the message",ge);
				return new Status(Status.SERVER_ERROR, Status.RESULT_FALSE);
			}
			
			
		} catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
			this.logger.error("Unable to create the request", e);
			return new Status(Status.INTERNAL_ERROR, Status.RESULT_FALSE);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status deletePolicy(String alias, String policyId)
	{
		return deleteElement(alias, policyId, PolicyManagerConstants.PAP_REMOVE_POLICY_TAG, PolicyManagerConstants.PAP_POLICY_ID_TAG,PolicyManagerConstants.PAP_REMOVE_POLICY_RETURN_TAG);
	}
	
	
	/**
	 * 
	 * @param response
	 * @param tagResponse
	 * @return
	 * @throws Exception
	 */
	private boolean parseResponse (SOAPMessage response, String tagResponse) throws Exception
	{
		this.logger.debug("Parsing response message...");
		this.logger.debug("Response message "+SoapUtils.soapMessage2String(response));
		SOAPBody responseBody = response.getSOAPBody();
		NodeList nodeList = responseBody.getElementsByTagNameNS(PolicyManagerConstants.PAP_SERVICE_NAMESPACE,tagResponse);
		
		if (nodeList.getLength()>0)
		{
			
			String info = ((Element) nodeList.item(0)).getTextContent();
			this.logger.debug("Text response = "+info);
			return (info != null && info.equalsIgnoreCase("true")); 

		}
		else return false;	
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status deletePolicySet(String alias, String policySetId) 
	{

		return deleteElement(alias, policySetId, PolicyManagerConstants.PAP_REMOVE_POLICY_SET_TAG, PolicyManagerConstants.PAP_POLICY_SET_ID_TAG, PolicyManagerConstants.PAP_REMOVE_POLICY_SET_RETURN_TAG);
	}
	
	
	private Status deleteElement (String alias, String elementId, String operationTag, String elementTag, String responseTag)
	{
		SOAPMessage soapResponse = null;
		
		try
		{
			String url = new StringBuilder(this.url).append(PolicyManagerConstants.XACML_POLICY_MANAGEMENT_SERVICE).toString();
			soapResponse =  PolicyGetUtils.identifyElement(url,alias, elementId, operationTag,elementTag);

			if (soapResponse == null) 
			{
				this.logger.error("Server error");
				return new Status(Status.SERVER_ERROR, Status.RESULT_FALSE);
			}

		} 
		catch (Exception e)
		{
			this.logger.error("Unable to generate soap message",e);
			return new Status(Status.INTERNAL_ERROR, Status.RESULT_FALSE);
		}
		
		try 
		{
		
			if (parseResponse(soapResponse,responseTag)) return new Status(Status.OPERATION_OK, Status.RESULT_TRUE);
			else return new Status(Status.OPERATION_OK, Status.RESULT_FALSE);
		} catch (Exception e)
		{
			this.logger.error("Invalid response received",e);
			return new Status(Status.SERVER_ERROR, Status.RESULT_FALSE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status deleteRule(String alias, String ruleId) 
	{
		return deleteElement(alias, ruleId, PolicyManagerConstants.PAP_REMOVE_OBJECT_TAG, PolicyManagerConstants.PAP_OBJECT_ID_TAG,PolicyManagerConstants.PAP_REMOVE_OBJECT_RETURN_TAG);

	}

}
