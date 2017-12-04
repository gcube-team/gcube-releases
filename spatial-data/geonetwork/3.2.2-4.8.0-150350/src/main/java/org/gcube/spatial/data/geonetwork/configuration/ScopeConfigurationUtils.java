package org.gcube.spatial.data.geonetwork.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.spatial.data.geonetwork.model.Account;
import org.gcube.spatial.data.geonetwork.model.Account.Type;
import org.gcube.spatial.data.geonetwork.model.ScopeConfiguration;
import org.gcube.spatial.data.geonetwork.model.faults.EncryptionException;
import org.gcube.spatial.data.geonetwork.model.faults.MissingConfigurationException;
import org.gcube.spatial.data.geonetwork.utils.EncryptionUtils;
import org.gcube.spatial.data.geonetwork.utils.RuntimeParameters;

@Slf4j
public class ScopeConfigurationUtils {

	static Set<ScopeConfiguration> fromMap(Map<String,Property> theMap, Properties parameters) throws EncryptionException{		
		HashSet<ScopeConfiguration> toReturn=new HashSet<>();
		String[] suffixes=getExistingSuffixes(theMap,parameters).split(",");
		log.debug("Suffixes to use are "+Arrays.toString(suffixes));
		for(String suff:suffixes)
			try{
				toReturn.add(getConfigurationBySuffix(theMap, suff,parameters));		
			}catch(Exception e){
				log.warn("Invalid configuration "+suff,e);
			}
		return toReturn;
	}


	static String getExistingSuffixes(Map<String,Property> props,Properties params){
		if(!props.containsKey(params.getProperty(RuntimeParameters.availableGroupSuffixList)))
			props.put(params.getProperty(RuntimeParameters.availableGroupSuffixList), 
					new Property().nameAndValue(params.getProperty(RuntimeParameters.availableGroupSuffixList), ""));
		return props.get(params.getProperty(RuntimeParameters.availableGroupSuffixList)).value();				
	}


	static ScopeConfiguration getConfigurationBySuffix(Map<String,Property> theMap,String suffix, Properties parameters) throws EncryptionException{
		HashMap<Account.Type,Account> accounts=new HashMap<>();
		String scopeAccountName=theMap.get(parameters.get(RuntimeParameters.scopeUserPrefix)+suffix).value();
		String scopeAccountPassword=EncryptionUtils.decrypt(theMap.get(parameters.get(RuntimeParameters.scopePasswordPrefix)+suffix).value());

		accounts.put(Account.Type.SCOPE,new Account(scopeAccountName,scopeAccountPassword,Type.SCOPE));

		String ckanAccountName=theMap.get(parameters.get(RuntimeParameters.ckanUserPrefix)+suffix).value();
		String ckanPassword=EncryptionUtils.decrypt(theMap.get(parameters.get(RuntimeParameters.ckanPasswordPrefix)+suffix).value());
		accounts.put(Type.CKAN, new Account(ckanAccountName,ckanPassword,Type.CKAN));
		Integer publicGroup=Integer.parseInt(theMap.get(parameters.get(RuntimeParameters.publicGroupPrefix)+suffix).value());
		Integer privateGroup=Integer.parseInt(theMap.get(parameters.get(RuntimeParameters.privateGroupPrefix)+suffix).value());
		Integer defaultGroup=Integer.parseInt(theMap.get(parameters.get(RuntimeParameters.defaultGroupPrefix)+suffix).value());

		String assignedScope=theMap.get(parameters.get(RuntimeParameters.assignedScopePrefix)+suffix).value();

		return new ScopeConfiguration(assignedScope, publicGroup, privateGroup, accounts, defaultGroup);
	}

	public static ScopeConfiguration getByScope(Set<ScopeConfiguration> existing, String scope) throws MissingConfigurationException{
		for(ScopeConfiguration configuration : existing)
			if(configuration.getAssignedScope().equals(scope)) return configuration;
		throw new MissingConfigurationException("Scope "+scope+" has no configuration");	
	}

