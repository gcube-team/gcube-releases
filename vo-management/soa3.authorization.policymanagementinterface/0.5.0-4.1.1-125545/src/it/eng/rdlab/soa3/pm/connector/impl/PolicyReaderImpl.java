package it.eng.rdlab.soa3.pm.connector.impl;

import it.eng.rdlab.soa3.connector.utils.XMLUtils;
import it.eng.rdlab.soa3.pm.connector.beans.Status;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyManagerConstants;
import it.eng.rdlab.soa3.pm.connector.interfaces.PolicyReader;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 
 * Standard implementation of Policy Reader
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class PolicyReaderImpl implements PolicyReader
{

	private Logger logger;
	private String url;
	
	public PolicyReaderImpl() 
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	
	public PolicyReaderImpl(String url) 
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status listPolicies(String alias) 
	{
		return getElement(alias, null, PolicyManagerConstants.PAP_LIST_POLICIES_TAG, null, PolicyManagerConstants.PAP_LIST_POLICIES_RESPONSE);

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status getPolicy(String alias, String policyId) 
	{
		return getElement(alias, policyId, PolicyManagerConstants.PAP_GET_POLICY_TAG, PolicyManagerConstants.PAP_POLICY_ID_TAG,PolicyManagerConstants.PAP_GET_POLICY_RETURN_TAG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status listPolicySets(String alias) 
	{
		return getElement(alias, null, PolicyManagerConstants.PAP_LIST_POLICY_SETS_TAG, null, PolicyManagerConstants.PAP_LIST_POLICY_SETS_RESPONSE_TAG);

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status getPolicySet(String alias, String policySetId) 
	{
		return getElement(alias, policySetId, PolicyManagerConstants.PAP_GET_POLICY_SET_TAG, PolicyManagerConstants.PAP_POLICY_SET_ID_TAG, PolicyManagerConstants.PAP_GET_POLICY_SET__RETURN_TAG);
	}
	
	
	
	/* ******************************************************************************************************************************
	
		PRIVATE METHODS
		
	*******************************************************************************************************************************/
	


	/**
	 * 
	 */
	private Status getElement (String alias, String elementId, String tagRequest, String tagElementID, String tagReturnElement)
	{
		SOAPMessage soapResponse = null;
		
		try
		{
			String url = new StringBuilder(this.url).append(PolicyManagerConstants.XACML_POLICY_MANAGEMENT_SERVICE).toString();		
			this.logger.debug("Url = "+url);
			soapResponse =  PolicyGetUtils.identifyElement(url,alias, elementId, tagRequest,tagElementID);

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
			String xacmlResponse = parseResponse(soapResponse, tagReturnElement);
			//this.logger.debug("XACML Response = "+xacmlResponse);

			
			if (xacmlResponse != null)
			{
				Status responseStatus = new Status(Status.OPERATION_OK, Status.RESULT_TRUE);
				responseStatus.setInfo(xacmlResponse);
				return responseStatus;
			}
			else
			{
				this.logger.debug("Response false");
				return new Status(Status.OPERATION_OK, Status.RESULT_FALSE);
			}
		} catch (Exception e)
		{
			this.logger.error("Invalid response received",e);
			return new Status(Status.SERVER_ERROR, Status.RESULT_FALSE);
		}

	}
	
	/**
	 * 
	 * @param response
	 * @param tagResponse
	 * @return
	 * @throws Exception
	 */
	private String parseResponse (SOAPMessage response, String tagResponse) throws Exception
	{
		this.logger.debug("Parsing response message...");
		SOAPBody responseBody = response.getSOAPBody();
		NodeList nodeList = responseBody.getElementsByTagName(tagResponse);
		
		if (nodeList.getLength()>0)
		{
			NodeList policies = nodeList.item(0).getChildNodes();
			
			String info = null;
			
			if (policies.getLength()>0) info = XMLUtils.element2String((Element) policies.item(0));

			return info;
		}
		else return null;
		
	}


}
