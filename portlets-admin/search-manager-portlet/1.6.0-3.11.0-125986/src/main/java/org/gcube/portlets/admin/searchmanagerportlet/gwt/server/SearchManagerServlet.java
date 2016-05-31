package org.gcube.portlets.admin.searchmanagerportlet.gwt.server;

import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.bridge.RegistryBridge;
import gr.uoa.di.madgik.rr.element.config.StaticConfiguration;
import gr.uoa.di.madgik.rr.element.data.DataCollection;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;
import gr.uoa.di.madgik.rr.element.search.Field;
import gr.uoa.di.madgik.rr.element.search.Presentable;
import gr.uoa.di.madgik.rr.element.search.Searchable;
import gr.uoa.di.madgik.rr.element.search.index.DataSource;
import gr.uoa.di.madgik.rr.element.search.index.FTIndex;
import gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainer;
import gr.uoa.di.madgik.rr.element.search.index.FieldIndexContainer.FieldType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions.CommunicationFailureException;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions.DeleteFieldFailureException;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions.FieldsRetrievalFailureException;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions.SearchableFieldInfoMissingException;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.exceptions.StoreFieldFailureException;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.client.interfaces.SearchManagerService;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.CollectionFieldsBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.CollectionInfoBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.FieldInfoBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.PresentableFieldInfoBean;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.RecipientTypeConstants;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.SMConstants;
import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.SearchableFieldInfoBean;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The server side implementation of the RPC service.
 */
public class SearchManagerServlet extends RemoteServiceServlet implements SearchManagerService {

	private static final long serialVersionUID = -5809760334596481925L;

	/** Logger */
	private static Logger logger = Logger.getLogger(SearchManagerServlet.class);


	/**
	 * 
	 */
	public SearchManagerServlet() {
		try {
			logger.debug("initializing servlet and starting RR bridging");
			ResourceRegistry.startBridging();
		} catch (Exception e) {
			logger.error("Servlet failed to initialize", e);
		}
	}

