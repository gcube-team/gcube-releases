package org.gcube.portlets.admin.policydefinition.vaadin.containers;

import java.io.Serializable;

import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

public class ServicesQueryFactory implements QueryFactory, Serializable {

	private static final long serialVersionUID = -8793912201962583844L;
	private QueryDefinition definition;
    
    @Override
    public void setQueryDefinition(QueryDefinition definition) {
            this.definition=definition;
    }
    
    @Override
    public Query constructQuery(Object[] sortPropertyIds, boolean[] sortStates) {
            return new ServicesQuery(definition,sortPropertyIds,sortStates);
    }

}
