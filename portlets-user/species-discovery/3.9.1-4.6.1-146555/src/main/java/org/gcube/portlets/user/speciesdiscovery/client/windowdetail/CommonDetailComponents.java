package org.gcube.portlets.user.speciesdiscovery.client.windowdetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.advancedsearch.AdvancedSearchPanelManager;
import org.gcube.portlets.user.speciesdiscovery.client.event.SearchEvent;
import org.gcube.portlets.user.speciesdiscovery.client.externalsystem.OtherInformationSystemsEnum;
import org.gcube.portlets.user.speciesdiscovery.client.externalsystem.OtherMappingSystemsEnum;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.window.WindowCredits;
import org.gcube.portlets.user.speciesdiscovery.client.window.WindowOpenUrl;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.SpeciesCapability;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;

public class CommonDetailComponents {
	
	public final static String FIVEPX = "5px";
	private SearchEvent lastSearchEvent;
	private AbstractImagePrototype imageYes = AbstractImagePrototype.create(Resources.INSTANCE.getCheckYes());
	private AbstractImagePrototype imageNo = AbstractImagePrototype.create(Resources.INSTANCE.getCheckNo());
	
	CommonDetailComponents(SearchEvent lastSearchEvent){
		this.lastSearchEvent = lastSearchEvent;
	}
	
	private String getSearchType(){
			
			String searchType = "";
			
			//USED BY TEXT QUERY
			if(lastSearchEvent.getMapTermsSearched()!=null){
				
				searchType = lastSearchEvent.getMapTermsSearched().toString();
				
			}else{ 
				//USED BY SIMPLE QUERY
				switch (lastSearchEvent.getType()) {
					case BY_COMMON_NAME:
						
						searchType = ConstantsSpeciesDiscovery.BYCOMMONNAME;
						
						break;
						
					case BY_SCIENTIFIC_NAME:
						
						searchType = ConstantsSpeciesDiscovery.BYSCIENTIFICNAME;
						
						break;
				}
			}
			return searchType;
		}
	
	public String getSearchTitle(){

		
		return "<br/><br/><p style=\"font-size:18px;\"><b>" + lastSearchEvent.getSearchTerm() + "</b></p>" +
		   "<p style=\"font-family:\"Times New Roman\", Times, serif; font-size:12px; font-style:italic; \">search "+getSearchType()+"</p><br/><hr><br/>";
		
	}
	
	public String createTableWithCheckCommonNameDataSource(HashMap<String, ArrayList<String>> hashCommonNameDataSources, ArrayList<String> listDataSourceFound){
		
		
		List<String> listCommonNames = new ArrayList<String>(hashCommonNameDataSources.keySet());
		Collections.sort(listCommonNames, String.CASE_INSENSITIVE_ORDER);
		String table;
	    /*
	      To sort an ArrayList object, use Collection.sort method. This is a
	      static method. It sorts an ArrayList object's elements into ascending order.
	    */
	    Collections.sort(listDataSourceFound);
	    
	    
		//USE FOR DEBUG
//	    System.out.println("############# Common Name size " + listCommonNames.size());
//	    System.out.println("############# Data Source  size " + listDataSourceFound.size());
//	    
//	    for (String dataSource : listDataSourceFound) {
//	    	  System.out.println("############# Data Source  " + dataSource);
//		}		
		
	    if(listCommonNames.size()>0){
	    	
		    int[][] checkCommonNameDataSources = new int[listCommonNames.size()][listDataSourceFound.size()];
			
			//Create a matrix common names / data sources with 1 in coincidence values
			for (int i=0; i< listCommonNames.size(); i++) {
				
				String commonName = listCommonNames.get(i);
				
				ArrayList<String> cmDataSources = hashCommonNameDataSources.get(commonName);
				
				for (String dataSource : cmDataSources) {
//					System.out.println("dataSource " + dataSource);
					int index = listDataSourceFound.indexOf(dataSource);
//					System.out.println("i: " + i + " index " + index);
					checkCommonNameDataSources[i][index] = 1;
				}
				
			}
		
		//USE FOR DEBUG
//	    for (int i=0; i<listCommonNames.size(); i++){
//	        for (int j=0; j<listDataSourceFound.size(); j++) {
//	        	System.out.println(" ############ common name " + listCommonNames.get(i) + " i, j " + checkCommonNameDataSources[i][j]);
//	        }
//		}
		
			table = "<table class=\"simpletable\"\"><tr><th>Common names / Data Sources</th>";
			
			for (String dataSource: listDataSourceFound) {
				
				table+="<th>"+dataSource+"</th>";
			}
			
			table+="</tr>";
		    for (int i=0; i<listCommonNames.size(); i++){
		    	
		    	table+=	"<tr><td class=\"commonname\">"+listCommonNames.get(i)+"</td>";
		    	
		        for (int j=0; j<listDataSourceFound.size(); j++) {
		        	
		        	if(checkCommonNameDataSources[i][j]==1)
		        		table+="<td>"+imageYes.getHTML()+"</td>"; 
		        	else
		        		table+="<td>"+imageNo.getHTML()+"</td>"; 
		        }
		    	table+="</tr>";
			}
		    
			table+="</table>";
		}
	    else
	    	 table = "Not found";
		
		
		return table;
		
	}
	
