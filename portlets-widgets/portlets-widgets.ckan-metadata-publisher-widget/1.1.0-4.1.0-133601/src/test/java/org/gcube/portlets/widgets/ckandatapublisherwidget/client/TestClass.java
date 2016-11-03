package org.gcube.portlets.widgets.ckandatapublisherwidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.catalogue.WorkspaceCatalogue;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.metadatadiscovery.DataCalogueMetadataFormatReader;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataType;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataField;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataValidator;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataVocabulary;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.CKANPublisherServicesImpl;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DataType;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetaDataTypeWrapper;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetadataFieldWrapper;
import org.junit.Test;


public class TestClass {

	@Test
	public void testUser() {

		assert(new CKANPublisherServicesImpl().getDevelopmentUser().equals(CKANPublisherServicesImpl.TEST_USER));

	}

	//@Test
	@SuppressWarnings("deprecation")
	public void testCopyResources() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException, UserNotFoundException, ItemNotFoundException{

		ScopeProvider.instance.set("/gcube/devNext/NextNext");

		Workspace ws = HomeLibrary
				.getHomeManagerFactory()
				.getHomeManager()
				.getHome("costantino.perciante").getWorkspace();

		WorkspaceItem originalFolder = ws.getItem("d3a37eb9-1589-4c95-a9d0-c473a02d4f0f");

		List<? extends WorkspaceItem> children = originalFolder.getChildren();
		//		System.out.println("Folder  is " + originalFolder.getName());
		//		for (WorkspaceItem workspaceItem : children) {
		//			System.out.println("Child is " + workspaceItem.getName());
		//		}

		// copy to catalogue
		WorkspaceCatalogue userCatalogue = ws.getCatalogue();

		WorkspaceItem copiedFolder = userCatalogue.addWorkspaceItem(originalFolder.getId(), userCatalogue.getId());
		System.out.println(copiedFolder.isHidden());
		//		for (WorkspaceItem workspaceItem : children) {
		//			WorkspaceItem copiedChildren = userCatalogue.addWorkspaceItem(workspaceItem.getId(), copiedFolder.getId());
		//			System.out.println("****************************** Copied file has path " + copiedChildren.getPath());
		//		}

		// look at catalogue structure
		List<WorkspaceItem> catalogueChildrens = ((WorkspaceFolder)userCatalogue).getChildren(true);
		if(catalogueChildrens.isEmpty())
			System.out.println("****************************** Catalogue Child list is empty");
		else for (WorkspaceItem catalogueItem : catalogueChildrens) {
			System.out.println("****************************** Catalogue Child is " + catalogueItem.getName());
			if(catalogueItem.isFolder()){
				WorkspaceFolder catalogueFolder = (WorkspaceFolder) catalogueItem;
				System.out.println("**************************" + catalogueFolder.getName() + " it is a folder, printing children");
				List<? extends WorkspaceItem> copiedFolderChildren = catalogueFolder.getChildren(true);
				for (WorkspaceItem copiedFolderChildrenItem : copiedFolderChildren) {
					System.out.println("****************************** Child is " + copiedFolderChildrenItem.getName());
					copiedFolderChildrenItem.getPublicLink(true);
				}
			}
		}
	}

	//@Test
	public void testTitle(){

		//		String regexTitleSubWord = "[^a-zA-Z0-9_.-]";
		//		String title = "this is a test title .";
		//		String[] splittedTitle = title.split(" ");
		//
		//		for (String word : splittedTitle) {
		//			System.out.println("Word is " + word);
		//			String replaced = word.replaceAll(regexTitleSubWord, "");	
		//			System.out.println("Replaced Word is " + replaced);
		//			if(!replaced.equals(word)){
		//				System.out.println("Please note that only alphanumeric characters are allowed for the title");
		//			}
		//		}

		System.out.println("Notification_portlet".replaceAll("[^A-Za-z0-9.-_]", " "));
	}

	//@Test
	public void retrieveMetadata(){

		try {

			ScopeProvider.instance.set("/gcube/devNext/NextNext");

			DataCalogueMetadataFormatReader reader = new DataCalogueMetadataFormatReader();

			for (MetadataType mt : reader.getListOfMetadataTypes()) {

				System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + mt.getName());
				MetadataFormat metadata = reader.getMetadataFormatForMetadataType(mt);

				// we need to wrap the list of metadata
				List<MetadataFieldWrapper> wrapperList = new ArrayList<MetadataFieldWrapper>();
				List<MetadataField> toWrap = metadata.getMetadataFields();

				for(MetadataField metadataField: toWrap){



					MetadataFieldWrapper wrapperObj = new MetadataFieldWrapper();
					wrapperObj.setDefaultValue(metadataField.getDefaultValue());
					wrapperObj.setFieldName(metadataField.getFieldName());
					wrapperObj.setType(DataType.valueOf(metadataField.getDataType().toString()));
					wrapperObj.setMandatory(metadataField.getMandatory());
					wrapperObj.setNote(metadataField.getNote());

					MetadataValidator validator = metadataField.getValidator();
					if(validator != null)
						wrapperObj.setValidator(validator.getRegularExpression());

					MetadataVocabulary vocabulary = metadataField.getVocabulary();

					if(vocabulary != null){
						wrapperObj.setVocabulary(vocabulary.getVocabularyFields());	
						wrapperObj.setMultiSelection(vocabulary.isMultiSelection());
					}

					// add to the list
					wrapperList.add(wrapperObj);

				}

				// wrap the mt as well
				MetaDataTypeWrapper typeWrapper = new MetaDataTypeWrapper();
				typeWrapper.setDescription(mt.getDescription());
				typeWrapper.setId(mt.getId());
				typeWrapper.setName(mt.getName());

			}

			System.out.println("List of profiles has been saved into session");

		} catch (Exception e) {
			System.out.println("Error while retrieving metadata beans " + e);
		}

	}

}