	private ASLSession getASLsession() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
		ASLSession session = SessionManager.getInstance().getASLSession(httpSession.getId(), username);
		return session;
	}

	/**
	 * This method returns all the existing Fields
	 * @return A List with the existing fields
	 * 
	 */
	public List<FieldInfoBean> getFieldsInfo(boolean loadDetails) throws FieldsRetrievalFailureException {
		ArrayList<FieldInfoBean> fieldsBean = new ArrayList<FieldInfoBean>();
		boolean bridgingStatus = true;
		try {
			bridgingStatus  = ResourceRegistry.isInitialBridgingComplete();
			logger.debug("Current Bridging Status......... " + bridgingStatus);
			getASLsession().setAttribute(SMConstants.BRIDGING_STATUS, new Boolean(bridgingStatus));
		} catch (ResourceRegistryException e) {
			logger.warn("Could not get the status of the registry's bridging");
		}
		if (bridgingStatus) {
			try {
				logger.debug("Trying to retrieve the available fields info..");
				// loadDetails true to load everything, else false to load only basic fields' information
				List<Field> dbFields = Field.getAll(loadDetails, getASLsession().getScopeName());
				if (dbFields != null && dbFields.size() > 0) {
					logger.debug("Fields are available");
					// For each FieldInfo
					for (Field dbf : dbFields) {
						FieldInfoBean fBean;
						// create the list with the searchable fields
						if (loadDetails) {
							ArrayList<SearchableFieldInfoBean> searchableFieldsBean = new ArrayList<SearchableFieldInfoBean>();
							Set<Searchable> dbSearchable = dbf.getSearchables();
							if (dbSearchable != null && dbSearchable.size() > 0) {
								for (Searchable dbs : dbSearchable) {
									Set<String> dbCabs = dbs.getCapabilities();
									ArrayList<String> capabilitiesBean = new ArrayList<String>(); 
									// For each search capability
									if (dbCabs != null && dbCabs.size() > 0) {
										// add the available capabilities to the bean list
										for (String dbc : dbCabs) {
											logger.debug("Cap --> " + dbc);
											capabilitiesBean.add(dbc);
										}
									}
									// add the searchable fields to the client bean list
									SearchableFieldInfoBean sBean = new SearchableFieldInfoBean(dbs.getID(), dbs.getCollection(), getCollectionNameByID(dbs.getCollection()), dbs.getLocator(), capabilitiesBean,
											dbs.getExpression(), dbs.isOrder());
									searchableFieldsBean.add(sBean);
								}
							}
							// create the list with the presentable fields
							ArrayList<PresentableFieldInfoBean> presentableFieldsBean = new ArrayList<PresentableFieldInfoBean>();
							Set<Presentable> dbPresentable = dbf.getPresentables();
							if (dbPresentable != null && dbPresentable.size() > 0) {
								for (Presentable dbp : dbPresentable) {
									// add the presentable fields to the client bean list.
									PresentableFieldInfoBean pBean = new PresentableFieldInfoBean(dbp.getID(), dbp.getCollection(), getCollectionNameByID(dbp.getCollection()), dbp.getLocator(), dbp.isOrder(), dbp.getExpression(), dbp.getPresentationInfo());
									presentableFieldsBean.add(pBean);
								}
							}

							// create a Field Info and add it to the list 
							fBean = new FieldInfoBean(dbf.getID(), dbf.getName(), dbf.getDescription(), searchableFieldsBean, presentableFieldsBean);
						}
						else {
							// create a Field Info and add it to the list without any searchables and presentables
							fBean = new FieldInfoBean(dbf.getID(), dbf.getName(), dbf.getDescription(), null, null);
						}

						if (loadDetails) {
							// Now add the available collections if any. To be used for suggestions
							// Available for Searchable
							List<FieldIndexContainer> searchableContainer = FieldIndexContainer.queryByFieldIDAndTypeAndScope(dbf.getID(), FieldType.Searchable, getASLsession().getScopeName());
							logger.debug("Quering for searchables  for the field ID -> " + dbf.getID());
							Set<CollectionInfoBean> searchableColIDs = new HashSet<CollectionInfoBean>();
							if (searchableContainer != null) {
								for (FieldIndexContainer f : searchableContainer) {
									logger.debug("Adding Searchable Collection ID --> " + f.getCollection());
									String colID = f.getCollection();
									String colName = getCollectionNameByID(colID);
									logger.debug("Collection name --> " + colName);
									CollectionInfoBean colBean = new CollectionInfoBean(colID, colName);
									searchableColIDs.add(colBean);
								}
							}
							fBean.setAvailableSearchableCollectionsIDs(searchableColIDs);
							// Available for Presentable
							List<FieldIndexContainer> presentableContainer = FieldIndexContainer.queryByFieldIDAndTypeAndScope(dbf.getID(), FieldType.Presentable, getASLsession().getScopeName());
							Set<CollectionInfoBean> presentableColIDs = new HashSet<CollectionInfoBean>();
							if (presentableContainer != null) {
								for (FieldIndexContainer f : presentableContainer) {
									logger.debug("Adding -> " + f.getCollection());
									String colID = f.getCollection();
									DataCollection colData = new DataCollection();
									colData.setID(colID);
									colData.load(true);
									String colName = colData.getName();
									CollectionInfoBean colBean = new CollectionInfoBean(colID, colName);
									presentableColIDs.add(colBean);
								}
							}
							fBean.setAvailablePresentableCollectionsIDs(presentableColIDs);
						}
						logger.debug("Adding a new field bean with name : " + fBean.getLabel());
						fieldsBean.add(fBean);
					}
				}
			} catch (ResourceRegistryException e) {
				logger.error("An exception was thrown while trying to retrieve the available fiels", e);
				throw new FieldsRetrievalFailureException(e.getMessage(), e.getCause());
			}
			Collections.sort(fieldsBean);
		}
		return fieldsBean;
	}

	public void saveAnnotations(String fieldID, ArrayList<String> annotationsToBeAdded, ArrayList<String> annotationsToBeRemoved) {
		try {

			Field f = Field.getById(true, fieldID);
			Set<Presentable> presentables = f.getPresentables();

			for (Presentable p : presentables) {
				for (String annToAdd : annotationsToBeAdded) {
					logger.debug("Adding -> " + annToAdd);
					p.getPresentationInfo().add(annToAdd);
				}
				for (String annToRemove : annotationsToBeRemoved) {
					logger.debug("Removing -> " + annToRemove);
					p.getPresentationInfo().remove(annToRemove);	
				}
			}
			f.store(true);
		} catch (ResourceRegistryException e) {
			logger.error("Failed to get the Field Info. Could not save the annotations to the Presentables of this field");
		}
	}

	public Boolean resetRegistry() {
		RegistryBridge.forceReset();
		return true;
	}

	public ArrayList<String> getFieldAnnotations(String fieldID) {
		ArrayList<String> annotations = new ArrayList<String>();

		try {
			Field f = Field.getById(true, fieldID);
			Set<String> keywords = QueryHelper.getPresentationInfoOfField(f);
			ArrayList<String> semanticAnnotations = getSemanticAnnotations();
			for (String keyword : keywords) {
				if (semanticAnnotations.contains(keyword)) {
					annotations.add(keyword);
				}
			}
		} catch (ResourceRegistryException e) {
			logger.error("Failed to get field's semantic annnotations", e);
		}

		return annotations;
	}

	public ArrayList<String> getSemanticAnnotations() {
		ArrayList<String> annotations = new ArrayList<String>();

		try {
			Set<String> semanticKeywords = StaticConfiguration.getInstance().getPresentationInfoKeywords(StaticConfiguration.SemanticGroupName);
			annotations.addAll(semanticKeywords);
		} catch (ResourceRegistryException e) {
			logger.error("Failed to retrieve the available semantic annotations", e);
		}

		return annotations;
	}

	public HashMap<String, ArrayList<String>> getGroupsAndKeywords() {
		HashMap<String, ArrayList<String>> groupsAndKeywords = new HashMap<String, ArrayList<String>>();
		try {
			StaticConfiguration sc = StaticConfiguration.getInstance();
			Set<String> groups = sc.getPresentationInfoGroups();
			for (String group : groups) {
				Set<String> keywords = StaticConfiguration.getInstance().getPresentationInfoKeywords(group);
				ArrayList<String> anns = new ArrayList<String>();
				anns.addAll(keywords);
				groupsAndKeywords.put(group, anns);
			}
		} catch (ResourceRegistryException e) {
			logger.error("Failed to retrieve the available presentation groups");
		} 	
		return groupsAndKeywords;
	}

	public ArrayList<String> getGroups() {
		ArrayList<String> groups = new ArrayList<String>();
		try {
			StaticConfiguration sc = StaticConfiguration.getInstance();
			Set<String> g = sc.getPresentationInfoGroups();
			groups.addAll(g);
			logger.debug("groups:");
			logger.debug(g.toString());
		} catch (ResourceRegistryException e) {
			logger.error("Failed to retrieve the available presentation groups");
		} 	
		return groups;
	}

	public void addKeywordToPresentationGroup(String group, String keyword) {
		try {
			StaticConfiguration sc = StaticConfiguration.getInstance();
			sc.addPresentationInfoKeyword(group, keyword);
			sc.store(false);
		} catch (ResourceRegistryException e) {
			logger.error("Failed to add a new keyword to the --> " + group + " presentation group");
		} 	
	}

	public void removeKeywordsFromPresentationGroup(String group, ArrayList<String> keywords) {
		try {
			StaticConfiguration sc = StaticConfiguration.getInstance();
			logger.debug("Removing keywords from the --> " + group + " group");
			for (String keyword : keywords) {
				sc.deletePresentationInfoKeyword(group, keyword);
				logger.debug("Deleted keyword --> " + keyword);
			}
			sc.store(false);
		} catch (ResourceRegistryException e) {
			logger.error("Failed to remove the keyword", e);
		} 	
	}

	public Boolean getBridgingStatusFromSession() {
		return (Boolean)(getASLsession().getAttribute(SMConstants.BRIDGING_STATUS));

	}

	public ArrayList<String> getIndexLocatorList(String fieldID, String collectionID, String type) throws CommunicationFailureException {
		ArrayList<String> locators = new ArrayList<String>();
		FieldType fType = FieldType.Presentable;
		if (type.equals(SMConstants.SEARCHABLE))
			fType = FieldType.Searchable;
		try {
			logger.debug("Quering for field ID -> " + fieldID + " and collection ID -> " + collectionID);
			List<DataSource> fc = FTIndex.queryByFieldIDAndTypeAndCollectionAndScope(true, fieldID, collectionID, fType, getASLsession().getScopeName());
			if (fc != null) {
				for (DataSource f : fc) {
					logger.debug("Adding source locator ->" + f.getID());
					locators.add(f.getID());
				}
			}
		} catch (ResourceRegistryException e) {
			logger.error("Error while trying to get the available index locators for the collection with ID: " + collectionID);
			throw new CommunicationFailureException(e.getMessage(), e.getCause());
		}
		return locators;
	}

	public Set<String> getIndexCapabilities(String indexLocator) throws CommunicationFailureException {
		Set<String> caps = null;
		try {
			DataSource ds = DataSource.getById(true, indexLocator);
			if (ds != null) {
				caps  = ds.getCapabilities();
			}
		} catch (ResourceRegistryException e) {
			logger.error("Failed to get The index capabilities (Index ID: " + indexLocator + ")");
			throw new CommunicationFailureException(e.getMessage(), e.getCause());
		}
		return caps;
	}

	/**
	 * This method creates a new FieldInfo 
	 * The ID will be auto-generated by the library
	 * 
	 * @param field The FieldInfo information
	 * @throws SearchableFieldInfoMissingException 
	 * @throws StoreFieldFailureException 
	 * @throws CommunicationFailureException 
	 */
	public FieldInfoBean createField(FieldInfoBean field, boolean isUpdated) throws SearchableFieldInfoMissingException, StoreFieldFailureException, CommunicationFailureException {
		try {
			Field dbField = new Field();

			if (field.getDescription() != null) {
				dbField.setDescription(field.getDescription());
			}
			if (field.getLabel() != null)
				dbField.setName(field.getLabel());

			if (isUpdated && field.getID().length() > 0)
				dbField.setID(field.getID());

			// This is for the returned object
			field.setID(dbField.getID());

			ArrayList<SearchableFieldInfoBean> searchableInfoBean = field.getSearchableFields();
			//Create the searchable info list
			if (searchableInfoBean != null && searchableInfoBean.size() > 0) {
				for (SearchableFieldInfoBean sBean : searchableInfoBean) {
					if (sBean.getCollectionID() != null) {
						logger.debug("Searchable --> " + sBean.getCollectionName());
						Searchable dbs = new Searchable();

						// Add the capabilities
						ArrayList<String> capabilitiesBean = sBean.getIndexCapabilities();
						if (capabilitiesBean != null && capabilitiesBean.size() > 0) {
							for (String cap : capabilitiesBean) {
								logger.debug("Adding capability --> " + cap);
								dbs.getCapabilities().add(cap);
							}
						}

						dbs.setCollection(sBean.getCollectionID());
						dbs.setLocator(sBean.getSourceLocator());
						if (isUpdated && sBean.getID() != null && sBean.getID().length() > 0)
							dbs.setID(sBean.getID());
						dbs.setField(dbField.getID());
						dbs.setOrder(sBean.isSortable());
						dbs.setExpression(sBean.getIndexQueryLanguage());

						dbField.getSearchables().add(dbs);
						// for the returned object
						sBean.setID(dbs.getID());
					}
				}
			}

			ArrayList<PresentableFieldInfoBean> presentableInfoBean = field.getPresentableFields();
			// Create the presentable info list. If no presentable fields exist then an empty list will be passed to the FieldInfo
			if (presentableInfoBean != null && presentableInfoBean.size() > 0) {
				for (PresentableFieldInfoBean pBean : presentableInfoBean) {
					if (pBean.getCollectionID() != null) {
						Presentable dbp = new Presentable();
						dbp.setCollection(pBean.getCollectionID());
						dbp.setLocator(pBean.getSourceLocator());
						dbp.setOrder(pBean.isSortable());
						dbp.setExpression(pBean.getQueryExpression());
						dbp.setPresentationInfo(pBean.getPresentationInfo());
						if (isUpdated && pBean.getID() != null && pBean.getID().length() > 0)
							dbp.setID(pBean.getID());

						dbp.setField(dbField.getID());
						dbField.getPresentables().add(dbp);

						// for the returned object
						pBean.setID(dbp.getID());
					}
				}
			}
			// STORE the field
			dbField.store(true, getASLsession().getScopeName());
			// If a new field is stored then invoke the setDirty
			if (!isUpdated)
				ResourceRegistry.setDirty();
			return field;
		} catch (ResourceRegistryException e) {
			logger.error("Failed to create a new FieldInfo. An exception was thrown", e);
			throw new CommunicationFailureException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * This method deletes the Field with the given ID
	 * @param fieldID The ID of the field to be deleted
	 * 
	 */
	public void deleteFieldInfo(String fieldID) throws DeleteFieldFailureException {
		Field field;
		try {
			logger.debug("Going to delete field with ID --> " + fieldID);
			field = new Field();
			field.setID(fieldID);
			field.delete(true, getASLsession().getScopeName());
		} catch (ResourceRegistryException e) {
			logger.debug("An exception is thrown while trying to delete the field", e);
			throw new DeleteFieldFailureException(e.getMessage(), e.getCause());
		}
	}

	public void sendEmailWithErrorToSupport(Throwable caught) {
		String subject = "[PORTAL-iMarine] Fields Management Portlet - Error Notification";
		String rec[] = new String[1];
		rec[0] = "support_team@d4science.org";
		String senderEmail;
		try {
			senderEmail = "no-reply@imarine.research-infrastructures.eu";
			ErrorNotificationEmailMessageTemplate msgTemp = new ErrorNotificationEmailMessageTemplate(caught, getASLsession().getUsername());
			EmailNotification emailNot = new EmailNotification(senderEmail, rec, subject, msgTemp.createBodyMessage(), RecipientTypeConstants.EMAIL_TO, false);
			emailNot.sendEmail();
		} catch (Exception e) {
			logger.error("Failed to send the email to the support team.", e);
		} 
	}

	public ArrayList<CollectionFieldsBean> getCollectionAndFieldsInfo() {
		ArrayList<CollectionFieldsBean> collectionsAndFieldsBean = new ArrayList<CollectionFieldsBean>();
		try {
			List<DataCollection> collections = DataCollection
					.getCollectionsOfScope(true, getASLsession().getScopeName());
			if (collections != null) {
				// For each collection
				for (DataCollection col : collections) {
					List<Field> searchableFields = Field.getSearchableFieldsOfCollection(true, col.getID());
					List<Field> presentableFields = Field.getPresentableFieldsOfCollection(true, col.getID());

					// Create the bean and set the searchable and presentables
					CollectionFieldsBean colFieldBean = new CollectionFieldsBean(col.getID(), col.getName());
					ArrayList<String> searchableFieldBean = new ArrayList<String>();
					for (Field f : searchableFields) {
						String fieldDesc = f.getName() + " (" + f.getID() + ")";
						searchableFieldBean.add(fieldDesc);
					}
					colFieldBean.setSearchableFields(searchableFieldBean);

					ArrayList<String> presentableFieldBean = new ArrayList<String>();
					for (Field f : presentableFields) {
						String fieldDesc = f.getName() + " (" + f.getID() + ")";
						presentableFieldBean.add(fieldDesc);
					}
					colFieldBean.setPresentableFields(presentableFieldBean);
					// Add the collection Bean to the list
					collectionsAndFieldsBean.add(colFieldBean);
				}
			}
		} catch (Exception e) {
			logger.error("An exception was thrown. Failed to get the available colllections and their fields");
		}
		return collectionsAndFieldsBean;
	}

	private String getCollectionNameByID(String id) throws ResourceRegistryException {
		DataCollection colData = new DataCollection();
		colData.setID(id);
		colData.load(true);
		String colName = colData.getName();
		return colName;
	}
}
