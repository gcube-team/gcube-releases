//package gr.uoa.di.madgik.environment.cms;
//
//import gr.uoa.di.madgik.environment.cms.elements.Collection;
//import gr.uoa.di.madgik.environment.cms.elements.alternative.DocumentAlternative;
//import gr.uoa.di.madgik.environment.cms.elements.annotation.DocumentAnnotation;
//import gr.uoa.di.madgik.environment.cms.elements.document.Document;
//import gr.uoa.di.madgik.environment.cms.elements.metadata.DocumentMetadata;
//import gr.uoa.di.madgik.environment.cms.elements.part.DocumentPart;
//import gr.uoa.di.madgik.environment.exception.EnvironmentContentManagementSystemException;
//import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
//import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
//
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.List;
//
//public class ContentManagementSystemProvider implements IContentManagementSystemProvider
//{
//
//	private static IContentManagementSystemProvider StaticProvider=null;
//	private static final Object lockMe=new Object();
//	
//	private IContentManagementSystemProvider Provider=null;
//	private EnvHintCollection InitHints=null;
//	
//	public static IContentManagementSystemProvider Init(String ProviderName, EnvHintCollection Hints) throws EnvironmentValidationException
//	{
//		try
//		{
//			synchronized(ContentManagementSystemProvider.lockMe)
//			{
//				if(ContentManagementSystemProvider.StaticProvider==null)
//				{
//					if(ProviderName.equals(ContentManagementSystemProvider.class.getName())) throw new EnvironmentValidationException("Class "+ContentManagementSystemProvider.class.getName()+" cannot be defined as provider");
//					Class<?> c=Class.forName(ProviderName);
//					Object o=c.newInstance();
//					if(!(o instanceof IContentManagementSystemProvider)) throw new EnvironmentValidationException("");
//					ContentManagementSystemProvider prov=new ContentManagementSystemProvider();
//					prov.Provider=(IContentManagementSystemProvider)o;
//					prov.InitHints=Hints;
//					prov.Provider.SessionInit(Hints);
//					ContentManagementSystemProvider.StaticProvider=prov;
//				}
//			}
//			return ContentManagementSystemProvider.StaticProvider;
//		}catch(Exception ex)
//		{
//			throw new EnvironmentValidationException("Could not initialize Storage System Provider", ex);
//		}
//	}
//	
//	public static boolean IsInit()
//	{
//		synchronized(ContentManagementSystemProvider.lockMe)
//		{
//			return (ContentManagementSystemProvider.StaticProvider!=null);
//		}		
//	}
//
//	public void SessionInit(EnvHintCollection Hints) throws EnvironmentContentManagementSystemException
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		this.Provider.SessionInit(this.MergeHints(Hints));
//	}
//	
//	
//	private EnvHintCollection MergeHints(EnvHintCollection Hints)
//	{
//		if(this.InitHints==null && Hints==null) return new EnvHintCollection();
//		if(this.InitHints==null) return Hints;
//		else if(Hints==null) return this.InitHints;
//		else return this.InitHints.Merge(Hints);
//	}
//
//	@Override
//	public List<Collection> GetCollections(EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.GetCollections(this.MergeHints(Hints));
//	}
//
//	@Override
//	public List<Collection> FindCollectionByName(String name, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.FindCollectionByName(name, this.MergeHints(Hints));
//	}
//
//	@Override
//	public List<Collection> FindCollectionById(String collectionId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.FindCollectionById(collectionId, this.MergeHints(Hints));
//	}
//	
//	@Override
//	public String CreateCollection(Collection col, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.CreateCollection(col, this.MergeHints(Hints));
//	}
//
//	@Override
//	public void DeleteCollection(String collectionId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		this.Provider.DeleteCollection(collectionId, this.MergeHints(Hints));
//		
//	}
//
//	@Override
//	public List<Document> GetDocuments(String collectionId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.GetDocuments(collectionId, this.MergeHints(Hints));
//	}
//
//	@Override
//	public List<DocumentMetadata> GetMetadataDocuments(String collectionId,EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.GetMetadataDocuments(collectionId, this.MergeHints(Hints));
//	}
//
//	@Override
//	public Document GetDocument(String collectionId, String documentId,EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.GetDocument(collectionId, documentId, this.MergeHints(Hints));
//	}
//
//	@Override
//	public Document GetMainDocument(String collectionId, String documentId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.GetMainDocument(collectionId, documentId, this.MergeHints(Hints));
//	}
//
//	@Override
//	public List<DocumentMetadata> GetDocumentMetadata(String collectionId, String documentId, String schemaName, String schemaUri, String language, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.GetDocumentMetadata(collectionId, documentId, schemaName, schemaUri, language, this.MergeHints(Hints));
//	}
//
//	@Override
//	public String AddDocument(Document document, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.AddDocument(document, this.MergeHints(Hints));
//	}
//
//	@Override
//	public void DeleteDocument(String collectionId, String documentId,EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		this.Provider.DeleteDocument(collectionId, documentId, this.MergeHints(Hints));
//	}
//
//	@Override
//	public void AddAlternative(String collectionId, String documentId, DocumentAlternative alternative, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		this.Provider.AddAlternative(collectionId, documentId, alternative, this.MergeHints(Hints));
//	}
//	
//	@Override
//	public void DeleteAlternative(String collectionId, String documentId,DocumentAlternative alternative, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		this.Provider.DeleteAlternative(collectionId, documentId, alternative, Hints);
//	}
//	
//
//	@Override
//	public void AddMetadata(String collectionId, String documentId,DocumentMetadata metadata, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		this.Provider.AddMetadata(collectionId, documentId, metadata, this.MergeHints(Hints));
//	}
//
//	@Override
//	public void DeleteMetadata(String collectionId, String documentId, DocumentMetadata metadata, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		this.Provider.GetCollections(this.MergeHints(Hints));
//		
//	}
//
//	@Override
//	public void AddPart(String collectionId, String documentId, DocumentPart part, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		this.Provider.AddPart(collectionId, documentId, part, this.MergeHints(Hints));
//		
//	}
//
//	@Override
//	public InputStream ResolveContent(String locator) throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.ResolveContent(locator);
//	}
//
//	@Override
//	public Document NewDocument() throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.NewDocument();
//	}
//
//	@Override
//	public DocumentMetadata NewDocumentMetadata() throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.NewDocumentMetadata();
//	}
//
//	@Override
//	public DocumentPart NewDocumentPart() throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.NewDocumentPart();
//	}
//
//	@Override
//	public DocumentAlternative NewDocumentAlternative() throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.NewDocumentAlternative();
//	}
//
//	@Override
//	public DocumentAnnotation NewDocumentAnnotation() throws EnvironmentContentManagementSystemException 
//	{
//		if(this.Provider==null) throw new EnvironmentContentManagementSystemException("Provider not initialized");
//		return this.Provider.NewDocumentAnnotation();
//	}
//
//	@Override
//	public List<Document> GetDocumentsWithProperties(String collectionId, HashMap<String, String> properties,
//			EnvHintCollection Hints)
//			throws EnvironmentContentManagementSystemException {
//		return this.Provider.GetDocumentsWithProperties(collectionId, properties, Hints);
//	}
//
//	@Override
//	public void updateDocumentContent(Document document, EnvHintCollection Hints)
//			throws EnvironmentContentManagementSystemException {
//		this.Provider.updateDocumentContent(document, Hints);
//		
//	}
//}
