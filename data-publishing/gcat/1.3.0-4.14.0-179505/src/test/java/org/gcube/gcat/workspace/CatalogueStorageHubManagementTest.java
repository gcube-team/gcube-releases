package org.gcube.gcat.workspace;

import java.net.URL;
import java.util.Map;

import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.gcat.ContextTest;
import org.gcube.gcat.persistence.ckan.CKANResource;
import org.gcube.storagehub.StorageHubManagement;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogueStorageHubManagementTest extends ContextTest {
	
	private static final Logger logger = LoggerFactory.getLogger(CatalogueStorageHubManagementTest.class);
	
	/*
	 * PRE
	 * 
	 * Workspace(luca.frosini) > RESTful Transaction Model.pdf
	 * https://data1-d.d4science.org/shub/E_YjI4STdKKzRlNjgzMm9jQWxjcmtReDNwbDFYR3lpTHo3SjdtN1RDZ3c2OGk0ZHZhdE5iZElBKzNxUDAyTGFqZw==
	 * https://goo.gl/HcUWni
	 * 
	 * Workspace(luca.frosini) > RESTful Transaction Model v 1.0.pdf
	 * https://data1-d.d4science.org/shub/E_aThRa1NpWFJpTGEydEU2bEJhMXNjZy8wK3BxekJKYnpYTy81cUkwZVdicEZ0aGFRZmY4MkRnUC8xWW0zYzVoVg==
	 * https://goo.gl/J8AwQW
	 * 
	 * 
	 * Workspace(luca.frosini) > RESTful Transaction Model v 1.1.pdf
	 * https://data1-d.d4science.org/shub/E_NkhrbVV4VTluT0RKVUtCRldobFZTQU5ySTZneFdpUzJ2UjJBNlZWNDlURDVHamo4WjY5RnlrcHZGTGNkT2prUg==
	 * https://goo.gl/78ViuR
	 * 
	 */
	
	public static final String ORIGINAL_STORAGE_URL_STRING = "https://data1-d.d4science.org/shub/E_YjI4STdKKzRlNjgzMm9jQWxjcmtReDNwbDFYR3lpTHo3SjdtN1RDZ3c2OGk0ZHZhdE5iZElBKzNxUDAyTGFqZw==";
	public static final URL ORIGINAL_STORAGE_URL;
	
	public static final String SHORT_URL_STRING = "https://goo.gl/HcUWni";
	public static final URL SHORT_STORAGE_URL;
	
	
	
	public static final String MIME_TYPE = "application/pdf";
	
	
	static {
		try {
			ORIGINAL_STORAGE_URL = new URL(ORIGINAL_STORAGE_URL_STRING);
			SHORT_STORAGE_URL = new URL(SHORT_URL_STRING);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static final String ITEM_ID = "MyItem";
	public static final String RESOURCE_ID = "1234";
	
	protected CatalogueStorageHubManagement catalogueStorageHubManagement;
	protected StorageHubManagement storageHubManagement;
	protected CatalogueMetadata catalogueMetadata;
	
	
	public CatalogueStorageHubManagementTest() {
		catalogueStorageHubManagement = new CatalogueStorageHubManagement();
		storageHubManagement = catalogueStorageHubManagement.storageHubManagement;
		catalogueMetadata = new CatalogueMetadata(ITEM_ID);
	}
	
	@Test
	public void getFinalURL() {
		URL finalURL = CKANResource.getFinalURL(SHORT_STORAGE_URL);
		Assert.assertTrue(finalURL.toString().compareTo(ORIGINAL_STORAGE_URL_STRING)==0);
	}
	
	protected void checkMetadata(FileContainer fileContainer, String version) {
		Metadata gotMetadata = fileContainer.get().getMetadata();
		Map<String, Object> gotMap = gotMetadata.getMap(); 
				
		CatalogueMetadata catalogueMetadata = new CatalogueMetadata(ITEM_ID);
		
		Metadata expectedMetadata = catalogueMetadata.getMetadata(ORIGINAL_STORAGE_URL, fileContainer.get().getName(), RESOURCE_ID);
		Map<String, Object> expectedMap = expectedMetadata.getMap(); 
		
		for(String key : gotMap.keySet()) {
			String value = (String) gotMap.get(key);
			if(key.compareTo(CatalogueMetadata.CATALOGUE_RESOURCE_REVISION_ID)==0) {
				Assert.assertTrue(value.compareTo(version)==0);
			}else {
				String expectedValue = (String) expectedMap.get(key);
				Assert.assertTrue(value.compareTo(expectedValue)==0);
			}
		}
	}
	
	
	@Test
	public void testPersistence() throws Exception {
		
		URL persistedURL = catalogueStorageHubManagement.ensureResourcePersistence(ORIGINAL_STORAGE_URL, ITEM_ID, RESOURCE_ID);
		logger.debug("Publick Link of persisted file is {}", persistedURL);
		
		Assert.assertTrue(catalogueStorageHubManagement.getMimeType().compareTo(MIME_TYPE)==0);
		
		FileContainer createdFileContainer = storageHubManagement.getCreatedFile();
		
		
		String version = "2";
		catalogueStorageHubManagement.renameFile(RESOURCE_ID, version);
		checkMetadata(createdFileContainer, version);
		
		
		version = "3";
		catalogueStorageHubManagement.addRevisionID(RESOURCE_ID, version);
		checkMetadata(createdFileContainer, version);
		
		
		catalogueStorageHubManagement.deleteResourcePersistence(ITEM_ID, RESOURCE_ID, MIME_TYPE);
		
	}
	
}
