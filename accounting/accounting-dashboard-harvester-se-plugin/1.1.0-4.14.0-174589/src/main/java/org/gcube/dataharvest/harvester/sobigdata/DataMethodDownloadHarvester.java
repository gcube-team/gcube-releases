package org.gcube.dataharvest.harvester.sobigdata;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.lang.Validate;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceItem;
import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.gcube.dataharvest.datamodel.HarvestedDataKey;
import org.gcube.dataharvest.utils.DateUtils;
import org.gcube.dataharvest.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DataMethodDownloadHarvester.
 *
 * @author Eric Perrone (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 * @author Francesco Mangiacrapa (ISTI - CNR)
 */
public class DataMethodDownloadHarvester extends SoBigDataHarvester {
	
	private static Logger logger = LoggerFactory.getLogger(DataMethodDownloadHarvester.class);
	
	private int count = 0;
	
	/**
	 * Instantiates a new data method download harvester.
	 *
	 * @param start the start
	 * @param end the end
	 * @param catalogueContext the catalogue context
	 * @param contexts the contexts
	 * @throws ParseException the parse exception
	 */
	public DataMethodDownloadHarvester(Date start, Date end, SortedSet<String> contexts) throws Exception {
		super(start, end, contexts);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.dataharvest.harvester.BasicHarvester#getData()
	 */
	@Override
	public List<AccountingRecord> getAccountingRecords() throws Exception {
		String defaultContext = Utils.getCurrentContext();
		logger.debug("The context is {}", defaultContext);
		
		try {
			
			String vreName = getVRENameToHL(defaultContext);
			logger.debug("Getting VRE Name to HL from context/scope returns {} ", vreName);
			
			String user = vreName + "-Manager";
			logger.debug("Using user '{}' to getHome from HL", user);
			
			//Getting HL instance and home for VRE MANAGER
			HomeManager manager = HomeLibrary.getHomeManagerFactory().getHomeManager();
			@SuppressWarnings("deprecation")
			Home home = manager.getHome(user);
			JCRWorkspace ws = (JCRWorkspace) home.getWorkspace();
			
			String path = "/Workspace/MySpecialFolders/" + vreName;
			logger.debug("Getting item by Path {}", path);
			JCRWorkspaceItem item = (JCRWorkspaceItem) ws.getItemByPath(path);
			
			logger.debug("Analyzing {} in the period [{} to {}] starting from root {}", defaultContext,
					DateUtils.format(start), DateUtils.format(end), item.getName());
			
			
			ScopeDescriptor defaultScopeDescriptor = AccountingDataHarvesterPlugin.getScopeDescriptor();
			
			
			AccountingRecord defaultHarvesteData = new AccountingRecord(defaultScopeDescriptor, instant, getDimension(HarvestedDataKey.DATA_METHOD_DOWNLOAD), (long) count);
			logger.debug("{} : {}", defaultHarvesteData.getDimension().getId(), defaultHarvesteData.getMeasure());
						
			ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();
			
			for(WorkspaceItem children : item.getChildren()) {
				count = 0; //resettings the counter
				
				//HarvestedData harvestedData;
				
				//Getting statistics for folder
				if(children.isFolder()) {
					logger.debug("Getting statistics for folder {}", children.getName());
					getStats(children, start, end);
					
					String normalizedName = children.getName().replaceAll("[^A-Za-z0-9]", "");
					String context = mapWsFolderNameToVRE.get(normalizedName);
					//Checking if it is a VRE name to right accounting...
					if(context != null && !context.isEmpty()) {
						logger.debug("Found context '{}' matching with normalized VRE name {} ", context, normalizedName);
						
						ScopeDescriptor scopeDescriptor = AccountingDataHarvesterPlugin.getScopeDescriptor(context);
						AccountingRecord ar = new AccountingRecord(scopeDescriptor, instant, getDimension(HarvestedDataKey.DATA_METHOD_DOWNLOAD), (long) count);
						logger.debug("{} : {}", ar.getDimension().getId(), ar.getMeasure());
						accountingRecords.add(ar);
						
					} else {
						logger.debug(
								"No scope found matching the folder name {}, accounting its stats in the default context {}",
								normalizedName, defaultContext);
						//INCREASING THE DEFAULT CONTEXT COUNTER...
						defaultHarvesteData.setMeasure(defaultHarvesteData.getMeasure() + count);
						logger.trace("Increased default context stats {}", defaultHarvesteData);
					}
					
				}
			}
			
			//ADDING DEFAULT ACCOUNTING
			accountingRecords.add(defaultHarvesteData);
			
			
			logger.debug("In the period [from {} to {} ] returning workspace accouting data {}", DateUtils.format(start),
					DateUtils.format(end), accountingRecords);
			
			return accountingRecords;
			
		} catch(Exception e) {
			throw e;
		}
		
	}
	
	/**
	 * Gets the stats.
	 *
	 * @param baseItem the base item
	 * @param start the start
	 * @param end the end
	 * @return the stats
	 * @throws InternalErrorException the internal error exception
	 */
	private void getStats(WorkspaceItem baseItem, Date start, Date end) throws InternalErrorException {
		List<? extends WorkspaceItem> children;
		if(baseItem.isFolder()) {
			children = baseItem.getChildren();
			for(WorkspaceItem child : children)
				getStats(child, start, end);
		} else {
			try {
				
				List<AccountingEntry> accounting = baseItem.getAccounting();
				for(AccountingEntry entry : accounting) {
					
					switch(entry.getEntryType()) {
						case CREATE:
						case UPDATE:
						case READ:
							Calendar calendar = entry.getDate();
							if(calendar.after(DateUtils.dateToCalendar(start))
									&& calendar.before(DateUtils.dateToCalendar(end))) {
								count++;
							}
							
							break;
						
						default:
							break;
					}
					
				}
			} catch(Exception e) {
				throw new InternalErrorException(e);
			}
		}
	}
	
	/**
	 * Gets the VRE name to HL.
	 *
	 * @param vre the vre
	 * @return the VRE name to HL
	 */
	private static String getVRENameToHL(String vre) {
		Validate.notNull(vre, "scope must be not null");
		
		String newName;
		if(vre.startsWith(JCRRepository.PATH_SEPARATOR))
			newName = vre.replace(JCRRepository.PATH_SEPARATOR, "-").substring(1);
		else
			newName = vre.replace(JCRRepository.PATH_SEPARATOR, "-");
		return newName;
	}
	
}
