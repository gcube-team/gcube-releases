package org.gcube.portlets.user.tdwx.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class RequestData implements Serializable {

	private static final long serialVersionUID = -7854462973039253712L;

	private String offset;
	private String limit;
	private ArrayList<SortInformation> sorts;
	private ArrayList<FilterInformation> filters;
	private ArrayList<StaticFilterInformation> staticFilters;
	
	public RequestData() {
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public ArrayList<SortInformation> getSorts() {
		return sorts;
	}

	public void setSorts(ArrayList<SortInformation> sorts) {
		this.sorts = sorts;
	}

	public ArrayList<FilterInformation> getFilters() {
		return filters;
	}

	public void setFilters(ArrayList<FilterInformation> filters) {
		this.filters = filters;
	}

	
	public ArrayList<StaticFilterInformation> getStaticFilters() {
		return staticFilters;
	}

	public void setStaticFilters(ArrayList<StaticFilterInformation> staticFilters) {
		this.staticFilters = staticFilters;
	}

	@Override
	public String toString() {
		return "RequestData [offset=" + offset + ", limit=" + limit
				+ ", sorts=" + sorts + ", filters=" + filters 
				+ ", staticFilters=" + staticFilters +"]";
	}
	
	
	public String toJsonObject(){
		StringBuilder json=new StringBuilder();
		json.append("{"+
			   " \"limit\": \""+limit+"\","+
			   " \"offset\": \""+offset+"\"");
		
		if(sorts!=null && !sorts.isEmpty()){
			json.append(",");
			json.append(" \"sorts\": [");
			boolean first=true;
			for(SortInformation sort:sorts){
				if(first){ 
					first=false; 
				} else{
					json.append(",");
				}
				json.append("{ \"sortDir\":\""+sort.getSortDir()+"\" ," +
						"\"sortField\": \""+sort.getSortField()+"\" }");
				
			}
			json.append("]");
		}
		
		if(filters!=null && !filters.isEmpty()){
			json.append(",");
			json.append(" \"filters\": [");
			boolean first=true;
			for(FilterInformation filter:filters){
				if(first){ 
					first=false; 
				} else{
					json.append(",");
				}
				json.append("{ \"filterField\":\""+filter.getFilterField()+"\" ," +
						"\"filterType\":\""+filter.getFilterType()+"\" ," +
						"\"filterComparison\":\""+filter.getFilterComparison()+"\" ," +
						"\"filterValue\": \""+filter.getFilterValue()+"\" }");
				
			}
			json.append("]");
		}
		
		if(staticFilters!=null && !staticFilters.isEmpty()){
			json.append(",");
			json.append(" \"staticFilters\": [");
			boolean first=true;
			for(StaticFilterInformation staticFilter:staticFilters){
				if(first){ 
					first=false; 
				} else{
					json.append(",");
				}
				json.append("{ \"columnName\":\""+staticFilter.getColumnName()+"\" ," +
						"\"columnLocalId\":\""+staticFilter.getColumnLocalId()+"\" ," +
						"\"filterValue\": \""+staticFilter.getFilterValue()+"\" }");
				
			}
			json.append("]");
		}
		
		
		
		json.append("}");
			 
		
		/*
		 * { "limit": "1000",
		 *   "offset": "330",
		 *   "filters": [
		 *  	 { "filterField":"kqljyp" ,"filterType":"date" ,"filterComparison":"on" ,"filterValue": "1399370400000" },	
		 *    	 { "filterField":"kqasdp" ,"filterType":"date" ,"filterComparison":"on" ,"filterValue": "1399370400000" },	
		 *    	],
		 *    "staticFilter: ["
		 *    	 { "columnName:"kdsafd" ,"columnLocalId":"dsafadfacaseasdsafafe","filterValue":"10" }
		 *    	]
		 * } 	
		*/			  
		
		
		
		return json.toString();
	}

}
