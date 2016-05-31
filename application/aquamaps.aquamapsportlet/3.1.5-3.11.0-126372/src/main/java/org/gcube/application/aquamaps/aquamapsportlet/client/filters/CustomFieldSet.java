package org.gcube.application.aquamaps.aquamapsportlet.client.filters;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsportlet.client.rpc.data.ClientFilter;

import com.gwtext.client.widgets.form.FieldSet;

public abstract class CustomFieldSet extends FieldSet {

	public abstract List<ClientFilter> getFilter() throws Exception;
	
}
