package org.gcube.data_catalogue.grsf_publish_ws.utils.threads;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.catalogue.WorkspaceCatalogue;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.CustomField;
import org.gcube.data_catalogue.grsf_publish_ws.custom_annotations.TimeSeries;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.Common;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.FisheryRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.StockRecord;
import org.gcube.data_catalogue.grsf_publish_ws.utils.CSVHelpers;
import org.gcube.data_catalogue.grsf_publish_ws.utils.HelperMethods;
import org.gcube.datacatalogue.ckanutillibrary.DataCatalogue;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanResourceBase;

/**
 * Extract the time series present in the record, load them as resource on ckan and on the .catalogue
 * folder under the vre folder.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ManageTimeSeriesThread extends Thread{

	private static final String CSV_FILE_FORMAT = ".csv";
	private static final String PATH_SEPARATOR = "/";

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ManageTimeSeriesThread.class);

	// try to attach the source at most CANCHES times ..
	private static final int CANCHES = 3;

	private Common record;
	private String packageName;
	private String username;
	private DataCatalogue catalogue;
	private String context;
	private String token;

	/**
	 * @param record
	 * @param packageId
	 * @param username
	 * @param catalogue
	 * @param context
	 */
	public ManageTimeSeriesThread(Common record, String packageName,
			String username, DataCatalogue catalogue, String context, String token) {
		super();
		this.record = record;
		this.packageName = packageName;
		this.username = username;
		this.catalogue = catalogue;
		this.context = context;
		this.token = token;
	}

	@Override
	public void run() {
		logger.info("Time series manager thread started");

		ScopeProvider.instance.set(context);
		SecurityTokenProvider.instance.set(token);

		try {
			manageTimeSeries(record, packageName, username, catalogue);
			logger.info("The time series manager thread ended correctly");
			return;
		} catch (IllegalAccessException e) {
			logger.error("Error was " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("Error was " + e.getMessage());
		} catch (InvocationTargetException e) {
			logger.error("Error was " + e.getMessage());
		} catch (WorkspaceFolderNotFoundException e) {
			logger.error("Error was " + e.getMessage());
		} catch (ItemNotFoundException e) {
			logger.error("Error was " + e.getMessage());
		} catch (IntrospectionException e) {
			logger.error("Error was " + e.getMessage());
		} catch (InternalErrorException e) {
			logger.error("Error was " + e.getMessage());
		} catch (HomeNotFoundException e) {
			logger.error("Error was " + e.getMessage());
		} catch (UserNotFoundException e) {
			logger.error("Error was " + e.getMessage());
		}finally{
			ScopeProvider.instance.reset();
			SecurityTokenProvider.instance.reset();
		}

		logger.error("Failed to attach csv files to the product...");
	}

	/**
	 * Manage the time series bean within a resource (e.g., catches or landings, exploitation rate and so on).
	 * The method save the time series as csv on ckan, and also save the file in the .catalogue area of the shared vre folder.
	 * @param record
	 * @throws IntrospectionException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws UserNotFoundException 
	 * @throws HomeNotFoundException 
	 * @throws InternalErrorException 
	 * @throws WorkspaceFolderNotFoundException 
	 * @throws ItemNotFoundException 
	 */
	public static void manageTimeSeries(Common record, String packageName, String username, DataCatalogue catalogue) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException, WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException{

		if(record == null)
			throw new IllegalArgumentException("The given record is null!!");

		Workspace ws = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
		WorkspaceSharedFolder vreFolder = ws.getVREFolderByScope(ScopeProvider.instance.get());
		WorkspaceCatalogue catalogueFolder = vreFolder.getVRECatalogue();

		logger.debug("Catalogue folder in vre has path " + catalogueFolder.getPath());

		// the structure under the .catalogue will be as follows:
		// .catalogue:
		//	- stock:
		//		- first_letter_of_the_product
		//			- product_name
		//				- type of files (e.g., csv)
		//					-files (csv)
		//	- fishery 
		//		- first_letter_of_the_product
		//			- product_name
		//				- type of files (e.g., csv)
		//					-files (csv)

		String recordTypeFolderName = record.getProductType().toLowerCase();
		String productName = record.getClass().equals(StockRecord.class) ? ((StockRecord)record).getStockName() : ((FisheryRecord)record).getFisheryName();
		char firstLetter = productName.charAt(0);

		// the whole path of the directory is going to be...
		String csvDirectoryForThisProduct = recordTypeFolderName + PATH_SEPARATOR + firstLetter + PATH_SEPARATOR + productName + PATH_SEPARATOR + "csv";
		logger.debug("The path under which the time series are going to be saved is " + csvDirectoryForThisProduct);
		WorkspaceFolder csvFolder = HelperMethods.createOrGetSubFoldersByPath(catalogueFolder, csvDirectoryForThisProduct);

		if(csvFolder != null){

			String apiKeyUser = catalogue.getApiKeyFromUsername(username);

			Class<?> current = record.getClass();
			do{
				Field[] fields = current.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(TimeSeries.class)) {

						Object f = new PropertyDescriptor(field.getName(), current).getReadMethod().invoke(record);
						if(f != null){

							List asList = (List)f;

							if(!asList.isEmpty()){

								CustomField customAnnotation = field.getAnnotation(CustomField.class);
								logger.debug("A time series has been just found (from field " + customAnnotation.key() + ")");
								String resourceToAttachName = (productName + "_" + customAnnotation.key()).replaceAll("\\s", "_") + CSV_FILE_FORMAT;
								String resourceToAttachDescription = productName + " : " +  customAnnotation.key() + " time series";

								File csvFile = CSVHelpers.listToCSV(asList);

								CkanResourceBase ckanResource = null;
								ExternalFile createdFileOnWorkspace = null;
								if(csvFile != null){

									for (int i = 0; i < CANCHES; i++) {

										// upload this file on ckan
										if(ckanResource == null)
											ckanResource = uploadFileOnCatalogue(csvFile, packageName, catalogue, username, resourceToAttachName, resourceToAttachDescription, apiKeyUser);

										//upload this file on the folder of the vre (under .catalogue) and change the url of the resource
										if(ckanResource != null){
											
											if(createdFileOnWorkspace == null)
												createdFileOnWorkspace = HelperMethods.uploadExternalFile(csvFolder, resourceToAttachName, resourceToAttachDescription, csvFile);
											
											if(createdFileOnWorkspace != null){

												String publicUrlToSetOnCkan = createdFileOnWorkspace.getPublicLink(true);
												logger.info("going to patch the created resource with id " + ckanResource.getId() + " with url " + publicUrlToSetOnCkan);
												boolean updated = catalogue.patchResource(ckanResource.getId(), publicUrlToSetOnCkan, resourceToAttachName, resourceToAttachDescription, "", apiKeyUser);

												if(updated){
													logger.info("Resource has been updated with the new url");
													break;
												}

											}
										}

									}

									// delete the file
									csvFile.delete();
								}
							}
						}
					}
				}
			}
			while((current = current.getSuperclass())!=null); // iterate from the inherited class up to the Object.class
		}
	}

	/**
	 * Upload a resource on ckan
	 * @param csvFile
	 * @param packageName
	 * @param catalogue
	 * @param username
	 * @param resourceToAttachName
	 * @param description
	 * @return a ckan resource on success, null otherwise
	 */
	private static CkanResourceBase uploadFileOnCatalogue(File csvFile,
			String packageName, DataCatalogue catalogue, String username,
			String resourceToAttachName, String description, String apiKey) {
		return catalogue.uploadResourceFile(
				csvFile, 
				packageName, 
				apiKey, 
				resourceToAttachName, 
				description);
	}
}
