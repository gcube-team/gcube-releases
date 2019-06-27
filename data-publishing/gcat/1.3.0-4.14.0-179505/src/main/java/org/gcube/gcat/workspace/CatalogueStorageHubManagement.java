package org.gcube.gcat.workspace;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Map;

import org.gcube.common.gxhttp.request.GXHTTPStringRequest;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.gcat.utils.Constants;
import org.gcube.storagehub.ApplicationMode;
import org.gcube.storagehub.StorageHubManagement;
import org.glassfish.jersey.media.multipart.ContentDisposition;

public class CatalogueStorageHubManagement {
	
	protected StorageHubManagement storageHubManagement;
	
	protected String originalFilename; 
	protected String mimeType;
	
	public String getOriginalFilename() {
		return originalFilename;
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public CatalogueStorageHubManagement() {
		this.storageHubManagement = new StorageHubManagement();
	}
	
	protected String getOriginalFileName(HttpURLConnection httpURLConnection) throws ParseException {
		String contentDisposition = httpURLConnection.getHeaderFields().get("Content-Disposition").get(0);
		contentDisposition = contentDisposition.replaceAll("= ", "=").replaceAll(" =", "=");
		ContentDisposition formDataContentDisposition = new ContentDisposition(contentDisposition);
		return formDataContentDisposition.getFileName();
	}
	
	public URL ensureResourcePersistence(URL persistedURL, String itemID, String resourceID) throws Exception {
		ApplicationMode applicationMode = new ApplicationMode(Constants.getCatalogueApplicationToken());
		try {
			applicationMode.start();
			GXHTTPStringRequest gxhttpStringRequest = GXHTTPStringRequest.newRequest(persistedURL.toString());
			gxhttpStringRequest.from(Constants.CATALOGUE_NAME);
			gxhttpStringRequest.isExternalCall(true);
			HttpURLConnection httpURLConnection = gxhttpStringRequest.get();
			mimeType = httpURLConnection.getContentType().split(";")[0];
			originalFilename = getOriginalFileName(httpURLConnection);
			CatalogueMetadata catalogueMetadata = new CatalogueMetadata(itemID);
			storageHubManagement.setCheckMetadata(catalogueMetadata);
			Metadata metadata = catalogueMetadata.getMetadata(persistedURL, originalFilename, resourceID);
			persistedURL = storageHubManagement.persistFile(httpURLConnection.getInputStream(), resourceID, mimeType, metadata);
			mimeType = storageHubManagement.getMimeType();
			return persistedURL;
		} finally {
			applicationMode.end();
		}
	}

	public void deleteResourcePersistence(String itemID, String resourceID, String mimeType) throws Exception {
		ApplicationMode applicationMode = new ApplicationMode(Constants.getCatalogueApplicationToken());
		try {
			applicationMode.start();
			storageHubManagement = new StorageHubManagement();
			CatalogueMetadata catalogueMetadata = new CatalogueMetadata(itemID);
			storageHubManagement.setCheckMetadata(catalogueMetadata);
			storageHubManagement.removePersistedFile(resourceID, mimeType);
		} finally {
			applicationMode.end();
		}
	}

	
	protected void internalAddRevisionID(String resourceID, String revisionID) throws Exception {
		FileContainer fileContainer = storageHubManagement.getCreatedFile();
		Metadata metadata = fileContainer.get().getMetadata();
		Map<String,Object> map = metadata.getMap();
		map.put(CatalogueMetadata.CATALOGUE_RESOURCE_ID, resourceID);
		map.put(CatalogueMetadata.CATALOGUE_RESOURCE_REVISION_ID, revisionID);
		metadata.setMap(map);
		fileContainer.setMetadata(metadata);
	}
	
	public void renameFile(String resourceID, String revisionID) throws Exception {
		ApplicationMode applicationMode = new ApplicationMode(Constants.getCatalogueApplicationToken());
		try {
			applicationMode.start();
			FileContainer createdfile = storageHubManagement.getCreatedFile();
			createdfile.rename(resourceID);
			internalAddRevisionID(resourceID, revisionID);
		}finally {
			applicationMode.end();
		}
		
	}
	
	public void addRevisionID(String resourceID, String revisionID) throws Exception {
		ApplicationMode applicationMode = new ApplicationMode(Constants.getCatalogueApplicationToken());
		try {
			applicationMode.start();
			internalAddRevisionID(resourceID, revisionID);
		}finally {
			applicationMode.end();
		}
	}
	
}
