/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ResourceStorageManager.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcebroker.impl.contexts.StatefulBrokerContext;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNScoreTable;
import org.gcube.vremanagement.resourcebroker.utils.console.PrettyFormatter;
import org.globus.wsrf.ResourceException;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ResourceStorageManager {
	public static final ResourceStorageManager INSTANCE = new ResourceStorageManager();
	private final GCUBELog logger = new GCUBELog(this, BrokerConfiguration.getProperty("LOGGING_PREFIX") + "::[RES-STORAGE]");

	public final SingletonResourceStorage getResource() throws GCUBEFault {
		try {
			GCUBEWSResource resource = StatefulBrokerContext.getContext()
			.getWSHome().find(StatefulBrokerContext.getResPlanKey());
			return (SingletonResourceStorage) resource;
		} catch (ResourceException e) {
			throw new GCUBEFault(e, "During getResource");
		}
	}

	public final void loadScores() throws Exception {
		SingletonResourceStorage resource = this.getResource();
		if (resource != null && !resource.containsKey(BrokerConfiguration.getProperty("GHN_SCORE_KEY"))) {

			File file = ServiceContext.getContext().getPersistentFile("GHN.scores", true);
			if (!file.exists()) {
				logger.warn("[LOAD] no persistent storage found");
				// avoid further access to load function
				resource.addElement(BrokerConfiguration.getProperty("GHN_SCORE_KEY"), null);
				return;
			}
			logger.debug(PrettyFormatter.bold("[LOAD] Loading scores from : " + file.getPath()));
			ObjectInputStream mine = new ObjectInputStream(new FileInputStream(file));
			Object o = null;
			try {
				o = mine.readObject();
			} catch (Exception e) {
				logger.warn("[LOAD-ERR] The file " + file.getAbsolutePath() + " is inconsistent it will be removed");
				logger.warn("[STORE] file deleted: [" + file.delete() + "]");
				return;
			}
			if (o == null || !(o instanceof GHNScoreTable) || ((GHNScoreTable) o).size() == 0) {
				logger.warn("[LOAD] no persistent storage found");
				// avoid further access to load function
				resource.addElement(BrokerConfiguration.getProperty("GHN_SCORE_KEY"), new GHNScoreTable());
				return;
			}
			GHNScoreTable elem = (GHNScoreTable) o;
			resource.addElement(BrokerConfiguration.getProperty("GHN_SCORE_KEY"), elem);
			logger.info(PrettyFormatter.bold("[STORE-LOAD] restored (" + elem.size() + ") elements"));
		}
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public final void storeScores() {
		logger.debug("[STORE] Storing GHN score table.");

		File file = ServiceContext.getContext().getPersistentFile("GHN.scores", true);
		logger.debug("Writing scores in : " + file.getPath());
		GHNScoreTable tblToStore = new GHNScoreTable();

		try {
			if (!file.exists()) {
				logger.warn("[STORE] created new file: [" + file.createNewFile() + "]");
			} else {
				try {
					// locally stores the previous savings
					ObjectInputStream mine = new ObjectInputStream(new FileInputStream(file));
					Object o = mine.readObject();
					if (o != null && (o instanceof GHNScoreTable) && ((GHNScoreTable) o).size() > 0) {
						tblToStore = (GHNScoreTable) o;
					}
				} catch (Exception e) {
					logger.error(e);
				}
				logger.warn("[STORE] file deleted: [" + file.delete() + "]");
				logger.warn("[STORE] created new file: [" + file.createNewFile() + "]");
			}
		} catch (IOException e) {
			logger.error(e);
		}

		ObjectOutputStream mine = null;
		try {
			mine = new ObjectOutputStream(new FileOutputStream(file));
		} catch (Exception e) {
			logger.error(e);
			return;
		}

		SingletonResourceStorage resource = null;
		try {
			resource = this.getResource();
		} catch (GCUBEFault e) {
			logger.error(e);
			return;
		}
		if (resource == null || resource.getState() == null) {
			logger.error("[STORE-ERR] Invalid resource of the state of resource is not valid");
			return;
		}

		HashMap<String, Object> map = resource.getState();
		String key = null;
		Object value = null;

		for (Entry<String, Object> entry : map.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			if (value == null) {
				logger.warn("Found a null entry for key: " + key + " in the storage");
				continue;
			}
			logger.info("KEY: " + key + " type: " + value.getClass().getSimpleName());
			if (key.startsWith("GLOBAL_GHN_KEY")) {
				if (value != null && value instanceof List<?>) {
					logger.info("STORING global GHN profiles of: " + key);
					List<GHNDescriptor> descriptors = (List<GHNDescriptor>) value;
					for (GHNDescriptor desc : descriptors) {
						if (desc.getAccuracy() != 1 || desc.getHits() != 1) {
							tblToStore.registerGHNScore(desc.getID(), desc.getAccuracy(), desc.getHits());
						}
					}
				}
			} else {
				logger.info("Skipping key: " + key);
			}
		}

		logger.debug(PrettyFormatter.bold("[STORE] ") + PrettyFormatter.underlined("storing **(" + tblToStore.size() + ")** elements"));
		if (tblToStore.size() > 0) {
			try {
				mine.reset();
				mine.writeObject(tblToStore);
				mine.flush();
				mine.close();
			} catch (IOException e) {
				logger.error(e);
			}
			logger.debug(PrettyFormatter.bold("[STORE] Successfully stored: " + tblToStore.size() + " elements"));
		} else {
			try {
				logger.warn("[STORE] No persistent file will be created.");
				mine.close();
				logger.warn("[STORE] file deleted: [" + file.delete() + "]");
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
}
