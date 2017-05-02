package org.gcube.data.spd.gbifplugin.search.query;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class QueryCondition{
	
	public static QueryCondition cond(String key, String value){
		return new QueryCondition(key, value);
	}
	
	@Getter
	private @NonNull String key;
	@Getter
	private @NonNull String value;
	
}
