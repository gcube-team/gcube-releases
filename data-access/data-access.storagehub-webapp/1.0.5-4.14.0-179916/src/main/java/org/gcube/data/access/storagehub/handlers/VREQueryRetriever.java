package org.gcube.data.access.storagehub.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventJournal;
import javax.jcr.query.Query;

import org.gcube.common.storagehub.model.Excludes;
import org.gcube.common.storagehub.model.NodeConstants;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.data.access.storagehub.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VREQueryRetriever implements Callable<List<Item>> {

	private static final Logger logger = LoggerFactory.getLogger(VREQueryRetriever.class); 

	private static final int CACHE_DIMENSION = 50;
		
	private Repository repository;
	private Credentials credentials;
	private Item vreFolder;
	List<Item> cachedList = new ArrayList<>(CACHE_DIMENSION);
	long lastTimestamp =0;

	private Node2ItemConverter node2Item = new Node2ItemConverter();
	

	public VREQueryRetriever(Repository repository, Credentials credentials, Item vreFolder) {
		super();
		this.repository = repository;
		this.credentials = credentials;
		this.vreFolder = vreFolder;
	}

	public List<Item> call() {
		logger.trace("executing recents task");
		Session ses = null;
		if (lastTimestamp==0) {
			try {
				long start = System.currentTimeMillis();
				ses = repository.login(credentials);
				String query = String.format("SELECT * FROM [nthl:workspaceLeafItem] AS node WHERE ISDESCENDANTNODE('%s') ORDER BY node.[jcr:lastModified] DESC ",vreFolder.getPath());
				logger.trace("query for recents is {}",query);
				Query jcrQuery = ses.getWorkspace().getQueryManager().createQuery(query, Constants.QUERY_LANGUAGE);
				jcrQuery.setLimit(CACHE_DIMENSION);
				lastTimestamp = System.currentTimeMillis();
				NodeIterator it =  jcrQuery.execute().getNodes();
				logger.trace("query for recents took {}",System.currentTimeMillis()-start);
				while (it.hasNext()) {
					Node node = it.nextNode();
					Item item =node2Item.getItem(node, Excludes.EXCLUDE_ACCOUNTING);
					cachedList.add(item);
					logger.trace("adding item {} with node {}",item.getTitle(), node.getName());
				}
				logger.trace("creating objects took {}",System.currentTimeMillis()-start);
				if (cachedList.size()<=10) return cachedList;
				else return cachedList.subList(0, 10);
			} catch (Exception e) {
				logger.error("error querying vre {}",vreFolder.getTitle(),e);
				throw new RuntimeException(e);
			}finally{
				if (ses!=null)
					ses.logout();
				logger.trace("recents task finished");
			}
		} else {
			try {
				
				long timestampToUse = lastTimestamp;
				lastTimestamp = System.currentTimeMillis();

				long start = System.currentTimeMillis();
				ses = repository.login(credentials);
				final String[] types = { "nthl:workspaceLeafItem", "nthl:workspaceItem"};

				EventJournal journalChanged = ses.getWorkspace().getObservationManager().getEventJournal(Event.PROPERTY_CHANGED^Event.NODE_REMOVED^Event.NODE_MOVED^Event.NODE_ADDED, vreFolder.getPath(), true, null, types);
				journalChanged.skipTo(timestampToUse);
				
				logger.trace("getting the journal took {}",System.currentTimeMillis()-start);
				
				int events = 0;
				
				while (journalChanged.hasNext()) {
					events++;
					Event event = journalChanged.nextEvent();
					switch(event.getType()) {
					
					case Event.NODE_ADDED:
						if (ses.nodeExists(event.getPath())) {
							Node nodeAdded = ses.getNode(event.getPath());
							if (nodeAdded.isNodeType("nthl:workspaceLeafItem")) {
								logger.trace("node added event received with name {}", nodeAdded.getName());
								Item item = node2Item.getItem(nodeAdded, Arrays.asList(NodeConstants.ACCOUNTING_NAME));
								insertItemInTheRightPlace(item);
							}
						}
						break;
					
					case Event.PROPERTY_CHANGED:
						if (ses.propertyExists(event.getPath())) {
							Property property = ses.getProperty(event.getPath());
							if (property.getName().equalsIgnoreCase("jcr:lastModified")) {
								logger.trace("event property changed on {} with value {} and parent {}",property.getName(), property.getValue().getString(), property.getParent().getPath());
								String identifier = property.getParent().getIdentifier();
								cachedList.removeIf(i -> i.getId().equals(identifier));
								Item item = node2Item.getItem(property.getParent(), Excludes.EXCLUDE_ACCOUNTING);
								insertItemInTheRightPlace(item);
							}
						}
						break;
					case Event.NODE_REMOVED:
						logger.trace("node removed event received with type {}", event.getIdentifier());
						cachedList.removeIf(i -> {
							try {
								return i.getId().equals(event.getIdentifier()) && i.getLastModificationTime().getTime().getTime()<event.getDate();
							} catch (RepositoryException e) {
								return false;
							}
						});

						break;
					case Event.NODE_MOVED:
						Node nodeMoved = ses.getNode(event.getPath());
						logger.trace("node moved event received with type {}", nodeMoved.getPrimaryNodeType());
						if (nodeMoved.isNodeType("nthl:workspaceLeafItem")) {
							logger.trace("event node moved on {} with path {}",nodeMoved.getName(), nodeMoved.getPath());
							String identifier = nodeMoved.getIdentifier();
							cachedList.removeIf(i -> i.getId().equals(identifier) && !i.getPath().startsWith(vreFolder.getPath()));
						}
						break;
					default:
						throw new Exception("error in event handling");	
					}

				}

				if (cachedList.size()>CACHE_DIMENSION)
					cachedList.subList(51, cachedList.size()).clear();
				logger.trace("retrieving event took {} with {} events",System.currentTimeMillis()-start, events);
				if (cachedList.size()<=10) return cachedList;
				else return cachedList.subList(0, 10);
			} catch (Exception e) {
				logger.error("error getting events for vre {}",vreFolder.getTitle(),e);
				throw new RuntimeException(e);
			}finally{
				if (ses!=null)
					ses.logout();
			}
		}

	}

	private void insertItemInTheRightPlace(Item item) {
		Iterator<Item> it = cachedList.iterator();
		int index =0;
		while (it.hasNext()) {
			Item inListItem = it.next();
			if (item.getLastModificationTime().getTime().getTime()>=inListItem.getLastModificationTime().getTime().getTime()) break;
			index++;
		}
		if (index<CACHE_DIMENSION) 
			cachedList.add(index, item);
	}

	/*	@Override
	public void onEvent(EventIterator events) {
		logger.trace("on event called");
		while (events.hasNext()) {
			Event event = events.nextEvent();
			try {
				logger.trace("new event received of type {} on node {}",event.getType(),event.getIdentifier());
			} catch (RepositoryException e) {
				logger.error("error reading event",e);
			}
		}
	}*/

}