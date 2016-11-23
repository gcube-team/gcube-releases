package org.gcube.portlets.admin.policydefinition.vaadin.containers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.portlets.admin.policydefinition.common.util.PresentationHelper;
import org.gcube.portlets.admin.policydefinition.services.informationsystem.InformationSystemClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

public class ServicesQuery implements Query, Serializable {

	private static final long serialVersionUID = -1457331065528770676L;
	private static Logger logger = LoggerFactory.getLogger(ServicesQuery.class);
	private String criteria="";
	
	public static final String SERVICE_NAME = "Service Name";
	public static final String SERVICE_CLASS = "Service Class";
    
    public ServicesQuery(QueryDefinition definition,
                            Object[] sortPropertyIds, boolean[] sortStates) {
        super();
        if(sortPropertyIds.length == 0)
        	criteria = "order by $service/Profile/ServiceName, $service/Profile/ServiceClass ";
        for(int i=0;i<sortPropertyIds.length;i++) {
            if(i==0) {
                    criteria="order by";
            } else {
                    criteria+=",";
            }
            criteria+=" $service/Profile/"+(capitalize((String)sortPropertyIds[i]));
            if(sortStates[i]) {
                    criteria+=" ascending ";
            }
            else {
                    criteria+=" descending ";                              
            }
        }
    }
    
    private static String capitalize(String word){
    	StringBuilder b = new StringBuilder(word);
    	b.replace(0, 1, b.substring(0,1).toUpperCase());
    	return b.toString();
    }

    @Override
    public Item constructItem() {
        return new BeanItem<ServiceBean>(new ServiceBean());
    }

    @Override
    public int size() {
    	int countServices = InformationSystemClient.getInstance().countServices();
    	logger.debug("Services size: "+countServices);
        return countServices;
    }

    @Override
    public List<Item> loadItems(int startIndex, int count) {
    	logger.debug("Loading "+count+" items starting from "+startIndex);
    	List<Item> items=new ArrayList<Item>();
    	List<GCoreEndpoint> retrieveServices = InformationSystemClient.getInstance().retrieveServices(startIndex, count, criteria);
    	for (GCoreEndpoint gCoreEndpoint : retrieveServices) {
			ServiceBean bean = new ServiceBean(
    				PresentationHelper.buildNameHelper(gCoreEndpoint.profile().serviceName(), gCoreEndpoint.profile().serviceClass()), 
    				gCoreEndpoint.profile().serviceName(), 
    				gCoreEndpoint.profile().serviceClass());
    		items.add(new BeanItem<ServiceBean>(bean));
		}
		return items;
    }

    @Override
    public void saveItems(List<Item> addedItems, List<Item> modifiedItems,
                    List<Item> removedItems) {
            throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean deleteAllItems() {
            throw new UnsupportedOperationException();
    }
    
}
