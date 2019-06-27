package org.gcube.portlets.user.dataminerexecutor.server.util;

import org.gcube.data.analysis.dataminermanagercl.server.util.ServiceCredentials;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.Computations;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.DataMinerWorkArea;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.InputDataSets;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.OutputDataSets;
import org.gcube.portlets.user.dataminerexecutor.server.storage.StorageUtil;
import org.gcube.portlets.user.dataminerexecutor.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DataMinerWorkAreaManager {
	private static final String DATA_MINER_FOLDER = "DataMiner";
	private static final String IMPORTED_DATA_FOLDER = "Input Data Sets";
	private static final String COMPUTED_DATA_FOLDER = "Output Data Sets";
	private static final String COMPUTATIONS_FOLDER = "Computations";

	public static final Logger logger = LoggerFactory.getLogger(DataMinerWorkAreaManager.class);

	private ServiceCredentials serviceCredentials;

	public DataMinerWorkAreaManager(ServiceCredentials serviceCredentials) {
		this.serviceCredentials = serviceCredentials;
	}

	public DataMinerWorkArea getDataMinerWorkArea() throws ServiceException {
		DataMinerWorkArea dataMinerWorkArea = null;
		StorageUtil storageUtil = new StorageUtil();
		try {

			ItemDescription dataMinerWorkAreaFolder = storageUtil.getItemInRootFolderOnWorkspace(serviceCredentials.getUserName(),
					DATA_MINER_FOLDER);
			if (dataMinerWorkAreaFolder == null) {
				dataMinerWorkArea = new DataMinerWorkArea(null);
				return dataMinerWorkArea;
			} else {
				dataMinerWorkArea = new DataMinerWorkArea(dataMinerWorkAreaFolder);
			}

		} catch (Throwable e) {
			logger.debug("DataMiner Folder is set to null");
			e.printStackTrace();
			dataMinerWorkArea = new DataMinerWorkArea(null);
			return dataMinerWorkArea;
		}

		InputDataSets inputDataSets = null;
		try {
			ItemDescription importedDataFolder  = storageUtil.getItemInFolderOnWorkspace(serviceCredentials.getUserName(),
					dataMinerWorkArea.getDataMinerWorkAreaFolder().getId(), IMPORTED_DATA_FOLDER);
			inputDataSets = new InputDataSets(importedDataFolder);

		} catch (Throwable e) {
			logger.debug("ImportedData Folder is set to null");
		}
		dataMinerWorkArea.setInputDataSets(inputDataSets);

		OutputDataSets outputDataSets = null;
		try {
			ItemDescription computedDataFolder = storageUtil.getItemInFolderOnWorkspace(serviceCredentials.getUserName(),
					dataMinerWorkArea.getDataMinerWorkAreaFolder().getId(), COMPUTED_DATA_FOLDER);
	    	outputDataSets = new OutputDataSets(computedDataFolder);

		} catch (Throwable e) {
			logger.debug("ComputedData Folder is set to null");
		}
		dataMinerWorkArea.setOutputDataSets(outputDataSets);

		Computations computations = null;
		try {
			ItemDescription computationsDataFolder = storageUtil.getItemInFolderOnWorkspace(serviceCredentials.getUserName(),
					dataMinerWorkArea.getDataMinerWorkAreaFolder().getId(), COMPUTATIONS_FOLDER);
			computations = new Computations(computationsDataFolder);

		} catch (Throwable e) {
			logger.debug("Computations Folder is set to null");
		}
		dataMinerWorkArea.setComputations(computations);

		logger.debug("DataMinerWorkArea: "+dataMinerWorkArea);
		return dataMinerWorkArea;

	}

}