	/**
	 * Looks for default user name among properties to determine used suffix
	 * 
	 * @param toLookFor
	 * @param map
	 * @return
	 * @throws MissingConfigurationException 
	 */
	static String getSuffixByConfiguration(ScopeConfiguration toLookFor,Map<String,Property> map, Properties parameters) throws MissingConfigurationException{
		String defaultUser=toLookFor.getAccounts().get(Type.SCOPE).getUser();
		for(Entry<String,Property> entry:map.entrySet())
			if(entry.getValue().value().equals(defaultUser)) return entry.getKey().substring(parameters.getProperty(RuntimeParameters.scopeUserPrefix).length());
		throw new MissingConfigurationException("Unable to find suffix for configuration "+toLookFor);
	}

	static Map<String,Property> asMap(ScopeConfiguration config,String toUseSuffix, Properties parameters){
		Map<String,Property> toReturn=new HashMap<>();
		toReturn.put(parameters.getProperty(RuntimeParameters.assignedScopePrefix)+toUseSuffix, new Property().nameAndValue(parameters.getProperty(RuntimeParameters.assignedScopePrefix)+toUseSuffix, config.getAssignedScope()));

		Account scopeAccount=config.getAccounts().get(Account.Type.SCOPE);
		toReturn.put(parameters.getProperty(RuntimeParameters.scopeUserPrefix)+toUseSuffix, new Property().nameAndValue(parameters.getProperty(RuntimeParameters.scopeUserPrefix)+toUseSuffix, scopeAccount.getUser()));
		toReturn.put(parameters.getProperty(RuntimeParameters.scopePasswordPrefix)+toUseSuffix, new Property().encrypted(true).nameAndValue(parameters.getProperty(RuntimeParameters.scopePasswordPrefix)+toUseSuffix, EncryptionUtils.encrypt(scopeAccount.getPassword())));

		Account ckanAccount=config.getAccounts().get(Account.Type.CKAN);
		toReturn.put(parameters.getProperty(RuntimeParameters.ckanUserPrefix)+toUseSuffix,new Property().nameAndValue(parameters.getProperty(RuntimeParameters.ckanUserPrefix)+toUseSuffix, ckanAccount.getUser()));
		toReturn.put(parameters.getProperty(RuntimeParameters.ckanPasswordPrefix)+toUseSuffix,new Property().encrypted(true).nameAndValue(parameters.getProperty(RuntimeParameters.ckanPasswordPrefix)+toUseSuffix, EncryptionUtils.encrypt(ckanAccount.getPassword())));

		toReturn.put(parameters.getProperty(RuntimeParameters.defaultGroupPrefix)+toUseSuffix,new Property().nameAndValue(parameters.getProperty(RuntimeParameters.defaultGroupPrefix)+toUseSuffix, config.getDefaultGroup().toString()));
		toReturn.put(parameters.getProperty(RuntimeParameters.publicGroupPrefix)+toUseSuffix,new Property().nameAndValue(parameters.getProperty(RuntimeParameters.publicGroupPrefix)+toUseSuffix, config.getPublicGroup().toString()));
		toReturn.put(parameters.getProperty(RuntimeParameters.privateGroupPrefix)+toUseSuffix,new Property().nameAndValue(parameters.getProperty(RuntimeParameters.privateGroupPrefix)+toUseSuffix, config.getPrivateGroup().toString()));

		return toReturn;
	}


	static Map<String,Property> insertSuffix(Map<String,Property> toUpdate,String toAddSuffix,Properties params){
		String current=getExistingSuffixes(toUpdate,params);
		String toSet=current.length()>0?current+","+toAddSuffix:toAddSuffix;
		
		toUpdate.put(params.getProperty(RuntimeParameters.availableGroupSuffixList), 
				new Property().nameAndValue(params.getProperty(RuntimeParameters.availableGroupSuffixList), toSet));
		return toUpdate;
	}

	static String generateSuffix(String existingSuffixes){
		log.debug("Generating suffix, existing are : "+existingSuffixes);
		String[] suffixArray=existingSuffixes.split(",");
		int maxIndex=0;
		for(String suff:suffixArray){
			try{
				int actual=Integer.parseInt(suff);
				if(actual>maxIndex) maxIndex=actual;
			}catch(Throwable t){

			}
		}
		String generated=(maxIndex+1)+"";
		log.debug("Generated suffix is : "+generated);
		return generated;
	}


}
