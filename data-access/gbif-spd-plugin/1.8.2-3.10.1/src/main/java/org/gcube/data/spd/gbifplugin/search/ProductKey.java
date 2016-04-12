package org.gcube.data.spd.gbifplugin.search;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.gcube.data.spd.gbifplugin.search.query.QueryCondition;
import org.gcube.data.spd.model.products.DataSet;

@AllArgsConstructor
@Getter
public class ProductKey {

	private List<QueryCondition> queryCondition;
	private DataSet dataset;
	
	
	
}
