package org.gcube.data.spd.gbifplugin.search.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class PagedQueryObject {

	private @NonNull String baseUri;
			
	@Getter
	List<QueryCondition> conditions = new ArrayList<QueryCondition>();
	
	private @NonNull ResultType resultType;
	
	private @NonNull Integer resultPerQuery;
	
	private int offset = 0;
	
	public void setConditions(QueryCondition ... conditions){
		this.conditions = Arrays.asList(conditions);
	}
	
	
	
	public String buildNext(){
		StringBuilder query = new StringBuilder(baseUri);
		if (!baseUri.endsWith("/")) query.append("/");
		query.append(this.resultType.getQueryEntry()).append("/");
		query.append("search/?limit=").append(resultPerQuery);
		query.append("&offset=").append(offset);

		if (conditions.size()>0)
			for (QueryCondition queryCond: conditions)
				query.append("&").append(queryCond.getKey()).append("=").append(queryCond.getValue());
		offset = offset+resultPerQuery;
		return query.toString();
	}
		
	
}
