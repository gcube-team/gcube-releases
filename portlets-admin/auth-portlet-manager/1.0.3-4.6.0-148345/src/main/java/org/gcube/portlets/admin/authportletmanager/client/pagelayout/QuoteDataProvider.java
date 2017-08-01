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

import org.gcube.portlets.admin.authportletmanager.shared.ConstantsSharing;
import org.gcube.portlets.admin.authportletmanager.shared.Quote;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

/**
 * The data source for contact information used in the sample.
 */
public class QuoteDataProvider {



	/**
	 * The singleton instance of the database.
	 */
	private static QuoteDataProvider instance;

	/**
	 * Get the singleton instance of the contact database.
	 * 
	 * @return the singleton instance
	 */
	public static QuoteDataProvider get() {
		if (instance == null) {
			instance = new QuoteDataProvider();
		}
		return instance;
	}

	/**
	 * The provider that holds the list of contacts in the database.
	 */
	private ListDataProvider<Quote> dataProvider = new ListDataProvider<Quote>();


	/**
	 * The list string used for search
	 */
	private List<String> initialSearch= new ArrayList<String>();;


	/**
	 * The list quote used for datagrid
	 */
	private List<Quote> initialQuote;



	/**
	 * Construct a new QuoteDataProvider
	 */
	private QuoteDataProvider() {
	}

	/**
	 * Add a display to the database. The current range of interest of the display
	 * will be populated with data.
	 * 
	 * @param display a {@Link HasData}.
	 */
	public void addDataDisplay(HasData<Quote> display) {
		dataProvider.addDataDisplay(display);
	}

	public ListDataProvider<Quote> getDataProvider() {
		return dataProvider;
	}

	/**
	 * Load list quote provider
	 * @param listResultQuote
	 */
	public void loadQuoteProvider(List<Quote> listResultQuote) {
		initialQuote = listResultQuote;
		//load list quote from servlet	
		List<Quote> quote = dataProvider.getList();
		quote.removeAll(quote);
		for (Quote quota : listResultQuote){
			quote.add(quota);
		}
	}

	/***
	 * Reset a provider
	 */
	public void resetQuoteProvider(){
		List<Quote> quote = dataProvider.getList();
		quote.clear();
	}

	/**
	 * Refresh all displays.
	 */
	public void refreshDisplays() {
		dataProvider.refresh();
	}

	/**
	 * Method for remove quote from provider
	 * @param idquote
	 */
	public void removeQuoteProvider(Long idquote) {
		// TODO Auto-generated method stub
		List<Quote> quote = dataProvider.getList();
		for (int i=0; i<quote.size(); i++ ){
			if (quote.get(i).getIdQuote().equals(idquote)){
				quote.remove(i);
				initialQuote.remove(i);
				break;
			}
		}
			
	}

	/**
	 * Method for add a quote into provider
	 * @param quota
	 */
	public  void addQuoteProvider(Quote quota) {
		// TODO Auto-generated method stub
		List<Quote> quote = dataProvider.getList();
		quote.add(quota);
		initialQuote.add(quota);
		dataProvider.setList(quote);
		
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
		List<Quote> quote = new	ArrayList<Quote>();
		Collections.copy(quote, initialQuote);
		List<Quote> toRemove = new ArrayList<Quote>(quote.size());
		for (int index=0; index<quote.size(); index++ ){
			for(String stringSearch: initialSearch) {
				String ricerca=null;
				
				
				if (stringSearch.substring(0, 1).equals(ConstantsSharing.TagCaller)){
					stringSearch=stringSearch.substring(1);
					ricerca=quote.get(index).getCallerAsString();
				}	
				else if (stringSearch.substring(0, 1).equals(ConstantsSharing.TagTime)){
					ricerca=quote.get(index).getTimeInterval().toString();
					stringSearch=stringSearch.substring(1);
				}
				else if (stringSearch.substring(0, 1).equals(ConstantsSharing.TagType)){
					stringSearch=stringSearch.substring(1);
					ricerca=quote.get(index).getManager().toString();
				}
				else
					ricerca=quote.get(index).getCallerAsString();
				
				if (typeSearch.equals("contains")){
					if(!ricerca.toLowerCase().contains(stringSearch.toLowerCase())){ 
						toRemove.add(quote.get(index));
					}
				}
				else{
					if(!ricerca.toLowerCase().startsWith(stringSearch.toLowerCase())){ 
						toRemove.add(quote.get(index));
					}
				}
				
				
			}
		}
		quote.removeAll(toRemove);
		dataProvider.setList(quote);  
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
		List<Quote> quoteFilter = new	ArrayList<Quote>();
		if (typefilter.isEmpty()){
			Collections.copy(quoteFilter, initialQuote);
		}
		else{
			for (int index=0; index<initialQuote.size(); index++ ){
				if (initialQuote.get(index).getCallerTypeAsString().trim().equalsIgnoreCase(typefilter))					
					quoteFilter.add(initialQuote.get(index));
			}
		}
		dataProvider.setList(quoteFilter); 
	}
}