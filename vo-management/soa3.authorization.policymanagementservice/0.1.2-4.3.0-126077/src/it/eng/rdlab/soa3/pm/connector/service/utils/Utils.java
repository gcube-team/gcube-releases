package it.eng.rdlab.soa3.pm.connector.service.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.eng.rdlab.soa3.pm.connector.javaapi.beans.RuleBean;
import it.eng.rdlab.soa3.pm.connector.service.beans.ListRules;
import it.eng.rdlab.soa3.pm.connector.service.beans.RuleJaxBean;

import org.apache.commons.digester.plugins.RuleLoader;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 * 
 * Utilities
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class Utils 
{
	

	/**
	 * 
	 * @param ruleBean
	 * @return
	 */
	public static RuleJaxBean fromRuleBean (RuleBean ruleBean)
	{
		if (ruleBean == null) return null;
		else
		{
		
			RuleJaxBean response = new RuleJaxBean();
			response.setAttributes(new HashMap<String, String> (ruleBean.getAttributes()));
			response.setAction(ruleBean.getAction());
			response.setResource(ruleBean.getResource());
			response.setRuleId(ruleBean.getRuleId());
			response.setPermitted(ruleBean.isPermitted());
			response.setTimeRange(ruleBean.getTimeRange());
			response.setDateRange(ruleBean.getDateRange());
			return response;
		}
	}
	
	/**
	 * 
	 * @param ruleJaxBean
	 * @return
	 */
	public static RuleBean fromRuleJaxBean (RuleJaxBean ruleJaxBean)
	{
		
		RuleBean response = new RuleBean();
		response.setAttributes(new HashMap<String, String> (ruleJaxBean.getAttributes()));
		response.setAction(ruleJaxBean.getAction());
		response.setResource(ruleJaxBean.getResource());
		response.setRuleId(ruleJaxBean.getRuleId());
		response.setPermitted(ruleJaxBean.isPermitted());
		response.setTimeRange(ruleJaxBean.getTimeRange());
		response.setDateRange(ruleJaxBean.getDateRange());
		return response;
	}
	
	/**
	 * 
	 * @param beanList
	 * @return
	 */
	public static ListRules fromRuleBeanList (List<RuleBean> beanList)
	{
		ListRules listRules = new ListRules();
		
		if (beanList != null)
		{
		
			for (RuleBean bean : beanList)
			{
				listRules.getBeanList().add(Utils.fromRuleBean(bean));
			}
		}
		
		return listRules;
		
	}
	
	/**
	  * Chooses JAXB as the annotation for the serialization/deserialization
	  *  
	  * @return mapper ObjectMapper
	  * 
	  */
	public static ObjectMapper getMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.getDeserializationConfig().appendAnnotationIntrospector(new JaxbAnnotationIntrospector());
		return mapper;
	}
	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		Map<String, String> prova = new HashMap<String, String>();
		prova.put("ciro", "ciao");
		System.out.println(getMapper().writeValueAsString(new HashMap<String, String>(prova)));
	}

}
