/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.authportletmanager.shared.ConstantsSharing;
import org.gcube.portlets.admin.authportletmanager.shared.PolicyAuth;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

/**
 * The data source for contact information used in the sample.
 */
public class PolicyDataProvider {

	/**
	 * The singleton instance of the database.
	 */
	private static PolicyDataProvider instance;

	/**
	 * Get the singleton instance of the contact database.
	 * 
	 * @return the singleton instance
	 */
	public static PolicyDataProvider get() {
		if (instance == null) {
			instance = new PolicyDataProvider();
		}
		return instance;
	}

	/**
	 * The provider that holds the list of contacts in the database.
	 */
	private ListDataProvider<PolicyAuth> dataProvider = new ListDataProvider<PolicyAuth>();

	private String context=null;

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	
	private ArrayList<String> contextList=new ArrayList<String>();

	public ArrayList<String> getContextList() {
		return contextList;
	}

	public void setContextList(ArrayList<String> contextList) {
		this.contextList = contextList;
	}

	
	/**
	 * The list string used for search
	 */
	private List<String> initialSearch= new ArrayList<String>();


	/**
	 * The list policy used for datagrid
	 */
	private List<PolicyAuth> initialPolicies;

	/**
	 * Construct a new PolicyDataProvider
	 */
	private PolicyDataProvider() {

	}

	/**
	 * Add a display to the database. The current range of interest of the display
	 * will be populated with data.
	 * 
	 * @param display a {@Link HasData}.
	 */
	public void addDataDisplay(HasData<PolicyAuth> display) {
		dataProvider.addDataDisplay(display);
	}

	public ListDataProvider<PolicyAuth> getDataProvider() {
		return dataProvider;
	}

	/**
	 * Load list policy provider
	 * @param listResultPolicy
	 */
	public void loadPolicyProvider(List<PolicyAuth> listResultPolicy) {
		initialPolicies = listResultPolicy;
		//load list policy from servlet	
		List<PolicyAuth> policies = dataProvider.getList();
		policies.removeAll(policies);
		for (PolicyAuth policy : listResultPolicy){
			policies.add(policy);
		}
	}

	/***
	 * Reset a provider
	 */
	public void resetPolicyProvider(){
		List<PolicyAuth> policies = dataProvider.getList();
		policies.clear();
	}

	/**
	 * Refresh all displays.
	 */
	public void refreshDisplays() {
		dataProvider.refresh();
	}

	/**
	 * Method for remove policy from provider
	 * @param idpolicy
	 */
	public void removePolicyProvider(Long idpolicy) {
		// TODO Auto-generated method stub
		List<PolicyAuth> policies = dataProvider.getList();
		for (int i=0; i<policies.size(); i++ )
			if (policies.get(i).getIdpolicy()==(idpolicy)){
				policies.remove(i);
				initialPolicies.remove(i);
				break;
			}
	}

	/**
	 * Method for add a policy into provider
	 * @param policy
	 */
	public  void addPolicyProvider(PolicyAuth policy) {
		// TODO Auto-generated method stub
		List<PolicyAuth> policies = dataProvider.getList();
		policies.add(policy);
		initialPolicies.add(policy);
		dataProvider.setList(policies);

	}
	/**
	 * Used for research a string filter
	 * @return
	 */
	public List<String> getInitialSearch() {
		return initialSearch;
	}

	/**
	 * Refresh list from a list search
	 */
	public void refreshlistFromSearch(String typeSearch){
		List<PolicyAuth> policies = new	ArrayList<PolicyAuth>();
		Collections.copy(policies, initialPolicies);
		List<PolicyAuth> toRemove = new ArrayList<PolicyAuth>(policies.size());
		for (int index=0; index<policies.size(); index++ ){
			for(String stringSearch: initialSearch) {
				String ricerca=null;
				if (stringSearch.substring(0, 1).equals(ConstantsSharing.TagCaller)){
					stringSearch=stringSearch.substring(1);
					ricerca=policies.get(index).getCallerAsString();
				}	
				else if (stringSearch.substring(0, 1).equals(ConstantsSharing.TagService)){
					ricerca=policies.get(index).getServiceAsString();
					stringSearch=stringSearch.substring(1);
				}
				else if (stringSearch.substring(0, 1).equals(ConstantsSharing.TagAccess)){
					stringSearch=stringSearch.substring(1);
					ricerca=policies.get(index).getAccessString();
				}
				else
					ricerca=policies.get(index).getCallerAsString();

				if (typeSearch.equals("contains")){
					if(!ricerca.toLowerCase().contains(stringSearch.toLowerCase())){ 
						toRemove.add(policies.get(index));
					}
				}
				else{
					if(!ricerca.toLowerCase().startsWith(stringSearch.toLowerCase())){ 
						toRemove.add(policies.get(index));
					}
				}
			}
		}
		policies.removeAll(toRemove);
		dataProvider.setList(policies);  
	}


	/**
	 * Used for add a List string filter
	 * @param initialSearch
	 */
	public void setInitialSearch(List<String> initialSearch) {
		this.initialSearch = initialSearch;
	}

	/**
	 * Used for add a string search
	 * @param search
	 */
	public void setAddStringSearch(String search){
		this.initialSearch.add(search);
	}
	/**
	 * Used for remove string search
	 * @param filter
	 */
	public void removeStringSearch(String filter) {
		// TODO Auto-generated method stub
		this.initialSearch.remove(filter);

	}

	/**
	 * Used for remove all string search 
	 */
	public void removeAllStringSearch() {
		// TODO Auto-generated method stub
		this.initialSearch.clear();

	}

	/**
	 * Used for insert a filter button 
	 * @param typefilter
	 */
	public void setFilterList(String typefilter) {

		// TODO Auto-generated method stub
		List<PolicyAuth> policiesFilter = new	ArrayList<PolicyAuth>();
		if (typefilter.isEmpty()){
			Collections.copy(policiesFilter, initialPolicies);
		}
		else{
			for (int index=0; index<initialPolicies.size(); index++ ){
				GWT.log("initialPolicies:"+initialPolicies.get(index).getCallerTypeAsString().trim()+" typefilter:"+typefilter);

				if (initialPolicies.get(index).getCallerTypeAsString().trim().equalsIgnoreCase(typefilter))					
					policiesFilter.add(initialPolicies.get(index));
			}
		}
		dataProvider.setList(policiesFilter); 
	}
}