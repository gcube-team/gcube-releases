package org.gcube.data.analysis.dataminermanagercl.server.storage;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.dataminermanagercl.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class StorageUtil {

	private static Logger logger = LoggerFactory.getLogger(StorageUtil.class);


	public static String saveOnStorageInTemporalFile(InputStream is)
			throws ServiceException {
		try {
			logger.debug("SaveOnStorageInTemporalFile()");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			Double v = Math.random() * 10000;
			String tempFile = "P_" + sdf.format(new Date()) + "_"
					+ v.intValue() + ".xml";
			String remotePath = "/DataMiner/AlgoritmsParameters/" + tempFile;
			IClient client = new StorageClient("DataAnalysis", "DataMiner",
					"DataMiner", AccessType.PUBLIC, MemoryType.VOLATILE)
					.getClient();
			String storageId = client.put(true).LFile(is).RFile(remotePath);
			logger.debug("Storage id: " + storageId);
			String publicLink = client.getHttpUrl().RFile(remotePath);
			logger.debug("Storage public link: " + publicLink);
			return publicLink;

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

}
