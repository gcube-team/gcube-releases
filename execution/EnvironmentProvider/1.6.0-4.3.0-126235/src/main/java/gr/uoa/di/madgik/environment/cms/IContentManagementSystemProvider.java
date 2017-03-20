package gr.uoa.di.madgik.environment.cms;

import gr.uoa.di.madgik.environment.cms.elements.Collection;
import gr.uoa.di.madgik.environment.cms.elements.alternative.DocumentAlternative;
import gr.uoa.di.madgik.environment.cms.elements.annotation.DocumentAnnotation;
import gr.uoa.di.madgik.environment.cms.elements.document.Document;
import gr.uoa.di.madgik.environment.cms.elements.metadata.DocumentMetadata;
import gr.uoa.di.madgik.environment.cms.elements.part.DocumentPart;
import gr.uoa.di.madgik.environment.exception.EnvironmentContentManagementSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public interface IContentManagementSystemProvider 
{
	public void SessionInit(EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public List<Collection> GetCollections(EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public List<Collection> FindCollectionByName(String name, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public List<Collection> FindCollectionById(String id, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public String CreateCollection(Collection col, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public void DeleteCollection(String collectionId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public List<Document> GetDocuments(String collectionId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public List<DocumentMetadata> GetMetadataDocuments(String collectionId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public Document GetDocument(String collectionId, String documentId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public Document GetMainDocument(String collectionId, String documentId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public List<DocumentMetadata> GetDocumentMetadata(String collectionId, String documentId, String schemaName, String schemaUri, String language, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public String AddDocument(Document document, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public void DeleteDocument(String collectionId, String documentId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public void AddAlternative(String collectionId, String documentId, DocumentAlternative alternative, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public void DeleteAlternative(String collectionId, String documentId,DocumentAlternative alternative, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public void AddMetadata(String collectionId, String documentId, DocumentMetadata metadata, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public void DeleteMetadata(String collectionId, String documentId, DocumentMetadata metadata, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public void AddPart(String collectionId, String documentId, DocumentPart part, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public InputStream ResolveContent(String locator) throws EnvironmentContentManagementSystemException;
	public Document NewDocument() throws EnvironmentContentManagementSystemException;
	public DocumentMetadata NewDocumentMetadata() throws EnvironmentContentManagementSystemException;
	public DocumentPart NewDocumentPart() throws EnvironmentContentManagementSystemException;
	public DocumentAlternative NewDocumentAlternative() throws EnvironmentContentManagementSystemException;
	public DocumentAnnotation NewDocumentAnnotation() throws EnvironmentContentManagementSystemException;
	public List<Document> GetDocumentsWithProperties(String collectionId, HashMap<String, String> properties, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
	public void updateDocumentContent(Document document, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException;
}
