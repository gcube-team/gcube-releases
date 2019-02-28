package org.gcube.portlets.user.statisticalalgorithmsimporter;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.OpenResolver;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class StorageHubTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(StorageHubTest.class);

	@Test
	public void testCopy() {
		if (Constants.TEST_ENABLE) {

			try {

				String itemId = "5bd115b1-3235-4256-bf29-14e2f6adcba3";
				String folderId = "08c47365-d534-45ff-a690-882ced63100f";

				String scope = Constants.DEFAULT_SCOPE;
				String user = Constants.DEFAULT_USER;

				logger.info(
						"Copy item on folder: [user=" + user + ", itemId=" + itemId + ", folderId=" + folderId + "]");

				List<String> userRoles = new ArrayList<>();
				userRoles.add(Constants.DEFAULT_ROLE);
				/*
				 * if (aslSession.getUsername().compareTo("lucio.lelii") == 0)
				 * userRoles.add("VRE-Manager");
				 */

				String token;
				try {
					token = authorizationService().generateUserToken(new UserInfo(user, userRoles),
							scope);
				} catch (Exception e) {
					logger.error("Error generating the token for test: " + e.getLocalizedMessage(),e);
					throw new Exception("Error generating the token for test: " + e.getLocalizedMessage(),e);
				}

				logger.debug("Set SecurityToken: " + token);
				SecurityTokenProvider.instance.set(token);
				logger.debug("Set ScopeProvider: " + scope);
				ScopeProvider.instance.set(scope);

				StorageHubClient shc = new StorageHubClient();
				logger.debug("StorageHub client retrieved!");
				logger.debug("Retrieve file: "+itemId);
				OpenResolver openResolverForFile = shc.open(itemId);
				logger.debug("Retrieve FileContainer");
				FileContainer fileContainer = openResolverForFile.asFile();
				logger.debug("FileContainer retrieved!");
				
				OpenResolver openResolverForFolder = shc.open(folderId);
				FolderContainer folderContainer = openResolverForFolder.asFolder();
				logger.debug("FolderContainer retrieved!");
				logger.debug("Copy Start");
				FileContainer fileCreatedContainer = fileContainer.copy(folderContainer, fileContainer.get().getName());
				logger.debug("Copy Done");
				AbstractFileItem item = fileCreatedContainer.get();
				logger.debug("File retrieved");
				ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
						item.getPath(), "");
				logger.debug("Item: " + itemDescription);
				assertTrue("Success", true);
			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				fail("Error:" + e.getLocalizedMessage());

			}

		} else {
			assertTrue("Success", true);
		}
	}

}
