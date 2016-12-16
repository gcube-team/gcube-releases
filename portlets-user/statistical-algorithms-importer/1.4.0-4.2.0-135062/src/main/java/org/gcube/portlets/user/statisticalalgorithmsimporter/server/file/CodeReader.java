package org.gcube.portlets.user.statisticalalgorithmsimporter.server.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage.FilesStorage;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code.CodeData;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CodeReader
 * 
 * Read code and convert it in ArrayList
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class CodeReader {
	private Logger logger = LoggerFactory.getLogger(CodeReader.class);
	private ArrayList<CodeData> code;

	public CodeReader(Project projectSession,
			ServiceCredentials serviceCredentials)
			throws StatAlgoImporterServiceException {
		try {

			if (projectSession == null
					|| projectSession.getMainCode() == null
					|| projectSession.getMainCode().getItemDescription() == null
					|| projectSession.getMainCode().getItemDescription()
							.getId() == null) {
				throw new StatAlgoImporterServiceException(
						"Project hasn't a valid main set!");
			}

			String itemId = projectSession.getMainCode().getItemDescription()
					.getId();

			code = new ArrayList<CodeData>();
			FilesStorage filesStorage = new FilesStorage();
			InputStream is = filesStorage.retrieveItemOnWorkspace(
					serviceCredentials.getUserName(), itemId);

			InputStreamReader isr = new InputStreamReader(is);

			BufferedReader br = new BufferedReader((Reader) isr);

			String s;
			int i = 1;
			while ((s = br.readLine()) != null) {
				CodeData codeData = new CodeData(i, s);
				code.add(codeData);
				i++;
			}
			is.close();
			logger.trace("Code size: " + code.size());

		} catch (IOException e) {
			e.printStackTrace();
			new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	public ArrayList<CodeData> getCodeList() {
		return code;
	}

}
