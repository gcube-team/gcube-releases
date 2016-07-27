//package gr.uoa.di.madgik.environment.gcube;
//
//import gr.uoa.di.madgik.environment.cms.IContentManagementSystemProvider;
//import gr.uoa.di.madgik.environment.cms.elements.Collection;
//import gr.uoa.di.madgik.environment.cms.elements.DocumentProperty;
//import gr.uoa.di.madgik.environment.cms.elements.alternative.DocumentAlternative;
//import gr.uoa.di.madgik.environment.cms.elements.annotation.DocumentAnnotation;
//import gr.uoa.di.madgik.environment.cms.elements.document.Document;
//import gr.uoa.di.madgik.environment.cms.elements.metadata.DocumentMetadata;
//import gr.uoa.di.madgik.environment.cms.elements.part.DocumentPart;
//import gr.uoa.di.madgik.environment.exception.EnvironmentContentManagementSystemException;
//import gr.uoa.di.madgik.environment.gcube.cms.elements.GCubeOriginatingDocument;
//import gr.uoa.di.madgik.environment.gcube.cms.elements.GCubeOriginatingDocumentAlternative;
//import gr.uoa.di.madgik.environment.gcube.cms.elements.GCubeOriginatingDocumentAnnotation;
//import gr.uoa.di.madgik.environment.gcube.cms.elements.GCubeOriginatingDocumentMetadata;
//import gr.uoa.di.madgik.environment.gcube.cms.elements.GCubeOriginatingDocumentPart;
//import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
//
//import java.io.InputStream;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//
//import org.gcube.common.core.informationsystem.client.RPDocument;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.contentmanagement.contentmanager.smsplugin.util.GCubeCollections;
//import org.gcube.contentmanagement.contentmanager.stubs.CollectionReference;
//import org.gcube.contentmanagement.contentmanager.stubs.calls.iterators.RemoteIterator;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.DocumentWriter;
//import org.gcube.contentmanagement.gcubedocumentlibrary.io.ViewReader;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.DocumentProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.MetadataProjection;
//import org.gcube.contentmanagement.gcubedocumentlibrary.projections.Projections;
//import org.gcube.contentmanagement.gcubedocumentlibrary.util.Collections;
//import org.gcube.contentmanagement.gcubedocumentlibrary.views.MetadataView;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeAlternative;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeAnnotation;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeDocument;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeElementProperty;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubeMetadata;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.GCubePart;
//import org.gcube.contentmanagement.gcubemodellibrary.elements.MetadataElements;
//
//public class GCubeContentManagementSystemProvider implements IContentManagementSystemProvider {
//
//	private static class ReaderWriterPair {
//		public ReaderWriterPair(DocumentReader reader, DocumentWriter writer) {
//			this.reader = reader;
//			this.writer = writer;
//		}
//		public DocumentReader reader;
//		public DocumentWriter writer;
//	}
//	
//	private Map<String, ReaderWriterPair> accessors = new HashMap<String, ReaderWriterPair>();
//	private Map<String, String> collectionNameToId = new HashMap<String, String>();
//	
//	public static final String GCubeActionScopeHintName="GCubeActionScope";
//	public static final String SynchronousCreationHintName="SynchronousCreation";
//	public static final String SynchronousDeletionHintName="SynchronousDeletion";
//	public static final String SynchronousPollingIntervalHintName="SynchronousPollingInterval";
//	public static final String SynchronousPollingIntervalUnitHintName="SynchronousPollingIntervalUnit";
//	
//	public static final boolean SynchronousCreationDef = false;
//	public static final boolean SynchronousDeletionDef = false;
//	public static final long SynchronousPollingIntervalDef = 5;
//	public static final TimeUnit SynchronousPollingIntervalUnitDef = TimeUnit.SECONDS;
//
//	@Override
//	public List<Collection> GetCollections(EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		List<Collection> cols = new ArrayList<Collection>();
//		
//		try 
//		{
//			for (org.gcube.contentmanagement.gcubedocumentlibrary.util.Collection gCubeCol : Collections.list(GCUBEScope.getScope(scope))){
//				Collection col = new Collection();
//				col.id = gCubeCol.getId();
//				col.name = gCubeCol.getName();
//				if(gCubeCol.getDescription() != null) col.description = gCubeCol.getDescription();
//				if(gCubeCol.getCreationTime() != null) col.creationTime = new Long(gCubeCol.getCreationTime().getTimeInMillis()).toString();
//				col.isUserCollection = gCubeCol.isUserCollection();
//				cols.add(col);
//			}
//			return cols;
//		} catch (Exception e) 
//		{
//			throw new EnvironmentContentManagementSystemException("Could not retrieve collections", e);
//		}
//	}
//
//	@Override
//	public List<Collection> FindCollectionByName(String name, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		List<Collection> cols = new ArrayList<Collection>();
//		long synchronousPollingInterval = GetSynchronousPollingInterval(Hints);
//		TimeUnit synchronousPollingIntervalUnit = GetSynchronousPollingIntervalUnit(Hints);
//		
//		try 
//		{
//			for (org.gcube.contentmanagement.gcubedocumentlibrary.util.Collection gCubeCol : Collections.findByName(GCUBEScope.getScope(scope), name))
//			{
//				Collection col = new Collection();
//				col.id = gCubeCol.getId();
//				col.name = gCubeCol.getName();
//				col.description = gCubeCol.getDescription();
//				col.creationTime = new Long(gCubeCol.getCreationTime().getTimeInMillis()).toString();
//				col.isUserCollection = gCubeCol.isUserCollection();
//				cols.add(col);
//			}
//			return cols;
//		} catch (Exception e) 
//		{
//			throw new EnvironmentContentManagementSystemException("Could not find collection", e);
//		}
//	}
//
//	@Override
//	public List<Collection> FindCollectionById(String id, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException {
//		String scope = GetActionScope(Hints);
//		List<Collection> cols = new ArrayList<Collection>();
//		long synchronousPollingInterval = GetSynchronousPollingInterval(Hints);
//		TimeUnit synchronousPollingIntervalUnit = GetSynchronousPollingIntervalUnit(Hints);
//		
//		try 
//		{
//			List<org.gcube.contentmanagement.gcubedocumentlibrary.util.Collection> gCubeCols = null;
////			while(true)
////			{
//				gCubeCols = Collections.findById(GCUBEScope.getScope(scope), id);
////				if(gCubeCols.size() == 0 && accessors.containsKey(id)) synchronousPollingIntervalUnit.sleep(synchronousPollingInterval);
////				else break;
////			}
//			for (org.gcube.contentmanagement.gcubedocumentlibrary.util.Collection gCubeCol : gCubeCols)
//			{
//				Collection col = new Collection();
//				col.id = gCubeCol.getId();
//				col.name = gCubeCol.getName();
//				col.description = gCubeCol.getDescription();
//				col.creationTime = new Long(gCubeCol.getCreationTime().getTimeInMillis()).toString();
//				col.isUserCollection = gCubeCol.isUserCollection();
//				cols.add(col);
//			}
//			return cols;
//		} catch (Exception e) 
//		{
//			throw new EnvironmentContentManagementSystemException("Could not find collection", e);
//		}
//	}
//	
//	@Override
//	public String CreateCollection(Collection col, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		boolean synchronousCreation = GetSynchronousCreation(Hints);
//		TimeUnit synchronousIntervalUnit = GetSynchronousPollingIntervalUnit(Hints);
//		long synchronousInterval = GetSynchronousPollingInterval(Hints);
//		
//		//we don't want to propagate the request to others CM
//		boolean propagateRequest = false;
//
//		//we want the collection to be readable and writable
//		boolean readable = true;
//		boolean writable = true;
//
//		//finally we create the collection
//		List<CollectionReference> collectionReferences;
//		try 
//		{
//			GCUBEScope gCubeScope = GCUBEScope.getScope(scope);
//			collectionReferences = GCubeCollections.createGCubeCollection(propagateRequest, col.name, col.description, 
//					col.isUserCollection, readable, writable, gCubeScope);
//
//			if(collectionReferences.size()!=1) throw new Exception("Not exactly one collection reference has been returned");
//			accessors.put(collectionReferences.get(0).getCollectionID(), new ReaderWriterPair(
//					new DocumentReader(collectionReferences.get(0), gCubeScope), new DocumentWriter(collectionReferences.get(0), gCubeScope)));
//			collectionNameToId.put(col.name, collectionReferences.get(0).getCollectionID());
//			
//			if(synchronousCreation)
//			{
//				List<org.gcube.contentmanagement.gcubedocumentlibrary.util.Collection> gCubeCols = null;
//				do
//				{
//					gCubeCols = Collections.findById(GCUBEScope.getScope(scope), collectionReferences.get(0).getCollectionID());
//					if(gCubeCols.size() == 0) synchronousIntervalUnit.sleep(synchronousInterval);
//				}while(gCubeCols.size() == 0);
//			}
//			return collectionReferences.get(0).getCollectionID();
//		} catch (Exception e) 
//		{
//			throw new EnvironmentContentManagementSystemException("Could not create collection", e);
//		}
//		
//	}
//
//	@Override
//	public void DeleteCollection(String id, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		boolean synchronousDeletion = GetSynchronousDeletion(Hints);
//		TimeUnit synchronousIntervalUnit = GetSynchronousPollingIntervalUnit(Hints);
//		long synchronousInterval = GetSynchronousPollingInterval(Hints);
//		//we want to delete the collection profile
//		boolean deleteProfile = true;
//
//		List<Future<RPDocument>> results;
//		try {
//			List<Collection> foundCol = FindCollectionById(id, Hints);
//			if(foundCol.size() != 1) return;
//			results = GCubeCollections.deleteGCubeCollection(id, deleteProfile, GCUBEScope.getScope(scope));
//
//			for (Future<RPDocument> result: results){
//				RPDocument doc = result.get();
//			}
//			accessors.remove(id);
//			collectionNameToId.remove(foundCol.get(0).name);
//			
//			if(synchronousDeletion)
//			{
//				List<org.gcube.contentmanagement.gcubedocumentlibrary.util.Collection> gCubeCols = null;
//				do 
//				{
//					gCubeCols = Collections.findById(GCUBEScope.getScope(scope), id);
//					if(gCubeCols.size() != 0) synchronousIntervalUnit.sleep(synchronousInterval);
//				}while(gCubeCols.size() != 0);
//			}
//		} catch (Exception e) {
//			throw new EnvironmentContentManagementSystemException("Could not delete collection", e);
//		}
//		
//	}
//
//	private Document translateDocument(GCubeDocument gCubeDoc, boolean includeParts, boolean includeAlternatives, boolean includeMetadata, boolean includeAnnotations) throws Exception
//	{
//		GCubeOriginatingDocument doc = new GCubeOriginatingDocument();
//		doc.id = gCubeDoc.id();
//		doc.name = gCubeDoc.name();
//		doc.collectionId = gCubeDoc.collectionID();
//		doc.language = gCubeDoc.language();
//		//doc.uri = gCubeDoc.uri().toString();
//		doc.mimeType = gCubeDoc.mimeType();
//		doc.schemaName = gCubeDoc.schemaName();
//		if(gCubeDoc.schemaURI() != null) doc.schemaURI = gCubeDoc.schemaURI().toString();
//		doc.content = gCubeDoc.bytestream();
//		if(gCubeDoc.bytestreamURI() != null) doc.contentLocator = gCubeDoc.bytestreamURI().toString();
//		if(includeParts)
//		{
//			if(gCubeDoc.parts() != null && gCubeDoc.parts().size() != 0)
//			{
//				doc.parts = new ArrayList<DocumentPart>();
//				for(GCubePart gCubePart : gCubeDoc.parts().toList())
//				{
//					DocumentPart part = new GCubeOriginatingDocumentPart();
//					part.id = gCubePart.id();
//					part.name = gCubePart.name();
//				//	part.uri = gCubePart.uri().toString();
//					part.creationTime = new Long(gCubePart.creationTime().getTimeInMillis()).toString();
//					part.document = doc;
//					part.content = gCubePart.bytestream();
//					part.contentLocator = gCubePart.bytestreamURI().toString();
//					part.language = gCubePart.language();
//					part.lastUpdate = new Long(gCubePart.lastUpdate().getTimeInMillis()).toString();
//					part.length = gCubePart.length();
//					part.mimeType = gCubePart.mimeType();
//					part.order = gCubePart.order();
//					part.schemaName = gCubePart.schemaName();
//					part.schemaUri = gCubePart.schemaURI().toString();
//					part.properties = new HashMap<String, DocumentProperty>();
//					for(Map.Entry<String, GCubeElementProperty> e : gCubePart.properties().entrySet())
//					{
//						DocumentProperty prop = new DocumentProperty();
//						prop.key = e.getValue().key();
//						prop.type = e.getValue().type();
//						prop.value = e.getValue().value();
//						part.properties.put(e.getKey(), prop);
//						
//					}
//					doc.parts.add(part);
//				}
//				for(GCubePart gCubePart : gCubeDoc.parts().toList())
//				{
//					DocumentPart locatedPart = null;
//					for(DocumentPart part : doc.parts)
//					{
//						if(part.id.equals(gCubePart.id()))
//						{
//							locatedPart = part;
//							break;
//						}
//					}
//					String prevGCubePartId = gCubePart.previous().id();
//					
//					for(DocumentPart previousPart : doc.parts)
//					{
//						if(previousPart.id.equals(prevGCubePartId))
//						{
//							locatedPart.previous = previousPart;
//							break;
//						}
//					}
//				}
//			}
//		}
//		
//		if(includeMetadata)
//		{
//			if(gCubeDoc.metadata() != null && gCubeDoc.metadata().size() != 0)
//			{
//				doc.metadata = new ArrayList<DocumentMetadata>();
//				for(GCubeMetadata gCubeMetadata : gCubeDoc.metadata().toList())
//				{
//					DocumentMetadata metadata = new GCubeOriginatingDocumentMetadata();
//					metadata.id = gCubeMetadata.id();
//					metadata.name = gCubeMetadata.name();
//					//if(gCubeMetadata.uri() != null) metadata.uri = gCubeMetadata.uri().toString();
//					metadata.content = gCubeMetadata.bytestream();
//					if(gCubeMetadata.bytestreamURI() != null) metadata.contentLocator = gCubeMetadata.bytestreamURI().toString();
//					if(gCubeMetadata.creationTime() != null) metadata.creationTime = new Long(gCubeMetadata.creationTime().getTimeInMillis()).toString();
//					metadata.document = doc;
//					metadata.language = gCubeMetadata.language();
//					if(gCubeMetadata.lastUpdate() != null) metadata.lastUpdate = new Long(gCubeMetadata.lastUpdate().getTimeInMillis()).toString();
//					metadata.length = gCubeMetadata.length();
//					metadata.mimeType = gCubeMetadata.mimeType();
//					metadata.schemaName = gCubeMetadata.schemaName();
//					if(gCubeMetadata.schemaURI() != null) metadata.schemaUri = gCubeMetadata.schemaURI().toString();
//					metadata.properties = new HashMap<String, DocumentProperty>();
//					if(gCubeMetadata.properties() != null)
//					{
//						for(Map.Entry<String, GCubeElementProperty> e : gCubeMetadata.properties().entrySet())
//						{
//							DocumentProperty prop = new DocumentProperty();
//							prop.key = e.getValue().key();
//							prop.type = e.getValue().type();
//							prop.value = e.getValue().value();
//							metadata.properties.put(e.getKey(), prop);
//						}
//					}
//					doc.metadata.add(metadata);
//				}
//			}
//		}
//		
//		if(includeAlternatives)
//		{
//			if(gCubeDoc.alternatives() != null && gCubeDoc.alternatives().size() != 0)
//			{
//				doc.alternatives = new ArrayList<DocumentAlternative>();
//				for(GCubeAlternative gCubeAlternative : gCubeDoc.alternatives().toList())
//				{
//					DocumentAlternative alternative = new GCubeOriginatingDocumentAlternative();
//					alternative.id = gCubeAlternative.id();
//					alternative.name = gCubeAlternative.name();
//					//if(gCubeAlternative.uri() != null) alternative.uri = gCubeAlternative.uri().toString();
//					alternative.content = gCubeAlternative.bytestream();
//					if(gCubeAlternative.bytestreamURI() != null) alternative.contentLocator = gCubeAlternative.bytestreamURI().toString();
//					if(gCubeAlternative.creationTime() != null) alternative.creationTime = new Long(gCubeAlternative.creationTime().getTimeInMillis()).toString();
//					alternative.document = doc;
//					alternative.language = gCubeAlternative.language();
//					if(gCubeAlternative.lastUpdate() != null) alternative.lastUpdate = new Long(gCubeAlternative.lastUpdate().getTimeInMillis()).toString();
//					alternative.length = gCubeAlternative.length();
//					alternative.mimeType = gCubeAlternative.mimeType();
//					alternative.schemaName = gCubeAlternative.schemaName();
//					if(gCubeAlternative.schemaURI() != null) alternative.schemaUri = gCubeAlternative.schemaURI().toString();
//					alternative.properties = new HashMap<String, DocumentProperty>();
//					if(gCubeAlternative.properties() != null)
//					{
//						for(Map.Entry<String, GCubeElementProperty> e : gCubeAlternative.properties().entrySet())
//						{
//							DocumentProperty prop = new DocumentProperty();
//							prop.key = e.getValue().key();
//							prop.type = e.getValue().type();
//							prop.value = e.getValue().value();
//							alternative.properties.put(e.getKey(), prop);
//							
//						}
//						doc.alternatives.add(alternative);
//					}
//				}
//			}
//		}
//		
//		if(includeAnnotations)
//		{
//			if(gCubeDoc.annotations() != null && gCubeDoc.annotations().size() != 0)
//			{
//				doc.annotations = new ArrayList<DocumentAnnotation>();
//				for(GCubeAnnotation gCubeAnnotation : gCubeDoc.annotations().toList())
//				{
//					DocumentAnnotation annotation = new GCubeOriginatingDocumentAnnotation();
//					annotation.id = gCubeAnnotation.id();
//					annotation.name = gCubeAnnotation.name();
//					if(gCubeAnnotation.uri() != null) annotation.uri = gCubeAnnotation.uri().toString();
//					annotation.content = gCubeAnnotation.bytestream();
//					if(gCubeAnnotation.bytestreamURI() != null) annotation.contentLocator = gCubeAnnotation.bytestreamURI().toString();
//					if(gCubeAnnotation.creationTime() != null) annotation.creationTime = new Long(gCubeAnnotation.creationTime().getTimeInMillis()).toString();
//					annotation.document = doc;
//					annotation.language = gCubeAnnotation.language();
//					if(gCubeAnnotation.lastUpdate() != null) annotation.lastUpdate = new Long(gCubeAnnotation.lastUpdate().getTimeInMillis()).toString();
//					annotation.length = gCubeAnnotation.length();
//					annotation.mimeType = gCubeAnnotation.mimeType();
//					annotation.schemaName = gCubeAnnotation.schemaName();
//					if(gCubeAnnotation.schemaURI() != null) annotation.schemaUri = gCubeAnnotation.schemaURI().toString();
//					annotation.properties = new HashMap<String, DocumentProperty>();
//					if(gCubeAnnotation.properties() != null)
//					{
//						for(Map.Entry<String, GCubeElementProperty> e : gCubeAnnotation.properties().entrySet())
//						{
//							DocumentProperty prop = new DocumentProperty();
//							prop.key = e.getValue().key();
//							prop.type = e.getValue().type();
//							prop.value = e.getValue().value();
//							annotation.properties.put(e.getKey(), prop);
//						}
//						doc.annotations.add(annotation);
//					}
//				}
//				
//				for(GCubeAnnotation gCubeAnnotation : gCubeDoc.annotations().toList())
//				{
//					DocumentAnnotation locatedAnnotation = null;
//					for(DocumentAnnotation annotation : doc.annotations)
//					{
//						if(annotation.id.equals(gCubeAnnotation.id()))
//						{
//							locatedAnnotation = annotation;
//							break;
//						}
//					}
//					String prevGCubeAnnotationId = gCubeAnnotation.previous().id();
//					
//					for(DocumentAnnotation previousAnnotation : doc.annotations)
//					{
//						if(previousAnnotation.id.equals(prevGCubeAnnotationId))
//						{
//							locatedAnnotation.previous = previousAnnotation;
//							break;
//						}
//					}
//				}
//			}
//		}
//		return doc;
//	}
//	
//	@Override
//	public List<Document> GetDocuments(String collectionId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		
//		try 
//		{
//			DocumentProjection dp = Projections.document();
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			RemoteIterator<GCubeDocument> documentIterator = cmReader.get(dp);
//
//			List<Document> docs = new ArrayList<Document>();
//			while(documentIterator.hasNext())
//			{
//				GCubeDocument gCubeDoc = documentIterator.next();
//				Document doc = translateDocument(gCubeDoc, true, true, true, true);
//				docs.add(doc);
//			}
//
//			return docs;
//		} catch (Exception e) {
//			throw new EnvironmentContentManagementSystemException("Could not retrieve documents of collection", e);
//		}
//	}
//	
//	@Override
//	public InputStream ResolveContent(String locator) throws EnvironmentContentManagementSystemException
//	{
//		try
//		{
//			if(locator == null) return null;
//			return new URI(locator).toURL().openStream();
//		}catch(Exception e)
//		{
//			throw new EnvironmentContentManagementSystemException("Could not resolve content locator", e);
//		}
//	}
//
//	@Override
//	public void SessionInit(EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		
//	}
//
//	@Override
//	public List<DocumentMetadata> GetMetadataDocuments(String collectionId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		
//		List<Document> documents = GetDocuments(collectionId, Hints);
//		List<DocumentMetadata> allMetadata = new ArrayList<DocumentMetadata>();
//		
//		try 
//		{
//			MetadataView tView = new MetadataView(GCUBEScope.getScope(scope));
//			tView.setCollectionId(collectionId);
//			List<MetadataView> newView = tView.findSimilar();
//
//			ViewReader reader = newView.get(0).reader();
//			MetadataProjection mp = Projections.metadata();
//			RemoteIterator<GCubeDocument> iterator = reader.get(mp);
//			while(iterator.hasNext()){
//				GCubeDocument gCubeDoc = iterator.next();
//				MetadataElements elements = gCubeDoc.metadata();
//				for (GCubeMetadata gCubeMetadata: elements){
//					DocumentMetadata metadata = new GCubeOriginatingDocumentMetadata();
//					metadata.id = gCubeMetadata.id();
//					metadata.name = gCubeMetadata.name();
//					//metadata.uri = gCubeMetadata.uri().toString();
//					metadata.content = gCubeMetadata.bytestream();
//					metadata.contentLocator = gCubeMetadata.bytestreamURI().toString();
//					metadata.creationTime = new Long(gCubeMetadata.creationTime().getTimeInMillis()).toString();
//					for(Document doc : documents)
//					{
//						if(doc.id.equals(gCubeMetadata.document().id()))
//						{
//							metadata.document = doc;
//							break;
//						}
//					}
//					metadata.language = gCubeMetadata.language();
//					metadata.lastUpdate = new Long(gCubeMetadata.lastUpdate().getTimeInMillis()).toString();
//					metadata.length = gCubeMetadata.length();
//					metadata.mimeType = gCubeMetadata.mimeType();
//					metadata.schemaName = gCubeMetadata.schemaName();
//					metadata.schemaUri = gCubeMetadata.schemaURI().toString();
//					metadata.properties = new HashMap<String, DocumentProperty>();
//					for(Map.Entry<String, GCubeElementProperty> e : gCubeMetadata.properties().entrySet())
//					{
//						DocumentProperty prop = new DocumentProperty();
//						prop.key = e.getValue().key();
//						prop.type = e.getValue().type();
//						prop.value = e.getValue().value();
//						metadata.properties.put(e.getKey(), prop);
//					}
//					allMetadata.add(metadata);
//				}
//			}
//			return allMetadata;
//
//		} catch (Exception e) {
//			throw new EnvironmentContentManagementSystemException("Could not retrieve metadata documents of collection", e);
//		}
//	}
//
//	@Override
//	public Document GetDocument(String collectionId, String documentId, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		
//		try 
//		{
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			DocumentProjection dp = Projections.document();
//
//			GCubeDocument gCubeDoc = cmReader.get(documentId, dp);
//			return translateDocument(gCubeDoc, true, true, true, true);
//
//		} catch (Exception e) 
//		{
//			throw new EnvironmentContentManagementSystemException("Could not retrieve document", e);
//		}
//	}
//
//	@Override
//	public Document GetMainDocument(String collectionId, String documentId,EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//	
//		try 
//		{
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			DocumentProjection dp = Projections.document().with(Projections.LENGTH,
//					Projections.opt(Projections.BYTESTREAM_URI),
//					Projections.NAME
//			);
//
//			GCubeDocument gCubeDoc = cmReader.get(documentId, dp);
//
//			return translateDocument(gCubeDoc, false, false, false, false);
//		}catch(Exception e)
//		{
//			throw new EnvironmentContentManagementSystemException("Could not retrieve main document", e);
//		}
//	}
//
//	@Override
//	public List<DocumentMetadata> GetDocumentMetadata(String collectionId,String documentId, String schemaName, String schemaUri,String language, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		try 
//		{
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			DocumentProjection dp = Projections.document().with(Projections.LENGTH,
//					Projections.opt(Projections.BYTESTREAM_URI),
//					Projections.NAME
//			);
//
//			GCubeDocument gCubeDoc = cmReader.get(documentId, dp);
//			Document doc = translateDocument(gCubeDoc, false, false, true, false);
//			
//			return doc.metadata;
//
//		} catch (Exception e) 
//		{
//			throw new EnvironmentContentManagementSystemException("Could not retrieve document metadata", e);
//		}
//	}
//
//	@Override
//	public String AddDocument(Document document, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		
//		//Collection id, document name, document mimetype, document content, document inbound stream
//		DocumentWriter cmWriter;
//		try 
//		{
//			if(document.collectionId == null) throw new Exception("Missing collection id");
//			if(document.name == null) throw new Exception("Missing document name");
//			if(accessors.get(document.collectionId) != null) cmWriter = accessors.get(document.collectionId).writer;
//			else cmWriter = new DocumentWriter(document.collectionId, GCUBEScope.getScope(scope));
//
//			GCubeDocument newDocument = new GCubeDocument();
//			newDocument.setMimeType(document.mimeType);
//			if(document.language != null) newDocument.setLanguage(new Locale(document.language));
//			if(document.schemaName != null) newDocument.setSchemaName(document.schemaName);
//			if(document.schemaURI != null) newDocument.setSchemaURI(new URI(document.schemaURI));
//			if(document.type != null) newDocument.setType(document.type);
//			InputStream contentStream = document.GetContentStream();
//			if(contentStream == null) throw new Exception("Missing content");
//			newDocument.setBytestream(contentStream);
//			newDocument.setName(document.name);
//			return cmWriter.add(newDocument);
//		}
//		catch(Exception e)
//		{
//			throw new EnvironmentContentManagementSystemException("Could not add document", e);
//		}	
//	}
//
//	@Override
//	public void DeleteDocument(String collectionId, String documentId,EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		try 
//		{
//			DocumentWriter cmWriter;
//			if(accessors.get(collectionId) != null) cmWriter = accessors.get(collectionId).writer;
//			else cmWriter = new DocumentWriter(collectionId, GCUBEScope.getScope(scope));
//
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//			GCubeDocument document = cmReader.get(documentId,dp);
//
//			cmWriter.delete(document);
//		} catch (Exception e) 
//		{
//			throw new EnvironmentContentManagementSystemException("Could not delete document", e);
//		}
//	}
//
//	@Override
//	public void AddAlternative(String collectionId, String documentId,DocumentAlternative alternative, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		try 
//		{
//			//alternative mimetype, alternative content stream
//			//we create the alternative to add
//			if(alternative.mimeType == null) throw new Exception("Missing mime type");
//			
//			GCubeAlternative newgCubeAlternative = new GCubeAlternative(alternative.mimeType);
//			newgCubeAlternative.setMimeType(alternative.mimeType);
//			InputStream contentStream = alternative.GetContentStream();
//			if(contentStream == null) throw new Exception("Missing content");
//			newgCubeAlternative.setBytestream(contentStream);
//			if(alternative.name != null) newgCubeAlternative.setName(alternative.name);
//			if(alternative.language != null) newgCubeAlternative.setLanguage(new Locale(alternative.language));
//			if(alternative.schemaName != null) newgCubeAlternative.setSchemaName(alternative.schemaName);
//			if(alternative.schemaUri != null) newgCubeAlternative.setSchemaURI(new URI(alternative.schemaUri));
//			if(alternative.type != null) newgCubeAlternative.setType(alternative.type);
//			
//			DocumentWriter cmWriter;
//			if(accessors.get(collectionId) != null) cmWriter = accessors.get(collectionId).writer;
//			else cmWriter = new DocumentWriter(collectionId, GCUBEScope.getScope(scope));
//
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//			GCubeDocument document = cmReader.get(documentId,dp);
//
//			document.alternatives().add(newgCubeAlternative);
//			cmWriter.update(document);
//		} catch (Exception e) 
//		{
//			throw new EnvironmentContentManagementSystemException("Could not add document alternative", e);
//		}
//	}
//
//	@Override
//	public void DeleteAlternative(String collectionId, String documentId,DocumentAlternative alternative, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		try 
//		{
//			//alternative mime type
//
//			GCubeAlternative alt = new GCubeAlternative();
//			alt.setMimeType(alternative.mimeType);
//
//			DocumentWriter cmWriter;
//			if(accessors.get(collectionId) != null) cmWriter = accessors.get(collectionId).writer;
//			else cmWriter = new DocumentWriter(collectionId, GCUBEScope.getScope(scope));
//
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//			GCubeDocument document = cmReader.get(documentId,dp);
//
//			document.alternatives().remove(alt);
//			cmWriter.update(document);
//		} catch (Exception e) {
//			throw new EnvironmentContentManagementSystemException("Could not delete document alternative", e);
//		}
//	}
//
//	@Override
//	public void AddMetadata(String collectionId, String documentId, DocumentMetadata metadata, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		try 
//		{
//			//metadata schema name, schema uri, language
//			if(metadata.schemaName == null) throw new Exception("Missing schema name");
//			if(metadata.schemaUri == null) throw new Exception("Missing schema uri");
//			if(metadata.language == null) throw new Exception("Missing language");
//			
//			GCubeMetadata newgCubeMetadata = new GCubeMetadata();
//			newgCubeMetadata.setSchemaName(metadata.schemaName);
//			newgCubeMetadata.setSchemaURI(new URI(metadata.schemaUri));
//			newgCubeMetadata.setLanguage(new Locale(metadata.language));
//			if(metadata.mimeType != null) newgCubeMetadata.setMimeType(metadata.mimeType);
//			if(metadata.type != null) newgCubeMetadata.setType(metadata.type);
//			//then we ask for adding the new document
//			
//			InputStream contentStream = metadata.GetContentStream();
//			if(contentStream == null) throw new Exception("Missing content");
//			newgCubeMetadata.setBytestream(contentStream);
//
//			DocumentWriter cmWriter;
//			if(accessors.get(collectionId) != null) cmWriter = accessors.get(collectionId).writer;
//			else cmWriter = new DocumentWriter(collectionId, GCUBEScope.getScope(scope));
//
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//			GCubeDocument document = cmReader.get(documentId,dp);
//
//			document.metadata().add(newgCubeMetadata);
//			cmWriter.update(document);
//		} catch (Exception e) 
//		{
//			throw new EnvironmentContentManagementSystemException("Could not add document metadata", e);
//		}
//	}
//	
//
//	public void AddAnnotation(String collectionId, String documentId, DocumentAnnotation annotation, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		try 
//		{
//			//metadata schema name, schema uri, language
//			if(annotation.schemaName == null) throw new Exception("Missing schema name");
//			if(annotation.schemaUri == null) throw new Exception("Missing schema uri");
//			if(annotation.language == null) throw new Exception("Missing language");
//			
//			GCubeAnnotation newgCubeAnnotation = new GCubeAnnotation();
//			if(annotation.schemaName != null) newgCubeAnnotation.setSchemaName(annotation.schemaName);
//			if(annotation.schemaUri != null) newgCubeAnnotation.setSchemaURI(new URI(annotation.schemaUri));
//			if(annotation.language != null) newgCubeAnnotation.setLanguage(new Locale(annotation.language));
//			if(annotation.mimeType != null) newgCubeAnnotation.setMimeType(annotation.mimeType);
//			if(annotation.type != null) newgCubeAnnotation.setType(annotation.type);
//			
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			
//			if(annotation.previous != null)
//			{
//				DocumentProjection dp = Projections.document();
//				GCubeDocument doc = cmReader.get(documentId, dp);
//				if(doc.annotations() != null)
//				{
//					for(GCubeAnnotation ann : doc.annotations().toList())
//					{
//						if(ann.id().equals(annotation.previous.id))
//						{
//							newgCubeAnnotation.setPrevious(ann);
//							break;
//						}
//					}
//				}
//			}
//			
//			InputStream contentStream = annotation.GetContentStream();
//			if(contentStream == null) throw new Exception("Missing content");
//			newgCubeAnnotation.setBytestream(contentStream);
//
//			DocumentWriter cmWriter;
//			if(accessors.get(collectionId) != null) cmWriter = accessors.get(collectionId).writer;
//			else cmWriter = new DocumentWriter(collectionId, GCUBEScope.getScope(scope));
//
//			DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//			GCubeDocument document = cmReader.get(documentId,dp);
//
//			document.annotations().add(newgCubeAnnotation);
//			cmWriter.update(document);
//		} catch (Exception e) 
//		{
//			throw new EnvironmentContentManagementSystemException("Could not add document metadata", e);
//		}
//	}
//
//	@Override
//	public void DeleteMetadata(String collectionId, String documentId,DocumentMetadata metadata, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		try 
//		{
//			//metadata schema name, schema uri
//
//			GCubeMetadata meta = new GCubeMetadata();
//			meta.setSchemaName(metadata.schemaName);
//			meta.setSchemaURI(new URI(metadata.schemaUri));
//			meta.setLanguage(new Locale(metadata.language));
//
//			DocumentWriter cmWriter;
//			if(accessors.get(collectionId) != null) cmWriter = accessors.get(collectionId).writer;
//			else cmWriter = new DocumentWriter(collectionId, GCUBEScope.getScope(scope));
//
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//			GCubeDocument document = cmReader.get(documentId,dp);
//
//			document.metadata().remove(meta);
//			cmWriter.update(document);
//		} catch (Exception e) {
//			throw new EnvironmentContentManagementSystemException("Could not delete document metadata", e);
//		}
//	}
//
//	@Override
//	public void AddPart(String collectionId, String documentId, DocumentPart part, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException 
//	{
//		String scope = GetActionScope(Hints);
//		try 
//		{
//			if(part.mimeType == null) throw new Exception("Missing part");
//			
//			//part mime type
//			GCubePart newgCubePart = new GCubePart();
//			newgCubePart.setMimeType(part.mimeType);
//			if(part.language != null) newgCubePart.setLanguage(new Locale(part.language));
//			if(part.name != null) newgCubePart.setName(part.name);
//			if(part.order >= 0) newgCubePart.setOrder(part.order);
//			if(part.schemaName != null) newgCubePart.setSchemaName(part.schemaName);
//			if(part.schemaUri != null) newgCubePart.setSchemaURI(new URI(part.schemaUri));
//			if(part.type != null) newgCubePart.setType(part.type);
//		
//			InputStream contentStream = part.GetContentStream();
//			if(contentStream == null) throw new Exception("Missing content");
//			newgCubePart.setBytestream(contentStream);
//
//			DocumentWriter cmWriter;
//			if(accessors.get(collectionId) != null) cmWriter = accessors.get(collectionId).writer;
//			else cmWriter = new DocumentWriter(collectionId, GCUBEScope.getScope(scope));
//
//			DocumentReader cmReader;
//			if(accessors.get(collectionId) != null) cmReader = accessors.get(collectionId).reader;
//			else cmReader = new DocumentReader(collectionId, GCUBEScope.getScope(scope));
//			DocumentProjection dp = Projections.document().with(Projections.NAME);
//
//			GCubeDocument document = cmReader.get(documentId,dp);
//
//			document.setMimeType("multipart/mixed");
//			document.parts().add(newgCubePart);
//			cmWriter.update(document);
//		} catch (Exception e) {
//			throw new EnvironmentContentManagementSystemException("Could not add document part", e);
//		}
//	}
//	
//	private static String GetActionScope(EnvHintCollection Hints)
//	{
//		if(Hints==null) return null;
//		if(!Hints.HintExists(GCubeContentManagementSystemProvider.GCubeActionScopeHintName)) return null;
//		return Hints.GetHint(GCubeContentManagementSystemProvider.GCubeActionScopeHintName).Hint.Payload;
//	}
//	
//	private static boolean GetSynchronousCreation(EnvHintCollection Hints)
//	{
//		if(Hints==null) return SynchronousCreationDef;
//		if(!Hints.HintExists(GCubeContentManagementSystemProvider.SynchronousCreationHintName)) return SynchronousCreationDef;
//		return Boolean.parseBoolean(Hints.GetHint(GCubeContentManagementSystemProvider.SynchronousCreationHintName).Hint.Payload);
//	}
//	
//	private static boolean GetSynchronousDeletion(EnvHintCollection Hints)
//	{
//		if(Hints==null) return SynchronousDeletionDef;
//		if(!Hints.HintExists(GCubeContentManagementSystemProvider.SynchronousDeletionHintName)) return SynchronousDeletionDef;
//		return Boolean.parseBoolean(Hints.GetHint(GCubeContentManagementSystemProvider.SynchronousDeletionHintName).Hint.Payload);
//	}
//	
//	private static long GetSynchronousPollingInterval(EnvHintCollection Hints)
//	{
//		if(Hints==null) return SynchronousPollingIntervalDef;
//		if(!Hints.HintExists(GCubeContentManagementSystemProvider.SynchronousPollingIntervalHintName)) return SynchronousPollingIntervalDef;
//		return Long.parseLong(Hints.GetHint(GCubeContentManagementSystemProvider.SynchronousPollingIntervalHintName).Hint.Payload);
//	}
//	
//	private static TimeUnit GetSynchronousPollingIntervalUnit(EnvHintCollection Hints)
//	{
//		if(Hints==null) return SynchronousPollingIntervalUnitDef;
//		if(!Hints.HintExists(GCubeContentManagementSystemProvider.SynchronousPollingIntervalUnitHintName)) return SynchronousPollingIntervalUnitDef;
//		return TimeUnit.valueOf(Hints.GetHint(GCubeContentManagementSystemProvider.SynchronousPollingIntervalUnitHintName).Hint.Payload);
//	}
//
//	@Override
//	public Document NewDocument() throws EnvironmentContentManagementSystemException 
//	{
//		return new GCubeOriginatingDocument();
//	}
//
//	@Override
//	public DocumentMetadata NewDocumentMetadata() throws EnvironmentContentManagementSystemException 
//	{
//		return new GCubeOriginatingDocumentMetadata();
//	}
//
//	@Override
//	public DocumentPart NewDocumentPart() throws EnvironmentContentManagementSystemException 
//	{
//		return new GCubeOriginatingDocumentPart();
//	}
//
//	@Override
//	public DocumentAlternative NewDocumentAlternative() throws EnvironmentContentManagementSystemException 
//	{
//		return new GCubeOriginatingDocumentAlternative();
//	}
//
//	@Override
//	public DocumentAnnotation NewDocumentAnnotation() throws EnvironmentContentManagementSystemException 
//	{
//		return new GCubeOriginatingDocumentAnnotation();
//	}
//
//	@Override
//	public List<Document> GetDocumentsWithProperties(String collectionId, HashMap<String, String> properties, EnvHintCollection Hints)
//			throws EnvironmentContentManagementSystemException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void updateDocumentContent(Document document, EnvHintCollection Hints) throws EnvironmentContentManagementSystemException {
//		throw new EnvironmentContentManagementSystemException("",new UnsupportedOperationException("Operation not supported")); //TODO implement
//	}
//}
