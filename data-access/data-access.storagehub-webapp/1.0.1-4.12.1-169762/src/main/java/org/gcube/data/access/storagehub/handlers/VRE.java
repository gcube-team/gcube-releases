package org.gcube.data.access.storagehub.handlers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;

import org.gcube.common.storagehub.model.items.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VRE {

	private static final Logger logger = LoggerFactory.getLogger(VRE.class);
	
	private Item vreFolder;
	private Future<List<Item>> result;
	private VREQueryRetriever vreQueryRetriever;
	private ExecutorService executor;
	
	public VRE(Item item, Repository repository, SimpleCredentials credentials, ExecutorService executor) {
		super();
		this.vreFolder = item;
		this.executor = executor;
		vreQueryRetriever = new VREQueryRetriever(repository, credentials, vreFolder);
		result = executor.submit(vreQueryRetriever);
	}
	
	public Item getVreFolder() {
		return vreFolder;
	}
	
	public synchronized List<Item> getRecents() throws Exception{
		logger.trace("getting recents");
		if (result.isDone()) {
			result = executor.submit(vreQueryRetriever);
		}
		return result.get();
	}
	
	

}



