package it.eng.rdlab.um.ldap.service;

import it.eng.rdlab.um.ldap.LdapBasicConstants;
import it.eng.rdlab.um.ldap.LdapDataModelWrapper;
import it.eng.rdlab.um.ldap.configuration.LdapConfiguration;
import it.eng.rdlab.um.ldap.service.exceptions.LdapManagerException;
import it.eng.rdlab.um.ldap.user.bean.LdapUserModel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapManager implements LdapBasicConstants
{
	private Log log;
	private DirContext dirContext;
	private static LdapManager instance;
	private boolean isClose = true;
	
	private LdapManager (LdapConfiguration configuration) throws NamingException
	{
		this.log = LogFactory.getLog(this.getClass());
		log.debug("Initializing LDAP manager...");
		String userDn = configuration.getUserDn();
		String password = configuration.getPassword();
		String url = configuration.getUrl();
		log.debug("Ldap URL = "+url);
		log.debug("User DN = "+userDn);
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY,JAVA_LDAP_DRIVER);
		env.put(Context.PROVIDER_URL,url);
		log.debug(userDn);
		if (userDn != null) env.put(Context.SECURITY_PRINCIPAL,userDn);
		if (password!= null) env.put(Context.SECURITY_CREDENTIALS,password);
		this.dirContext = new InitialDirContext(env);
		this.isClose = false;
		log.debug("LDAP manager initialized");
	}
	
	public static LdapManager initInstance (LdapConfiguration configuration) throws NamingException
	{
		instance = new LdapManager(configuration);
		return instance;
	}
	
	public static LdapManager getInstance () throws ConfigurationException
	{
		if (instance == null) throw new ConfigurationException("No Ldap Manager istance configured, use initInstance!!!");
		
		return instance;
	}
	
	public boolean createDataElement (LdapDataModelWrapper dataModelWrapper) throws LdapManagerException
	{
		this.log.debug("Loading object classes");
		
		if (!dataModelWrapper.validateData()) 
		{
			this.log.error("Invalid data");
			throw new LdapManagerException("Invalid data");
		}
		else
		{
			Attribute objectClassAttributes = loadObjectClassAttribute(dataModelWrapper);
			Attributes attributeContainer = new BasicAttributes();
			attributeContainer.put(objectClassAttributes);
			Map<String, String> modelAttributes =  dataModelWrapper.getAttributeMap();
			Map<String, List<String>> modelListAttributes = dataModelWrapper.getListAttributeMap();
			Iterator<String> attributesKey = modelAttributes.keySet().iterator();
				
			while (attributesKey.hasNext())
			{
				String key = attributesKey.next();
				String value = modelAttributes.get(key);
				this.log.debug("Attribute key = "+key);
				this.log.debug("Attribute value = "+value);
				attributeContainer.put(new BasicAttribute(key, value));
			}
			
			Iterator<String> listAttributesKey = modelListAttributes.keySet().iterator();
			
			while (listAttributesKey.hasNext())
			{
				
				String key = listAttributesKey.next();
				List<String> value = modelListAttributes.get(key);
				log.debug("Attribute key = "+key);
				BasicAttribute basicAttribute = new BasicAttribute(key);
				
				for (String singleValue : value)
				{
					this.log.debug("Attribute value = "+singleValue);
					basicAttribute.add(singleValue);
				}
				
				attributeContainer.put(basicAttribute);
			}
			
			String dn = dataModelWrapper.getDistinguishedName();
			this.log.debug("Attributes generated, creating the data element "+dn);

			try 
			{
				this.dirContext.createSubcontext(dn, attributeContainer);
				return true;
			} catch (NamingException e) 
			{
				this.log.error("Unable to create the new user", e);
				throw new LdapManagerException("Unable to create the new data element", e);
			}
		}
	}

	public boolean deleteData (String dn) throws LdapManagerException 
	{
		try {
			this.dirContext.destroySubcontext(dn);
			return true;
		} 
		catch (NamingException e) 
		{
			this.log.error("Unable to complete the operation",e);
			throw new LdapManagerException("Unable to complete the operation",e);
		}
	}

	public Attributes getData (String dn) throws LdapManagerException
	{
		this.log.debug("Getting data with dn "+dn);
		
		try {
			Attributes result = this.dirContext.getAttributes(dn);
			this.log.debug("Data found");
			return result;
		} 
		catch (NamingException e) {
		
			throw new LdapManagerException("Unable to get data",e);
		}
	}

	public NamingEnumeration<SearchResult> searchData (LdapDataModelWrapper filter) throws LdapManagerException
	{
		this.log.debug("Searching data");
		String filterString = buildFilter(filter);
		log.debug("with filter "+filterString);
		String dn = filter.getDistinguishedName();
		this.log.debug("Base DN = "+dn);
		SearchControls ctls = new SearchControls();
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE); 
		try 
		{
			NamingEnumeration<SearchResult> answer = this.dirContext.search(dn, filterString, ctls);
			log.debug("Operation completed");
			return answer;
		} 
		catch (NamingException e)
		{
			this.log.error("Unable to complete the operation",e);
			throw new LdapManagerException("Unable to complete the search",e);
		}
	}
	
	public boolean updateData (LdapDataModelWrapper oldData, LdapDataModelWrapper newData) throws NamingException
	{
		return this.updateData(oldData, newData, new LdapGenericDataModelComparator());
	}
	
	public boolean updateData (LdapDataModelWrapper oldData, LdapDataModelWrapper newData, LdapDataModelComparator comparator) throws NamingException
	{
		this.log.debug("Comparing and updating datas");
		List<ModificationItem> modificationItems = comparator.compare(oldData, newData);
		
		if (modificationItems.size()>0)
		{
			this.log.debug("Performing the modifications");
			this.dirContext.modifyAttributes(oldData.getDistinguishedName(),modificationItems.toArray(new ModificationItem [modificationItems.size()]));
			this.log.debug("Modifications completed");
			return true;
		}
		else
		{
			this.log.debug("The attributes are equal, no modifications requider");
			return false;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private Attribute loadObjectClassAttribute (LdapDataModelWrapper dataModelWrapper)
	{
		this.log.debug("Loading object class attributes");
		List<String> objectClasses = (List<String>) dataModelWrapper.getObjectParameter(LdapUserModel.OBJECT_CLASSES);
		Attribute objectClassAttributes = new BasicAttribute(OBJECT_CLASS);
		
		for (String oc : objectClasses)
		{
			this.log.debug("Object class Attribute "+oc);
			objectClassAttributes.add(oc);
		}
		this.log.debug("Object class attributes loaded");
		return objectClassAttributes;
	}
	
	private String buildFilter (LdapDataModelWrapper filter)
	{
		this.log.debug("building filter");
		StringBuilder builder = new StringBuilder("(&");
		List<String> objectClasses = filter.getObjectClasses();
		Map<String, String> attributes = filter.getAttributeMap();
		Map<String, List<String>> listAttributes = filter.getListAttributeMap();
		
		if (objectClasses != null)
		{
			for (String objectClassParameter : objectClasses)
			{
				this.log.debug("Filter for object class parameter "+objectClassParameter);
				builder.append("(").append(OBJECT_CLASS).append("=").append(objectClassParameter).append(")");
			}
		}
		
		if (attributes != null)
		{
			Iterator<String> keys = attributes.keySet().iterator();
			
			while (keys.hasNext())
			{
				String key = keys.next();
				this.log.debug("Filter for parameter "+key);
				String attributeValue = attributes.get(key);
				this.log.debug("value "+attributeValue);
				if (attributeValue.trim().length()>0) builder.append("(").append(key).append("=").append(attributeValue).append(")");
				else this.log.debug("attribute value "+ attributeValue +" not valid");
				
			}

		}
		
		if (listAttributes != null)
		{
			Iterator<String> keys = listAttributes.keySet().iterator();
			
			while (keys.hasNext())
			{
				String key = keys.next();
				this.log.debug("Filter for parameter "+key);
				List<String> attributeValue = listAttributes.get(key);
				
				for (String singleValue : attributeValue)
				{
					this.log.debug("value "+singleValue);
					builder.append("(").append(key).append("=").append(singleValue).append(")");
				}
				
			}

		}
		
		builder.append(")");
		return builder.toString();
	}

	public void close () throws NamingException
	{
		if (!isClose) this.dirContext.close();
		this.isClose = true;
	}

	
}