	public VerticalPanel createIndexOfContents(List<String> listScientificName){
			
			VerticalPanel verticalPanel = new VerticalPanel();
			verticalPanel.setStyleAttribute("margin", FIVEPX);
			verticalPanel.setStyleAttribute("padding", FIVEPX);
	
			int i=0;
			
			for (final String scientificName : listScientificName) {
				
				Anchor anchor = new Anchor(++i +". "+scientificName);
				
				anchor.addClickHandler(new ClickHandler() {
				    public void onClick(ClickEvent event) {
				         com.google.gwt.user.client.Window.Location.assign("#"+scientificName);
				    }
				});
				
				verticalPanel.add(anchor);
			}
			
			return verticalPanel;
	}
	
	public VerticalPanel createPanelAbout(SpeciesCapability capability, List<String> listDataSourcesFound) {
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setStyleAttribute("margin", FIVEPX);
		verticalPanel.setStyleAttribute("padding", FIVEPX);
		
		verticalPanel.add(new Html("<p style=\"font-size:12px;\"><b> About Data Sources</b></p><br/>"));
		
		for (String dataSoruceName : listDataSourcesFound) {
			 DataSourceModel dataSource = AdvancedSearchPanelManager.getInstance().findDataSourceByCapabilityAndName(capability, dataSoruceName);
			 Anchor createAbout = createAbout(dataSource,false);
			 if(createAbout!=null)
				 verticalPanel.add(createAbout);
		}
		
		return verticalPanel;
	}
	
	public Anchor createAbout(final DataSourceModel ds, boolean insertAboutCitation)
	{
		Anchor anchor = null;

		if(ds!=null){
			String anchorValue = "";
			
			if(insertAboutCitation)
				anchorValue = "<nobr>About "+ds.getName()+"</nobr>";
			else
				anchorValue = "<nobr>"+ds.getName()+"</nobr>";
	
			anchor = new Anchor(anchorValue, true);
			
			anchor.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					ArrayList<DataSourceModel> list = new ArrayList<DataSourceModel>();
					list.add(ds);
					new WindowCredits("About "+ds.getName(), list);
					
				}
			});
		}
		
		return anchor;
	}
	
	/**
	 * 
	 * @param searchValue
	 * @return a vetical panel with external links on input
	 */
	public VerticalPanel createExternalLinks(final String searchValue){
		
		VerticalPanel vpExternalLink = new VerticalPanel();
		
		// Create Links to Other Information Systems
		Html htmlLinkOIS = new Html("<p style=\"font-size:12px;\"><b>Links to Other Information Systems</b></p>");
		VerticalPanel vpOIS = new VerticalPanel();
		vpOIS.setStyleAttribute("margin", FIVEPX);
		vpOIS.setStyleAttribute("padding", FIVEPX);
		
		
		for (final OtherInformationSystemsEnum infSystem : OtherInformationSystemsEnum.values()) {
			
			Anchor anchor = new Anchor(infSystem.getName());
			anchor.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					String url = infSystem.getBaseUrl() + searchValue + infSystem.getSuffixUrl();
					new WindowOpenUrl(url, "_blank", null);
					
				}
			});
			
			vpOIS.add(anchor);
		}

		// Create Links to Other Mapping Systems
		Html htmlLinkOMS = new Html("<p style=\"font-size:12px;\"><b>Links to Other Mapping Systems</b></p>");
		VerticalPanel vpOMS = new VerticalPanel();
		vpOMS.setStyleAttribute("margin", FIVEPX);
		vpOMS.setStyleAttribute("padding", FIVEPX);
		
		
		for (final OtherMappingSystemsEnum infSystem : OtherMappingSystemsEnum.values()) {
			
			Anchor anchor = new Anchor(infSystem.getName());
			anchor.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					String url = infSystem.getBaseUrl() + searchValue + infSystem.getSuffixUrl();
					new WindowOpenUrl(url, "_blank", null);
					
				}
			});
			
			vpOMS.add(anchor);
		}
		
		vpExternalLink.add(htmlLinkOIS);
		vpExternalLink.add(vpOIS);
		
		vpExternalLink.add(htmlLinkOMS);
		vpExternalLink.add(vpOMS);
		
		return vpExternalLink;
	}

}
