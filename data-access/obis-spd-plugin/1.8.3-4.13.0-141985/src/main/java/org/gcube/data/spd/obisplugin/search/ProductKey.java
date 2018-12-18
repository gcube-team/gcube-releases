package org.gcube.data.spd.obisplugin.search;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.gcube.data.spd.obisplugin.search.query.QueryCondition;

@AllArgsConstructor
@Getter
public class ProductKey {

	private List<QueryCondition> queryCondition;	
	
}
