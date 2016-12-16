package org.gcube.data.analysis.tabulardata.service.tabular.metadata;

import org.gcube.data.analysis.tabulardata.metadata.Metadata;

public interface TabularResourceMetadata<T> extends Metadata {

	void setValue(T value);
	
	T getValue();

}
